package com.acme.sica.service;

import com.acme.sica.config.PasswordUtil;
import com.acme.sica.model.RegistroAuditoria;
import com.acme.sica.model.Rol;
import com.acme.sica.model.Usuario;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.repository.RolRepository;
import com.acme.sica.repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AutorizacionService autorizacionService;
    private final AuditoriaRepository auditoriaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                           AutorizacionService autorizacionService, AuditoriaRepository auditoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.autorizacionService = autorizacionService;
        this.auditoriaRepository = auditoriaRepository;
    }

    public Usuario crearUsuario(Usuario operador, String nombre, String email, String passwordPlano, int rolId) {
        autorizacionService.verificarPermiso(operador, "crear_usuario");

        Rol rol = rolRepository.buscarConPermisos(rolId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no existe: " + rolId));

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setEmail(email);
        nuevo.setPasswordHash(PasswordUtil.hash(passwordPlano));
        nuevo.setRol(rol);
        nuevo.setActivo(true);
        usuarioRepository.guardar(nuevo);

        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "CREACION_USUARIO", "usuarios",
                nuevo.getId(), "Usuario creado: " + email + " con rol " + rol.getNombreRol()));
        return nuevo;
    }

    public void desactivarUsuario(Usuario operador, int usuarioId) {
        autorizacionService.verificarPermiso(operador, "eliminar_usuario");
        usuarioRepository.desactivar(usuarioId);
        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "DESACTIVACION_USUARIO", "usuarios",
                usuarioId, "Usuario desactivado (id=" + usuarioId + ")"));
    }

    public List<Usuario> listarUsuarios(Usuario operador) {
        autorizacionService.verificarPermiso(operador, "crear_usuario");
        return usuarioRepository.listarTodos();
    }
}
