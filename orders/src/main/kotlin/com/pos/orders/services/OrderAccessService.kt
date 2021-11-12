package com.pos.orders.services

import com.pos.orders.controllers.OrderController
import com.pos.orders.interfaces.OrderAccessInterface
import com.pos.orders.interfaces.StockValidationInterface
import com.pos.orders.models.Order
import com.pos.orders.views.OrderModelAssembler
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

@Service
class OrderAccessService : OrderAccessInterface {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var orderModelAssembler: OrderModelAssembler

    @Autowired
    private lateinit var stockValidationInterface: StockValidationInterface

    override fun getAllOrders(clientId: Long): CollectionModel<EntityModel<Order>> =
        mongoTemplate.findAll(Order::class.java, collectionNameFor(clientId))
            .map {
                orderModelAssembler.toModelWithClientId(it, clientId)
            }.let { modelList ->
                CollectionModel.of(
                    modelList,
                    linkTo(methodOn(OrderController::class.java).getAllOrders(clientId)).withSelfRel()
                )
            }

    override fun getOrder(clientId: Long, orderId: String): ResponseEntity<EntityModel<Order>> =
        mongoTemplate.findAll(Order::class.java, collectionNameFor(clientId))
            .firstOrNull {
                it.getOrderId() == orderId
            }
            ?.let {
                ResponseEntity.ok(orderModelAssembler.toModelWithClientId(it, clientId))
            }
            ?: ResponseEntity.notFound().build()

    override fun postOrder(clientId: Long, order: Order): ResponseEntity<EntityModel<Order>> {
        if (!validOrder(order))
            return ResponseEntity.status(HttpStatus.CONFLICT).build()

        if (!stockValidationInterface.postOrder(order))
            return ResponseEntity.status(HttpStatus.CONFLICT).build()

        return try {
            mongoTemplate.save(order, collectionNameFor(clientId))
            val entityModel = orderModelAssembler.toModelWithClientId(order, clientId)
            ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel)
        } catch (e: Exception) {
            println(e)
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    override fun putOrder(clientId: Long, orderId: String, order: Order): ResponseEntity<EntityModel<Order>> {
        order.setOrderId(orderId)
        return postOrder(clientId, order)
    }

    override fun deleteAllOrders(clientId: Long): ResponseEntity<Unit> {
        return try {
            mongoTemplate.dropCollection(collectionNameFor(clientId))
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            println(e)
            ResponseEntity.notFound().build()
        }
    }

    override fun deleteOrder(clientId: Long, orderId: String): ResponseEntity<Unit> {
        return try {
            mongoTemplate
                .findAll(Order::class.java, collectionNameFor(clientId))
                .firstOrNull {
                    it.getOrderId() == orderId
                }?.let {
                    mongoTemplate.remove(it, collectionNameFor(clientId))
                    ResponseEntity.noContent().build()
                } ?: ResponseEntity.notFound().build()
        } catch (e: Exception) {
            println(e)
            ResponseEntity.notFound().build()
        }
    }

    private fun collectionNameFor(clientId: Long) = "client$clientId"
    private fun validOrder(order: Order) =
        order.getItems().all {
            it.getPrice() >= 0 && it.getQuantity() >= 0
        }
}