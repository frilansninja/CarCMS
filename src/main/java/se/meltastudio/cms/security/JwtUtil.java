package se.meltastudio.cms.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import se.meltastudio.cms.model.User;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 min
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 24 timmar


    public String generateAccessToken(User user) {
        System.out.println("Generating access token for " + user.getUsername());

        // 🔹 Konvertera roller till en lista av strängar
        Set<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .collect(Collectors.toSet());


        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", roles) // 🔹 Spara roller i token som en lista
                .claim("companyId", user.getCompany().getId())
                .issuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        System.out.println("generating refresh token for " + user.getUsername());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) secretKey) // ✅ Validerar med SecretKey
                    .build()
                    .parseSignedClaims(token);
            System.out.println("validated token");
            return true;
        } catch (JwtException e) {
            System.out.println("failed to validate token?!");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public Set<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 🔹 Hämta roller som en lista och konvertera till en Set
        List<String> rolesList = claims.get("roles", List.class);
        return new HashSet<>(rolesList);
    }

    public Long extractCompanyId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        System.out.println("extractCompanyId " + claims.get("companyId", Long.class));
        return claims.get("companyId", Long.class); // 🔹 Hämta companyId från token
    }

}
