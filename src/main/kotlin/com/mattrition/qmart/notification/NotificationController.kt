package com.mattrition.qmart.notification

import com.mattrition.qmart.notification.dto.NotificationDto
import com.mattrition.qmart.user.UserRole
import jakarta.annotation.security.RolesAllowed
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {
    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    @GetMapping("/user/{userId}")
    fun getMessagesForUser(
        @PathVariable userId: UUID,
    ): List<NotificationDto> = notificationService.getActiveNotifsForUser(userId)

    @RolesAllowed(UserRole.USER)
    @PatchMapping("/{notificationId}/hide")
    fun hideNotificationFromUser(
        @PathVariable notificationId: UUID,
    ) = notificationService.hideNotification(notificationId)

    @RolesAllowed(UserRole.USER)
    @PatchMapping("/{notificationId}/read")
    fun readNotificationForUser(
        @PathVariable notificationId: UUID,
    ) = notificationService.readNotification(notificationId)

    @RolesAllowed(UserRole.USER)
    @PatchMapping("/user/{userId}/hideAll")
    fun hideAllForUser(
        @PathVariable userId: UUID,
    ) = notificationService.hideAllNotifications(userId)
}
