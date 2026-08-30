package com.acme.sica.model;

/** Usuario que opera el sistema (guarda, funcionario, supervisor, superusuario). */
public class Usuario {

    private int id;
    private String nombre;
    private String email;
    private String passwordHash;
    private Rol rol;
    private boolean activo;

    public Usuario() {}

    public Usuario(int id, String nombre, String email, Rol rol, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() { return nombre + " (" + (rol != null ? rol.getNombreRol() : "sin rol") + ")"; }
}
