package com.acme.sica.model;

import java.time.LocalDateTime;

/** Fila inmutable de bitacora_auditoria. Nunca se actualiza ni se borra, solo se inserta. */
public class RegistroAuditoria {

    private long id;
    private Integer usuarioId;
    private LocalDateTime fechaHora;
    private String accionRealizada;
    private String tablaAfectada;
    private Integer registroIdAfectado;
    private String detalles;

    public RegistroAuditoria() {}

    public RegistroAuditoria(Integer usuarioId, String accionRealizada, String tablaAfectada,
                              Integer registroIdAfectado, String detalles) {
        this.usuarioId = usuarioId;
        this.accionRealizada = accionRealizada;
        this.tablaAfectada = tablaAfectada;
        this.registroIdAfectado = registroIdAfectado;
        this.detalles = detalles;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getAccionRealizada() { return accionRealizada; }
    public void setAccionRealizada(String accionRealizada) { this.accionRealizada = accionRealizada; }
    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }
    public Integer getRegistroIdAfectado() { return registroIdAfectado; }
    public void setRegistroIdAfectado(Integer registroIdAfectado) { this.registroIdAfectado = registroIdAfectado; }
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
}
