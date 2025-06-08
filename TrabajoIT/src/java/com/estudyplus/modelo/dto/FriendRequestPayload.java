/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.dto;

import java.io.Serializable;

/**
 *
 * @author Parri
 */
public class FriendRequestPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer remitenteId;
    private Integer receptorId;

    public FriendRequestPayload() {
    }

    public FriendRequestPayload(Integer remitenteId, Integer receptorId) {
        this.remitenteId = remitenteId;
        this.receptorId = receptorId;
    }

    public Integer getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(Integer remitenteId) {
        this.remitenteId = remitenteId;
    }

    public Integer getReceptorId() {
        return receptorId;
    }

    public void setReceptorId(Integer receptorId) {
        this.receptorId = receptorId;
    }
}
