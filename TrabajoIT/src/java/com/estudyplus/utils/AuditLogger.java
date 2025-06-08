/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Parri
 */
public class AuditLogger {
    // Este logger usará la configuración específica en logback.xml para "com.estudyplus.audit"
    private static final Logger auditLogger = LoggerFactory.getLogger("com.estudyplus.audit");

    public static void log(String eventType, String entityType, String entityId, String username, String details) {
        String logMessage = String.format("AUDIT_EVENT: %s | ENTITY_TYPE: %s | ENTITY_ID: %s | USER: %s | DETAILS: %s",
                                           eventType, entityType, entityId, username, details);
        auditLogger.info(logMessage);
    }

    public static void logSecurity(String eventType, String username, String details) {
        String logMessage = String.format("SECURITY_EVENT: %s | USER: %s | DETAILS: %s",
                                           eventType, username, details);
        auditLogger.warn(logMessage); // Nivel WARN para eventos de seguridad críticos
    }
}
