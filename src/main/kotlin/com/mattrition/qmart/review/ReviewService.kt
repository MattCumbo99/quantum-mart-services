package com.mattrition.qmart.review

import com.mattrition.qmart.auth.CustomUserDetails
import com.mattrition.qmart.exception.BadRequestException
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import com.mattrition.qmart.itemlisting.ItemListing
import com.mattrition.qmart.itemlisting.ItemListingRepository
import com.mattrition.qmart.review.dto.CreateReviewRequest
import com.mattrition.qmart.review.dto.EditReviewRequest
import com.mattrition.qmart.review.dto.ReviewDto
import com.mattrition.qmart.review.mapper.ReviewMapper
import com.mattrition.qmart.user.UserRepository
import com.mattrition.qmart.util.authPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val itemListingRepository: ItemListingRepository,
    private val userRepository: UserRepository,
) {
    /** Retrieves all reviews left on an item listing sorted newest first. */
    fun getListingReviews(listingId: UUID) =
        reviewRepository.findReviewsByListingId(listingId).map { review ->
            ReviewMapper.toDto(review)
        }

    /** Retrieves all reviews left by a user sorted newest first. */
    fun getReviewsByUser(userId: UUID) = reviewRepository.findReviewsByUserId(userId).map { ReviewMapper.toDto(it) }

    /** Saves a review to the database. */
    @Transactional
    fun createReview(
        request: CreateReviewRequest,
        listingId: UUID,
    ): ReviewDto {
        val listing =
            itemListingRepository.findById(listingId).getOrElse {
                throw NotFoundException("Listing $listingId not found.")
            }

        val authUser = authPrincipal()!!

        enforceCreationRules(authUser, request, listing)

        val reviewAuthor = userRepository.findById(authUser.id).get()

        val reviewEntity =
            Review(
                listingId = listingId,
                user = reviewAuthor,
                body = request.body,
                score = request.score,
            )

        val saved = reviewRepository.save(reviewEntity)

        // Adjust review data on the item listing
        val prevCount = listing.reviewCount
        val prevAvg = listing.averageScore.toDouble()

        val newAvg = ((prevAvg * prevCount) + request.score) / (prevCount + 1)

        listing.reviewCount += 1
        listing.averageScore = newAvg.toBigDecimal()

        return ReviewMapper.toDto(saved)
    }

    /**
     * Modifies a review entity in the database.
     *
     * @param reviewId ID of the review to edit
     * @param request Entry data to patch in
     * @return Response entity with the new review information.
     */
    @Transactional
    fun editReview(
        reviewId: UUID,
        request: EditReviewRequest,
    ): ResponseEntity<ReviewDto> {
        val review =
            reviewRepository.findById(reviewId).getOrElse {
                throw NotFoundException("Review $reviewId not found.")
            }

        val authUser = authPrincipal()!!
        if (review.user.id != authUser.id) {
            throw ForbiddenException("Forbidden.")
        }

        // Cancel transaction if no new changes are submitted
        if (isEditableNotChanged(request, review)) {
            return ResponseEntity.noContent().build()
        }

        request.newBody?.let { review.body = it }
        request.newScore?.let { newScore ->
            if (newScore !in 1..5) {
                throw BadRequestException("New score must be between 1 and 5 (was $newScore)")
            }

            // Adjust the listing with the new score
            val listing = itemListingRepository.findById(review.listingId).get()

            val reviewCount = listing.reviewCount
            val prevAvg = listing.averageScore.toDouble()
            val prevScore = review.score

            val newAvg = ((prevAvg * reviewCount) - prevScore + newScore) / reviewCount

            review.score = newScore

            listing.averageScore = newAvg.toBigDecimal()
            itemListingRepository.save(listing)
        }

        review.isEdited = true
        review.updatedAt = OffsetDateTime.now()

        reviewRepository.save(review)

        return ResponseEntity.ok(ReviewMapper.toDto(review))
    }

    @Transactional
    fun deleteReview(reviewId: UUID): ResponseEntity<Void> {
        val review =
            reviewRepository.findById(reviewId).getOrElse {
                throw NotFoundException("Review $reviewId not found.")
            }

        val authUser = authPrincipal()!!
        if (review.user.id != authUser.id) {
            throw ForbiddenException("Forbidden.")
        }

        reviewRepository.deleteById(review.id!!)

        // Update the average score on the item listing
        val listing = itemListingRepository.findById(review.listingId).get()

        val prevAvg = listing.averageScore.toDouble()
        val prevCount = listing.reviewCount
        val removedScore = review.score

        val newCount = prevCount - 1
        val newAvg =
            if (newCount == 0) {
                0.0
            } else {
                ((prevAvg * prevCount) - removedScore) / newCount
            }

        listing.reviewCount = newCount
        listing.averageScore = newAvg.toBigDecimal()

        itemListingRepository.save(listing)

        return ResponseEntity.noContent().build()
    }

    private fun isEditableNotChanged(
        request: EditReviewRequest,
        review: Review,
    ): Boolean {
        val bodyChanged = request.newBody != null && request.newBody != review.body
        val scoreChanged = request.newScore != null && request.newScore != review.score

        return !bodyChanged && !scoreChanged
    }

    /** Runs a bunch of checks to ensure the authenticated user can create the review. */
    private fun enforceCreationRules(
        authUser: CustomUserDetails,
        request: CreateReviewRequest,
        listing: ItemListing,
    ) {
        val userId = authUser.id

        // 1. User must not own listing
        if (userId == listing.sellerId) {
            throw ForbiddenException("User $userId owns listing ${listing.id}")
        }

        // 2. User must not have placed a previous review on the listing
        val exists = reviewRepository.hasUserReviewedListing(userId, listing.id!!)
        if (exists) {
            throw ForbiddenException("User $userId already has review for ${listing.id}")
        }

        // 3. Score must be between 1 and 5
        if (request.score !in 1..5) {
            throw BadRequestException("Score must be between 1 and 5. Actual: ${request.score}")
        }
    }
}
