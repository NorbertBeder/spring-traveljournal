package org.example.springtraveljournal.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user");
        }

        if (!(auth.getPrincipal() instanceof Long userId)) {
            throw new IllegalStateException("Invalid authentication principal");
        }

        return userId;
    }
}