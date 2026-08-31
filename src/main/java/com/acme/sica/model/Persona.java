package com.acme.sica.model;

/** Trabajador o invitado controlado por el sistema (no confundir con Usuario, que opera el sistema). */
public class Persona {

    private int id;
    private String nombre;
    private String documentoIdentidad;
    private Empresa empresa;
    private TipoPersona tipoPersona;
    private EstadoAcceso estadoAcceso;
    private String urlFoto;

    public Persona() {}

    public Persona(int id, String nombre, String documentoIdentidad, Empresa empresa,
                    TipoPersona tipoPersona, EstadoAcceso estadoAcceso, String urlFoto) {
        this.id = id;
        this.nombre = nombre;
        this.documentoIdentidad = documentoIdentidad;
        this.empresa = empresa;
        this.tipoPersona = tipoPersona;
        this.estadoAcceso = estadoAcceso;
        this.urlFoto = urlFoto;
    }

    /** Regla de negocio central: nadie con prohibición de ingreso puede entrar, sin excepción. */
    public boolean puedeIngresar() {
        return estadoAcceso == null || !estadoAcceso.esProhibicion();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public TipoPersona getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(TipoPersona tipoPersona) { this.tipoPersona = tipoPersona; }
    public EstadoAcceso getEstadoAcceso() { return estadoAcceso; }
    public void setEstadoAcceso(EstadoAcceso estadoAcceso) { this.estadoAcceso = estadoAcceso; }
    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }

    @Override
    public String toString() { return nombre + " (" + documentoIdentidad + ")"; }
}
