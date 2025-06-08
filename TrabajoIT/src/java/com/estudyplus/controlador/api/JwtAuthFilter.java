/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Parri
 */
public class JwtAuthFilter implements Filter {
    
    private JwtUtil jwtUtil;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicializar el JwtUtil una vez
        this.jwtUtil = new JwtUtil();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Obtener el token de la cabecera Authorization
        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7); // Quitar "Bearer "

            // 2. Validar el token
            if (jwtUtil.validateToken(jwt)) {
                // 3. Si el token es válido, extraer la identidad del usuario y sus roles
                Claims claims = jwtUtil.extractAllClaims(jwt);
                String username = claims.getSubject();
                List<String> roles = (List<String>) claims.get("roles"); // Asegúrate de que el tipo sea correcto

                // TODO: Aquí podrías establecer la identidad en algún contexto de seguridad
                // Para Servlets básicos, puedes establecer atributos en la petición
                httpRequest.setAttribute("username", username);
                httpRequest.setAttribute("roles", roles);

                // 4. Continuar la cadena de filtros/Servlets
                chain.doFilter(request, response);
            } else {
                // Token inválido (expirado, firma incorrecta, etc.)
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.getWriter().write("{\"message\": \"Token JWT inválido o expirado.\"}");
            }
        } else {
            // No hay token o no tiene el formato esperado
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write("{\"message\": \"Token de autenticación Bearer requerido.\"}");
        }
    }

    @Override
    public void destroy() {
        // Limpieza si es necesaria
    }
}
