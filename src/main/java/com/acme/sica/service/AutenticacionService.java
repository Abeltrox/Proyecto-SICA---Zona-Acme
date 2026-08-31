package com.acme.sica.service;

import com.acme.sica.config.PasswordUtil;
import com.acme.sica.exception.AccesoDenegadoException;
import com.acme.sica.model.RegistroAuditoria;
import com.acme.sica.model.Usuario;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.repository.UsuarioRepository;

import java.util.Optional;

public class AutenticacionService {

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaRepository auditoriaRepository;

    public AutenticacionService(UsuarioRepository usuarioRepository, AuditoriaRepository auditoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    /**
     * Regla de negocio: login válido requiere email existente, usuario activo y
     * contraseña coincidente. Cada intento (exitoso o fallido) queda en bitácora,
     * tal como exige la rúbrica.
     */
    public Usuario login(String email, String passwordPlano) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(email);

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().isActivo()
                || !PasswordUtil.coincide(passwordPlano, usuarioOpt.get().getPasswordHash())) {
            Integer usuarioId = usuarioOpt.map(Usuario::getId).orElse(null);
            auditoriaRepository.registrar(new RegistroAuditoria(usuarioId, "LOGIN_FALLIDO", "usuarios",
                    usuarioId, "Intento de login fallido para email: " + email));
            throw new AccesoDenegadoException("Credenciales inválidas o usuario inactivo.");
        }

        Usuario usuario = usuarioOpt.get();
        auditoriaRepository.registrar(new RegistroAuditoria(usuario.getId(), "LOGIN_EXITOSO", "usuarios",
                usuario.getId(), "Login exitoso de " + usuario.getNombre()));
        return usuario;
    }
}
