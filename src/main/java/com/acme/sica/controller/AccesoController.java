package com.acme.sica.controller;

import com.acme.sica.model.Usuario;
import com.acme.sica.model.Visita;
import com.acme.sica.repository.VisitaRepository;
import com.acme.sica.service.AccesoServiceI;
import com.acme.sica.view.ConsolaView;

import java.util.List;

public class AccesoController {

    private final AccesoServiceI accesoService;
    private final VisitaRepository visitaRepository;
    private final ConsolaView view;

    public AccesoController(AccesoServiceI accesoService, VisitaRepository visitaRepository, ConsolaView view) {
        this.accesoService = accesoService;
        this.visitaRepository = visitaRepository;
        this.view = view;
    }

    public void registrarIngreso(Usuario operador) {
        view.mostrar("\n--- Registrar ingreso ---");
        String documento = view.leerTexto("Documento de identidad");
        String placa = view.leerTexto("Placa de vehículo (ENTER si no aplica)");
        try {
            Visita visita = accesoService.registrarIngreso(documento, operador, placa.isEmpty() ? null : placa);
            view.mostrarExito("Procesado: " + visita + " -> estado: " + visita.getEstadoVisita());
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }

    public void registrarSalida(Usuario operador) {
        view.mostrar("\n--- Registrar salida ---");
        int visitaId = view.leerEntero("ID de la visita");
        try {
            Visita visita = accesoService.registrarSalida(visitaId, operador);
            view.mostrarExito("Salida registrada: " + visita);
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }

    public void gestionarAprobaciones(Usuario funcionario) {
        view.mostrar("\n--- Visitas pendientes de aprobación ---");
        int empresaId = funcionario.getRol() != null ? obtenerEmpresaIdDeFuncionario() : 0;
        List<Visita> pendientes = visitaRepository.listarPendientesPorFuncionario(empresaId);

        if (pendientes.isEmpty()) {
            view.mostrar("No hay visitas pendientes en este momento.");
            return;
        }
        pendientes.forEach(v -> view.mostrar(v.getId() + " -> " + v.getPersona() + " (llegó "
                + v.getFechaEntrada() + ")"));

        int visitaId = view.leerEntero("ID de visita a resolver (0 para cancelar)");
        if (visitaId == 0) return;
        String decision = view.leerTexto("¿Aprobar o Rechazar? (A/R)");
        try {
            if (decision.equalsIgnoreCase("A")) {
                Visita v = accesoService.aprobarVisita(visitaId, funcionario);
                view.mostrarExito("Visita aprobada: " + v);
            } else {
                Visita v = accesoService.rechazarVisita(visitaId, funcionario);
                view.mostrarExito("Visita rechazada: " + v);
            }
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }

    // Simplificación académica: se pide el id de empresa manualmente porque el
    // modelo de Usuario no guarda una empresa asociada (solo Persona la tiene).
    private int obtenerEmpresaIdDeFuncionario() {
        return view.leerEntero("Tu ID de empresa (para filtrar pendientes)");
    }
}
