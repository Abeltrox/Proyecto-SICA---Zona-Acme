package com.acme.sica.repository.impl;

import com.acme.sica.config.ConexionBD;
import com.acme.sica.model.Empresa;
import com.acme.sica.repository.EmpresaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpresaRepositoryJDBC implements EmpresaRepository {

    @Override
    public Optional<Empresa> buscarPorId(int id) {
        String sql = "SELECT id, nombre, contacto_principal FROM empresas WHERE id = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar empresa", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Empresa> listarTodas() {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT id, nombre, contacto_principal FROM empresas";
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) empresas.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar empresas", e);
        }
        return empresas;
    }

    @Override
    public Empresa guardar(Empresa empresa) {
        String sql = "INSERT INTO empresas (nombre, contacto_principal) VALUES (?,?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getContactoPrincipal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) empresa.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar empresa", e);
        }
        return empresa;
    }

    @Override
    public void actualizar(Empresa empresa) {
        String sql = "UPDATE empresas SET nombre=?, contacto_principal=? WHERE id=?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getContactoPrincipal());
            ps.setInt(3, empresa.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar empresa", e);
        }
    }

    private Empresa mapear(ResultSet rs) throws SQLException {
        return new Empresa(rs.getInt("id"), rs.getString("nombre"), rs.getString("contacto_principal"));
    }
}
