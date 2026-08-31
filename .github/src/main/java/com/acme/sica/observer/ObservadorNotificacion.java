package com.acme.sica.observer;

import com.acme.sica.model.Visita;

/**
 * PATRÓN DE DISEÑO: Observer.
 * Define el contrato que debe cumplir cualquier "observador" que quiera ser
 * notificado cuando ocurre un evento de acceso relevante (ej. visita pendiente
 * de aprobación). Desacopla al AccesoService (el "sujeto") de quién y cómo
 * reacciona a la notificación — hoy es la consola del funcionario, mañana
 * podría ser un correo, un push a una app móvil, etc., sin tocar el servicio.
 */
public interface ObservadorNotificacion {
    void notificarVisitaPendiente(Visita visita);
}
