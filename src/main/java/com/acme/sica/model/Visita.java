package com.acme.sica.model;

import java.time.LocalDateTime;

/** Registro atómico de un evento de entrada/salida. Es el corazón transaccional del sistema. */
public class Visita {

    private int id;
    private Persona persona;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private EstadoVisita estadoVisita;
    private String vehiculoPlaca;
    private Usuario visitaAprobadaPor;
    private Usuario anfitrion;

    public Visita() {}

    public boolean estaAbierta() {
        return estadoVisita != null && EstadoVisita.DENTRO.equals(estadoVisita.getNombreEstado());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }
    public LocalDateTime getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDateTime fechaEntrada) { this.fechaEntrada = fechaEntrada; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }
    public EstadoVisita getEstadoVisita() { return estadoVisita; }
    public void setEstadoVisita(EstadoVisita estadoVisita) { this.estadoVisita = estadoVisita; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
    public Usuario getVisitaAprobadaPor() { return visitaAprobadaPor; }
    public void setVisitaAprobadaPor(Usuario visitaAprobadaPor) { this.visitaAprobadaPor = visitaAprobadaPor; }
    public Usuario getAnfitrion() { return anfitrion; }
    public void setAnfitrion(Usuario anfitrion) { this.anfitrion = anfitrion; }

    @Override
    public String toString() {
        return "Visita#" + id + " [" + persona + " - " + estadoVisita + "]";
    }
}
