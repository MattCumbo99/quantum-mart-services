package com.mattrition.qmart.review.dto

import java.time.OffsetDateTime
import java.util.UUID

data class ReviewDto(
    val id: UUID,
    val userId: UUID,
    val username: String,
    val score: Int,
    val body: String,
    val isEdited: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)
