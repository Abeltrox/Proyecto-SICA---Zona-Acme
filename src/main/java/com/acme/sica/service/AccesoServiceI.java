package com.acme.sica.service;

import com.acme.sica.model.Usuario;
import com.acme.sica.model.Visita;

/**
 * Contrato del servicio de control de acceso. Existir como interfaz (y no solo
 * como clase concreta) es lo que permite aplicar el patrón Decorator para
 * envolverlo con auditoría sin que el resto del sistema note la diferencia
 * (SOLID - LSP: el decorador es sustituible por el componente que decora).
 */
public interface AccesoServiceI {
    Visita registrarIngreso(String documentoIdentidad, Usuario operador, String vehiculoPlaca);
    Visita registrarSalida(int visitaId, Usuario operador);
    Visita aprobarVisita(int visitaId, Usuario funcionario);
    Visita rechazarVisita(int visitaId, Usuario funcionario);
}
