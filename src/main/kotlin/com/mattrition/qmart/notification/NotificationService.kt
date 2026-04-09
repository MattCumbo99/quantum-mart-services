package com.mattrition.qmart.notification

import com.mattrition.qmart.exception.ForbiddenException
import com.mattrition.qmart.exception.NotFoundException
import com.mattrition.qmart.notification.dto.NotificationDto
import com.mattrition.qmart.notification.mapper.NotificationMapper
import com.mattrition.qmart.util.authPrincipal
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
) {
    /** Retrieves a list of active notifications for a user, sorted newest first. */
    fun getActiveNotifsForUser(userId: UUID): List<NotificationDto> =
        notificationRepository.findByUser(userId).map { NotificationMapper.toDto(it) }

    /**
     * Saves a notification to the database.
     *
     * @param userId ID of the user the notification will be for
     * @param message Notification message
     * @param route URL route the notification will take the user to when they click it
     */
    fun createNotification(
        userId: UUID,
        message: String,
        route: String,
    ) {
        val newEntity = Notification(userId = userId, message = message, route = route)

        notificationRepository.save(newEntity)
    }

    /** Marks a notification as "read" by setting the current date to the "read at" field. */
    fun readNotification(notificationId: UUID) {
        val notif = getNotification(notificationId)

        ensureUserOwnership(notif)

        notif.readAt = OffsetDateTime.now()

        notificationRepository.save(notif)
    }

    /**
     * Gives the current time to the "deleted at" field on a notification, marking it as "inactive."
     */
    fun hideNotification(notificationId: UUID) {
        val notif = getNotification(notificationId)

        ensureUserOwnership(notif)

        notif.deletedAt = OffsetDateTime.now()

        notificationRepository.save(notif)
    }

    /** Hides all active notifications for a user. */
    fun hideAllNotifications(userId: UUID) {
        notificationRepository.findByUser(userId).forEach { notif -> hideNotification(notif.id!!) }
    }

    private fun getNotification(id: UUID) =
        notificationRepository.findById(id).getOrElse {
            throw NotFoundException("Notification $id not found.")
        }

    private fun ensureUserOwnership(notification: Notification) {
        val auth = authPrincipal()

        if (auth.id != notification.userId) {
            throw ForbiddenException("Forbidden.")
        }
    }
}
