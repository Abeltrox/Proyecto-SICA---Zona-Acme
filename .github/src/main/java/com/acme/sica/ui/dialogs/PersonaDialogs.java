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
import java.util.function.Consumer;

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
        tipo.setMaxWidth(Double.MAX_VALUE);

        List<Empresa> empresas = app.getEmpresaRepository().listarTodas();
        ComboBox<Empresa> empresa = new ComboBox<>();
        empresa.getItems().addAll(empresas);
        empresa.setPromptText("Empresa (solo si es Trabajador)");
        empresa.setMaxWidth(Double.MAX_VALUE);

        TextField urlFoto = new TextField();
        urlFoto.setPromptText("URL de foto (opcional)");

        VBox contenido = new VBox(16,
                UiUtil.campoConEtiqueta("Nombre completo", nombre),
                UiUtil.campoConEtiqueta("Documento de identidad", documento),
                UiUtil.campoConEtiqueta("Tipo de persona", tipo),
                UiUtil.campoConEtiqueta("Empresa (si aplica)", empresa),
                UiUtil.campoConEtiqueta("URL de foto (opcional)", urlFoto));
        contenido.setPadding(new Insets(24));
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

    /** Se elige la persona de un desplegable (solo aparecen las que hoy pueden ingresar) en vez de escribir su documento. */
    public static void bloquearPersona(MainApp app) {
        List<Persona> candidatas = app.getPersonaRepository().listarTodas().stream()
                .filter(Persona::puedeIngresar)
                .toList();
        seleccionarYAplicar(app, "Bloquear persona", "Persona a bloquear", "Bloquear", candidatas,
                persona -> {
                    app.getPersonaService().bloquearPersona(app.getSesionActual(), persona.getDocumentoIdentidad());
                    UiUtil.mostrarExito("Persona bloqueada", persona.getNombre() + " ha sido bloqueada.");
                },
                "No hay personas activas para bloquear.");
    }

    /** Se elige la persona de un desplegable (solo aparecen las que hoy están bloqueadas) en vez de escribir su documento. */
    public static void reactivarPersona(MainApp app) {
        List<Persona> candidatas = app.getPersonaRepository().listarTodas().stream()
                .filter(p -> !p.puedeIngresar())
                .toList();
        seleccionarYAplicar(app, "Reactivar persona", "Persona a reactivar", "Reactivar", candidatas,
                persona -> {
                    app.getPersonaService().reactivarPersona(app.getSesionActual(), persona.getDocumentoIdentidad());
                    UiUtil.mostrarExito("Persona reactivada", persona.getNombre() + " ha sido reactivada.");
                },
                "No hay personas bloqueadas para reactivar.");
    }

    private static void seleccionarYAplicar(MainApp app, String titulo, String etiquetaCampo, String textoBoton,
                                             List<Persona> candidatas, Consumer<Persona> accion, String mensajeVacio) {
        if (candidatas.isEmpty()) {
            UiUtil.mostrarExito(titulo, mensajeVacio);
            return;
        }

        ComboBox<Persona> combo = new ComboBox<>();
        combo.getItems().addAll(candidatas);
        combo.setPromptText("Selecciona una persona");
        combo.setMaxWidth(Double.MAX_VALUE);

        Dialog<Void> dialog = DialogoBase.crear(titulo);
        VBox contenido = new VBox(16, UiUtil.campoConEtiqueta(etiquetaCampo, combo));
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, textoBoton, () -> {
            Persona seleccionada = combo.getValue();
            if (seleccionada == null) throw new IllegalArgumentException("Selecciona una persona de la lista.");
            accion.accept(seleccionada);
        });

        dialog.showAndWait();
    }
}
