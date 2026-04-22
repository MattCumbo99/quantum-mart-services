package com.mattrition.qmart.order.mapper

import com.mattrition.qmart.order.Order
import com.mattrition.qmart.order.dto.CreateOrderRequestDto
import com.mattrition.qmart.order.dto.OrderDto
import com.mattrition.qmart.orderitem.mapper.OrderItemMapper

object OrderMapper {
    fun toDto(entity: Order) =
        OrderDto(
            id = entity.id!!,
            buyerId = entity.buyerId,
            guestEmail = entity.guestEmail,
            status = entity.status,
            totalPaid = entity.totalPaid,
            createdAt = entity.createdAt,
            shippingFirstname = entity.shippingFirstname,
            shippingLastname = entity.shippingLastname,
            shippingAddress1 = entity.shippingAddress1,
            shippingAddress2 = entity.shippingAddress2,
            shippingCity = entity.shippingCity,
            shippingState = entity.shippingState,
            shippingZip = entity.shippingZip,
            shippingPhone = entity.shippingPhone,
            orderItems = entity.orderItems.map { OrderItemMapper.toDto(it) },
        )

    fun asNewEntity(dto: CreateOrderRequestDto) =
        Order(
            buyerId = dto.buyerId,
            guestEmail = dto.guestEmail,
            totalPaid = dto.totalPaid,
            shippingFirstname = dto.shippingFirstname,
            shippingLastname = dto.shippingLastname,
            shippingAddress1 = dto.shippingAddress1,
            shippingAddress2 = dto.shippingAddress2,
            shippingCity = dto.shippingCity,
            shippingState = dto.shippingState,
            shippingZip = dto.shippingZip,
            shippingPhone = dto.shippingPhone,
        )
}
