package com.mattrition.qmart.util

import com.mattrition.qmart.auth.CustomUserDetails
import org.springframework.security.core.context.SecurityContextHolder

/** Retrieves authenticated information in a request. */
fun authPrincipal(): CustomUserDetails {
    val auth = SecurityContextHolder.getContext().authentication

    return auth!!.principal as CustomUserDetails
}

/** Checks if the authenticated user has moderator privileges. */
fun authHasMod() = authPrincipal().authorities.any { it.authority == "ROLE_MODERATOR" }
