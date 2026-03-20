package com.mattrition.qmart.address

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface AddressRepository : JpaRepository<Address, UUID> {
    @Query(
        """
        SELECT a FROM Address a
        WHERE a.userId = :userId
        ORDER BY a.createdAt DESC
    """,
    )
    fun findByUserIdSorted(userId: UUID): List<Address>

    @Query(
        """
            SELECT a FROM Address a
            WHERE a.userId = :userId
                AND a.isPrimary = true
        """,
    )
    fun findPrimaryAddress(userId: UUID): Address?

    fun deleteAddressById(id: UUID)
}
