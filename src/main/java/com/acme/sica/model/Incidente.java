package com.acme.sica.model;

import java.time.LocalDateTime;

public class Incidente {
    private int id;
    private Visita visita;
    private Usuario reportadoPor;
    private LocalDateTime fecha;
    private String descripcion;

    public Incidente() {}

    public Incidente(Visita visita, Usuario reportadoPor, String descripcion) {
        this.visita = visita;
        this.reportadoPor = reportadoPor;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Visita getVisita() { return visita; }
    public void setVisita(Visita visita) { this.visita = visita; }
    public Usuario getReportadoPor() { return reportadoPor; }
    public void setReportadoPor(Usuario reportadoPor) { this.reportadoPor = reportadoPor; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
