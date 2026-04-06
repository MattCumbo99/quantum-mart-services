package com.mattrition.qmart.itemlisting.dto

import com.mattrition.qmart.itemlisting.ItemListing
import com.mattrition.qmart.util.EntityMapper

object ItemListingMapper : EntityMapper<ItemListing, ItemListingDto> {
    /**
     * Converts an item listing database entity into its data transfer object.
     *
     * @param sellerUsername Username extracted from the sellerId
     */
    fun toDto(
        entity: ItemListing,
        sellerUsername: String,
    ) = ItemListingDto(
        id = entity.id,
        title = entity.title,
        description = entity.description,
        price = entity.price,
        imageUrl = entity.imageUrl,
        sellerId = entity.sellerId,
        sellerUsername = sellerUsername,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )

    @Deprecated(
        message = "Unsupported method. Will throw an error.",
        level = DeprecationLevel.ERROR,
    )
    override fun toDto(entity: ItemListing): ItemListingDto =
        throw UnsupportedOperationException("Use toDto(entity, sellerUsername) instead.")

    override fun asNewEntity(dto: ItemListingDto) =
        ItemListing(
            title = dto.title,
            description = dto.description,
            price = dto.price,
            imageUrl = dto.imageUrl,
            sellerId = dto.sellerId,
        )
}
