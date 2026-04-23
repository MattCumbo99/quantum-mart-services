package com.mattrition.qmart.order.dto

import com.mattrition.qmart.orderitem.dto.OrderItemDto
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class OrderDto(
    val id: UUID,
    val buyerId: UUID?,
    val guestEmail: String?,
    val status: String,
    val totalPaid: BigDecimal,
    val createdAt: OffsetDateTime,
    val shippingFirstname: String,
    val shippingLastname: String,
    val shippingAddress1: String,
    val shippingAddress2: String?,
    val shippingCity: String,
    val shippingState: String,
    val shippingZip: String,
    val shippingPhone: String,
    val orderItems: List<OrderItemDto>,
)
