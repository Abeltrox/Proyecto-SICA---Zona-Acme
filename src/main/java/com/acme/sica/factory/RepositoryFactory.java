package com.acme.sica.factory;

import com.acme.sica.repository.*;
import com.acme.sica.repository.impl.*;

/**
 * PATRÓN DE DISEÑO: Factory Method (simplificado como fábrica estática).
 * Centraliza la creación de las implementaciones concretas de los repositorios.
 * Si mañana se cambia JDBC por otro mecanismo de persistencia (ej. un ORM),
 * solo se modifica esta clase — el resto de la aplicación (servicios, controladores)
 * sigue programando contra las interfaces y no se entera del cambio (SOLID - DIP/OCP).
 */
public final class RepositoryFactory {

    private static UsuarioRepository usuarioRepository;
    private static RolRepository rolRepository;
    private static PersonaRepository personaRepository;
    private static EmpresaRepository empresaRepository;
    private static VisitaRepository visitaRepository;
    private static IncidenteRepository incidenteRepository;
    private static AuditoriaRepository auditoriaRepository;
    private static EstadoRepository estadoRepository;

    private RepositoryFactory() {}

    public static UsuarioRepository crearUsuarioRepository() {
        if (usuarioRepository == null) usuarioRepository = new UsuarioRepositoryJDBC();
        return usuarioRepository;
    }

    public static RolRepository crearRolRepository() {
        if (rolRepository == null) rolRepository = new RolRepositoryJDBC();
        return rolRepository;
    }

    public static PersonaRepository crearPersonaRepository() {
        if (personaRepository == null) personaRepository = new PersonaRepositoryJDBC();
        return personaRepository;
    }

    public static EmpresaRepository crearEmpresaRepository() {
        if (empresaRepository == null) empresaRepository = new EmpresaRepositoryJDBC();
        return empresaRepository;
    }

    public static VisitaRepository crearVisitaRepository() {
        if (visitaRepository == null) visitaRepository = new VisitaRepositoryJDBC();
        return visitaRepository;
    }

    public static IncidenteRepository crearIncidenteRepository() {
        if (incidenteRepository == null) incidenteRepository = new IncidenteRepositoryJDBC();
        return incidenteRepository;
    }

    public static AuditoriaRepository crearAuditoriaRepository() {
        if (auditoriaRepository == null) auditoriaRepository = new AuditoriaRepositoryJDBC();
        return auditoriaRepository;
    }

    public static EstadoRepository crearEstadoRepository() {
        if (estadoRepository == null) estadoRepository = new EstadoRepositoryJDBC();
        return estadoRepository;
    }
}
