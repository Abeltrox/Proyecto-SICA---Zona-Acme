package com.acme.sica.service;

import com.acme.sica.exception.EntidadNoEncontradaException;
import com.acme.sica.model.*;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.repository.EstadoRepository;
import com.acme.sica.repository.PersonaRepository;
import com.acme.sica.repository.VisitaRepository;
import com.acme.sica.util.Validaciones;

public class PersonaService {

    private final PersonaRepository personaRepository;
    private final EstadoRepository estadoRepository;
    private final VisitaRepository visitaRepository;
    private final AutorizacionService autorizacionService;
    private final AuditoriaRepository auditoriaRepository;

    public PersonaService(PersonaRepository personaRepository, EstadoRepository estadoRepository,
                           VisitaRepository visitaRepository, AutorizacionService autorizacionService,
                           AuditoriaRepository auditoriaRepository) {
        this.personaRepository = personaRepository;
        this.estadoRepository = estadoRepository;
        this.visitaRepository = visitaRepository;
        this.autorizacionService = autorizacionService;
        this.auditoriaRepository = auditoriaRepository;
    }

    public Persona registrarPersona(Usuario operador, String nombre, String documento, Empresa empresa,
                                     TipoPersona tipo, String urlFoto) {
        autorizacionService.verificarPermiso(operador, "crear_persona");
        Validaciones.validarCedula(documento);

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

    /**
     * Edita nombre y correo de una persona ya registrada (trabajador o invitado).
     * No toca empresa, tipo ni foto — para eso ya existe el flujo de registro.
     * Exige el permiso 'editar_persona' (ya definido en rol_permisos para
     * Superusuario, Supervisor de Seguridad y Funcionario de Empresa).
     */
    public Persona editarPersona(Usuario operador, String documento, String nuevoNombre, String nuevoCorreo) {
        autorizacionService.verificarPermiso(operador, "editar_persona");

        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        String correoValidado = Validaciones.normalizarYValidarCorreo(nuevoCorreo);

        Persona persona = personaRepository.buscarPorDocumento(documento)
                .orElseThrow(() -> new EntidadNoEncontradaException("Persona", documento));

        persona.setNombre(nuevoNombre.trim());
        persona.setCorreo(correoValidado);
        personaRepository.actualizar(persona);

        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "EDICION_PERSONA", "personas",
                persona.getId(), "Datos actualizados: " + persona.getNombre()
                + (correoValidado != null ? " (" + correoValidado + ")" : "")));
        return persona;
    }

    /**
     * Elimina definitivamente a una persona (trabajador o invitado) del sistema — para el caso de un
     * trabajador despedido, por ejemplo. Solo se permite si no tiene visitas registradas en su historial:
     * si ya ingresó/salió alguna vez, se bloquea el borrado (perdería trazabilidad de auditoría) y se
     * recomienda usar {@link #bloquearPersona} en su lugar, que le revoca el acceso sin borrar el historial.
     */
    public void eliminarPersona(Usuario operador, String documento) {
        autorizacionService.verificarPermiso(operador, "editar_persona");

        Persona persona = personaRepository.buscarPorDocumento(documento)
                .orElseThrow(() -> new EntidadNoEncontradaException("Persona", documento));

        if (!visitaRepository.listarPorPersona(persona.getId()).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar a " + persona.getNombre()
                    + ": ya tiene visitas registradas en su historial. Usa \"Bloquear persona\" para revocarle"
                    + " el acceso sin perder ese historial.");
        }

        personaRepository.eliminar(persona.getId());
        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "ELIMINACION_PERSONA", "personas",
                persona.getId(), "Persona eliminada: " + persona.getNombre() + " (" + documento + ")"));
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
