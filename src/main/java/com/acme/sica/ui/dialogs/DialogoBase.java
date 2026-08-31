package com.acme.sica.ui.dialogs;

import com.acme.sica.ui.UiUtil;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

/**
 * Fábrica de diálogos base compartida por todas las pantallas de formulario.
 * Estandariza: aplicar el CSS del tema, quitar la barra de título nativa del
 * sistema operativo (queda solo la tarjeta redondeada verde pálido con su
 * propio encabezado), agregar el botón de acción + Cancelar, y envolver la
 * acción en un try/catch que muestra un error estilizado en vez de dejar que
 * la excepción rompa la interfaz (todas las excepciones de negocio
 * —RuntimeException de com.acme.sica.exception— terminan aquí).
 */
final class DialogoBase {

    private DialogoBase() {}

    static <T> Dialog<T> crear(String titulo) {
        Dialog<T> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle(titulo);

        var pane = dialog.getDialogPane();
        pane.getStylesheets().add(DialogoBase.class.getResource("/css/theme.css").toExternalForm());
        pane.getStyleClass().add("dialogo-pane");

        // El fondo de la escena debe ser transparente para que solo se vea
        // la tarjeta redondeada, sin un rectángulo blanco detrás recortándola.
        pane.sceneProperty().addListener((obs, anterior, nueva) -> {
            if (nueva != null) nueva.setFill(Color.TRANSPARENT);
        });

        Label encabezado = new Label(titulo);
        encabezado.getStyleClass().add("dialogo-encabezado");
        StackPane header = new StackPane(encabezado);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("dialogo-header-panel");
        pane.setHeader(header);

        return dialog;
    }

    /** Agrega el botón de acción (con el texto dado) y Cancelar; ejecuta 'accion' y consume errores de negocio. */
    static void agregarBotones(Dialog<Void> dialog, String textoBotonAccion, Runnable accion) {
        ButtonType tipoAccion = new ButtonType(textoBotonAccion, javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tipoAccion, ButtonType.CANCEL);
        UiUtil.animarBotonesDialogo(dialog.getDialogPane());

        var boton = dialog.getDialogPane().lookupButton(tipoAccion);
        boton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                accion.run();
            } catch (NumberFormatException nfe) {
                UiUtil.mostrarError("Revisa los campos numéricos: " + nfe.getMessage());
                event.consume();
            } catch (RuntimeException e) {
                UiUtil.mostrarError(e.getMessage());
                event.consume();
            }
        });
    }
}
