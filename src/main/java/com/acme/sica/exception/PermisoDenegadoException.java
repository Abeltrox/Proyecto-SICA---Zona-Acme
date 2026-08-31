package com.acme.sica.exception;

/** Se lanza cuando un usuario intenta ejecutar una acción sin el permiso RBAC requerido. */
public class PermisoDenegadoException extends RuntimeException {
    public PermisoDenegadoException(String permisoRequerido) {
        super("Acceso denegado: se requiere el permiso '" + permisoRequerido + "' para esta acción.");
    }
}
