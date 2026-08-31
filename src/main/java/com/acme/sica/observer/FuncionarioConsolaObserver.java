package com.acme.sica.observer;

import com.acme.sica.model.Visita;

/**
 * Observador concreto: en esta versión de consola, "notificar" significa imprimir
 * una alerta visible en la terminal simulando el aviso en tiempo real al
 * Funcionario de Empresa. Es el punto de extensión si mañana se agrega una UI web
 * o notificaciones push — se implementaría otro ObservadorNotificacion sin tocar
 * el resto del sistema (OCP).
 */
public class FuncionarioConsolaObserver implements ObservadorNotificacion {

    @Override
    public void notificarVisitaPendiente(Visita visita) {
        System.out.println("\n>>> [NOTIFICACIÓN EN TIEMPO REAL] Nueva visita pendiente de aprobación: "
                + visita.getPersona().getNombre() + " (doc. " + visita.getPersona().getDocumentoIdentidad()
                + ") — revisa el menú de aprobaciones.\n");
    }
}
