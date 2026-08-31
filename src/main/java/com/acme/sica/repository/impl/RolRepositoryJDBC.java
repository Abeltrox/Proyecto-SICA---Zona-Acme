package com.acme.sica.repository.impl;

import com.acme.sica.config.ConexionBD;
import com.acme.sica.model.Permiso;
import com.acme.sica.model.Rol;
import com.acme.sica.repository.RolRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RolRepositoryJDBC implements RolRepository {

    @Override
    public Optional<Rol> buscarPorId(int id) {
        String sql = "SELECT id, nombre_rol FROM roles WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Rol(rs.getInt("id"), rs.getString("nombre_rol")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar rol por id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Rol> buscarConPermisos(int id) {
        Optional<Rol> rolOpt = buscarPorId(id);
        if (rolOpt.isEmpty()) return Optional.empty();

        Rol rol = rolOpt.get();
        String sql = "SELECT p.id, p.nombre_permiso, p.descripcion " +
                "FROM permisos p " +
                "JOIN rol_permisos rp ON rp.permiso_id = p.id " +
                "WHERE rp.rol_id = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rol.agregarPermiso(new Permiso(rs.getInt("id"), rs.getString("nombre_permiso"),
                            rs.getString("descripcion")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar permisos del rol", e);
        }
        return Optional.of(rol);
    }

    @Override
    public List<Rol> listarTodos() {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT id, nombre_rol FROM roles";
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Rol(rs.getInt("id"), rs.getString("nombre_rol")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar roles", e);
        }
        return roles;
    }
}
