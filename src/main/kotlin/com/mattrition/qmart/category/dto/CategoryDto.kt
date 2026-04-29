package com.mattrition.qmart.category.dto

import java.time.OffsetDateTime
import java.util.UUID

data class CategoryDto(
    val id: UUID,
    val name: String,
    val slug: String,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
)
