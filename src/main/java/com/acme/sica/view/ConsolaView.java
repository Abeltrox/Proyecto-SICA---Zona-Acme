package com.acme.sica.view;

import java.util.Scanner;

/**
 * Capa VIEW del patrón MVC exigido por la rúbrica. Encapsula toda la
 * interacción de entrada/salida por consola. Los controladores nunca llaman
 * a System.out/Scanner directamente — siempre pasan por aquí (SOLID - SRP).
 */
public class ConsolaView {

    private final Scanner scanner = new Scanner(System.in);

    public void mostrar(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }

    public void mostrarExito(String mensaje) {
        System.out.println("[OK] " + mensaje);
    }

    public String leerTexto(String etiqueta) {
        System.out.print(etiqueta + ": ");
        return scanner.nextLine().trim();
    }

    public int leerEntero(String etiqueta) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(etiqueta));
            } catch (NumberFormatException e) {
                mostrarError("Ingresa un número válido.");
            }
        }
    }

    public void pausa() {
        System.out.print("\nPresiona ENTER para continuar...");
        scanner.nextLine();
    }
}
