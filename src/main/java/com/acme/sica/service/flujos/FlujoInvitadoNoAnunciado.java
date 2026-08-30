package com.acme.sica.service.flujos;

import com.acme.sica.model.*;
import com.acme.sica.observer.GestorNotificaciones;
import com.acme.sica.repository.EstadoRepository;
import com.acme.sica.repository.VisitaRepository;

import java.time.LocalDateTime;

/**
 * Flujo 2 — "El invitado no anunciado" (flujo en tiempo real).
 * Se crea la visita en estado "Pendiente de Aprobacion" y se emite una
 * notificación (patrón Observer) al Funcionario de Empresa correspondiente
 * para que apruebe o rechace desde su propio menú.
 */
public class FlujoInvitadoNoAnunciado implements FlujoAcceso {

    private final VisitaRepository visitaRepository;
    private final EstadoRepository estadoRepository;

    public FlujoInvitadoNoAnunciado(VisitaRepository visitaRepository, EstadoRepository estadoRepository) {
        this.visitaRepository = visitaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public Visita procesarIngreso(Persona persona, Usuario operador, String vehiculoPlaca) {
        EstadoVisita pendiente = estadoRepository.buscarEstadoVisitaPorNombre(EstadoVisita.PENDIENTE)
                .orElseThrow(() -> new IllegalStateException("Estado 'Pendiente de Aprobacion' no configurado"));

        Visita visita = new Visita();
        visita.setPersona(persona);
        visita.setEstadoVisita(pendiente);
        visita.setVehiculoPlaca(vehiculoPlaca);
        visita.setFechaEntrada(LocalDateTime.now());
        visitaRepository.guardar(visita);

        // Notificación en tiempo real al funcionario (Observer).
        GestorNotificaciones.getInstancia().emitirVisitaPendiente(visita);
        return visita;
    }
}
