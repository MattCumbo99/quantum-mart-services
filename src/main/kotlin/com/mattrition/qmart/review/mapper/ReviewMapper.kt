package com.mattrition.qmart.review.mapper

import com.mattrition.qmart.review.Review
import com.mattrition.qmart.review.dto.ReviewDto

object ReviewMapper {
    fun toDto(entity: Review) =
        ReviewDto(
            id = entity.id!!,
            userId = entity.user.id!!,
            username = entity.user.username,
            score = entity.score,
            body = entity.body,
            isEdited = entity.isEdited,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
