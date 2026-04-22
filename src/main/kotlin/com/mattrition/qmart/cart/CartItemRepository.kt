package com.mattrition.qmart.cart

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CartItemRepository : JpaRepository<CartItem, UUID> {
    fun findCartItemsByUserId(userId: UUID): List<CartItem>

    @Query(
        """
            SELECT ci FROM CartItem ci
            WHERE ci.guestSessionId = :guestSessionId
        """,
    )
    fun findGuestCartItems(guestId: UUID): List<CartItem>

    fun deleteCartItemsByUserId(userId: UUID)

    fun deleteCartItemsByGuestSessionId(guestSessionId: UUID)

    fun deleteByUserIdAndListingId(
        userId: UUID,
        listingId: UUID,
    )

    @Modifying
    @Query(
        """
            DELETE FROM CartItem ci
            WHERE ci.guestSessionId = :guestSessionId
                AND ci.listingId = :listingId
        """,
    )
    fun deleteGuestItem(
        guestSessionId: UUID,
        listingId: UUID,
    )
}
