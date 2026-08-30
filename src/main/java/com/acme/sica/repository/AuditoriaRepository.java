package com.acme.sica.repository;

import com.acme.sica.model.RegistroAuditoria;
import java.util.List;

/** Solo permite insertar y listar: la bitácora es inmutable, nunca se actualiza ni se borra. */
public interface AuditoriaRepository {
    void registrar(RegistroAuditoria registro);
    List<RegistroAuditoria> listarTodos();
    List<RegistroAuditoria> listarPorUsuario(int usuarioId);
}
