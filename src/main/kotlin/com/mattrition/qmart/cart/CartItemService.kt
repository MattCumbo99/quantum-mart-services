package com.mattrition.qmart.cart

import com.mattrition.qmart.cart.dto.AddCartItemDto
import com.mattrition.qmart.cart.dto.CartItemWithListingDto
import com.mattrition.qmart.exception.BadRequestException
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.itemlisting.ItemListingRepository
import com.mattrition.qmart.itemlisting.dto.ItemListingMapper
import com.mattrition.qmart.user.UserRepository
import com.mattrition.qmart.util.authPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class CartItemService(
    private val cartItemRepo: CartItemRepository,
    private val itemListingRepo: ItemListingRepository,
    private val userRepo: UserRepository,
) {
    /** Retrieves a list of cart items and their listings held under a user's cart. */
    fun getCartItemsByUserId(userId: UUID): List<CartItemWithListingDto> {
        // Get the cart items held by this user
        val cartItems =
            cartItemRepo.findCartItemsByUserId(userId).ifEmpty {
                return emptyList()
            }

        return mappedCartItems(cartItems)
    }

    /** Retrieves a list of cart items associated with a guest session ID. */
    fun getCartItemsByGuestId(guestId: UUID): List<CartItemWithListingDto> {
        val cartItems =
            cartItemRepo.findGuestCartItems(guestId).ifEmpty {
                return emptyList()
            }

        return mappedCartItems(cartItems)
    }

    /** Clears items associated with a user ID. */
    @Transactional
    fun deleteCartItemsByUserId(userId: UUID) = cartItemRepo.deleteCartItemsByUserId(userId)

    /** Clears items associated with a guest session ID. */
    @Transactional
    fun deleteGuestCartItems(guestId: UUID) = cartItemRepo.deleteCartItemsByGuestSessionId(guestId)

    @Transactional
    fun deleteCartItemFromUser(
        userId: UUID,
        listingId: UUID,
    ) = cartItemRepo.deleteByUserIdAndListingId(userId, listingId)

    @Transactional
    fun deleteGuestCartItem(
        guestId: UUID,
        listingId: UUID,
    ) = cartItemRepo.deleteGuestItem(guestId, listingId)

    /**
     * Creates a new cart item entry in the database. If the listing already exists under the
     * requested user's ID, the quantity of the existing item is increased by `itemQuantity`.
     */
    fun addItemToCart(itemInfo: AddCartItemDto): CartItemWithListingDto {
        enforceValidCartItem(itemInfo)

        val existingItem =
            if (itemInfo.guestSessionId != null) {
                cartItemRepo.findGuestCartItems(itemInfo.guestSessionId)
            } else {
                // Get from user
                cartItemRepo.findCartItemsByUserId(itemInfo.userId!!)
            }.firstOrNull { it.listingId == itemInfo.listingInfo.id }

        return existingItem?.let { item ->
            // Update the existing item by adding the requested quantity to it
            item.quantity += itemInfo.itemQuantity
            item.updatedAt = OffsetDateTime.now()

            val newCartItem = cartItemRepo.save(item)

            CartItemWithListingDto(
                id = newCartItem.id!!,
                quantity = item.quantity,
                itemListing = itemInfo.listingInfo,
            )
        }
            ?: run {
                val itemEntity =
                    itemInfo.userId?.let {
                        CartItem(
                            userId = it,
                            listingId = itemInfo.listingInfo.id!!,
                            quantity = itemInfo.itemQuantity,
                        )
                    }
                        ?: CartItem(
                            guestSessionId = itemInfo.guestSessionId!!,
                            listingId = itemInfo.listingInfo.id!!,
                            quantity = itemInfo.itemQuantity,
                        )

                val savedCartItem = cartItemRepo.save(itemEntity)

                CartItemWithListingDto(
                    id = savedCartItem.id!!,
                    quantity = savedCartItem.quantity,
                    itemListing = itemInfo.listingInfo,
                )
            }
    }

    /**
     * Enforces the following rules:
     * 1. Identities do not clash (e.g. has either guest ID or user ID)
     * 2. If adding to user cart, user does not own the listing
     * 3. Quantity is above 0
     */
    private fun enforceValidCartItem(itemInfo: AddCartItemDto) {
        // Must belong to either guest OR user
        val hasNoOwner = (itemInfo.userId == null && itemInfo.guestSessionId == null)
        val hasBothOwners = (itemInfo.userId != null && itemInfo.guestSessionId != null)

        if (hasNoOwner || hasBothOwners) {
            throw BadRequestException(
                "Cart item ownership must not conflict between user and guest.",
            )
        }

        itemInfo.userId?.let { userId ->
            // User is not adding it to their own cart
            val authUser = authPrincipal()
            if (authUser == null || authUser.id != userId) {
                throw ForbiddenException("Forbidden.")
            }

            // User must not own listing
            val listing = itemInfo.listingInfo
            if (listing.sellerId == userId) {
                throw ForbiddenException("User $userId owns listing ${listing.id}")
            }
        }

        // Quantity must be above 0
        if (itemInfo.itemQuantity < 1) {
            throw BadRequestException("Cart item quantity must be greater than 0.")
        }
    }

    private fun mappedCartItems(cartItems: List<CartItem>): List<CartItemWithListingDto> {
        val listingIds = cartItems.map { it.listingId }

        // Retrieve information on each listing
        val itemListings = itemListingRepo.findAllById(listingIds).associateBy { it.id }

        return cartItems.map { cartItem ->
            val listing = itemListings[cartItem.listingId]!!
            val sellerUsername = userRepo.findById(listing.sellerId).get().username

            CartItemWithListingDto(
                id = cartItem.id!!,
                quantity = cartItem.quantity,
                itemListing = ItemListingMapper.toDto(listing, sellerUsername),
            )
        }
    }
}
