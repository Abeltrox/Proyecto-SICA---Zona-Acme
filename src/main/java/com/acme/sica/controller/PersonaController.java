package com.acme.sica.controller;

import com.acme.sica.model.*;
import com.acme.sica.repository.EmpresaRepository;
import com.acme.sica.service.PersonaService;
import com.acme.sica.view.ConsolaView;

import java.util.Optional;

public class PersonaController {

    private final PersonaService personaService;
    private final EmpresaRepository empresaRepository;
    private final ConsolaView view;

    public PersonaController(PersonaService personaService, EmpresaRepository empresaRepository, ConsolaView view) {
        this.personaService = personaService;
        this.empresaRepository = empresaRepository;
        this.view = view;
    }

    public void registrarPersona(Usuario operador) {
        view.mostrar("\n--- Registrar persona ---");
        String nombre = view.leerTexto("Nombre completo");
        String documento = view.leerTexto("Documento de identidad");
        String tipoStr = view.leerTexto("Tipo (1=Trabajador, 2=Invitado)");
        TipoPersona tipo = "1".equals(tipoStr) ? TipoPersona.Trabajador : TipoPersona.Invitado;

        Empresa empresa = null;
        if (tipo == TipoPersona.Trabajador) {
            int empresaId = view.leerEntero("ID de la empresa");
            Optional<Empresa> empresaOpt = empresaRepository.buscarPorId(empresaId);
            if (empresaOpt.isEmpty()) {
                view.mostrarError("Empresa no encontrada.");
                return;
            }
            empresa = empresaOpt.get();
        }
        String urlFoto = view.leerTexto("URL de foto (ENTER para omitir)");

        try {
            Persona persona = personaService.registrarPersona(operador, nombre, documento, empresa, tipo,
                    urlFoto.isEmpty() ? null : urlFoto);
            view.mostrarExito("Persona registrada: " + persona);
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }

    public void bloquearPersona(Usuario operador) {
        String documento = view.leerTexto("Documento de la persona a bloquear");
        try {
            personaService.bloquearPersona(operador, documento);
            view.mostrarExito("Persona bloqueada correctamente.");
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }

    public void reactivarPersona(Usuario operador) {
        String documento = view.leerTexto("Documento de la persona a reactivar");
        try {
            personaService.reactivarPersona(operador, documento);
            view.mostrarExito("Persona reactivada correctamente.");
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }
}
