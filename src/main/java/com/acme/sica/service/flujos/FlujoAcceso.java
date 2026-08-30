package com.acme.sica.service.flujos;

import com.acme.sica.model.Persona;
import com.acme.sica.model.Usuario;
import com.acme.sica.model.Visita;

/**
 * PATRÓN DE DISEÑO: Strategy.
 * Cada uno de los 4 escenarios descritos en la rúbrica (invitado pre-registrado,
 * invitado no anunciado, trabajador con carnet olvidado) es una estrategia
 * intercambiable de procesamiento de ingreso. AccesoService elige la estrategia
 * correcta según el caso y delega en ella — así, agregar un quinto escenario en
 * el futuro solo implica crear una nueva clase, sin tocar ni un if/else existente
 * en el resto del sistema (SOLID - OCP: abierto a extensión, cerrado a modificación).
 */
public interface FlujoAcceso {
    Visita procesarIngreso(Persona persona, Usuario operador, String vehiculoPlaca);
}
