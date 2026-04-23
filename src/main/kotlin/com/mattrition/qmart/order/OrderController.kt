package com.mattrition.qmart.order

import com.mattrition.qmart.order.dto.CreateOrderRequestDto
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
    @GetMapping("/user/{userId}")
    @RolesAllowed(UserRole.USER)
    fun getBuyerOrders(
        @PathVariable userId: UUID,
    ): List<OrderDto> = orderService.getOrdersBoughtBy(userId)

    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("isAuthenticated() && #sellerId == authentication.principal.id")
    fun getRelevantOrdersToSeller(
        @PathVariable sellerId: UUID,
        @RequestParam(required = true) unfinished: Boolean,
    ): List<OrderDto> = orderService.getOrdersForSeller(sellerId, unfinished)

    @PostMapping
    fun createOrder(
        @RequestBody orderReq: CreateOrderRequestDto,
    ): ResponseEntity<OrderDto> {
        val orderWithItems = orderService.createOrder(orderReq)

        return ResponseEntity(orderWithItems, HttpStatus.CREATED)
    }
}
