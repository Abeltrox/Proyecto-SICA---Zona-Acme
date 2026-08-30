package com.acme.sica.repository.impl;

import com.acme.sica.config.ConexionBD;
import com.acme.sica.model.*;
import com.acme.sica.repository.EmpresaRepository;
import com.acme.sica.repository.EstadoRepository;
import com.acme.sica.repository.PersonaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonaRepositoryJDBC implements PersonaRepository {

    private final EmpresaRepository empresaRepository = new EmpresaRepositoryJDBC();
    private final EstadoRepository estadoRepository = new EstadoRepositoryJDBC();

    @Override
    public Optional<Persona> buscarPorDocumento(String documento) {
        String sql = "SELECT * FROM personas WHERE documento_identidad = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar persona por documento", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Persona> buscarPorId(int id) {
        String sql = "SELECT * FROM personas WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar persona por id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Persona> listarTodas() {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM personas";
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) personas.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar personas", e);
        }
        return personas;
    }

    @Override
    public Persona guardar(Persona persona) {
        String sql = "INSERT INTO personas (nombre, documento_identidad, empresa_id, tipo_persona, estado_acceso_id, url_foto) " +
                "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getDocumentoIdentidad());
            if (persona.getEmpresa() != null) ps.setInt(3, persona.getEmpresa().getId());
            else ps.setNull(3, Types.INTEGER);
            ps.setString(4, persona.getTipoPersona().name());
            ps.setInt(5, persona.getEstadoAcceso().getId());
            ps.setString(6, persona.getUrlFoto());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) persona.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar persona", e);
        }
        return persona;
    }

    @Override
    public void actualizar(Persona persona) {
        String sql = "UPDATE personas SET nombre=?, empresa_id=?, tipo_persona=?, url_foto=? WHERE id=?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, persona.getNombre());
            if (persona.getEmpresa() != null) ps.setInt(2, persona.getEmpresa().getId());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, persona.getTipoPersona().name());
            ps.setString(4, persona.getUrlFoto());
            ps.setInt(5, persona.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar persona", e);
        }
    }

    @Override
    public void actualizarEstadoAcceso(int personaId, int estadoAccesoId) {
        String sql = "UPDATE personas SET estado_acceso_id=? WHERE id=?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, estadoAccesoId);
            ps.setInt(2, personaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar estado de acceso", e);
        }
    }

    private Persona mapear(ResultSet rs) throws SQLException {
        Persona p = new Persona();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setDocumentoIdentidad(rs.getString("documento_identidad"));
        int empresaId = rs.getInt("empresa_id");
        if (!rs.wasNull()) {
            p.setEmpresa(empresaRepository.buscarPorId(empresaId).orElse(null));
        }
        p.setTipoPersona(TipoPersona.valueOf(rs.getString("tipo_persona")));
        int estadoId = rs.getInt("estado_acceso_id");
        if (!rs.wasNull()) {
            p.setEstadoAcceso(estadoRepository.listarEstadosAcceso().stream()
                    .filter(e -> e.getId() == estadoId).findFirst().orElse(null));
        }
        p.setUrlFoto(rs.getString("url_foto"));
        return p;
    }
}
