package com.mattrition.qmart.address

import com.mattrition.qmart.BaseH2Test
import com.mattrition.qmart.address.mapper.AddressMapper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AddressControllerTest : BaseH2Test() {
    companion object {
        private const val BASE_PATH = "/api/address"
    }

    private lateinit var sampleAddress: Address

    @Autowired lateinit var addressRepository: AddressRepository

    @BeforeEach
    fun beforeEach() {
        sampleAddress =
            Address(
                userId = TestUsers.user.id!!,
                firstName = "test",
                lastName = "test",
                city = "test",
                state = "test",
                zip = "12345",
                addressLine1 = "555 Quahog",
            )
    }

    @Nested
    inner class GetAddress {
        @BeforeEach
        fun addAddress() {
            addressRepository.save(sampleAddress)
        }

        @Test
        fun `should return 403 forbidden on retrieving foreign addresses`() {
            mockRequest(
                requestType = HttpMethod.GET,
                path = "$BASE_PATH/user/${TestUsers.user.id}",
                token = null,
            ).andExpect(status().isForbidden)

            mockRequest(
                requestType = HttpMethod.GET,
                path = "$BASE_PATH/user/${TestUsers.user.id!!}",
                token = TestTokens.superadmin,
            ).andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class CreateAddress {
        @Test
        fun `should return 403 forbidden for non-users creating address`() {
            mockRequest(
                requestType = HttpMethod.POST,
                path = BASE_PATH,
                token = null,
                body = AddressMapper.toDto(sampleAddress),
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should create address and set as primary`() {
            sampleAddress.isPrimary shouldBe false

            mockRequest(
                requestType = HttpMethod.POST,
                path = BASE_PATH,
                token = TestTokens.user,
                body = AddressMapper.toDto(sampleAddress),
            ).andExpect(status().isCreated)

            val addresses = addressRepository.findByUserIdSorted(TestUsers.user.id!!)
            addresses shouldHaveSize 1

            val first = addresses.first()
            first.isPrimary shouldBe true
        }

        @Test
        fun `should return 400 bad request when creating another primary address`() {
            addressRepository.save(sampleAddress.copy(isPrimary = true))

            val userAddresses = addressRepository.findByUserIdSorted(TestUsers.user.id!!)
            userAddresses shouldHaveSize 1
            userAddresses.first().isPrimary shouldBe true

            mockRequest(
                requestType = HttpMethod.POST,
                path = BASE_PATH,
                token = TestTokens.user,
                body = AddressMapper.toDto(sampleAddress.copy(isPrimary = true)),
            ).andExpect(status().isBadRequest)
        }
    }
}
