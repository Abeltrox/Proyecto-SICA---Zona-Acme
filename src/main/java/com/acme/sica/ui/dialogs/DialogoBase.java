package com.acme.sica.ui.dialogs;

import com.acme.sica.ui.UiUtil;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Fábrica de diálogos base compartida por todas las pantallas de formulario.
 * Estandariza: aplicar el CSS del tema, agregar el botón de acción + Cancelar,
 * y envolver la acción en un try/catch que muestra un error estilizado en vez
 * de dejar que la excepción rompa la interfaz (todas las excepciones de
 * negocio —RuntimeException de com.acme.sica.exception— terminan aquí).
 */
final class DialogoBase {

    private DialogoBase() {}

    static Dialog<Void> crear(String titulo) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.getDialogPane().getStylesheets().add(
                DialogoBase.class.getResource("/css/theme.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        return dialog;
    }

    /** Agrega el botón de acción (con el texto dado) y Cancelar; ejecuta 'accion' y consume errores de negocio. */
    static void agregarBotones(Dialog<Void> dialog, String textoBotonAccion, Runnable accion) {
        ButtonType tipoAccion = new ButtonType(textoBotonAccion, javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tipoAccion, ButtonType.CANCEL);

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
