package com.mattrition.qmart.address

import com.mattrition.qmart.address.dto.AddressDto
import com.mattrition.qmart.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
@RequestMapping("/api/address")
class AddressController(
    private val addressService: AddressService,
) {
    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    @GetMapping("/user/{userId}")
    fun getUserAddresses(
        @PathVariable userId: UUID,
    ): List<AddressDto> = addressService.getUserAddresses(userId)

    @GetMapping("/primary/{userId}")
    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    fun getUserPrimaryAddress(
        @PathVariable userId: UUID,
    ): AddressDto = addressService.getUserPrimaryAddress(userId)

    @PostMapping
    @RolesAllowed(UserRole.USER)
    fun createAddress(
        @RequestBody addressDto: AddressDto,
    ): ResponseEntity<AddressDto> {
        val address = addressService.createAddress(addressDto)

        return ResponseEntity(address, HttpStatus.CREATED)
    }

    @DeleteMapping("/{addressId}")
    @RolesAllowed(UserRole.USER)
    fun deleteAddress(
        @PathVariable addressId: UUID,
    ) {
        addressService.deleteAddress(addressId)
    }
}
