package com.mattrition.qmart.review

import com.mattrition.qmart.review.dto.CreateReviewRequest
import com.mattrition.qmart.review.dto.EditReviewRequest
import com.mattrition.qmart.review.dto.ReviewDto
import com.mattrition.qmart.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val reviewService: ReviewService,
) {
    @GetMapping("/listing/{listingId}")
    fun getReviews(
        @PathVariable listingId: UUID,
    ): List<ReviewDto> = reviewService.getListingReviews(listingId)

    @GetMapping("/user/{userId}")
    fun getUserReviews(
        @PathVariable userId: UUID,
    ): List<ReviewDto> = reviewService.getReviewsByUser(userId)

    @PostMapping("/listing/{listingId}")
    @RolesAllowed(UserRole.USER)
    fun createReview(
        @PathVariable listingId: UUID,
        @RequestBody req: CreateReviewRequest,
    ): ResponseEntity<ReviewDto> {
        val review = reviewService.createReview(req, listingId)

        return ResponseEntity(review, HttpStatus.CREATED)
    }

    @PatchMapping("/{reviewId}")
    @RolesAllowed(UserRole.USER)
    fun updateReview(
        @PathVariable reviewId: UUID,
        @RequestBody request: EditReviewRequest,
    ): ResponseEntity<ReviewDto> = reviewService.editReview(reviewId, request)

    @DeleteMapping("/{reviewId}")
    @RolesAllowed(UserRole.USER)
    fun deleteReview(
        @PathVariable reviewId: UUID,
    ): ResponseEntity<Void> = reviewService.deleteReview(reviewId)
}
