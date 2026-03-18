package com.mattrition.qmart.user

import com.mattrition.qmart.BaseH2Test
import com.mattrition.qmart.user.dto.RegistrationInfo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class UserControllerTest : BaseH2Test() {
    companion object {
        const val BASE_PATH = "/api/users"
    }

    @Nested
    inner class GetUsers {
        @Test
        fun `should return 403 forbidden when no auth`() {
            mockRequest(GET, BASE_PATH).andExpect(status().isForbidden)
        }

        @Test
        fun `should return 403 forbidden if user`() {
            mockRequest(GET, BASE_PATH, TestTokens.user).andExpect(status().isForbidden)
        }

        @Test
        fun `should return 200 ok if moderator`() {
            mockRequest(GET, BASE_PATH, TestTokens.moderator).andExpect(status().isOk)
        }

        @Test
        fun `should return 200 ok if admin`() {
            mockRequest(GET, BASE_PATH, TestTokens.admin).andExpect(status().isOk)
        }

        @Test
        fun `should return 200 ok if superadmin`() {
            mockRequest(GET, BASE_PATH, TestTokens.superadmin).andExpect(status().isOk)
        }
    }

    @Nested
    inner class GetUserByUsername {
        @Test
        fun `should retrieve admin by username`() {
            mockRequest(GET, "$BASE_PATH/username/aDMin")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.username").value("Admin"))
        }

        @Test
        fun `should retrieve user by username`() {
            mockRequest(GET, "$BASE_PATH/username/test_user123")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.username").value("test_user123"))
        }

        @Test
        fun `should return 404 not found`() {
            mockRequest(GET, "$BASE_PATH/username/phantomUser210401").andExpect(status().isNotFound)
        }

        @Test
        fun `should retrieve current user`() {
            mockRequest(requestType = GET, path = "$BASE_PATH/me", token = TestTokens.user)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.username").value(TestUsers.user.username))
        }
    }

    @Nested
    inner class GetUserById {
        @Test
        fun `should retrieve user by id`() {
            mockRequest(GET, "$BASE_PATH/${TestUsers.user.id}")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(TestUsers.user.id.toString()))
        }

        @Test
        fun `should return 404 not found`() {
            mockRequest(GET, "$BASE_PATH/${UUID.randomUUID()}").andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class RegisterUser {
        @Test
        fun `should save user`() {
            val regInfo =
                RegistrationInfo(
                    username = "linus",
                    rawPassword = "qwerty",
                    email = "linus@linux.com",
                )

            mockRequest(POST, BASE_PATH, body = regInfo).andExpect(status().isCreated)
        }

        @Test
        fun `should return 409 conflict`() {
            val regInfo =
                RegistrationInfo(
                    username = TestUsers.user.username,
                    rawPassword = "qwerty",
                    email = "blah@test.com",
                )

            mockRequest(POST, BASE_PATH, body = regInfo).andExpect(status().isConflict)
        }
    }
}
