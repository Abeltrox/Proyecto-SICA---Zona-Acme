package com.acme.sica.ui.dialogs;

import com.acme.sica.model.Usuario;
import com.acme.sica.model.Visita;
import com.acme.sica.ui.MainApp;
import com.acme.sica.ui.UiUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

/** Diálogos relacionados con el control de acceso en tiempo real (puerta). */
public final class IngresoDialogs {

    private IngresoDialogs() {}

    public static void registrarIngreso(MainApp app) {
        Dialog<Void> dialog = DialogoBase.crear("Registrar ingreso");

        TextField documento = new TextField();
        documento.setPromptText("Documento de identidad");
        TextField placa = new TextField();
        placa.setPromptText("Placa (opcional)");

        VBox contenido = new VBox(12,
                UiUtil.campoConEtiqueta("Documento de identidad", documento),
                UiUtil.campoConEtiqueta("Placa de vehículo (opcional)", placa));
        contenido.setPadding(new Insets(6));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Registrar", () -> {
            Usuario operador = app.getSesionActual();
            Visita visita = app.getAccesoService().registrarIngreso(
                    documento.getText().trim(), operador, placa.getText().isBlank() ? null : placa.getText().trim());
            UiUtil.mostrarExito("Ingreso procesado", visita.getPersona().getNombre()
                    + " → estado: " + visita.getEstadoVisita());
        });

        dialog.showAndWait();
    }

    public static void registrarSalida(MainApp app) {
        Dialog<Void> dialog = DialogoBase.crear("Registrar salida");

        TextField visitaId = new TextField();
        visitaId.setPromptText("ID de la visita");

        VBox contenido = new VBox(12, UiUtil.campoConEtiqueta("ID de la visita", visitaId));
        contenido.setPadding(new Insets(6));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Registrar salida", () -> {
            int id = Integer.parseInt(visitaId.getText().trim());
            Visita visita = app.getAccesoService().registrarSalida(id, app.getSesionActual());
            UiUtil.mostrarExito("Salida registrada", visita.getPersona().getNombre() + " ha salido del complejo.");
        });

        dialog.showAndWait();
    }

    public static void gestionarAprobaciones(MainApp app) {
        TextInputDialog pedirEmpresa = new TextInputDialog();
        pedirEmpresa.setTitle("Aprobaciones pendientes");
        pedirEmpresa.setHeaderText(null);
        pedirEmpresa.setContentText("ID de tu empresa (para filtrar pendientes):");
        pedirEmpresa.getDialogPane().getStylesheets().add(
                IngresoDialogs.class.getResource("/css/theme.css").toExternalForm());

        Optional<String> resultado = pedirEmpresa.showAndWait();
        if (resultado.isEmpty() || resultado.get().isBlank()) return;

        int empresaId;
        try {
            empresaId = Integer.parseInt(resultado.get().trim());
        } catch (NumberFormatException e) {
            UiUtil.mostrarError("El ID de empresa debe ser un número.");
            return;
        }

        List<Visita> pendientes = app.getVisitaRepository().listarPendientesPorFuncionario(empresaId);
        if (pendientes.isEmpty()) {
            UiUtil.mostrarExito("Sin pendientes", "No hay visitas pendientes de aprobación para esa empresa.");
            return;
        }

        ListView<Visita> lista = new ListView<>();
        lista.getItems().addAll(pendientes);
        lista.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Visita v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getId() + " — " + v.getPersona()
                        + " (llegó " + v.getFechaEntrada() + ")");
            }
        });
        lista.setPrefHeight(220);

        Dialog<Void> dialog = DialogoBase.crear("Visitas pendientes de aprobación");
        VBox contenido = new VBox(10, new Label("Selecciona una visita y decide:"), lista);
        contenido.setPadding(new Insets(6));
        dialog.getDialogPane().setContent(contenido);

        ButtonType aprobarTipo = new ButtonType("Aprobar");
        ButtonType rechazarTipo = new ButtonType("Rechazar");
        dialog.getDialogPane().getButtonTypes().addAll(aprobarTipo, rechazarTipo, ButtonType.CANCEL);

        dialog.setResultConverter(boton -> {
            Visita seleccionada = lista.getSelectionModel().getSelectedItem();
            if (seleccionada == null || (boton != aprobarTipo && boton != rechazarTipo)) return null;
            try {
                if (boton == aprobarTipo) {
                    Visita v = app.getAccesoService().aprobarVisita(seleccionada.getId(), app.getSesionActual());
                    UiUtil.mostrarExito("Visita aprobada", v.getPersona().getNombre() + " puede ingresar.");
                } else {
                    Visita v = app.getAccesoService().rechazarVisita(seleccionada.getId(), app.getSesionActual());
                    UiUtil.mostrarExito("Visita rechazada", v.getPersona().getNombre() + " fue rechazada.");
                }
            } catch (RuntimeException e) {
                UiUtil.mostrarError(e.getMessage());
            }
            return null;
        });

        dialog.showAndWait();
    }
}
