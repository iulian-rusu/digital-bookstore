package com.pos.orders.persistence

import com.pos.orders.models.BookOrderInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : MongoRepository<BookOrderInfo, String>