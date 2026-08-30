package com.acme.sica.exception;

/** Se lanza cuando una persona no puede ingresar por una regla de negocio (ej. prohibición de acceso). */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
