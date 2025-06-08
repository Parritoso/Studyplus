/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Script para gestionar el modo claro/oscuro de la página,
 * persistencia con cookies y un sistema de día/noche automático.
 */

document.addEventListener('DOMContentLoaded', () => {
    // Referencias a los elementos <link> de los estilos
    const lightThemeLink = document.getElementById('light-theme');
    const darkThemeLink = document.getElementById('dark-theme');

    // Referencias a los botones/checkboxes de la interfaz de usuario
    const toggleThemeBtn = document.getElementById('toggle--daynight'); // Botón para alternar manualmente el tema
    const autoThemeToggle = document.getElementById('autoThemeToggle'); // Checkbox para activar/desactivar el modo día/noche

    // Preferencia del sistema operativo
    const darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)');

    /**
     * Establece una cookie con el nombre, valor y días de expiración especificados.
     * @param {string} name - El nombre de la cookie.
     * @param {string} value - El valor de la cookie.
     * @param {number} days - El número de días hasta que la cookie expire.
     */
    function setCookie(name, value, days) {
        let expires = '';
        if (days) {
            const date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = '; expires=' + date.toUTCString();
        }
        document.cookie = name + '=' + (value || '') + expires + '; path=/';
    }

    /**
     * Obtiene el valor de una cookie por su nombre.
     * @param {string} name - El nombre de la cookie.
     * @returns {string|null} El valor de la cookie, o null si no se encuentra.
     */
    function getCookie(name) {
        const nameEQ = name + '=';
        const ca = document.cookie.split(';');
        for (let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) === ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    /**
     * Elimina una cookie por su nombre.
     * @param {string} name - El nombre de la cookie a eliminar.
     */
    function deleteCookie(name) {
        document.cookie = name + '=; Max-Age=-99999999; path=/';
    }

    /**
     * Aplica el tema claro u oscuro a la página.
     * También sincroniza el estado visual del switch del tema.
     * @param {boolean} isDarkMode - True para modo oscuro, false para modo claro.
     */
    function applyTheme(isDarkMode) {
        if (lightThemeLink && darkThemeLink) {
            lightThemeLink.disabled = isDarkMode;
            darkThemeLink.disabled = !isDarkMode;
            document.body.classList.toggle('dark-mode', isDarkMode); // Añade una clase al body si es necesario para estilos adicionales

            // --- CÓDIGO AÑADIDO / MODIFICADO ---
            // Sincroniza el estado del checkbox del switch de día/noche
            // Si isDarkMode es true (modo oscuro), el checkbox debe estar desmarcado.
            // Si isDarkMode es false (modo claro), el checkbox debe estar marcado.
            if (toggleThemeBtn) { // Asegúrate de que el elemento existe
                toggleThemeBtn.checked = !isDarkMode; 
            }
            // --- FIN CÓDIGO AÑADIDO / MODIFICADO ---

        } else {
            console.error('Error: No se encontraron los elementos <link> de los temas. Asegúrate de que tienen los IDs "light-theme" y "dark-theme".');
        }
    }

    /**
     * Alterna el tema actual entre claro y oscuro.
     * Esta función es llamada por el botón manual.
     */
    function toggleTheme() {
        // Obtenemos el tema actual que está habilitado en los link rel="stylesheet"
        const currentThemeIsDark = darkThemeLink && !darkThemeLink.disabled;
        const newThemeIsDark = !currentThemeIsDark; // Alternamos al tema opuesto

        applyTheme(newThemeIsDark); // Aplica el tema y actualiza el switch
        setCookie('themePreference', newThemeIsDark ? 'dark' : 'light', 365); // Guarda la preferencia manual
        
        // Si el usuario cambia manualmente, desactivamos el modo automático
        if (autoThemeToggle) {
            autoThemeToggle.checked = false;
            setCookie('autoThemeEnabled', 'false', 365);
        }
    }

    /**
     * Determina si es de noche basándose en la hora actual del navegador.
     * Considera "noche" entre las 7 PM (19:00) y las 7 AM (07:00).
     * @returns {boolean} True si es de noche, false si es de día.
     */
    function isNightTime() {
        const now = new Date();
        const hour = now.getHours();
        return hour >= 19 || hour < 7; // De 7 PM a 7 AM
    }

    /**
     * Aplica el tema automáticamente (día/noche) si la opción está activada.
     * No modifica las cookies aquí, solo aplica el tema.
     */
    function applyAutoTheme() {
        applyTheme(isNightTime());
    }

    /**
     * Alterna la activación/desactivación del sistema de día/noche.
     * Esta función es llamada por el checkbox.
     */
    function toggleAutoTheme() {
        const autoEnabled = autoThemeToggle ? autoThemeToggle.checked : false;
        setCookie('autoThemeEnabled', autoEnabled ? 'true' : 'false', 365);

        if (autoEnabled) {
            // Si se activa el modo automático, eliminamos cualquier preferencia manual
            deleteCookie('themePreference');
            applyAutoTheme(); // Aplicar el tema automático inmediatamente
        } else {
            // Si se desactiva el modo automático, re-evaluamos el tema
            // Volvemos a la preferencia del sistema o al tema claro por defecto.
            // Para esto, es mejor llamar a initTheme nuevamente.
            initTheme(); // Re-inicializa el tema con la nueva prioridad
        }
    }

    /**
     * Gestiona el cambio de tema según la preferencia del sistema operativo.
     * Se activa solo si no hay una preferencia manual o automática activa.
     * @param {MediaQueryListEvent} event - El evento de cambio del media query.
     */
    function handleSystemThemeChange(event) {
        const userPreference = getCookie('themePreference');
        const autoEnabled = getCookie('autoThemeEnabled') === 'true';

        // Solo aplicamos la preferencia del sistema si NO hay una preferencia manual
        // y el modo automático NO está activado.
        if (!userPreference && !autoEnabled) {
            applyTheme(event.matches); // event.matches es true si es oscuro, false si es claro
        }
    }

    /**
     * Inicializa el tema al cargar la página.
     * Prioridad: Preferencia del usuario (cookie) > Modo automático (cookie) > Preferencia del sistema > Modo claro por defecto.
     */
    function initTheme() {
        const userPreference = getCookie('themePreference');
        const autoEnabled = getCookie('autoThemeEnabled') === 'true';
        let themeApplied = false;

        if (userPreference) {
            // 1. Si hay una preferencia manual, úsala
            applyTheme(userPreference === 'dark');
            if (autoThemeToggle) {
                autoThemeToggle.checked = false; // Asegura que el checkbox automático esté desmarcado
            }
            themeApplied = true;
        } else if (autoEnabled) {
            // 2. Si el modo automático está activado, úsalo
            applyAutoTheme();
            if (autoThemeToggle) {
                autoThemeToggle.checked = true; // Marca el checkbox automático
            }
            themeApplied = true;
        } else if (darkModeQuery && typeof darkModeQuery.matches !== 'undefined') {
            // 3. Si no hay preferencia manual ni modo automático, usa la preferencia del sistema
            applyTheme(darkModeQuery.matches); // true si el sistema prefiere oscuro, false si prefiere claro
            if (autoThemeToggle) {
                autoThemeToggle.checked = false; // Asegura que el checkbox automático esté desmarcado
            }
            themeApplied = true;
        }

        // 4. Si ninguna de las anteriores aplicó un tema, usa el modo claro por defecto
        if (!themeApplied) {
            applyTheme(false); // Por defecto, modo claro
            if (autoThemeToggle) {
                autoThemeToggle.checked = false; // Asegura que el checkbox automático esté desmarcado
            }
        }

        // Configura los event listeners si los elementos existen
        if (toggleThemeBtn) {
            toggleThemeBtn.addEventListener('click', toggleTheme);
        }
        if (autoThemeToggle) {
            autoThemeToggle.addEventListener('change', toggleAutoTheme);
        }

        // Añade el listener para cambios en la preferencia del sistema
        darkModeQuery.addEventListener('change', handleSystemThemeChange);

        // Si el modo automático está activado, revisa el tema periódicamente
        if (getCookie('autoThemeEnabled') === 'true') { 
            if (window._autoThemeInterval) {
                clearInterval(window._autoThemeInterval);
            }
            window._autoThemeInterval = setInterval(applyAutoTheme, 5 * 60 * 1000); // Revisa cada 5 minutos
        } else {
            if (window._autoThemeInterval) {
                clearInterval(window._autoThemeInterval);
                window._autoThemeInterval = null;
            }
        }
    }

    // Llama a la función de inicialización cuando el DOM esté completamente cargado
    initTheme();
});


