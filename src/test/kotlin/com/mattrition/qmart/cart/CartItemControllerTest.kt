package com.mattrition.qmart.cart

import com.mattrition.qmart.BaseH2Test
import com.mattrition.qmart.itemlisting.ItemListing
import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import com.mattrition.qmart.itemlisting.dto.toDto
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.jvm.optionals.getOrNull

class CartItemControllerTest : BaseH2Test() {
    companion object {
        const val BASE_PATH = "/api/cart-items"
    }

    @Autowired lateinit var cartItemRepository: CartItemRepository

    private lateinit var listing1: ItemListing
    private lateinit var listing1Dto: ItemListingDto
    private lateinit var listing2: ItemListing
    private lateinit var listing2Dto: ItemListingDto

    private lateinit var cartItem1: CartItem
    private lateinit var cartItem2: CartItem

    @BeforeEach
    fun addCartItems() {
        val listings = super.initListings()
        listing1 = listings.last()
        listing1Dto = listing1.toDto(TestUsers.admin.username)

        listing2 = listings.first()
        listing2Dto = listing2.toDto(TestUsers.moderator.username)

        val item1 = CartItem(userId = TestUsers.user.id, listingId = listing1.id, quantity = 1)

        val item2 = CartItem(userId = TestUsers.user.id, listingId = listing2.id, quantity = 1)

        cartItem1 = cartItemRepository.save(item1)
        cartItem2 = cartItemRepository.save(item2)
    }

    @Nested
    inner class AddItem {
        @Test
        fun `adding item to other cart returns forbidden 403`() {
            mockRequest(
                requestType = POST,
                path = "$BASE_PATH/user/${TestUsers.user.id}",
                token = TestTokens.admin,
                body = listing1Dto,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should add item to cart`() {
            cartItemRepository.findCartItemsByUserId(TestUsers.superadmin.id!!).size shouldBe 0

            mockRequest(
                requestType = POST,
                path = "$BASE_PATH/user/${TestUsers.superadmin.id}",
                token = TestTokens.superadmin,
                body = listing1Dto,
            ).andExpect(status().isOk)

            val userItems = cartItemRepository.findCartItemsByUserId(TestUsers.superadmin.id!!)
            userItems.size shouldBe 1
            userItems.first().listingId shouldBe listing1.id
        }

        @Test
        fun `user should not be able to add owned items to cart`() {
            val ogItem = itemListingRepository.findById(listing1.id!!).getOrNull().shouldNotBeNull()
            val listingSellerId = ogItem.sellerId.shouldNotBeNull()
            listingSellerId shouldBeEqual TestUsers.admin.id!!
            listing1Dto.sellerId shouldBeEqual TestUsers.admin.id!!

            mockRequest(
                requestType = POST,
                path = "$BASE_PATH/user/${TestUsers.admin.id}",
                token = TestTokens.admin,
                body = listing1Dto,
            ).andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class RemoveItems {
        @Test
        fun `clearing items from another cart should return forbidden 403`() {
            mockRequest(
                requestType = DELETE,
                path = "$BASE_PATH/user/${TestUsers.user.id}",
                token = TestTokens.superadmin,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should clear all cart items`() {
            cartItemRepository.findCartItemsByUserId(TestUsers.user.id!!).size shouldBeGreaterThan 0

            mockRequest(
                requestType = DELETE,
                path = "$BASE_PATH/user/${TestUsers.user.id}",
                token = TestTokens.user,
            ).andExpect(status().isOk)

            cartItemRepository.findCartItemsByUserId(TestUsers.user.id!!).size shouldBe 0
        }

        @Test
        fun `removing an item from another cart should return forbidden 403`() {
            mockRequest(
                requestType = DELETE,
                path = "$BASE_PATH/user/${TestUsers.user.id}/listing/${listing1.id}",
                token = TestTokens.superadmin,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should remove an item from cart`() {
            cartItemRepository.findById(cartItem1.id!!).getOrNull().shouldNotBeNull()

            mockRequest(
                requestType = DELETE,
                path = "$BASE_PATH/user/${TestUsers.user.id}/listing/${listing1.id}",
                token = TestTokens.user,
            ).andExpect(status().isOk)

            cartItemRepository.findById(cartItem1.id!!).getOrNull().shouldBeNull()
            cartItemRepository.findCartItemsByUserId(TestUsers.user.id!!).size shouldBe 1
        }
    }

    @Nested
    inner class GetCartItems {
        @Test
        fun `getting items from another cart should return forbidden 403`() {
            mockRequest(
                requestType = GET,
                path = "$BASE_PATH/user/${TestUsers.user.id}",
                token = TestTokens.superadmin,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should get items from a users cart`() {
            mockRequest(
                requestType = GET,
                path = "$BASE_PATH/user/${TestUsers.user.id}",
                token = TestTokens.user,
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2))
        }
    }
}
