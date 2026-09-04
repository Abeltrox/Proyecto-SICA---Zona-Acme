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
        documento.setPromptText("Documento de identidad (máx. 10 dígitos)");
        UiUtil.restringirSoloDigitos(documento, 10);

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

    /** Se elige la persona de un desplegable y se edita su nombre y correo en un segundo diálogo, prellenado. */
    public static void gestionarPersona(MainApp app) {
        List<Persona> candidatas = app.getPersonaRepository().listarTodas();
        if (candidatas.isEmpty()) {
            UiUtil.mostrarExito("Gestionar persona", "Todavía no hay personas registradas.");
            return;
        }

        ComboBox<Persona> combo = new ComboBox<>();
        combo.getItems().addAll(candidatas);
        combo.setPromptText("Selecciona una persona");
        combo.setMaxWidth(Double.MAX_VALUE);

        Dialog<Persona> seleccion = DialogoBase.crear("Gestionar persona");
        VBox contenidoSeleccion = new VBox(16, UiUtil.campoConEtiqueta("Persona", combo));
        contenidoSeleccion.setPadding(new Insets(24));
        seleccion.getDialogPane().setContent(contenidoSeleccion);
        seleccion.getDialogPane().getButtonTypes().addAll(javafx.scene.control.ButtonType.OK,
                javafx.scene.control.ButtonType.CANCEL);
        UiUtil.animarBotonesDialogo(seleccion.getDialogPane());
        seleccion.setResultConverter(boton -> boton == javafx.scene.control.ButtonType.OK ? combo.getValue() : null);

        var elegida = seleccion.showAndWait();
        if (elegida.isEmpty()) return;
        Persona persona = elegida.get();

        Dialog<Void> dialog = DialogoBase.crear("Editar — " + persona.getNombre());

        TextField nombre = new TextField(persona.getNombre());
        TextField correo = new TextField(persona.getCorreo() == null ? "" : persona.getCorreo());
        correo.setPromptText("correo@ejemplo.com (opcional)");

        VBox contenido = new VBox(16,
                new javafx.scene.control.Label("Documento: " + persona.getDocumentoIdentidad()),
                UiUtil.campoConEtiqueta("Nombre completo", nombre),
                UiUtil.campoConEtiqueta("Correo electrónico", correo));
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Guardar cambios", () -> {
            app.getPersonaService().editarPersona(app.getSesionActual(), persona.getDocumentoIdentidad(),
                    nombre.getText().trim(), correo.getText().isBlank() ? null : correo.getText().trim());
            UiUtil.mostrarExito("Persona actualizada", nombre.getText().trim() + " se guardó correctamente.");
        });

        dialog.showAndWait();
    }

    /**
     * Elimina definitivamente a una persona (p. ej. un trabajador despedido). El servicio bloquea el
     * borrado si esa persona ya tiene visitas en su historial, y sugiere "Bloquear persona" en su lugar.
     */
    public static void eliminarPersona(MainApp app) {
        List<Persona> candidatas = app.getPersonaRepository().listarTodas();
        seleccionarYAplicar(app, "Eliminar persona", "Persona a eliminar", "Eliminar", candidatas,
                persona -> {
                    app.getPersonaService().eliminarPersona(app.getSesionActual(), persona.getDocumentoIdentidad());
                    UiUtil.mostrarExito("Persona eliminada", persona.getNombre() + " fue eliminada del sistema.");
                },
                "Todavía no hay personas registradas.");
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
