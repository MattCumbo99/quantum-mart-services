package com.mattrition.qmart.address

import com.mattrition.qmart.address.dto.AddressDto
import com.mattrition.qmart.address.mapper.AddressMapper
import com.mattrition.qmart.auth.CustomUserDetails
import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class AddressService(
    private val addressRepository: AddressRepository,
) {
    /** Retrieves all saved addresses associated with a user. */
    fun getUserAddresses(userId: UUID): List<AddressDto> = addressRepository.findByUserIdSorted(userId).map { AddressMapper.toDto(it) }

    /**
     * Retrieves a user's primary address.
     *
     * @throws NotFoundException If the user doesn't have any saved addresses.
     */
    fun getUserPrimaryAddress(userId: UUID): AddressDto {
        val address =
            addressRepository.findPrimaryAddress(userId)
                ?: throw NotFoundException("No primary address found.")

        return AddressMapper.toDto(address)
    }

    /** Saves a new address to the database. */
    fun createAddress(addressDto: AddressDto): AddressDto {
        val needsPrimary = addressRepository.findPrimaryAddress(addressDto.userId) == null

        val entity = AddressMapper.asNewEntity(addressDto.copy(isPrimary = needsPrimary))
        val saved = addressRepository.save(entity)

        return AddressMapper.toDto(saved)
    }

    /**
     * Deletes an address from the repository.
     *
     * @throws NotFoundException If no such address exists.
     */
    @Transactional
    fun deleteAddress(addressId: UUID) {
        val address =
            addressRepository.findById(addressId).getOrElse {
                throw NotFoundException("No address found with id $addressId")
            }

        ensureUserOwnsAddress(address)

        addressRepository.deleteAddressById(addressId)

        adjustPrimaryAddress(address.userId)
    }

    /** Marks the earliest address for a user the primary address. */
    private fun adjustPrimaryAddress(userId: UUID) {
        val addresses =
            addressRepository.findByUserIdSorted(userId).ifEmpty {
                return
            }

        // Ignore operation if a primary address exists
        if (addresses.any { it.isPrimary }) return

        val newPrimary = addresses.first()
        newPrimary.isPrimary = true

        addressRepository.save(newPrimary)
    }

    private fun ensureUserOwnsAddress(address: Address) {
        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth?.principal as CustomUserDetails

        if (address.userId != principal.id) {
            throw ForbiddenException("User not authorized.")
        }
    }
}
