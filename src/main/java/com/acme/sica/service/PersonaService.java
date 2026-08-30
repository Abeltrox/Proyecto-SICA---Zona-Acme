package com.acme.sica.service;

import com.acme.sica.exception.EntidadNoEncontradaException;
import com.acme.sica.model.*;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.repository.EstadoRepository;
import com.acme.sica.repository.PersonaRepository;

public class PersonaService {

    private final PersonaRepository personaRepository;
    private final EstadoRepository estadoRepository;
    private final AutorizacionService autorizacionService;
    private final AuditoriaRepository auditoriaRepository;

    public PersonaService(PersonaRepository personaRepository, EstadoRepository estadoRepository,
                           AutorizacionService autorizacionService, AuditoriaRepository auditoriaRepository) {
        this.personaRepository = personaRepository;
        this.estadoRepository = estadoRepository;
        this.autorizacionService = autorizacionService;
        this.auditoriaRepository = auditoriaRepository;
    }

    public Persona registrarPersona(Usuario operador, String nombre, String documento, Empresa empresa,
                                     TipoPersona tipo, String urlFoto) {
        autorizacionService.verificarPermiso(operador, "crear_persona");

        EstadoAcceso activo = estadoRepository.buscarEstadoAccesoPorNombre("Activo")
                .orElseThrow(() -> new IllegalStateException("Estado 'Activo' no configurado"));

        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setDocumentoIdentidad(documento);
        persona.setEmpresa(empresa);
        persona.setTipoPersona(tipo);
        persona.setEstadoAcceso(activo);
        persona.setUrlFoto(urlFoto);
        personaRepository.guardar(persona);

        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "CREACION_PERSONA", "personas",
                persona.getId(), "Persona registrada: " + nombre + " (" + documento + ")"));
        return persona;
    }

    /** Regla de negocio: bloquear a alguien exige el permiso explícito 'bloquear_persona'. */
    public void bloquearPersona(Usuario operador, String documento) {
        autorizacionService.verificarPermiso(operador, "bloquear_persona");

        Persona persona = personaRepository.buscarPorDocumento(documento)
                .orElseThrow(() -> new EntidadNoEncontradaException("Persona", documento));

        EstadoAcceso prohibido = estadoRepository.buscarEstadoAccesoPorNombre("Con Prohibicion de Ingreso")
                .orElseThrow(() -> new IllegalStateException("Estado 'Con Prohibicion de Ingreso' no configurado"));

        personaRepository.actualizarEstadoAcceso(persona.getId(), prohibido.getId());
        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "BLOQUEO_PERSONA", "personas",
                persona.getId(), "Persona bloqueada: " + persona.getNombre()));
    }

    public void reactivarPersona(Usuario operador, String documento) {
        autorizacionService.verificarPermiso(operador, "bloquear_persona");

        Persona persona = personaRepository.buscarPorDocumento(documento)
                .orElseThrow(() -> new EntidadNoEncontradaException("Persona", documento));

        EstadoAcceso activo = estadoRepository.buscarEstadoAccesoPorNombre("Activo")
                .orElseThrow(() -> new IllegalStateException("Estado 'Activo' no configurado"));

        personaRepository.actualizarEstadoAcceso(persona.getId(), activo.getId());
        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "REACTIVACION_PERSONA", "personas",
                persona.getId(), "Persona reactivada: " + persona.getNombre()));
    }
}
