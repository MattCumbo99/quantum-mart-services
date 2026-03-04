package com.mattrition.qmart

import com.mattrition.qmart.auth.JwtService
import com.mattrition.qmart.config.SecurityConfig
import com.mattrition.qmart.itemlisting.ItemListing
import com.mattrition.qmart.itemlisting.ItemListingRepository
import com.mattrition.qmart.order.OrderStatus
import com.mattrition.qmart.order.dto.OrderDto
import com.mattrition.qmart.orderitem.dto.OrderItemDto
import com.mattrition.qmart.user.User
import com.mattrition.qmart.user.UserRepository
import com.mattrition.qmart.user.UserRole
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Import(SecurityConfig::class)
abstract class BaseH2Test {
    @Autowired protected lateinit var objectMapper: ObjectMapper

    @Autowired protected lateinit var passwordEncoder: PasswordEncoder

    @Autowired protected lateinit var userRepository: UserRepository

    @Autowired protected lateinit var itemListingRepository: ItemListingRepository

    @Autowired protected lateinit var jwtService: JwtService

    @Autowired protected lateinit var mockMvc: MockMvc

    /**
     * A container for referencing preset registered users.
     *
     * @property user Default user.
     * @property moderator
     * @property admin
     * @property superadmin
     */
    protected object TestUsers {
        lateinit var user: User
        lateinit var moderator: User
        lateinit var admin: User
        lateinit var superadmin: User
    }

    /**
     * A container for holding preset Java Web Tokens.
     *
     * @property user Represents a regular user token.
     * @property moderator Client with moderator-level privilege.
     * @property admin Client with admin-level privilege.
     * @property superadmin Client with superadmin-level privilege.
     */
    protected object TestTokens {
        lateinit var user: String
        lateinit var moderator: String
        lateinit var admin: String
        lateinit var superadmin: String
    }

    @BeforeAll
    fun seedUsers() {
        val json = javaClass.getResourceAsStream("/data/users.json")
        val users = objectMapper.readValue(json, Array<TestUserSeed>::class.java)

        userRepository.deleteAll()

        users.forEach { seed ->
            val newUser =
                userRepository.save(
                    User(
                        username = seed.username,
                        passwordHash =
                            passwordEncoder.encode(seed.password)
                                ?: throw RuntimeException(
                                    "Cannot encode password: ${seed.password}",
                                ),
                        email = seed.email,
                        role = seed.role,
                    ),
                )

            val token = jwtService.generateToken(seed.username, newUser.id!!, newUser.role)

            when (seed.role.uppercase()) {
                UserRole.SUPERADMIN -> {
                    TestUsers.superadmin = newUser
                    TestTokens.superadmin = token
                }
                UserRole.ADMIN -> {
                    TestUsers.admin = newUser
                    TestTokens.admin = token
                }
                UserRole.MODERATOR -> {
                    TestUsers.moderator = newUser
                    TestTokens.moderator = token
                }
                else -> {
                    TestUsers.user = newUser
                    TestTokens.user = token
                }
            }
        }
    }

    /**
     * Initializes the item listing repository with two listings:
     * 1. Sold by `moderator` with a price of 100
     * 2. Sold by `admin` with a price of 250
     *
     * @return All item listings.
     */
    protected fun initListings(): List<ItemListing> {
        itemListingRepository.save(
            ItemListing(
                sellerId = TestUsers.moderator.id,
                title = "Test Listing 1",
                description = "Test listing.",
                price = BigDecimal.valueOf(100),
            ),
        )

        itemListingRepository.save(
            ItemListing(
                sellerId = TestUsers.admin.id,
                title = "Test Listing 2",
                description = "Test listing, but admin.",
                price = BigDecimal.valueOf(250),
            ),
        )

        return itemListingRepository.findAll()
    }

    /**
     * Sends a mock HTTP request to a specified rest controller.
     *
     * @param requestType Method type of the controller.
     * @param path URI of the controller.
     * @param token Which test token to use for this call, or `null` if via non-user.
     * @param body Data body in the request for `POST` calls.
     * @see TestTokens
     */
    protected fun mockRequest(
        requestType: HttpMethod,
        path: String,
        token: String? = null,
        body: Any? = null,
    ): ResultActions {
        val builder =
            when (requestType) {
                HttpMethod.GET -> MockMvcRequestBuilders.get(path)
                HttpMethod.POST -> MockMvcRequestBuilders.post(path)
                HttpMethod.PUT -> MockMvcRequestBuilders.put(path)
                HttpMethod.DELETE -> MockMvcRequestBuilders.delete(path)
                HttpMethod.OPTIONS -> MockMvcRequestBuilders.options(path)
                HttpMethod.HEAD -> MockMvcRequestBuilders.head(path)
                HttpMethod.PATCH -> MockMvcRequestBuilders.patch(path)
                else -> throw RuntimeException("Unhandled request: $requestType")
            }

        if (body != null) {
            builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        }

        if (token != null) {
            builder.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }

        return mockMvc.perform(builder)
    }

    protected fun orderWithAddress(
        buyerId: UUID,
        totalPaid: BigDecimal = BigDecimal.ZERO,
        orderItems: List<OrderItemDto> = emptyList(),
    ) = OrderDto(
        buyerId = buyerId,
        status = OrderStatus.PENDING,
        totalPaid = totalPaid,
        shippingFirstname = "Test1",
        shippingLastname = "Test2",
        shippingAddress1 = "1234 Main St",
        shippingCity = "London",
        shippingState = "California",
        shippingZip = "11111",
        shippingPhone = "555-555-5555",
        orderItems = orderItems,
    )
}
