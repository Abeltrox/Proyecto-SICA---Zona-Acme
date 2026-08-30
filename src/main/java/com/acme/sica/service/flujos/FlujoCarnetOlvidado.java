package com.acme.sica.service.flujos;

import com.acme.sica.exception.AccesoDenegadoException;
import com.acme.sica.model.*;
import com.acme.sica.observer.GestorNotificaciones;
import com.acme.sica.repository.EstadoRepository;
import com.acme.sica.repository.VisitaRepository;

import java.time.LocalDateTime;

/**
 * Flujo 3 — "Trabajador con carnet olvidado" (pase temporal puntual).
 * Solo aplica a personas de tipo Trabajador. El flujo es igual al del invitado
 * no anunciado (pendiente + notificación al Funcionario de Empresa), pero se
 * mantiene como estrategia separada porque representa una regla de negocio
 * distinta (solo vale para ese día, y solo para trabajadores) — si mañana
 * cambia esa regla, se modifica esta clase sin afectar el flujo de invitados.
 */
public class FlujoCarnetOlvidado implements FlujoAcceso {

    private final VisitaRepository visitaRepository;
    private final EstadoRepository estadoRepository;

    public FlujoCarnetOlvidado(VisitaRepository visitaRepository, EstadoRepository estadoRepository) {
        this.visitaRepository = visitaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public Visita procesarIngreso(Persona persona, Usuario operador, String vehiculoPlaca) {
        if (persona.getTipoPersona() != TipoPersona.Trabajador) {
            throw new AccesoDenegadoException("El flujo de carnet olvidado solo aplica a trabajadores registrados.");
        }

        EstadoVisita pendiente = estadoRepository.buscarEstadoVisitaPorNombre(EstadoVisita.PENDIENTE)
                .orElseThrow(() -> new IllegalStateException("Estado 'Pendiente de Aprobacion' no configurado"));

        Visita visita = new Visita();
        visita.setPersona(persona);
        visita.setEstadoVisita(pendiente);
        visita.setVehiculoPlaca(vehiculoPlaca);
        visita.setFechaEntrada(LocalDateTime.now());
        visitaRepository.guardar(visita);

        GestorNotificaciones.getInstancia().emitirVisitaPendiente(visita);
        return visita;
    }
}
