package com.acme.sica.repository.impl;

import com.acme.sica.config.ConexionBD;
import com.acme.sica.model.*;
import com.acme.sica.repository.PersonaRepository;
import com.acme.sica.repository.UsuarioRepository;
import com.acme.sica.repository.VisitaRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisitaRepositoryJDBC implements VisitaRepository {

    private final PersonaRepository personaRepository = new PersonaRepositoryJDBC();
    private final UsuarioRepository usuarioRepository = new UsuarioRepositoryJDBC();
    private final com.acme.sica.repository.EstadoRepository estadoRepository = new EstadoRepositoryJDBC();

    @Override
    public Visita guardar(Visita visita) {
        String sql = "INSERT INTO visitas (persona_id, fecha_entrada, fecha_salida, estado_visita_id, " +
                "vehiculo_placa, visita_aprobada_por, anfitrion_id) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, visita.getPersona().getId());
            ps.setTimestamp(2, visita.getFechaEntrada() != null ? Timestamp.valueOf(visita.getFechaEntrada()) : null);
            ps.setTimestamp(3, visita.getFechaSalida() != null ? Timestamp.valueOf(visita.getFechaSalida()) : null);
            ps.setInt(4, visita.getEstadoVisita().getId());
            ps.setString(5, visita.getVehiculoPlaca());
            if (visita.getVisitaAprobadaPor() != null) ps.setInt(6, visita.getVisitaAprobadaPor().getId());
            else ps.setNull(6, Types.INTEGER);
            if (visita.getAnfitrion() != null) ps.setInt(7, visita.getAnfitrion().getId());
            else ps.setNull(7, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) visita.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar visita", e);
        }
        return visita;
    }

    @Override
    public void actualizar(Visita visita) {
        String sql = "UPDATE visitas SET fecha_salida=?, estado_visita_id=?, visita_aprobada_por=? WHERE id=?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setTimestamp(1, visita.getFechaSalida() != null ? Timestamp.valueOf(visita.getFechaSalida()) : null);
            ps.setInt(2, visita.getEstadoVisita().getId());
            if (visita.getVisitaAprobadaPor() != null) ps.setInt(3, visita.getVisitaAprobadaPor().getId());
            else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, visita.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar visita", e);
        }
    }

    @Override
    public Optional<Visita> buscarPorId(int id) {
        String sql = "SELECT * FROM visitas WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar visita", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Visita> buscarVisitaAbiertaDePersona(int personaId) {
        String sql = "SELECT v.* FROM visitas v JOIN visita_estados e ON v.estado_visita_id = e.id " +
                "WHERE v.persona_id = ? AND e.nombre_estado = 'Dentro' ORDER BY v.fecha_entrada DESC LIMIT 1";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar visita abierta", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Visita> listarPendientesPorFuncionario(int empresaId) {
        List<Visita> visitas = new ArrayList<>();
        String sql = "SELECT v.* FROM visitas v " +
                "JOIN personas p ON v.persona_id = p.id " +
                "JOIN visita_estados e ON v.estado_visita_id = e.id " +
                "WHERE p.empresa_id = ? AND e.nombre_estado = 'Pendiente de Aprobacion'";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, empresaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) visitas.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar visitas pendientes", e);
        }
        return visitas;
    }

    @Override
    public List<Visita> listarTodas() {
        List<Visita> visitas = new ArrayList<>();
        String sql = "SELECT * FROM visitas ORDER BY fecha_entrada DESC";
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) visitas.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar visitas", e);
        }
        return visitas;
    }

    @Override
    public List<Visita> listarPorPersona(int personaId) {
        List<Visita> visitas = new ArrayList<>();
        String sql = "SELECT * FROM visitas WHERE persona_id = ? ORDER BY fecha_entrada DESC";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) visitas.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar visitas por persona", e);
        }
        return visitas;
    }

    private Visita mapear(ResultSet rs) throws SQLException {
        Visita v = new Visita();
        v.setId(rs.getInt("id"));
        v.setPersona(personaRepository.buscarPorId(rs.getInt("persona_id")).orElse(null));
        Timestamp entrada = rs.getTimestamp("fecha_entrada");
        if (entrada != null) v.setFechaEntrada(entrada.toLocalDateTime());
        Timestamp salida = rs.getTimestamp("fecha_salida");
        if (salida != null) v.setFechaSalida(salida.toLocalDateTime());
        int estadoId = rs.getInt("estado_visita_id");
        v.setEstadoVisita(estadoRepository.listarEstadosVisita().stream()
                .filter(e -> e.getId() == estadoId).findFirst().orElse(null));
        v.setVehiculoPlaca(rs.getString("vehiculo_placa"));
        int aprobadoPor = rs.getInt("visita_aprobada_por");
        if (!rs.wasNull()) v.setVisitaAprobadaPor(usuarioRepository.buscarPorId(aprobadoPor).orElse(null));
        int anfitrionId = rs.getInt("anfitrion_id");
        if (!rs.wasNull()) v.setAnfitrion(usuarioRepository.buscarPorId(anfitrionId).orElse(null));
        return v;
    }
}
