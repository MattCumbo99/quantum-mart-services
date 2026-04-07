package com.mattrition.qmart.itemlisting.dto

import java.math.BigDecimal

/** Request body for patching an item listing. */
data class UpdateListingRequest(
    val title: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val imageUrl: String? = null,
    val isActive: Boolean? = null,
)
