package com.acme.sica.repository;

import com.acme.sica.model.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaRepository {
    Optional<Persona> buscarPorDocumento(String documento);
    Optional<Persona> buscarPorId(int id);
    List<Persona> listarTodas();
    Persona guardar(Persona persona);
    void actualizar(Persona persona);
    void actualizarEstadoAcceso(int personaId, int estadoAccesoId);
}
