package com.mattrition.qmart.util

import com.mattrition.qmart.auth.CustomUserDetails
import org.springframework.security.core.context.SecurityContextHolder

/** Retrieves authenticated information in a request, or null if non-user. */
fun authPrincipal(): CustomUserDetails? {
    val auth = SecurityContextHolder.getContext().authentication
    val principal = auth?.principal

    return principal as? CustomUserDetails
}
