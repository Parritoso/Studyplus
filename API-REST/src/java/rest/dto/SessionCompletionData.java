/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.dto;

/**
 * DTO para encapsular los datos de finalización de una sesión de estudio.
 * Se utiliza para enviar información al endpoint PUT /rest.sesionestudio/{id}/end.
 * @author Parri
 */
public class SessionCompletionData {
    private static final long serialVersionUID = 1L;

    private String notes;           // Notas finales de la sesión
    private Integer focusRating;    // Calificación del enfoque (ej. 1-5)
    private String interruptionDetails; // Detalles sobre interrupciones
    private String outcome;         // Resultado de la sesión (ej. "completed" o "aborted")

    public SessionCompletionData() {
    }

    public SessionCompletionData(String notes, Integer focusRating, String interruptionDetails, String outcome) {
        this.notes = notes;
        this.focusRating = focusRating;
        this.interruptionDetails = interruptionDetails;
        this.outcome = outcome;
    }

    // --- Getters y Setters ---
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getFocusRating() {
        return focusRating;
    }

    public void setFocusRating(Integer focusRating) {
        this.focusRating = focusRating;
    }

    public String getInterruptionDetails() {
        return interruptionDetails;
    }

    public void setInterruptionDetails(String interruptionDetails) {
        this.interruptionDetails = interruptionDetails;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @Override
    public String toString() {
        return "SessionCompletionData{" +
               "notes='" + notes + '\'' +
               ", focusRating=" + focusRating +
               ", interruptionDetails='" + interruptionDetails + '\'' +
               ", outcome='" + outcome + '\'' +
               '}';
    }
}
