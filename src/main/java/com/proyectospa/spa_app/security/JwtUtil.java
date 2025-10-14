package com.proyectospa.spa_app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final String jwtSecretBase64 = "q3JkZWxsaWNhbnRlLXZlcnRlLWxhLXNlY3JldGEtMTIzNDU2Nzg5MGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretBase64));
    private final long jwtExpirationMs = 86400000; // 24h

    public String generateJwtToken(String username, String role, Integer id, String dni, String apellido, String nombre) {
    return Jwts.builder()
            .setSubject(username)        // email
            .claim("role", role)         // rol del usuario
            .claim("id", id)             // id del usuario
            .claim("dni", dni)
            .claim("apellido", apellido)
            .claim("nombre", nombre)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();
}

    // Obtener email (sub) del token
    public String getUserNameFromJwtToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // Obtener rol del token
    public String getRoleFromJwtToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    // Obtener id del token
    public Integer getIdFromJwtToken(String token) {
        return getClaimsFromToken(token).get("id", Integer.class);
    }

    // Obtener DNI del token
    public String getDniFromJwtToken(String token) {
        return getClaimsFromToken(token).get("dni", String.class);
    }

    // Método auxiliar para obtener claims
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validar token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            System.err.println("Firma JWT inválida: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token JWT malformado: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token JWT expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token JWT no soportado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string está vacío: " + e.getMessage());
        }
        return false;
    }
}