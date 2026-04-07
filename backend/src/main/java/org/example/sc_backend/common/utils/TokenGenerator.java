package org.example.sc_backend.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 独立的Token生成工具 —— 用于为硬件终端生成长期测试Token
 * 使用方式: 直接运行 main 方法即可在控制台输出 Token
 */
public class TokenGenerator {

    public static void main(String[] args) throws Exception {
        // ======= 配置区域 =======
        String secret = "screen-track-dev-secret-key-2026"; // 必须与 application.yml 中的 jwt.secret 一致
        Long userId = 1001L;                                 // 测试用户ID
        String username = "hardware_test";                   // 测试用户名
        String role = "user";                                // 角色
        // ========================

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        // 有效期设为100年 (100 * 365.25 * 24 * 60 * 60 * 1000 毫秒)
        long hundredYearsMs = (long) (100 * 365.25 * 24 * 60 * 60 * 1000);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + hundredYearsMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        try (PrintWriter pw = new PrintWriter(new FileWriter("generated_token.txt"))) {
            pw.println("FULL_TOKEN:");
            for (int i = 0; i < token.length(); i += 50) {
                int end = Math.min(i + 50, token.length());
                pw.println(token.substring(i, end));
            }
            pw.println("END");
            pw.println("LENGTH: " + token.length());
        }
        System.out.println("Done");
    }
}
