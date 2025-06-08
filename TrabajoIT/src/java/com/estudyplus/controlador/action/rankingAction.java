/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.GamificationService;
import com.estudyplus.modelo.entitys.Usuario;
import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel
 */
public class rankingAction extends ActionSupport implements SessionAware{
    
    private static final Logger logger = LoggerFactory.getLogger(rankingAction.class);
    private List<Usuario> us;
    private Map<String, Object> sessionMap;
    private int idUsuario;
    private String opcion;
    private Map<String, String> modos;
    
    private Usuario usuarioLogeado;
    private int puntosUsuarioLogeado;
    private int posicionUsuarioLogeado;
    
    public rankingAction() {
        this.opcion="global";
    }
    
    public String execute() throws Exception {
//        try{
//            
//            GamificationService gm = new GamificationService();
//            idUsuario = ((Usuario)sessionMap.get("loggedInUser")).getId();
//            
//            if(opcion.equals("global")){
//                us = gm.getRanking();
//            }else if(opcion.equals("friends")){
//                logger.debug("/////////////////////////[rankingAction] Estoy en FRIENDS///////////////////////////////");
//                us = gm.getRankingFriends(idUsuario);
//            }else{
//                us = gm.getRanking();
//            }
//            logger.debug("LISTA: "+Arrays.toString(us.toArray()));
//            return SUCCESS;
//        }catch(Exception e){
//            logger.debug("[rankingAction] Error con "+e.getMessage());
//            e.printStackTrace();
//            return ERROR;
//        }
        try {
            GamificationService gm = new GamificationService();
            usuarioLogeado = (Usuario) sessionMap.get("loggedInUser");
            
            if (usuarioLogeado != null) {
                idUsuario = usuarioLogeado.getId();
                puntosUsuarioLogeado = usuarioLogeado.getPuntos(); // Asumiendo que Usuario tiene un método getPuntos()
            } else {
                // Manejar el caso donde no hay usuario logeado, tal vez redirigir a login
                return LOGIN; // Define LOGIN en tu struts.xml si no está ya
            }

            if (opcion.equals("global")) {
                us = gm.getRanking();
            } else if (opcion.equals("friends")) {
                logger.debug("/////////////////////////[rankingAction] Estoy en FRIENDS///////////////////////////////");
                us = gm.getRankingFriends(idUsuario);
            } else {
                us = gm.getRanking(); // Default a global si la opción no es reconocida
            }

            // Calcular la posición del usuario logeado en la lista actual
            if (us != null && !us.isEmpty()) {
                for (int i = 0; i < us.size(); i++) {
                    if (us.get(i).getId() == idUsuario) { // Asumiendo que Usuario tiene un método getId()
                        posicionUsuarioLogeado = i + 1;
                        break;
                    }
                }
            } else {
                posicionUsuarioLogeado = 0; // O un valor que indique que no está en el ranking
            }
            
            logger.debug("LISTA: " + Arrays.toString(us.toArray()));
            return SUCCESS;
        } catch (Exception e) {
            logger.debug("[rankingAction] Error con " + e.getMessage());
            e.printStackTrace();
            return ERROR;
        }
    }

    public List<Usuario> getUs() {
        return us;
    }

    public void setUs(List<Usuario> us) {
        this.us = us;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }
    
    @Override
    public void setSession(Map<String, Object> map) {
        this.sessionMap = map; //To change body of generated methods, choose Tools | Templates.
    }

    public Map<String, String> getModos() {
        modos = new LinkedHashMap<>();
        modos.put("global", "global");
        modos.put("friends", "friends");
        return modos;
    }
    
    public Usuario getUsuarioLogeado() {
        return usuarioLogeado;
    }

    public int getPuntosUsuarioLogeado() {
        return puntosUsuarioLogeado;
    }

    public int getPosicionUsuarioLogeado() {
        return posicionUsuarioLogeado;
    }
}
