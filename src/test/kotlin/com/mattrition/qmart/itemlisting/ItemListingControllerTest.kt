package com.mattrition.qmart.itemlisting

import com.mattrition.qmart.BaseH2Test
import com.mattrition.qmart.itemlisting.dto.ItemListingDto
import com.mattrition.qmart.itemlisting.dto.UpdateListingRequest
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class ItemListingControllerTest : BaseH2Test() {
    companion object {
        const val BASE_PATH = "/api/item-listings"
    }

    private lateinit var listing1: ItemListing
    private lateinit var listing2: ItemListing

    @BeforeEach
    fun initializeListings() {
        val listings = super.initListings()
        listing1 = listings.first()
        listing2 = listings.last()
    }

    @Nested
    inner class CreateItemListing {
        @Test
        fun `item listing created by non-user should return 403 forbidden`() {
            val listing =
                ItemListingDto(
                    id = null,
                    title = "Bad Listing",
                    description = "Bad listing.",
                    price = BigDecimal.valueOf(5000),
                    imageUrl = null,
                    sellerId = UUID.randomUUID(), // How exactly is the guest user setting this?
                    sellerUsername = "UNKNOWN",
                    categorySlug = "uncategorized",
                    categoryName = "Uncategorized",
                )

            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = listing)
                .andExpect(status().isForbidden)
        }

        @Test
        fun `create item listing should be ok`() {
            itemListingRepository.deleteAll()

            val listing =
                ItemListingDto(
                    id = null,
                    title = "Good listing",
                    description = "Good listing.",
                    price = BigDecimal.valueOf(1),
                    imageUrl = null,
                    sellerId = TestUsers.user.id!!,
                    sellerUsername = TestUsers.user.username,
                    categorySlug = "uncategorized",
                    categoryName = "Uncategorized",
                )

            mockRequest(
                requestType = POST,
                path = BASE_PATH,
                token = TestTokens.user,
                body = listing,
            ).andExpect(status().isCreated)

            val userItems = itemListingRepository.findItemListingsBySellerId(TestUsers.user.id!!)
            userItems.size shouldBe 1
        }
    }

    @Nested
    inner class GetItemListing {
        @Test
        fun `should get items by user id`() {
            mockRequest(requestType = GET, path = "$BASE_PATH/seller/${TestUsers.moderator.id}")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].sellerUsername").value(TestUsers.moderator.username))
        }

        @Test
        fun `should return 404 not found on non-existing user`() {
            mockRequest(requestType = GET, path = "$BASE_PATH/seller/${UUID.randomUUID()}")
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should get all item listings`() {
            mockRequest(requestType = GET, path = BASE_PATH)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2))
        }

        @Test
        fun `should get single item listing by id`() {
            mockRequest(requestType = GET, path = "$BASE_PATH/${listing1.id}")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(listing1.id.toString()))
        }

        @Test
        fun `should return 404 not found on unknown listing id`() {
            mockRequest(requestType = GET, path = "$BASE_PATH/${UUID.randomUUID()}")
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class UpdateItemListing {
        @Test
        fun `should update item listing`() {
            val req =
                UpdateListingRequest(
                    title = "new title",
                    description = "new description",
                    price = BigDecimal.valueOf(5000),
                )

            // Pre-assertion
            listing1.title shouldNotBe req.title
            listing1.description shouldNotBe req.description
            listing1.price shouldNotBe req.price

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${listing1.id}",
                token = TestTokens.moderator,
                body = req,
            ).andExpect(status().isOk)

            val listing =
                itemListingRepository.findById(listing1.id!!).getOrNull().shouldNotBeNull()
            listing.title shouldBe req.title
            listing.description shouldBe req.description
            listing.price shouldBe req.price
        }

        @Test
        fun `should return 403 forbidden on unknown user changing listing details`() {
            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${listing1.id}",
                token = null,
                body = UpdateListingRequest(price = BigDecimal(0)),
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return 400 bad request when setting price to below 0`() {
            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${listing1.id}",
                token = TestTokens.moderator,
                body = UpdateListingRequest(price = BigDecimal(-1)),
            ).andExpect(status().isBadRequest)
        }
    }
}
