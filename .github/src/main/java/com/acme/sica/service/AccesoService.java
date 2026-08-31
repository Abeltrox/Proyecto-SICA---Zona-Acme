package com.acme.sica.service;

import com.acme.sica.exception.AccesoDenegadoException;
import com.acme.sica.exception.EntidadNoEncontradaException;
import com.acme.sica.model.*;
import com.acme.sica.repository.EstadoRepository;
import com.acme.sica.repository.PersonaRepository;
import com.acme.sica.repository.VisitaRepository;
import com.acme.sica.service.flujos.FlujoAcceso;
import com.acme.sica.service.flujos.FlujoCarnetOlvidado;
import com.acme.sica.service.flujos.FlujoInvitadoNoAnunciado;
import com.acme.sica.service.flujos.FlujoInvitadoPreregistrado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio central de control de acceso. Contiene la lógica de negocio pura
 * (independiente de JDBC/consola): valida RBAC, valida reglas de acceso,
 * detecta y regulariza salidas olvidadas, y delega el resto en la estrategia
 * (Strategy) correspondiente al escenario.
 *
 * NO escribe directamente en bitacora_auditoria — esa responsabilidad se
 * añade por fuera con el decorador AccesoServiceAuditoriaDecorator (Decorator),
 * cumpliendo SOLID - SRP: este servicio decide QUÉ pasa con el acceso,
 * el decorador decide CÓMO se audita.
 */
public class AccesoService implements AccesoServiceI {

    private final PersonaRepository personaRepository;
    private final VisitaRepository visitaRepository;
    private final EstadoRepository estadoRepository;
    private final AutorizacionService autorizacionService;

    public AccesoService(PersonaRepository personaRepository, VisitaRepository visitaRepository,
                          EstadoRepository estadoRepository, AutorizacionService autorizacionService) {
        this.personaRepository = personaRepository;
        this.visitaRepository = visitaRepository;
        this.estadoRepository = estadoRepository;
        this.autorizacionService = autorizacionService;
    }

    @Override
    public Visita registrarIngreso(String documentoIdentidad, Usuario operador, String vehiculoPlaca) {
        autorizacionService.verificarPermiso(operador, "registrar_visita");

        Persona persona = personaRepository.buscarPorDocumento(documentoIdentidad)
                .orElseThrow(() -> new EntidadNoEncontradaException("Persona", documentoIdentidad));

        // Regla de negocio central: nadie con prohibición de ingreso entra, sin excepciones.
        if (!persona.puedeIngresar()) {
            throw new AccesoDenegadoException("Acceso denegado: " + persona.getNombre()
                    + " tiene una prohibición de ingreso activa.");
        }

        // Flujo 4 — Regularización de salida olvidada: se detecta ANTES de decidir
        // qué estrategia de ingreso aplicar, y nunca bloquea el ingreso actual.
        regularizarSalidaOlvidadaSiAplica(persona);

        FlujoAcceso flujo = seleccionarFlujo(persona);
        return flujo.procesarIngreso(persona, operador, vehiculoPlaca);
    }

    /** Selecciona la estrategia (Strategy) correcta según el estado y tipo de la persona. */
    private FlujoAcceso seleccionarFlujo(Persona persona) {
        List<Visita> visitas = visitaRepository.listarPorPersona(persona.getId());
        boolean tieneVisitaPreaprobada = visitas.stream()
                .anyMatch(v -> EstadoVisita.APROBADO.equals(v.getEstadoVisita().getNombreEstado()));

        if (tieneVisitaPreaprobada) {
            return new FlujoInvitadoPreregistrado(visitaRepository, estadoRepository);
        }
        if (persona.getTipoPersona() == TipoPersona.Trabajador) {
            return new FlujoCarnetOlvidado(visitaRepository, estadoRepository);
        }
        return new FlujoInvitadoNoAnunciado(visitaRepository, estadoRepository);
    }

    private void regularizarSalidaOlvidadaSiAplica(Persona persona) {
        Optional<Visita> abierta = visitaRepository.buscarVisitaAbiertaDePersona(persona.getId());
        if (abierta.isPresent()) {
            Visita visitaAnterior = abierta.get();
            EstadoVisita cerradaPorSistema = estadoRepository
                    .buscarEstadoVisitaPorNombre(EstadoVisita.CERRADA_POR_SISTEMA)
                    .orElseThrow(() -> new IllegalStateException("Estado 'Cerrada por Sistema' no configurado"));
            visitaAnterior.setEstadoVisita(cerradaPorSistema);
            visitaAnterior.setFechaSalida(LocalDateTime.now());
            visitaRepository.actualizar(visitaAnterior);
        }
    }

    @Override
    public Visita registrarSalida(int visitaId, Usuario operador) {
        autorizacionService.verificarPermiso(operador, "registrar_salida");

        Visita visita = visitaRepository.buscarPorId(visitaId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Visita", String.valueOf(visitaId)));

        EstadoVisita fuera = estadoRepository.buscarEstadoVisitaPorNombre(EstadoVisita.FUERA)
                .orElseThrow(() -> new IllegalStateException("Estado 'Fuera' no configurado"));

        visita.setEstadoVisita(fuera);
        visita.setFechaSalida(LocalDateTime.now());
        visitaRepository.actualizar(visita);
        return visita;
    }

    @Override
    public Visita aprobarVisita(int visitaId, Usuario funcionario) {
        autorizacionService.verificarPermiso(funcionario, "aprobar_visita");

        Visita visita = visitaRepository.buscarPorId(visitaId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Visita", String.valueOf(visitaId)));

        EstadoVisita dentro = estadoRepository.buscarEstadoVisitaPorNombre(EstadoVisita.DENTRO)
                .orElseThrow(() -> new IllegalStateException("Estado 'Dentro' no configurado"));

        visita.setEstadoVisita(dentro);
        visita.setVisitaAprobadaPor(funcionario);
        visitaRepository.actualizar(visita);
        return visita;
    }

    @Override
    public Visita rechazarVisita(int visitaId, Usuario funcionario) {
        autorizacionService.verificarPermiso(funcionario, "aprobar_visita");

        Visita visita = visitaRepository.buscarPorId(visitaId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Visita", String.valueOf(visitaId)));

        EstadoVisita rechazado = estadoRepository.buscarEstadoVisitaPorNombre(EstadoVisita.RECHAZADO)
                .orElseThrow(() -> new IllegalStateException("Estado 'Rechazado' no configurado"));

        visita.setEstadoVisita(rechazado);
        visita.setVisitaAprobadaPor(funcionario);
        visitaRepository.actualizar(visita);
        return visita;
    }
}
