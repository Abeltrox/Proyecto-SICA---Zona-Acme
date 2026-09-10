package com.acme.sica.controller;

import com.acme.sica.model.Persona;
import com.acme.sica.model.Usuario;
import com.acme.sica.model.Visita;
import com.acme.sica.service.ReporteService;
import com.acme.sica.view.ConsolaView;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Opción de menú exclusiva del Funcionario de Empresa: lista el personal
     * (trabajadores e invitados) de su propia empresa que está actualmente "Dentro" del complejo.
     */
    public void mostrarPersonalPresente(Usuario funcionario) {
        view.mostrar("\n--- Personal Presente en el Complejo ---");
        // Simplificación académica: el modelo de Usuario no guarda una empresa asociada
        // (solo Persona la tiene), por lo que se solicita el ID de empresa del funcionario.
        int empresaId = view.leerEntero("Tu ID de empresa");

        try {
            List<Visita> visitas = reporteService.personalPresenteDeEmpresa(funcionario, empresaId);

            if (visitas.isEmpty()) {
                view.mostrar("No hay personal de tu empresa actualmente dentro del complejo.");
                return;
            }

            String formatoFila = "%-30s %-15s %-12s %-18s";
            view.mostrar(String.format(formatoFila, "Nombre Completo", "Documento", "Tipo", "Fecha de Entrada"));
            view.mostrar("-".repeat(78));
            for (Visita v : visitas) {
                Persona p = v.getPersona();
                String fechaEntrada = v.getFechaEntrada() != null ? v.getFechaEntrada().format(FORMATO_FECHA) : "-";
                view.mostrar(String.format(formatoFila,
                        p.getNombre(),
                        p.getDocumentoIdentidad(),
                        p.getTipoPersona(),
                        fechaEntrada));
            }
        } catch (RuntimeException e) {
            view.mostrarError(e.getMessage());
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
