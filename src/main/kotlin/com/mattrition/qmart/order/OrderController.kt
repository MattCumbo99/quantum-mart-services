package com.mattrition.qmart.order

import com.mattrition.qmart.order.dto.OrderDto
import com.mattrition.qmart.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @GetMapping("/userId/{userId}")
    @RolesAllowed(UserRole.USER)
    fun getBuyerOrders(
        @PathVariable userId: UUID,
    ): List<OrderDto> = orderService.getOrdersBoughtBy(userId)

    @GetMapping("/username/{username}")
    @RolesAllowed(UserRole.USER)
    fun getBuyerOrdersByUsername(
        @PathVariable username: String,
    ): List<OrderDto> = orderService.getOrdersBoughtBy(username)

    @GetMapping("/sellerId/{sellerId}")
    @PreAuthorize("#sellerId == authentication.principal.id")
    fun getRelevantOrdersToSeller(
        @PathVariable sellerId: UUID,
        @RequestParam(required = true) unfinished: Boolean,
    ): List<OrderDto> = orderService.getOrdersForSeller(sellerId, unfinished)

    @PostMapping
    @PreAuthorize("#orderInfo.buyerId == authentication.principal.id")
    fun createOrder(
        @RequestBody orderInfo: OrderDto,
    ): ResponseEntity<OrderDto> {
        val orderWithItems = orderService.createOrder(orderInfo)

        return ResponseEntity(orderWithItems, HttpStatus.CREATED)
    }
}
