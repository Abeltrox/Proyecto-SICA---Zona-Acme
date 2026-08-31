package com.acme.sica.controller;

import com.acme.sica.exception.AccesoDenegadoException;
import com.acme.sica.model.Usuario;
import com.acme.sica.service.AutenticacionService;
import com.acme.sica.view.ConsolaView;

public class AutenticacionController {

    private final AutenticacionService autenticacionService;
    private final ConsolaView view;

    public AutenticacionController(AutenticacionService autenticacionService, ConsolaView view) {
        this.autenticacionService = autenticacionService;
        this.view = view;
    }

    public Usuario iniciarSesion() {
        view.mostrar("\n=== INICIO DE SESIÓN - SICA ===");
        String email = view.leerTexto("Email");
        String password = view.leerTexto("Password");
        try {
            Usuario usuario = autenticacionService.login(email, password);
            view.mostrarExito("Bienvenido, " + usuario);
            return usuario;
        } catch (AccesoDenegadoException e) {
            view.mostrarError(e.getMessage());
            return null;
        }
    }
}
