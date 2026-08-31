package com.acme.sica.repository.impl;

import com.acme.sica.config.ConexionBD;
import com.acme.sica.model.RegistroAuditoria;
import com.acme.sica.repository.AuditoriaRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaRepositoryJDBC implements AuditoriaRepository {

    @Override
    public void registrar(RegistroAuditoria registro) {
        String sql = "INSERT INTO bitacora_auditoria (usuario_id, accion_realizada, tabla_afectada, " +
                "registro_id_afectado, detalles) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            if (registro.getUsuarioId() != null) ps.setInt(1, registro.getUsuarioId());
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, registro.getAccionRealizada());
            ps.setString(3, registro.getTablaAfectada());
            if (registro.getRegistroIdAfectado() != null) ps.setInt(4, registro.getRegistroIdAfectado());
            else ps.setNull(4, Types.INTEGER);
            ps.setString(5, registro.getDetalles());
            ps.executeUpdate();
        } catch (SQLException e) {
            // Nota de diseño: un fallo en auditoría NO debe tumbar la operación de negocio ya
            // confirmada, pero sí debe quedar visible para el equipo de soporte.
            System.err.println("ADVERTENCIA: no se pudo escribir en bitacora_auditoria -> " + e.getMessage());
        }
    }

    @Override
    public List<RegistroAuditoria> listarTodos() {
        List<RegistroAuditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM bitacora_auditoria ORDER BY fecha_hora DESC";
        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar bitácora de auditoría", e);
        }
        return lista;
    }

    @Override
    public List<RegistroAuditoria> listarPorUsuario(int usuarioId) {
        List<RegistroAuditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM bitacora_auditoria WHERE usuario_id = ? ORDER BY fecha_hora DESC";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar bitácora por usuario", e);
        }
        return lista;
    }

    private RegistroAuditoria mapear(ResultSet rs) throws SQLException {
        RegistroAuditoria r = new RegistroAuditoria();
        r.setId(rs.getLong("id"));
        int usuarioId = rs.getInt("usuario_id");
        if (!rs.wasNull()) r.setUsuarioId(usuarioId);
        r.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        r.setAccionRealizada(rs.getString("accion_realizada"));
        r.setTablaAfectada(rs.getString("tabla_afectada"));
        int registroId = rs.getInt("registro_id_afectado");
        if (!rs.wasNull()) r.setRegistroIdAfectado(registroId);
        r.setDetalles(rs.getString("detalles"));
        return r;
    }
}
