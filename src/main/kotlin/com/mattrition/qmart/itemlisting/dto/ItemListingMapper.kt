package com.mattrition.qmart.itemlisting.dto

import com.mattrition.qmart.category.Category
import com.mattrition.qmart.itemlisting.ItemListing

object ItemListingMapper {
    /**
     * Converts an item listing database entity into its data transfer object.
     *
     * @param sellerUsername Username extracted from the sellerId
     */
    fun toDto(
        entity: ItemListing,
        sellerUsername: String,
        category: Category,
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
        isActive = entity.isActive,
        quantitySold = entity.quantitySold,
        averageScore = entity.averageScore,
        reviewCount = entity.reviewCount,
        categoryName = category.name,
        categorySlug = category.slug,
    )
}
