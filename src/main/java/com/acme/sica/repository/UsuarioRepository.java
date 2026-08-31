package com.acme.sica.repository;

import com.acme.sica.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * SOLID - DIP: los servicios dependen de esta interfaz, no de la implementación JDBC.
 * SOLID - ISP: interfaz pequeña y específica para Usuario, no un repositorio genérico gigante.
 */
public interface UsuarioRepository {
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(int id);
    List<Usuario> listarTodos();
    Usuario guardar(Usuario usuario);
    void actualizar(Usuario usuario);
    void desactivar(int id);
}
