package com.acme.sica.controller;

import com.acme.sica.model.Persona;
import com.acme.sica.model.Usuario;
import com.acme.sica.service.ReporteService;
import com.acme.sica.view.ConsolaView;

public class ReporteController {

    private final ReporteService reporteService;
    private final ConsolaView view;

    public ReporteController(ReporteService reporteService, ConsolaView view) {
        this.reporteService = reporteService;
        this.view = view;
    }

    public void mostrarPersonasDentro() {
        view.mostrar("\n--- Personas actualmente DENTRO del complejo ---");
        var personas = reporteService.personasActualmenteDentro();
        if (personas.isEmpty()) {
            view.mostrar("No hay nadie registrado como 'Dentro' en este momento.");
        }
        for (Persona p : personas) {
            view.mostrar(" - " + p);
        }
    }

    public void mostrarConteoPorEstado() {
        view.mostrar("\n--- Conteo de visitas por estado ---");
        reporteService.conteoVisitasPorEstado()
                .forEach((estado, cantidad) -> view.mostrar(estado + ": " + cantidad));
    }

    public void mostrarConteoPorEmpresa() {
        view.mostrar("\n--- Conteo de visitas por empresa ---");
        reporteService.conteoVisitasPorEmpresa()
                .forEach((empresa, cantidad) -> view.mostrar(empresa + ": " + cantidad));
    }

    public void mostrarBitacora(Usuario operador) {
        view.mostrar("\n--- Bitácora de auditoría ---");
        try {
            reporteService.auditoriaCompleta(operador).forEach(r ->
                    view.mostrar("[" + r.getFechaHora() + "] usuario=" + r.getUsuarioId()
                            + " accion=" + r.getAccionRealizada() + " tabla=" + r.getTablaAfectada()
                            + " detalles=" + r.getDetalles()));
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
        }
    }
}
