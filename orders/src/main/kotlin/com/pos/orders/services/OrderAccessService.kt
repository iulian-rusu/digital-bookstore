package com.pos.orders.services

import com.pos.orders.controllers.OrderController
import com.pos.orders.interfaces.OrderAccessInterface
import com.pos.orders.interfaces.OrderResolutionInterface
import com.pos.orders.models.BookOrder
import com.pos.orders.models.requests.PostBookOrderRequest
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
import org.springframework.web.client.HttpClientErrorException

@Service
class OrderAccessService : IdentityAuthorized(), OrderAccessInterface {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var orderModelAssembler: OrderModelAssembler

    @Autowired
    private lateinit var orderResolutionInterface: OrderResolutionInterface

    private val logger = LoggerFactory.getLogger(OrderAccessService::class.java)

    override fun getAllOrders(clientId: Long, token: String?): ResponseEntity<CollectionModel<EntityModel<BookOrder>>> {
        return try {
            ifAuthorized(token, clientId) {
                mongoTemplate.findAll(BookOrder::class.java, collectionNameFor(clientId))
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
        } catch (e: Exception) {
            logger.error("getAllOrders(clientId=$clientId): $e")
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }


    override fun getOrder(clientId: Long, orderId: String, token: String?): ResponseEntity<EntityModel<BookOrder>> {
        return try {
            ifAuthorized(token, clientId) {
                mongoTemplate.findAll(BookOrder::class.java, collectionNameFor(clientId))
                    .firstOrNull {
                        it.getOrderId() == orderId
                    }?.let {
                        ResponseEntity.ok(orderModelAssembler.toModelWithClientId(it, clientId))
                    } ?: ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("getOrder(clientId=$clientId, orderId=$orderId): $e")
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun postOrder(
        clientId: Long,
        request: PostBookOrderRequest,
        token: String?
    ): ResponseEntity<EntityModel<BookOrder>> {
        return try {
            ifAuthorized(token, clientId) {
                if (!isValidRequest(request))
                    return@ifAuthorized ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()

                val order = orderResolutionInterface.resolveOrder(request)
                mongoTemplate.save(order, collectionNameFor(clientId))
                val entityModel = orderModelAssembler.toModelWithClientId(order, clientId)
                ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel)
            }
        } catch (e: HttpClientErrorException.NotFound) {
            logger.error("Not Found: $e")
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: Exception) {
            logger.error("postOrder(clientId=$clientId): $e")
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteAllOrders(clientId: Long, token: String?): ResponseEntity<Unit> {
        return try {
            ifAuthorized(token, clientId) {
                try {
                    mongoTemplate.dropCollection(collectionNameFor(clientId))
                    ResponseEntity.noContent().build()
                } catch (e: Exception) {
                    logger.error("deleteAllOrders(clientId=$clientId): $e")
                    ResponseEntity.notFound().build()
                }
            }
        } catch (e: Exception) {
            logger.error("deleteAllOrders(clientId=$clientId): $e")
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    override fun deleteOrder(clientId: Long, orderId: String, token: String?): ResponseEntity<Unit> {
        return try {
            ifAuthorized(token, clientId) {
                try {
                    mongoTemplate
                        .findAll(BookOrder::class.java, collectionNameFor(clientId))
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
        } catch (e: Exception) {
            logger.error("deleteOrder(clientId=$clientId, orderId=$orderId): $e")
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    private fun collectionNameFor(clientId: Long) = "client$clientId"
    private fun isValidRequest(request: PostBookOrderRequest) = request.items.all {
        it.quantity > 0 && it.isbn.isNotEmpty()
    }
}