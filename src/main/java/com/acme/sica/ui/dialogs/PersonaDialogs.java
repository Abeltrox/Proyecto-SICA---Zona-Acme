package com.acme.sica.ui.dialogs;

import com.acme.sica.model.Empresa;
import com.acme.sica.model.Persona;
import com.acme.sica.model.TipoPersona;
import com.acme.sica.model.Usuario;
import com.acme.sica.ui.MainApp;
import com.acme.sica.ui.UiUtil;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public final class PersonaDialogs {

    private PersonaDialogs() {}

    public static void registrarPersona(MainApp app) {
        Dialog<Void> dialog = DialogoBase.crear("Registrar persona");

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre completo");
        TextField documento = new TextField();
        documento.setPromptText("Documento de identidad");

        ComboBox<TipoPersona> tipo = new ComboBox<>();
        tipo.getItems().addAll(TipoPersona.values());
        tipo.setValue(TipoPersona.Invitado);

        List<Empresa> empresas = app.getEmpresaRepository().listarTodas();
        ComboBox<Empresa> empresa = new ComboBox<>();
        empresa.getItems().addAll(empresas);
        empresa.setPromptText("Empresa (solo si es Trabajador)");

        TextField urlFoto = new TextField();
        urlFoto.setPromptText("URL de foto (opcional)");

        VBox contenido = new VBox(12,
                UiUtil.campoConEtiqueta("Nombre completo", nombre),
                UiUtil.campoConEtiqueta("Documento de identidad", documento),
                UiUtil.campoConEtiqueta("Tipo de persona", tipo),
                UiUtil.campoConEtiqueta("Empresa (si aplica)", empresa),
                UiUtil.campoConEtiqueta("URL de foto (opcional)", urlFoto));
        contenido.setPadding(new Insets(6));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Registrar", () -> {
            Usuario operador = app.getSesionActual();
            Empresa empresaSeleccionada = tipo.getValue() == TipoPersona.Trabajador ? empresa.getValue() : null;
            Persona persona = app.getPersonaService().registrarPersona(operador, nombre.getText().trim(),
                    documento.getText().trim(), empresaSeleccionada, tipo.getValue(),
                    urlFoto.getText().isBlank() ? null : urlFoto.getText().trim());
            UiUtil.mostrarExito("Persona registrada", persona.toString());
        });

        dialog.showAndWait();
    }

    public static void bloquearPersona(MainApp app) {
        Optional<String> documento = pedirDocumento("Bloquear persona",
                "Documento de la persona a bloquear:");
        documento.ifPresent(doc -> {
            try {
                app.getPersonaService().bloquearPersona(app.getSesionActual(), doc);
                UiUtil.mostrarExito("Persona bloqueada", "El acceso de " + doc + " ha sido bloqueado.");
            } catch (RuntimeException e) {
                UiUtil.mostrarError(e.getMessage());
            }
        });
    }

    public static void reactivarPersona(MainApp app) {
        Optional<String> documento = pedirDocumento("Reactivar persona",
                "Documento de la persona a reactivar:");
        documento.ifPresent(doc -> {
            try {
                app.getPersonaService().reactivarPersona(app.getSesionActual(), doc);
                UiUtil.mostrarExito("Persona reactivada", "El acceso de " + doc + " ha sido reactivado.");
            } catch (RuntimeException e) {
                UiUtil.mostrarError(e.getMessage());
            }
        });
    }

    private static Optional<String> pedirDocumento(String titulo, String mensaje) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(mensaje);
        dialog.getDialogPane().getStylesheets().add(
                PersonaDialogs.class.getResource("/css/theme.css").toExternalForm());
        return dialog.showAndWait().filter(s -> !s.isBlank());
    }
}
