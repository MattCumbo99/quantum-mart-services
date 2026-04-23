package com.mattrition.qmart.cart.dto

import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import java.util.UUID

/**
 * Represents a cart item merged with listing information. Avoids having to make two separate calls
 * in order to retrieve listing information from a cart item listing ID.
 */
data class CartItemWithListingDto(
    val id: UUID,
    val quantity: Int,
    val itemListing: ItemListingDto,
)
