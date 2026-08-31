package com.acme.sica.repository;

import com.acme.sica.model.Incidente;
import java.util.List;

public interface IncidenteRepository {
    Incidente guardar(Incidente incidente);
    List<Incidente> listarTodos();
}
