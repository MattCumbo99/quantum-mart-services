package com.mattrition.qmart.orderitem.mapper

import com.mattrition.qmart.cart.dto.CartItemWithListingDto
import com.mattrition.qmart.orderitem.OrderItem
import com.mattrition.qmart.orderitem.dto.OrderItemDto
import com.mattrition.qmart.util.EntityMapper

object OrderItemMapper : EntityMapper<OrderItem, OrderItemDto> {
    /** Converts a cart item DTO to an order item DTO. */
    fun fromCartItemDto(cartItemDto: CartItemWithListingDto) =
        OrderItemDto(
            listingId = cartItemDto.itemListing.id!!,
            sellerId = cartItemDto.itemListing.sellerId,
            quantity = cartItemDto.quantity,
            listingPrice = cartItemDto.itemListing.price,
            listingTitle = cartItemDto.itemListing.title,
            listingDescription = cartItemDto.itemListing.description,
            listingImageUrl = cartItemDto.itemListing.imageUrl,
        )

    override fun toDto(entity: OrderItem) =
        OrderItemDto(
            id = entity.id!!,
            listingId = entity.listingId,
            sellerId = entity.sellerId,
            quantity = entity.quantity,
            listingPrice = entity.listingPrice,
            status = entity.status,
            paidAt = entity.paidAt,
            listingTitle = entity.listingTitle,
            listingDescription = entity.listingDescription,
            listingImageUrl = entity.listingImageUrl,
            shippedOn = entity.shippedOn,
            completedOn = entity.completedOn,
        )

    override fun asNewEntity(dto: OrderItemDto) =
        OrderItem(
            listingId = dto.listingId,
            sellerId = dto.sellerId,
            quantity = dto.quantity,
            listingPrice = dto.listingPrice,
            paidAt = dto.paidAt,
            listingTitle = dto.listingTitle,
            listingDescription = dto.listingDescription,
            listingImageUrl = dto.listingImageUrl,
        )
}
