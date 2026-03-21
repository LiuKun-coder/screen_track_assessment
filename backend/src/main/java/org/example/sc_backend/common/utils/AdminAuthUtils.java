package org.example.sc_backend.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.example.sc_backend.common.exception.BusinessException;

public final class AdminAuthUtils {

    private AdminAuthUtils() {
    }

    public static void requireAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role) && !"super_admin".equals(role)) {
            throw new BusinessException(403, "仅管理员可执行该操作");
        }
    }
}
