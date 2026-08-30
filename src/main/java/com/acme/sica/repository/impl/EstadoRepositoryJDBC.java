package com.acme.sica.repository.impl;

import com.acme.sica.config.ConexionBD;
import com.acme.sica.model.EstadoAcceso;
import com.acme.sica.model.EstadoVisita;
import com.acme.sica.repository.EstadoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstadoRepositoryJDBC implements EstadoRepository {

    @Override
    public Optional<EstadoVisita> buscarEstadoVisitaPorNombre(String nombre) {
        String sql = "SELECT id, nombre_estado FROM visita_estados WHERE nombre_estado = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new EstadoVisita(rs.getInt("id"), rs.getString("nombre_estado")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar estado de visita", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<EstadoAcceso> buscarEstadoAccesoPorNombre(String nombre) {
        String sql = "SELECT id, nombre_estado FROM persona_estados_acceso WHERE nombre_estado = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new EstadoAcceso(rs.getInt("id"), rs.getString("nombre_estado")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar estado de acceso", e);
        }
        return Optional.empty();
    }

    @Override
    public List<EstadoVisita> listarEstadosVisita() {
        List<EstadoVisita> lista = new ArrayList<>();
        String sql = "SELECT id, nombre_estado FROM visita_estados";
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(new EstadoVisita(rs.getInt("id"), rs.getString("nombre_estado")));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar estados de visita", e);
        }
        return lista;
    }

    @Override
    public List<EstadoAcceso> listarEstadosAcceso() {
        List<EstadoAcceso> lista = new ArrayList<>();
        String sql = "SELECT id, nombre_estado FROM persona_estados_acceso";
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(new EstadoAcceso(rs.getInt("id"), rs.getString("nombre_estado")));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar estados de acceso", e);
        }
        return lista;
    }
}
