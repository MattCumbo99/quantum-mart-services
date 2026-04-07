package com.mattrition.qmart.itemlisting.dto

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class ItemListingDto(
    val id: UUID? = null,
    val title: String,
    val description: String? = null,
    val price: BigDecimal,
    val imageUrl: String?,
    val sellerId: UUID,
    val sellerUsername: String,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    val isActive: Boolean = true,
)
