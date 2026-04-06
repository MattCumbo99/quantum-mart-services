package com.mattrition.qmart.cart

import com.mattrition.qmart.cart.dto.CartItemWithListingDto
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.itemlisting.ItemListingRepository
import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import com.mattrition.qmart.itemlisting.dto.ItemListingMapper
import com.mattrition.qmart.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

        val listingIds = cartItems.map { it.listingId }

        // Retrieve information on each listing
        val itemListings = itemListingRepo.findAllById(listingIds).associateBy { it.id }

        return cartItems.map { cartItem ->
            val listing = itemListings[cartItem.listingId]!!
            val sellerUsername = userRepo.findById(listing.sellerId).get().username

            CartItemWithListingDto(
                cartItemId = cartItem.id!!,
                quantity = cartItem.quantity,
                itemListing = ItemListingMapper.toDto(listing, sellerUsername),
            )
        }
    }

    /** Clears items associated with a user ID. */
    @Transactional
    fun deleteCartItemsByUserId(userId: UUID) = cartItemRepo.deleteCartItemsByUserId(userId)

    @Transactional
    fun deleteCartItemFromUser(
        userId: UUID,
        listingId: UUID,
    ) = cartItemRepo.deleteByUserIdAndListingId(userId, listingId)

    /**
     * Creates a new cart item entry in the database. If the listing already exists under the
     * requested user's ID, the quantity of the existing item is increased by `itemQuantity`.
     */
    fun addItemToCart(
        userId: UUID,
        listingDto: ItemListingDto,
        itemQuantity: Int = 1,
    ): CartItemWithListingDto {
        // Prevent users from adding items they are selling into their own cart
        if (listingDto.sellerId == userId) {
            throw ForbiddenException("User $userId owns listing ${listingDto.id}")
        }

        val existingItem =
            cartItemRepo.findCartItemsByUserId(userId).firstOrNull { it.listingId == listingDto.id }

        return if (existingItem == null) {
            val savedCartItem =
                cartItemRepo.save(
                    CartItem(userId = userId, listingId = listingDto.id!!, quantity = itemQuantity),
                )

            CartItemWithListingDto(
                cartItemId = savedCartItem.id!!,
                quantity = savedCartItem.quantity,
                itemListing = listingDto,
            )
        } else {
            // Update the existing item by adding the requested quantity to it
            existingItem.quantity += itemQuantity

            val newCartItem = cartItemRepo.save(existingItem)

            CartItemWithListingDto(
                cartItemId = newCartItem.id!!,
                quantity = existingItem.quantity,
                itemListing = listingDto,
            )
        }
    }
}
