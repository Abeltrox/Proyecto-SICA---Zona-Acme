package com.acme.sica.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un rol del sistema (ej. "Guarda de Seguridad").
 * Un rol es simplemente una agrupación de permisos (patrón Composite ligero:
 * el rol delega la decisión de autorización en el conjunto de permisos que contiene).
 */
public class Rol {

    private int id;
    private String nombreRol;
    private Set<Permiso> permisos = new HashSet<>();

    public Rol() {}

    public Rol(int id, String nombreRol) {
        this.id = id;
        this.nombreRol = nombreRol;
    }

    /** Regla de negocio: un rol tiene un permiso si el permiso está en su conjunto. */
    public boolean tienePermiso(String nombrePermiso) {
        return permisos.stream()
                .anyMatch(p -> p.getNombrePermiso().equalsIgnoreCase(nombrePermiso));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
    public Set<Permiso> getPermisos() { return permisos; }
    public void setPermisos(Set<Permiso> permisos) { this.permisos = permisos; }
    public void agregarPermiso(Permiso p) { this.permisos.add(p); }

    @Override
    public String toString() { return nombreRol; }
}
