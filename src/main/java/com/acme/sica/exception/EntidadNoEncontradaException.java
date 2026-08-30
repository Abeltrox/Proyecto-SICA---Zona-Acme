package com.acme.sica.exception;

public class EntidadNoEncontradaException extends RuntimeException {
    public EntidadNoEncontradaException(String entidad, String criterio) {
        super(entidad + " no encontrado(a) con criterio: " + criterio);
    }
}
