package com.acme.sica.service.flujos;

import com.acme.sica.exception.EntidadNoEncontradaException;
import com.acme.sica.model.*;
import com.acme.sica.repository.EstadoRepository;
import com.acme.sica.repository.VisitaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Flujo 1 — "El invitado pre-registrado" (el flujo ideal descrito en la rúbrica).
 * Ya existe una visita creada de antemano por un Funcionario de Empresa con
 * estado "Aprobado". El guarda solo confirma identidad y hace check-in,
 * pasando la visita a estado "Dentro".
 */
public class FlujoInvitadoPreregistrado implements FlujoAcceso {

    private final VisitaRepository visitaRepository;
    private final EstadoRepository estadoRepository;

    public FlujoInvitadoPreregistrado(VisitaRepository visitaRepository, EstadoRepository estadoRepository) {
        this.visitaRepository = visitaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public Visita procesarIngreso(Persona persona, Usuario operador, String vehiculoPlaca) {
        List<Visita> visitas = visitaRepository.listarPorPersona(persona.getId());
        Optional<Visita> aprobada = visitas.stream()
                .filter(v -> EstadoVisita.APROBADO.equals(v.getEstadoVisita().getNombreEstado()))
                .findFirst();

        Visita visita = aprobada.orElseThrow(() ->
                new EntidadNoEncontradaException("Visita pre-aprobada", "persona " + persona.getDocumentoIdentidad()));

        EstadoVisita dentro = estadoRepository.buscarEstadoVisitaPorNombre(EstadoVisita.DENTRO)
                .orElseThrow(() -> new IllegalStateException("Estado 'Dentro' no configurado en el catálogo"));

        visita.setEstadoVisita(dentro);
        visita.setFechaEntrada(LocalDateTime.now());
        visita.setVehiculoPlaca(vehiculoPlaca);
        visitaRepository.actualizar(visita);
        return visita;
    }
}
