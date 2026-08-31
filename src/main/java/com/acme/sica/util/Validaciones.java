package com.acme.sica.util;

import java.util.regex.Pattern;

/**
 * Validaciones de formato para datos de entrada, centralizadas aquí y
 * aplicadas desde la capa de servicio (PersonaService, AccesoService).
 * Al vivir en el servicio y no solo en la interfaz gráfica, la regla se
 * cumple sin importar qué interfaz la invoque — tanto la JavaFX (UI) como
 * la de consola (ConsolaView) pasan por los mismos métodos de servicio,
 * así que ninguna de las dos puede "saltarse" la validación.
 */
public final class Validaciones {

    /** Cédula: solo dígitos, máximo 10 caracteres. */
    private static final Pattern CEDULA = Pattern.compile("^\\d{1,10}$");

    /** Placa de carro: 3 letras + 3 números (ej. GTL251). */
    private static final Pattern PLACA_CARRO = Pattern.compile("^[A-Z]{3}\\d{3}$");

    /** Placa de moto: 3 letras + 2 números + 1 letra (ej. HGK20H). */
    private static final Pattern PLACA_MOTO = Pattern.compile("^[A-Z]{3}\\d{2}[A-Z]$");

    private Validaciones() {}

    /** @throws IllegalArgumentException si la cédula no es numérica o supera los 10 dígitos. */
    public static void validarCedula(String documento) {
        if (documento == null || !CEDULA.matcher(documento.trim()).matches()) {
            throw new IllegalArgumentException(
                    "La cédula debe contener solo números, con un máximo de 10 dígitos.");
        }
    }

    /**
     * La placa es opcional (puede venir null o vacía, ej. si la persona llega
     * a pie). Si se indica, debe cumplir el formato colombiano de carro
     * (AAA123) o de moto (AAA12A), sin importar mayúsculas/minúsculas ni
     * espacios de más.
     *
     * @return la placa normalizada (mayúsculas, sin espacios) lista para
     *         guardar, o null si no se indicó ninguna.
     * @throws IllegalArgumentException si se indicó una placa con formato inválido.
     */
    public static String normalizarYValidarPlaca(String placa) {
        if (placa == null || placa.isBlank()) return null;

        String normalizada = placa.trim().toUpperCase().replaceAll("\\s+", "");
        boolean esCarro = PLACA_CARRO.matcher(normalizada).matches();
        boolean esMoto = PLACA_MOTO.matcher(normalizada).matches();

        if (!esCarro && !esMoto) {
            throw new IllegalArgumentException(
                    "Placa inválida. Debe tener el formato de carro (3 letras + 3 números, ej. GTL251) "
                            + "o de moto (3 letras + 2 números + 1 letra, ej. HGK20H).");
        }
        return normalizada;
    }
}
