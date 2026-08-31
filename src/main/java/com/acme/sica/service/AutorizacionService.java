package com.acme.sica.service;

import com.acme.sica.exception.PermisoDenegadoException;
import com.acme.sica.model.Usuario;

/**
 * Servicio dedicado exclusivamente a la lógica de autorización RBAC (SOLID - SRP).
 * Antes de cualquier operación crítica, el controlador o servicio de negocio llama
 * a verificarPermiso(); si el rol del usuario no tiene el permiso, se corta la
 * operación con PermisoDenegadoException antes de tocar la base de datos.
 */
public class AutorizacionService {

    public void verificarPermiso(Usuario usuario, String nombrePermiso) {
        if (usuario == null || usuario.getRol() == null || !usuario.getRol().tienePermiso(nombrePermiso)) {
            throw new PermisoDenegadoException(nombrePermiso);
        }
    }

    public boolean tienePermiso(Usuario usuario, String nombrePermiso) {
        return usuario != null && usuario.getRol() != null && usuario.getRol().tienePermiso(nombrePermiso);
    }
}
