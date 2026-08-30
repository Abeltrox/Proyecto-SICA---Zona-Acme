package com.acme.sica.controller;

import com.acme.sica.model.Empresa;
import com.acme.sica.model.Usuario;
import com.acme.sica.service.EmpresaService;
import com.acme.sica.service.IncidenteService;
import com.acme.sica.service.UsuarioService;
import com.acme.sica.view.ConsolaView;

public class AdministracionController {

    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final IncidenteService incidenteService;
    private final ConsolaView view;

    public AdministracionController(UsuarioService usuarioService, EmpresaService empresaService,
                                     IncidenteService incidenteService, ConsolaView view) {
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.incidenteService = incidenteService;
        this.view = view;
    }

    public void crearUsuario(Usuario operador) {
        view.mostrar("\n--- Crear usuario del sistema ---");
        String nombre = view.leerTexto("Nombre");
        String email = view.leerTexto("Email");
        String password = view.leerTexto("Password temporal");
        view.mostrar("Roles: 1=Superusuario 2=Supervisor de Seguridad 3=Guarda de Seguridad 4=Funcionario de Empresa");
        int rolId = view.leerEntero("ID de rol");
        try {
            usuarioService.crearUsuario(operador, nombre, email, password, rolId);
            view.mostrarExito("Usuario creado correctamente.");
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }

    public void registrarEmpresa(Usuario operador) {
        String nombre = view.leerTexto("Nombre de la empresa");
        String contacto = view.leerTexto("Contacto principal");
        try {
            Empresa empresa = empresaService.registrarEmpresa(operador, nombre, contacto);
            view.mostrarExito("Empresa registrada: " + empresa);
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }

    public void reportarIncidente(Usuario operador) {
        Integer visitaId = null;
        String visitaStr = view.leerTexto("ID de visita relacionada (ENTER si no aplica)");
        if (!visitaStr.isEmpty()) visitaId = Integer.parseInt(visitaStr);
        String descripcion = view.leerTexto("Descripción del incidente");
        try {
            incidenteService.reportarIncidente(operador, visitaId, descripcion);
            view.mostrarExito("Incidente registrado.");
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }
}
