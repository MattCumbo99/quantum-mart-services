package com.mattrition.qmart.cart

import com.mattrition.qmart.cart.dto.AddCartItemDto
import com.mattrition.qmart.cart.dto.CartItemWithListingDto
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/cart-items")
class CartItemController(
    private val cartService: CartItemService,
) {
    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    @GetMapping("/user/{userId}")
    fun getCartItemsByUserId(
        @PathVariable userId: UUID,
    ): List<CartItemWithListingDto> = cartService.getCartItemsByUserId(userId)

    @GetMapping("/guest/{guestSessionId}")
    fun getGuestCartItems(
        @PathVariable guestSessionId: UUID,
    ): List<CartItemWithListingDto> = cartService.getCartItemsByGuestId(guestSessionId)

    @PostMapping
    fun addItemToCart(
        @RequestBody addItemReq: AddCartItemDto,
    ): CartItemWithListingDto = cartService.addItemToCart(addItemReq)

    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    @DeleteMapping("/user/{userId}")
    fun clearCartItems(
        @PathVariable userId: UUID,
    ) = cartService.deleteCartItemsByUserId(userId)

    @DeleteMapping("/guest/{guestSessionId}")
    fun clearGuestCartItems(
        @PathVariable guestSessionId: UUID,
    ) = cartService.deleteGuestCartItems(guestSessionId)

    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    @DeleteMapping("/user/{userId}/listing/{listingId}")
    fun deleteCartItemFromUser(
        @PathVariable userId: UUID,
        @PathVariable listingId: UUID,
    ) = cartService.deleteCartItemFromUser(userId, listingId)

    @DeleteMapping("/guest/{guestSessionId}/listing/{listingId}")
    fun deleteCartItemFromGuest(
        @PathVariable guestSessionId: UUID,
        @PathVariable listingId: UUID,
    ) = cartService.deleteGuestCartItem(guestSessionId, listingId)
}
