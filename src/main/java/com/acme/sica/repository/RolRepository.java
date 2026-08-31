package com.acme.sica.repository;

import com.acme.sica.model.Rol;
import java.util.List;
import java.util.Optional;

public interface RolRepository {
    Optional<Rol> buscarPorId(int id);
    /** Carga el rol junto con su conjunto de permisos (join rol_permisos + permisos). */
    Optional<Rol> buscarConPermisos(int id);
    List<Rol> listarTodos();
}
