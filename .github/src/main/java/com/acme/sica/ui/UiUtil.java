package com.acme.sica.ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

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
        aplicarEstilo(alert, false);
        alert.showAndWait();
    }

    public static void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No se pudo completar la acción");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarEstilo(alert, true);
        alert.showAndWait();
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarEstilo(alert, false);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    /**
     * Deja la alerta como una tarjeta redondeada verde pálido (o rojiza para
     * errores), sin la barra de título nativa del sistema operativo: solo la
     * ventana emergente limpia con su propio encabezado.
     */
    private static void aplicarEstilo(Alert alert, boolean esError) {
        alert.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(UiUtil.class.getResource("/css/theme.css").toExternalForm());
        pane.getStyleClass().add("dialogo-pane");

        pane.sceneProperty().addListener((obs, anterior, nueva) -> {
            if (nueva != null) nueva.setFill(Color.TRANSPARENT);
        });

        Label encabezado = new Label(alert.getTitle());
        encabezado.getStyleClass().add("dialogo-encabezado");
        StackPane header = new StackPane(encabezado);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("dialogo-header-panel");
        if (esError) header.getStyleClass().add("dialogo-header-error");
        pane.setHeader(header);
    }
}
