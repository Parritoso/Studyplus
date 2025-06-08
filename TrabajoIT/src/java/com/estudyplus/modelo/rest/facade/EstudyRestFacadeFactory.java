/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.rest.facade;

/**
 *
 * @author Parri
 */
public class EstudyRestFacadeFactory {
    private static EstudyRestFacade instance;
    private static final Object LOCK = new Object();

    // Constructor privado para evitar instanciación externa
    private EstudyRestFacadeFactory() {
        // Inicialización compleja de la fachada si es necesaria aquí
        // Por ejemplo:
        // client = ClientBuilder.newClient();
        // instance = new EstudyRestFacade(client, "http://localhost:8080/api");
    }

    public static EstudyRestFacade getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    // Aquí es donde inicializas tu EstudyRestFacade real.
                    // Necesitarás los parámetros de tu constructor EstudyRestFacade.
                    // Por ejemplo, si toma un cliente HTTP y una URL base:
                    // Cliente HTTP (ej. Jersey client, OkHttp, etc.)
                    // Este es un placeholder, debes reemplazarlo con la inicialización real de tu cliente REST.
                    // Por ejemplo: Client client = ClientBuilder.newClient();
                    // String baseUrl = "http://localhost:8080/estudyapi"; // O la URL de tu API
                    // instance = new EstudyRestFacade(client, baseUrl);
                    
                    // PARA PRUEBAS: Si tu EstudyRestFacade no tiene un constructor complejo,
                    // puedes inicializarlo directamente o pasarle mocks/dummies.
                    // Si tienes un constructor vacío o simple, úsalo:
                    instance = new EstudyRestFacade(/* parámetros si los necesita, ej. URL de la API */);
                    // O si necesita un cliente, inicialízalo aquí:
                    // Client client = ClientBuilder.newClient();
                    // instance = new EstudyRestFacade(client, "http://localhost:8080/api");

                    // Log para verificar la inicialización
                    System.out.println("EstudyRestFacade inicializado.");
                }
            }
        }
        return instance;
    }
}
