package com.mattrition.qmart.order.dto

import java.math.BigDecimal
import java.util.UUID

/** Structure for incoming requests to create a new order. */
data class CreateOrderRequestDto(
    val buyerId: UUID?,
    val guestSessionId: UUID?,
    val guestEmail: String?,
    val totalPaid: BigDecimal,
    val shippingFirstname: String,
    val shippingLastname: String,
    val shippingAddress1: String,
    val shippingAddress2: String?,
    val shippingCity: String,
    val shippingState: String,
    val shippingZip: String,
    val shippingPhone: String,
)
