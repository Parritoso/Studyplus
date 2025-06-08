/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Parri
 */
public class AuthApiServlet extends HttpServlet {

    private final EstudyRestFacade restFacade = new EstudyRestFacade(); // La Fachada que valida con la DB
    private final JwtUtil jwtUtil = new JwtUtil();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Suponemos que el cuerpo de la petición es un JSON con "username" y "password"
            // Puedes crear un DTO Credenciales para esto si es más limpio
            Map<String, String> credentials = objectMapper.readValue(request.getReader(), HashMap.class);
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Nombre de usuario y contraseña son requeridos.\"}");
                return;
            }

            // TODO: Lógica de autenticación real
            // Por ahora, un placeholder. Debes implementar este método en tu EstudyRestFacade
            // que verificará las credenciales contra la base de datos (Ej: buscar usuario, comparar contraseña hasheada).
            Usuario authenticatedUser = restFacade.authenticateUser(username, password);

            if (authenticatedUser != null) {
                // Generar el token JWT
                // Asumo que tienes un método para obtener roles del usuario. Por simplicidad, un rol fijo.
                List<String> roles = Arrays.asList("student"); // O roles reales del usuario desde la DB
                String jwt = jwtUtil.generateToken(authenticatedUser.getNombre(), roles); // Usar getNombre() como username

                // Devolver el token al cliente
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("token", jwt);
                responseBody.put("message", "Autenticación exitosa.");
                response.addHeader("Authorization", "Bearer " + jwt);// También puedes enviar en la cabecera
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(objectMapper.writeValueAsString(responseBody));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                out.print("{\"message\": \"Credenciales inválidas.\"}");
            }

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON de la solicitud: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error interno al autenticar: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

}
