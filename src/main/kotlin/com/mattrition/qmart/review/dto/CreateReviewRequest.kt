package com.mattrition.qmart.review.dto

data class CreateReviewRequest(
    val body: String,
    val score: Int,
)
