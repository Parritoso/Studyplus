/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author Parri
 */
public class JwtUtil {
    // CONSIDERACIÓN DE SEGURIDAD CRÍTICA:
    // Esta clave debe ser muy robusta y NO estar hardcodeada.
    // Lo ideal es leerla de variables de entorno o un servicio de configuración/secretos.
    // Para el desarrollo inicial, la pondremos aquí, pero ¡CÁMBIALA EN PRODUCCIÓN!
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Genera una clave segura HMAC-SHA-256
    private static final long EXPIRATION_TIME_MS = TimeUnit.HOURS.toMillis(1); // Token válido por 1 hora
    
    /**
     * Genera un JWT para un usuario.
     * @param username El nombre de usuario.
     * @param roles Los roles del usuario (ej. "admin", "student").
     * @return El JWT generado.
     */
    public String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles); // Añadir roles al token

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida un JWT.
     * @param token El JWT a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    /**
     * Extrae los claims del JWT.
     * @param token El JWT.
     * @return Los claims (cuerpo) del token.
     */
    public Claims extractAllClaims(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
        return claimsJws.getBody();
    }

    /**
     * Extrae el nombre de usuario del JWT.
     * @param token El JWT.
     * @return El nombre de usuario.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae los roles del JWT.
     * @param token El JWT.
     * @return Una lista de roles.
     */
    public List<String> extractRoles(String token) {
        // Los roles se almacenan como una lista en el claim "roles"
        // JJWT los recupera como un ArrayList por defecto
        return (List<String>) extractAllClaims(token).get("roles");
    }
}
