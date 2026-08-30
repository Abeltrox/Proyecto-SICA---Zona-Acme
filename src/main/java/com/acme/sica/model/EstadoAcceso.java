package com.acme.sica.model;

/** Fila del catálogo persona_estados_acceso ("Activo", "Con Prohibicion de Ingreso"). */
public class EstadoAcceso {
    private int id;
    private String nombreEstado;

    public EstadoAcceso() {}

    public EstadoAcceso(int id, String nombreEstado) {
        this.id = id;
        this.nombreEstado = nombreEstado;
    }

    public boolean esProhibicion() {
        return "Con Prohibicion de Ingreso".equalsIgnoreCase(nombreEstado);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }

    @Override
    public String toString() { return nombreEstado; }
}
