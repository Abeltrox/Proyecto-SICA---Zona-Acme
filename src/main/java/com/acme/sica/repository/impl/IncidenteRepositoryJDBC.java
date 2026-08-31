package com.acme.sica.repository.impl;

import com.acme.sica.config.ConexionBD;
import com.acme.sica.model.Incidente;
import com.acme.sica.repository.IncidenteRepository;
import com.acme.sica.repository.UsuarioRepository;
import com.acme.sica.repository.VisitaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidenteRepositoryJDBC implements IncidenteRepository {

    private final VisitaRepository visitaRepository = new VisitaRepositoryJDBC();
    private final UsuarioRepository usuarioRepository = new UsuarioRepositoryJDBC();

    @Override
    public Incidente guardar(Incidente incidente) {
        String sql = "INSERT INTO incidentes (visita_id, reportado_por_id, fecha, descripcion) VALUES (?,?,?,?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (incidente.getVisita() != null) ps.setInt(1, incidente.getVisita().getId());
            else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, incidente.getReportadoPor().getId());
            ps.setTimestamp(3, Timestamp.valueOf(incidente.getFecha()));
            ps.setString(4, incidente.getDescripcion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) incidente.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar incidente", e);
        }
        return incidente;
    }

    @Override
    public List<Incidente> listarTodos() {
        List<Incidente> incidentes = new ArrayList<>();
        String sql = "SELECT * FROM incidentes ORDER BY fecha DESC";
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Incidente i = new Incidente();
                i.setId(rs.getInt("id"));
                int visitaId = rs.getInt("visita_id");
                if (!rs.wasNull()) i.setVisita(visitaRepository.buscarPorId(visitaId).orElse(null));
                i.setReportadoPor(usuarioRepository.buscarPorId(rs.getInt("reportado_por_id")).orElse(null));
                i.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                i.setDescripcion(rs.getString("descripcion"));
                incidentes.add(i);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar incidentes", e);
        }
        return incidentes;
    }
}
