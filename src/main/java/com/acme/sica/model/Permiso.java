package com.acme.sica.model;

/** Representa una acción granular controlable del sistema (ej. "registrar_visita"). */
public class Permiso {

    private int id;
    private String nombrePermiso;
    private String descripcion;

    public Permiso() {}

    public Permiso(int id, String nombrePermiso, String descripcion) {
        this.id = id;
        this.nombrePermiso = nombrePermiso;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombrePermiso() { return nombrePermiso; }
    public void setNombrePermiso(String nombrePermiso) { this.nombrePermiso = nombrePermiso; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permiso)) return false;
        return nombrePermiso.equalsIgnoreCase(((Permiso) o).nombrePermiso);
    }

    @Override
    public int hashCode() { return nombrePermiso.toLowerCase().hashCode(); }

    @Override
    public String toString() { return nombrePermiso; }
}
