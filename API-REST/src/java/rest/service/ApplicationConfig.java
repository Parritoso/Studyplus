/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Parri
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(rest.service.AmistadFacadeREST.class);
        resources.add(rest.service.ConversacionFacadeREST.class);
        resources.add(rest.service.EntregaFacadeREST.class);
        resources.add(rest.service.ExamenFacadeREST.class);
        resources.add(rest.service.GrupoestudioFacadeREST.class);
        resources.add(rest.service.LogproductividadFacadeREST.class);
        resources.add(rest.service.MensajeFacadeREST.class);
        resources.add(rest.service.ParticipanteconversacionFacadeREST.class);
        resources.add(rest.service.ParticipantegrupoFacadeREST.class);
        resources.add(rest.service.SesionestudioFacadeREST.class);
        resources.add(rest.service.TecnicaestudioFacadeREST.class);
        resources.add(rest.service.UsuarioFacadeREST.class);
    }
    
}
