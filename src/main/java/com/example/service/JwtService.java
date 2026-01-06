package com.example.usermanagement.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // 1. Dùng KEY CỐ ĐỊNH (Hardcode hoặc lấy từ properties).
    // Chuỗi này là Base64 của một key 256-bit an toàn. Đừng dùng chuỗi ngắn như "123456".
    private static final String SECRET_KEY = 
        "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 giờ

    // 2. Tạo Key object từ chuỗi cố định trên
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ====== GENERATE TOKEN ======
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // Lưu role vào token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ====== EXTRACT DATA ======
    
    // Trích xuất tất cả Claims (Payload)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Dùng cùng 1 key để giải mã
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Trích xuất Username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Trích xuất Role
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Helper method để lấy 1 claim cụ thể
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ====== VALIDATE TOKEN ======
    public boolean isTokenValid(String token) {
        try {
            // Nếu parse được và không ném lỗi SignatureException thì là hợp lệ
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            // Log lỗi ra console để debug nếu cần
            // System.err.println("Token error: " + e.getMessage());
            return false;
        }
    }
}