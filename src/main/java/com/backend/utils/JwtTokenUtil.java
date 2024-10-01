//package com.backend.util;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class JwtTokenUtil {
//
//    private final String SECRET_KEY = "your_secret_key"; // Thay bằng secret key của bạn
//
//    // Tạo token từ id và role của user
//    public String generateToken(Long userId, String role) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", role); // Thêm thông tin role
//        claims.put("userId", userId); // Thêm thông tin userId
//        return createToken(claims);
//    }
//
//    // Phương thức tạo token với các claim
//    private String createToken(Map<String, Object> claims) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token có hiệu lực 10 giờ
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Ký token bằng thuật toán HS256
//                .compact();
//    }
//
//    // Lấy role từ token
//    public String extractRole(String token) {
//        return (String) Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody()
//                .get("role");
//    }
//
//    // Lấy userId từ token
//    public Long extractUserId(String token) {
//        return ((Number) Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody()
//                .get("userId")).longValue();
//    }
//
//    // Kiểm tra token đã hết hạn hay chưa
//    public boolean isTokenExpired(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody()
//                .getExpiration()
//                .before(new Date());
//    }
//}
