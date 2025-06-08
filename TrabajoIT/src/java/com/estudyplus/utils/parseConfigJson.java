/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author Parri
 */
public class parseConfigJson {

    public static Map<String, Object> parseConfigJson(String jsonString, ObjectMapper objectMapper) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new HashMap<>(); // Devuelve un mapa vacío si la cadena es nula o vacía
        }
        try {
            // Usa TypeReference para deserializar a Map<String, Object>
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            // Manejo de errores: si el JSON es inválido, loggea el error y devuelve un mapa vacío
            System.err.println("Error al parsear JSON de configuración de perfil: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
