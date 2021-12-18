package com.pos.orders.services

import com.pos.orders.controllers.OrderController
import com.pos.orders.interfaces.OrderAccessInterface
import com.pos.orders.interfaces.OrderValidationInterface
import com.pos.orders.models.Order
import com.pos.orders.models.OrderStatus
import com.pos.orders.models.PostOrderRequest
import com.pos.orders.views.OrderModelAssembler
import com.pos.shared.ws.IdentityAuthorized
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class OrderAccessService : IdentityAuthorized(), OrderAccessInterface {
    companion object {
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
    }

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var orderModelAssembler: OrderModelAssembler

    @Autowired
    private lateinit var stockValidationInterface: OrderValidationInterface

    private val logger = LoggerFactory.getLogger(OrderAccessService::class.java)

    override fun getAllOrders(clientId: Long, token: String?): ResponseEntity<CollectionModel<EntityModel<Order>>> =
        ifAuthorized(token, clientId) {
            mongoTemplate.findAll(Order::class.java, collectionNameFor(clientId))
                .map {
                    orderModelAssembler.toModelWithClientId(it, clientId)
                }.let { modelList ->
                    CollectionModel.of(
                        modelList,
                        linkTo(methodOn(OrderController::class.java).getAllOrders(clientId, null)).withSelfRel()
                    )
                }.let {
                    ResponseEntity.ok(it)
                }
        }

    override fun getOrder(clientId: Long, orderId: String, token: String?): ResponseEntity<EntityModel<Order>> =
        ifAuthorized(token, clientId) {
            mongoTemplate.findAll(Order::class.java, collectionNameFor(clientId))
                .firstOrNull {
                    it.getOrderId() == orderId
                }?.let {
                    ResponseEntity.ok(orderModelAssembler.toModelWithClientId(it, clientId))
                } ?: ResponseEntity.notFound().build()
        }


    override fun postOrder(
        clientId: Long,
        request: PostOrderRequest,
        token: String?
    ): ResponseEntity<EntityModel<Order>> = insertOrder(clientId, request, token)

    override fun putOrder(
        clientId: Long,
        orderId: String,
        request: PostOrderRequest,
        token: String?
    ): ResponseEntity<EntityModel<Order>> = insertOrder(clientId, request, token, orderId)

    override fun deleteAllOrders(clientId: Long, token: String?): ResponseEntity<Unit> =
        ifAuthorized(token, clientId) {
            try {
                mongoTemplate.dropCollection(collectionNameFor(clientId))
                ResponseEntity.noContent().build()
            } catch (e: Exception) {
                logger.error("deleteAllOrders(clientId=$clientId): $e")
                ResponseEntity.notFound().build()
            }
        }

    override fun deleteOrder(clientId: Long, orderId: String, token: String?): ResponseEntity<Unit> =
        ifAuthorized(token, clientId) {
            try {
                mongoTemplate
                    .findAll(Order::class.java, collectionNameFor(clientId))
                    .firstOrNull {
                        it.getOrderId() == orderId
                    }?.let {
                        mongoTemplate.remove(it, collectionNameFor(clientId))
                        ResponseEntity.noContent().build()
                    } ?: ResponseEntity.notFound().build()
            } catch (e: Exception) {
                logger.error("deleteOrder(clientId=$clientId, orderId=$orderId): $e")
                ResponseEntity.notFound().build()
            }
        }

    private fun insertOrder(
        clientId: Long,
        request: PostOrderRequest,
        token: String?,
        orderId: String? = null
    ): ResponseEntity<EntityModel<Order>> = ifAuthorized(token, clientId) {
        val validationStatus = stockValidationInterface.validateOrder(request)
        if (validationStatus != HttpStatus.OK)
            return@ifAuthorized ResponseEntity.status(validationStatus).build()

        return@ifAuthorized try {
            val currentDate = dateFormat.format(Date())
            val order = Order(orderId, currentDate.toString(), request.items, OrderStatus.PENDING)
            mongoTemplate.save(order, collectionNameFor(clientId))
            val entityModel = orderModelAssembler.toModelWithClientId(order, clientId)
            ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel)
        } catch (e: Exception) {
            logger.error("postOrder(clientId=$clientId): $e")
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    private fun collectionNameFor(clientId: Long) = "client$clientId"
}