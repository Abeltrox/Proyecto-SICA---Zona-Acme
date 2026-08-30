package com.acme.sica.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * PATRÓN DE DISEÑO: Singleton.
 * Garantiza una única instancia de conexión activa a la base de datos en toda
 * la aplicación de consola, evitando abrir conexiones innecesarias y centralizando
 * la configuración de acceso a MySQL.
 *
 * SOLID - SRP: esta clase tiene una única responsabilidad, gestionar el ciclo de
 * vida de la conexión JDBC. No sabe nada de SQL de negocio.
 */
public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/sica_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "292620";

    private static ConexionBD instancia;
    private Connection conexion;

    private ConexionBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("No fue posible conectar a la base de datos sica_db", e);
        }
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar la conexión a la base de datos", e);
        }
        return conexion;
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
