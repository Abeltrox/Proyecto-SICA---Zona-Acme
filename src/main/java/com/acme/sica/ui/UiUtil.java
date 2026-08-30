package com.acme.sica.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Utilidades compartidas por todas las pantallas JavaFX: crea controles ya
 * estilizados con las clases CSS del tema (verde suave + blanco) y estandariza
 * las alertas de éxito/error para no repetir código en cada diálogo.
 */
public final class UiUtil {

    private UiUtil() {}

    public static Button botonPrimario(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("boton-primario");
        return b;
    }

    public static Button botonSecundario(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("boton-secundario");
        return b;
    }

    public static Button botonPeligro(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("boton-peligro");
        return b;
    }

    public static VBox campoConEtiqueta(String etiqueta, Control control) {
        Label label = new Label(etiqueta);
        label.getStyleClass().add("campo-etiqueta");
        VBox box = new VBox(4, label, control);
        return box;
    }

    public static void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarEstilo(alert);
        alert.showAndWait();
    }

    public static void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No se pudo completar la acción");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarEstilo(alert);
        alert.showAndWait();
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarEstilo(alert);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    private static void aplicarEstilo(Alert alert) {
        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(UiUtil.class.getResource("/css/theme.css").toExternalForm());
        pane.setPadding(new Insets(12));
    }
}
