package com.acme.sica.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad de hashing de contraseñas con SHA-256.
 * NOTA ACADÉMICA: en un sistema de producción real se debería usar BCrypt o Argon2
 * (con salt) en lugar de un hash simple; se usa SHA-256 aquí por simplicidad del
 * alcance académico del proyecto.
 */
public final class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String textoPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(textoPlano.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Error al calcular el hash de la contraseña", e);
        }
    }

    public static boolean coincide(String textoPlano, String hashAlmacenado) {
        return hash(textoPlano).equalsIgnoreCase(hashAlmacenado);
    }
}
