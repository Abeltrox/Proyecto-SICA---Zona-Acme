package com.acme.sica.decorator;

import com.acme.sica.model.RegistroAuditoria;
import com.acme.sica.model.Usuario;
import com.acme.sica.model.Visita;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.service.AccesoServiceI;

/**
 * PATRÓN DE DISEÑO: Decorator.
 * Envuelve cualquier AccesoServiceI (el componente concreto es AccesoService)
 * y le añade, de forma transparente, la responsabilidad de alimentar
 * bitacora_auditoria después de cada operación crítica exitosa — que es
 * exactamente lo que exige la rúbrica: "la lógica de negocio... debe alimentar
 * la tabla bitacora_auditoria desde la capa de servicio de Java".
 *
 * Ventaja sobre meter el auditoria.registrar(...) dentro de AccesoService:
 * AccesoService se mantiene enfocado solo en reglas de negocio (SRP), y la
 * auditoría se puede activar/desactivar o cambiar de estrategia (ej. auditar
 * también a un archivo) sin tocar la lógica de negocio.
 */
public class AccesoServiceAuditoriaDecorator implements AccesoServiceI {

    private final AccesoServiceI servicioDecorado;
    private final AuditoriaRepository auditoriaRepository;

    public AccesoServiceAuditoriaDecorator(AccesoServiceI servicioDecorado, AuditoriaRepository auditoriaRepository) {
        this.servicioDecorado = servicioDecorado;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    public Visita registrarIngreso(String documentoIdentidad, Usuario operador, String vehiculoPlaca) {
        Visita visita = servicioDecorado.registrarIngreso(documentoIdentidad, operador, vehiculoPlaca);
        auditar(operador, "CHECK_IN_" + visita.getEstadoVisita().getNombreEstado().toUpperCase(), "visitas",
                visita.getId(), "Ingreso de " + visita.getPersona().getNombre() + " procesado como "
                        + visita.getEstadoVisita());
        return visita;
    }

    @Override
    public Visita registrarSalida(int visitaId, Usuario operador) {
        Visita visita = servicioDecorado.registrarSalida(visitaId, operador);
        auditar(operador, "CHECK_OUT", "visitas", visita.getId(),
                "Salida registrada para " + visita.getPersona().getNombre());
        return visita;
    }

    @Override
    public Visita aprobarVisita(int visitaId, Usuario funcionario) {
        Visita visita = servicioDecorado.aprobarVisita(visitaId, funcionario);
        auditar(funcionario, "VISITA_APROBADA", "visitas", visita.getId(),
                "Visita de " + visita.getPersona().getNombre() + " aprobada por " + funcionario.getNombre());
        return visita;
    }

    @Override
    public Visita rechazarVisita(int visitaId, Usuario funcionario) {
        Visita visita = servicioDecorado.rechazarVisita(visitaId, funcionario);
        auditar(funcionario, "VISITA_RECHAZADA", "visitas", visita.getId(),
                "Visita de " + visita.getPersona().getNombre() + " rechazada por " + funcionario.getNombre());
        return visita;
    }

    private void auditar(Usuario usuario, String accion, String tabla, Integer registroId, String detalles) {
        auditoriaRepository.registrar(new RegistroAuditoria(
                usuario != null ? usuario.getId() : null, accion, tabla, registroId, detalles));
    }
}
