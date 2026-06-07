package com.carrental.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 
 * 功能说明：
 * 1. 生成JWT令牌
 * 2. 解析JWT令牌
 * 3. 验证JWT令牌有效性
 * 4. 从JWT中提取用户信息
 * 
 * JWT令牌结构：
 * Header：{ "alg": "HS256", "typ": "JWT" }
 * Payload：{ "userId": 1, "username": "admin", "role": "admin", "exp": ... }
 * Signature：HMACSHA256签名
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
@Slf4j
@Component
public class JwtUtil {

    /** JWT密钥 */
    @Value("${jwt.secret}")
    private String secret;

    /** JWT过期时间（毫秒） */
    @Value("${jwt.expiration}")
    private Long expiration;

    /** JWT请求头名称 */
    @Value("${jwt.header}")
    private String header;

    /** JWT令牌前缀 */
    @Value("${jwt.prefix}")
    private String prefix;

    /**
     * 生成密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色（user或admin）
     * @return JWT令牌字符串
     */
    public String generateToken(Long userId, String username, String role) {
        // 创建载荷信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        // 设置过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);

        // 生成JWT令牌
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(getSecretKey())
                .compact();

        log.info("生成JWT令牌：用户={}, 角色={}, 过期时间={}", username, role, expirationDate);
        return token;
    }

    /**
     * 解析JWT令牌
     * 
     * @param token JWT令牌
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return jws.getPayload();
        } catch (Exception e) {
            log.error("解析JWT令牌失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从JWT令牌中获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("userId", Long.class);
        }
        return null;
    }

    /**
     * 从JWT令牌中获取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }

    /**
     * 从JWT令牌中获取角色
     */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("role", String.class);
        }
        return null;
    }

    /**
     * 验证JWT令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期：{}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌：{}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT令牌格式错误：{}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT令牌签名错误：{}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT令牌参数错误：{}", e.getMessage());
        }
        return false;
    }

    /**
     * 判断JWT令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        }
        return true;
    }

    /**
     * 刷新JWT令牌
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            return generateToken(userId, username, role);
        }
        return null;
    }

    /**
     * 获取JWT请求头名称
     */
    public String getHeader() {
        return header;
    }

    /**
     * 获取JWT令牌前缀
     */
    public String getPrefix() {
        return prefix;
    }
}
