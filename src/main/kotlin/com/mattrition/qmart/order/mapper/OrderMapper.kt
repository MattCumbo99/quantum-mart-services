package com.mattrition.qmart.order.mapper

import com.mattrition.qmart.order.Order
import com.mattrition.qmart.order.dto.OrderDto
import com.mattrition.qmart.orderitem.mapper.OrderItemMapper
import com.mattrition.qmart.util.EntityMapper

object OrderMapper : EntityMapper<Order, OrderDto> {
    override fun toDto(entity: Order) =
        OrderDto(
            id = entity.id!!,
            buyerId = entity.buyerId!!,
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

    override fun asNewEntity(dto: OrderDto) =
        Order(
            buyerId = dto.buyerId,
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
