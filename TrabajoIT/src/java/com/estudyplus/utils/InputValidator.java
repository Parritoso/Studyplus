/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Parri
 */
public class InputValidator {
    // Patrones Regex para validación común
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Patrón para IDs numéricos positivos
    private static final String ID_REGEX = "^[1-9][0-9]*$"; // Números enteros positivos
    private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    // Patrón para contraseñas: Mínimo 8 caracteres, al menos una mayúscula, una minúscula, un número y un carácter especial
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=_*-])(?=\\S+$).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    /**
     * Valida si un String es un ID numérico válido (entero positivo).
     * @param idStr El String a validar.
     * @return true si es un ID válido, false en caso contrario.
     */
    public static boolean isValidIntegerId(String idStr) {
        if (idStr == null || idStr.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = ID_PATTERN.matcher(idStr);
        return matcher.matches();
    }

    /**
     * Valida si una cadena de texto es un email válido.
     * @param email El email a validar.
     * @return true si es un email válido, false en caso contrario.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty() || email.length() > 255) { // Longitud máxima razonable
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Valida si una contraseña cumple con ciertos criterios de seguridad.
     * @param password La contraseña a validar.
     * @return true si la contraseña es válida, false en caso contrario.
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    /**
     * Valida si una cadena de texto contiene solo caracteres alfanuméricos y espacios básicos.
     * Útil para nombres, títulos, etc.
     * Puedes refinar este método según tus necesidades.
     * @param text El texto a validar.
     * @param minLength Longitud mínima permitida.
     * @param maxLength Longitud máxima permitida.
     * @return true si el texto es válido, false en caso contrario.
     */
    public static boolean isValidAlphanumeric(String text, int minLength, int maxLength) {
        if (text == null) {
            return false;
        }
        String trimmedText = text.trim();
        if (trimmedText.isEmpty() || trimmedText.length() < minLength || trimmedText.length() > maxLength) {
            return false;
        }
        // Permite letras, números, espacios, y algunos caracteres comunes como tildes, ñ, guiones.
        // ADVERTENCIA: Esta regex es un ejemplo. Adapta cuidadosamente según tus necesidades de internacionalización y caracteres permitidos.
        return trimmedText.matches("^[\\p{L}0-9\\s.,'-]+$"); // \p{L} para cualquier caracter de letra unicode
    }

    /**
     * Limpia un string de posibles ataques XSS básicos (aunque la prevención principal es escapar al mostrar).
     * Esto no reemplaza la validación fuerte ni el escape en el frontend.
     * @param value El string a limpiar.
     * @return El string limpiado.
     */
    public static String sanitizeHtml(String value) {
        if (value == null) {
            return null;
        }
        // Implementa una librería de sanitización de HTML más robusta como OWASP ESAPI o Jsoup
        // Para un ejemplo simple:
        return value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        // ESTO ES BÁSICO. USAR LIBRERÍAS DEDICADAS PARA PRODUCCIÓN.
    }
}
