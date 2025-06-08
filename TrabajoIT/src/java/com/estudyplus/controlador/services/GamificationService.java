/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services;

import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Propósito: Gestionar el sistema de puntos, medallas y rankings.
 * @see com.estudyplus.modelo.rest.facade.EstudyRestFacade
 * @see com.estudyplus.controlador.services.ProductivityService
 * @author Parri
 */
public class GamificationService {
    private static final Logger logger = LoggerFactory.getLogger(FriendshipService.class);
    private final EstudyRestFacade facade;

    public GamificationService() {
        facade = EstudyRestFacadeFactory.getInstance();
    }
    
    /**
     *  Concede puntos al usuario.
     * @param userId
     * @param points
     * @param reason 
     */
    public void awardPoints(Usuario user, int points, String reason){
        try{
            user.addPuntos(points);
            facade.updatePuntos(user, points);
        }catch(Exception e){
            System.err.println("Mensaje error"+e);
        }
    }
    
    public List<Usuario> getRankingFriends(int idUsuario) throws IOException {
        List<Usuario> us = new ArrayList<>();
        us=facade.getFriends(idUsuario);
        us.add(facade.getUsuarioById(idUsuario));
        if (us != null && !us.isEmpty()) {
            Collections.sort(us, new Comparator<Usuario>() {
                @Override
                public int compare(Usuario u1, Usuario u2) {
                    // Orden descendente por puntos
                    return Integer.compare(u2.getPuntos(), u1.getPuntos());
                }
            });
        }
        return us;
    }
    
    public List<Usuario> getRanking() throws IOException {
        return facade.getRanking();
    }
    
    /**
     * Concede una medalla por un logro.
     * @param userId
     * @param badgeName 
     */
    public static void awardBadge(int userId, String badgeName){
        
    }
    
    /**
     * Obtiene la posición del usuario en el ranking.
     * @param userId 
     */
    public static void getUserRank(int userId){
        
    }
    
    /**
     * Obtiene el ranking global o de amigos.
     */
    public static void getLeaderboard(){
        
    }
}
