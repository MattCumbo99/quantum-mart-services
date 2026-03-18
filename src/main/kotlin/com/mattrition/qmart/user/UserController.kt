package com.mattrition.qmart.user

import com.mattrition.qmart.auth.CustomUserDetails
import com.mattrition.qmart.user.dto.RegistrationInfo
import com.mattrition.qmart.user.dto.UserDto
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val service: UserService,
) {
    @RolesAllowed(UserRole.MODERATOR)
    @GetMapping
    fun getUsers(): List<UserDto> = service.getAllUsers()

    @GetMapping("/username/{username}")
    fun getUserByUsername(
        @PathVariable username: String,
    ): UserDto = service.getUserByUsername(username)

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: UUID,
    ): UserDto = service.getUserById(id)

    @GetMapping("/me")
    fun getMyself(
        @AuthenticationPrincipal user: CustomUserDetails,
    ): UserDto = service.getUserById(user.id)

    @PostMapping
    fun createUser(
        @RequestBody registerInfo: RegistrationInfo,
    ): ResponseEntity<UserDto> {
        val saved = service.createUser(registerInfo)

        return ResponseEntity(saved, HttpStatus.CREATED)
    }
}
