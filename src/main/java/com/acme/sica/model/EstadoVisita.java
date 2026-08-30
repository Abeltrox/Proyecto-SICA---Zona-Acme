package com.acme.sica.model;

/** Fila del catálogo visita_estados ("Dentro", "Fuera", "Pendiente de Aprobacion", etc.). */
public class EstadoVisita {

    public static final String DENTRO = "Dentro";
    public static final String FUERA = "Fuera";
    public static final String PENDIENTE = "Pendiente de Aprobacion";
    public static final String APROBADO = "Aprobado";
    public static final String RECHAZADO = "Rechazado";
    public static final String EXPIRADO = "Expirado";
    public static final String CERRADA_POR_SISTEMA = "Cerrada por Sistema (Salida Olvidada)";

    private int id;
    private String nombreEstado;

    public EstadoVisita() {}

    public EstadoVisita(int id, String nombreEstado) {
        this.id = id;
        this.nombreEstado = nombreEstado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }

    @Override
    public String toString() { return nombreEstado; }
}
