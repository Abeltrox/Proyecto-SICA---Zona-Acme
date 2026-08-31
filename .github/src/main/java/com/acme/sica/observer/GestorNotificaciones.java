package com.acme.sica.observer;

import com.acme.sica.model.Visita;

import java.util.ArrayList;
import java.util.List;

/**
 * PATRÓN DE DISEÑO: Observer (rol de "Sujeto").
 * Mantiene la lista de observadores suscritos y los notifica cuando se genera
 * una visita que requiere aprobación (invitado no anunciado / carnet olvidado).
 * SOLID - SRP: esta clase solo gestiona la suscripción/emisión de eventos,
 * no decide reglas de negocio.
 */
public class GestorNotificaciones {

    private static final GestorNotificaciones INSTANCIA = new GestorNotificaciones();
    private final List<ObservadorNotificacion> observadores = new ArrayList<>();

    private GestorNotificaciones() {}

    public static GestorNotificaciones getInstancia() {
        return INSTANCIA;
    }

    public void suscribir(ObservadorNotificacion observador) {
        observadores.add(observador);
    }

    public void emitirVisitaPendiente(Visita visita) {
        // Uso de lambda + forEach (Stream-friendly) para notificar a todos los suscritos.
        observadores.forEach(obs -> obs.notificarVisitaPendiente(visita));
    }
}
