package com.mattrition.qmart.itemlisting.dto

import java.math.BigDecimal

data class CreateListingRequest(
    val title: String,
    val description: String? = null,
    val price: BigDecimal,
    val imageUrl: String? = null,
    val categorySlug: String,
)
