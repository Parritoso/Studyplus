/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Comparator; // Para ordenar mensajes
import rest.Conversacion;
import rest.Mensaje;
import rest.Participanteconversacion;
/**
 *
 * @author Parri
 */
public class ConversacionDTO implements Serializable {
    private Integer id;
    private Date fechaCreacion;
    private Collection<ParticipanteconversacionDTO> participantes;
    private Date ultimoMensajeFecha;
    private int unreadMessagesCount;

    public ConversacionDTO() {
        // Constructor vacío para serialización si es necesario
    }

    public ConversacionDTO(Conversacion conv, Integer currentUserId) {
        this.id = conv.getId();
        this.fechaCreacion = conv.getFechaCreacion();

        // 1. Mapear participantes a DTOs de participantes
        if (conv.getParticipanteconversacionCollection() != null) {
            this.participantes = new ArrayList<>();
            for (Participanteconversacion pc : conv.getParticipanteconversacionCollection()) {
                this.participantes.add(new ParticipanteconversacionDTO(pc));
            }
        } else {
            this.participantes = new ArrayList<>();
        }

        // 2. Calcular ultimoMensajeFecha y unreadMessagesCount
        long unreadCount = 0;
        Date latestMessageDate = null;

        if (conv.getMensajeCollection() != null) {
            // Iterar sobre los mensajes para calcular el conteo de no leídos y la última fecha
            for (Mensaje m : conv.getMensajeCollection()) {
                // Un mensaje se considera no leído para el 'currentUserId' si no fue enviado por él
                // Y si el mensaje no está marcado como leído (esto es una simplificación,
                // un sistema real tendría un estado de lectura por participante).
                if (m.getEmisorId() != null && !m.getEmisorId().getId().equals(currentUserId) && !m.getLeido()) {
                    unreadCount++;
                }

                if (latestMessageDate == null || m.getFechaEnvio().after(latestMessageDate)) {
                    latestMessageDate = m.getFechaEnvio();
                }
            }
        }
        this.unreadMessagesCount = (int) unreadCount;
        this.ultimoMensajeFecha = latestMessageDate;
    }

    // Getters
    public Integer getId() { return id; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public Collection<ParticipanteconversacionDTO> getParticipantes() { return participantes; }
    public Date getUltimoMensajeFecha() { return ultimoMensajeFecha; }
    public int getUnreadMessagesCount() { return unreadMessagesCount; }

    // Setters (opcional, para serialización/deserialización si fuera necesario)
    public void setId(Integer id) { this.id = id; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setParticipantes(Collection<ParticipanteconversacionDTO> participantes) { this.participantes = participantes; }
    public void setUltimoMensajeFecha(Date ultimoMensajeFecha) { this.ultimoMensajeFecha = ultimoMensajeFecha; }
    public void setUnreadMessagesCount(int unreadMessagesCount) { this.unreadMessagesCount = unreadMessagesCount; }
}
