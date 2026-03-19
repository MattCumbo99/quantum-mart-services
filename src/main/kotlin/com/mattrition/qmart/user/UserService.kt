package com.mattrition.qmart.user

import com.mattrition.qmart.exception.ConflictException
import com.mattrition.qmart.exception.NotFoundException
import com.mattrition.qmart.user.dto.RegistrationInfo
import com.mattrition.qmart.user.dto.UserDto
import com.mattrition.qmart.user.mapper.UserMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val repo: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    /**
     * Saves a new user object to the database using the provided information.
     *
     * @throws ConflictException If a user exists using the requested username.
     */
    fun createUser(registerInfo: RegistrationInfo): UserDto {
        if (repo.existsByUsernameIgnoreCase(registerInfo.username)) {
            throw ConflictException("User already exists.")
        }

        val userEntity =
            repo.save(
                User(
                    username = registerInfo.username,
                    passwordHash = passwordEncoder.encode(registerInfo.rawPassword)!!,
                    email = registerInfo.email,
                ),
            )

        return UserMapper.toDto(userEntity)
    }

    /**
     * Retrieves a user by their [ID][User.id].
     *
     * @throws NotFoundException If there is no user with the provided ID.
     */
    fun getUserById(id: UUID): UserDto {
        val user =
            repo.findById(id).orElseThrow { NotFoundException("User with id $id not found.") }

        return UserMapper.toDto(user)
    }

    /** Retrieves a list containing every user in the database. */
    fun getAllUsers(): List<UserDto> = repo.findAll().map { UserMapper.toDto(it) }

    /**
     * Retrieves a user that matches against [username]. Casing is ignored.
     *
     * @throws NotFoundException If a user with the username does not exist.
     */
    fun getUserByUsername(username: String): UserDto {
        val user =
            repo.findByUsernameIgnoreCase(username)
                ?: throw NotFoundException("User $username does not exist.")

        return UserMapper.toDto(user)
    }
}
