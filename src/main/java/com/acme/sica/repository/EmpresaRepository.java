package com.acme.sica.repository;

import com.acme.sica.model.Empresa;
import java.util.List;
import java.util.Optional;

public interface EmpresaRepository {
    Optional<Empresa> buscarPorId(int id);
    List<Empresa> listarTodas();
    Empresa guardar(Empresa empresa);
    void actualizar(Empresa empresa);
}
