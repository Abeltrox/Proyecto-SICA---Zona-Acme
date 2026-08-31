package com.acme.sica.service;

import com.acme.sica.model.*;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.repository.VisitaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio de reportes. Es donde más sentido tiene aplicar lambdas y la
 * Stream API (requerido por la rúbrica): en vez de recorrer listas con
 * bucles for tradicionales y variables acumuladoras, se usan pipelines
 * declarativos de filter/map/collect.
 */
public class ReporteService {

    private final VisitaRepository visitaRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final AutorizacionService autorizacionService;

    public ReporteService(VisitaRepository visitaRepository, AuditoriaRepository auditoriaRepository,
                           AutorizacionService autorizacionService) {
        this.visitaRepository = visitaRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.autorizacionService = autorizacionService;
    }

    /** Todas las personas que en este momento están "Dentro" del complejo (para un conteo de evacuación). */
    public List<Persona> personasActualmenteDentro() {
        return visitaRepository.listarTodas().stream()
                .filter(Visita::estaAbierta)
                .map(Visita::getPersona)
                .distinct()
                .sorted(Comparator.comparing(Persona::getNombre))
                .collect(Collectors.toList());
    }

    /** Cuenta cuántas visitas hay agrupadas por estado (Dentro, Fuera, Pendiente, etc.). */
    public Map<String, Long> conteoVisitasPorEstado() {
        return visitaRepository.listarTodas().stream()
                .collect(Collectors.groupingBy(
                        v -> v.getEstadoVisita().getNombreEstado(),
                        Collectors.counting()));
    }

    /** Cuenta cuántas visitas ha tenido cada empresa (solo trabajadores/invitados con empresa asociada). */
    public Map<String, Long> conteoVisitasPorEmpresa() {
        return visitaRepository.listarTodas().stream()
                .filter(v -> v.getPersona().getEmpresa() != null)
                .collect(Collectors.groupingBy(
                        v -> v.getPersona().getEmpresa().getNombre(),
                        Collectors.counting()));
    }

    /** Visitas que quedaron marcadas como "Cerrada por Sistema" (indicador de salidas olvidadas frecuentes). */
    public List<Visita> visitasRegularizadasPorSistema() {
        return visitaRepository.listarTodas().stream()
                .filter(v -> EstadoVisita.CERRADA_POR_SISTEMA.equals(v.getEstadoVisita().getNombreEstado()))
                .collect(Collectors.toList());
    }

    public List<RegistroAuditoria> auditoriaCompleta(Usuario operador) {
        autorizacionService.verificarPermiso(operador, "generar_reporte_auditoria");
        return auditoriaRepository.listarTodos();
    }

    /** Resumen de cuántas acciones de auditoría ha generado cada usuario (top actividad). */
    public Map<Integer, Long> conteoAccionesPorUsuario(Usuario operador) {
        autorizacionService.verificarPermiso(operador, "generar_reporte_auditoria");
        return auditoriaRepository.listarTodos().stream()
                .filter(r -> r.getUsuarioId() != null)
                .collect(Collectors.groupingBy(RegistroAuditoria::getUsuarioId, Collectors.counting()));
    }
}
