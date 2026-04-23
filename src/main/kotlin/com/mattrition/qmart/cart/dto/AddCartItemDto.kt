package com.mattrition.qmart.cart.dto

import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import java.util.UUID

data class AddCartItemDto(
    val userId: UUID?,
    val guestSessionId: UUID?,
    val listingInfo: ItemListingDto,
    val itemQuantity: Int,
)
