package com.mattrition.qmart.review

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ReviewRepository : JpaRepository<Review, UUID> {
    @Query(
        """
            SELECT r FROM Review r
            WHERE r.listingId = :listingId
            ORDER BY r.createdAt DESC
        """,
    )
    fun findReviewsByListingId(listingId: UUID): List<Review>

    @Query(
        """
            SELECT r FROM Review r
            WHERE r.user.id = :userId
            ORDER BY r.createdAt DESC
        """,
    )
    fun findReviewsByUserId(userId: UUID): List<Review>

    @Query(
        """
            SELECT COUNT(r) > 0 FROM Review r
            WHERE r.user.id = :userId
                AND r.listingId = :listingId
        """,
    )
    fun hasUserReviewedListing(
        userId: UUID,
        listingId: UUID,
    ): Boolean
}
