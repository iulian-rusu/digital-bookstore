package com.pos.orders.business.services

import com.pos.orders.api.controllers.OrderController
import com.pos.orders.api.requests.BookOrderRequest
import com.pos.orders.api.requests.UpdateOrderRequest
import com.pos.orders.api.views.OrderModelAssembler
import com.pos.orders.business.models.BookOrder
import com.pos.orders.business.models.OrderStatus
import com.pos.orders.business.services.interfaces.OrderAccessInterface
import com.pos.orders.business.services.interfaces.OrderResolutionInterface
import com.pos.shared.identity.IdentityAuthorized
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
import org.springframework.web.server.ResponseStatusException

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
            ifAuthorizedAs(clientId, token) {
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
            ifAuthorizedAs(clientId, token) {
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
        request: BookOrderRequest,
        token: String?
    ): ResponseEntity<EntityModel<BookOrder>> {
        return try {
            ifAuthorizedAs(clientId, token) {
                if (!isValidRequest(request))
                    return@ifAuthorizedAs ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()

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

    override fun putOrder(
        clientId: Long,
        orderId: String,
        request: UpdateOrderRequest,
        token: String?
    ): ResponseEntity<EntityModel<BookOrder>> {
        if (request.status !in OrderStatus.VALUES)
            throw ResponseStatusException(HttpStatus.CONFLICT, "Invalid status for order: ${request.status}")

        return try {
            ifAuthorizedAs(clientId, token) {
                val order = mongoTemplate.findAll(BookOrder::class.java, collectionNameFor(clientId))
                    .firstOrNull {
                        it.getOrderId() == orderId
                    } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find order")
                order.setStatus(request.status)
                mongoTemplate.save(order, collectionNameFor(clientId))
                ResponseEntity.ok(orderModelAssembler.toModelWithClientId(order, clientId))
            }
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            logger.error("putOrder(clientId=$clientId, orderId=$orderId): $e")
            throw ResponseStatusException(HttpStatus.CONFLICT, "Error processing order update")
        }
    }

    override fun deleteAllOrders(clientId: Long, token: String?): ResponseEntity<Unit> {
        return try {
            ifAuthorizedAs(clientId, token) {
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
            ifAuthorizedAs(clientId, token) {
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
    private fun isValidRequest(request: BookOrderRequest) = request.items.all {
        it.quantity > 0 && it.isbn.isNotEmpty()
    }
}