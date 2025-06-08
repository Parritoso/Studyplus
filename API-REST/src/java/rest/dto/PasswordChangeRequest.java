/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO (Data Transfer Object) para encapsular la solicitud de cambio de contraseña.
 * Contiene la nueva contraseña en texto plano que un usuario desea establecer.
 */
@XmlRootElement // Permite que JAX-RS/JAXB serialice/deserialice este objeto a/desde XML. Si solo usas JSON, no es estrictamente necesario pero no molesta.
public class PasswordChangeRequest implements Serializable {

    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    private String newPassword;

    /**
     * Constructor por defecto requerido por JAX-RS para la deserialización de JSON/XML.
     */
    public PasswordChangeRequest() {
    }

    /**
     * Constructor para crear una instancia de PasswordChangeRequest.
     * @param newPassword La nueva contraseña en texto plano.
     */
    public PasswordChangeRequest(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Obtiene la nueva contraseña.
     * @return La nueva contraseña.
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Establece la nueva contraseña.
     * @param newPassword La nueva contraseña a establecer.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        // Por seguridad, NO incluir la contraseña real en toString en un entorno de producción.
        // Aquí se incluye para propósitos de depuración, pero ten cuidado.
        return "PasswordChangeRequest{" +
               "newPassword='[PROTECTED]'" + // Mejor no loguear la contraseña real
               '}';
    }
}
