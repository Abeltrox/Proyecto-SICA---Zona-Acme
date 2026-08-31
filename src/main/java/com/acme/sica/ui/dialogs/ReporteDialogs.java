package com.acme.sica.ui.dialogs;

import com.acme.sica.model.Persona;
import com.acme.sica.model.RegistroAuditoria;
import com.acme.sica.ui.MainApp;
import com.acme.sica.ui.UiUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Map;

public final class ReporteDialogs {

    private ReporteDialogs() {}

    public static void personasDentro(MainApp app) {
        var personas = app.getReporteService().personasActualmenteDentro();
        ListView<Persona> lista = new ListView<>();
        lista.getItems().addAll(personas);
        lista.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Persona p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : "🟢  " + p);
            }
        });
        mostrarVentanaLista("Personas dentro del complejo (" + personas.size() + ")", lista);
    }

    public static void conteoPorEstado(MainApp app) {
        mostrarMapa("Visitas por estado", app.getReporteService().conteoVisitasPorEstado());
    }

    public static void conteoPorEmpresa(MainApp app) {
        mostrarMapa("Visitas por empresa", app.getReporteService().conteoVisitasPorEmpresa());
    }

    public static void bitacora(MainApp app) {
        try {
            var registros = app.getReporteService().auditoriaCompleta(app.getSesionActual());
            ListView<RegistroAuditoria> lista = new ListView<>();
            lista.getItems().addAll(registros);
            lista.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(RegistroAuditoria r, boolean empty) {
                    super.updateItem(r, empty);
                    setText(empty || r == null ? null : "[" + r.getFechaHora() + "] usuario=" + r.getUsuarioId()
                            + " · " + r.getAccionRealizada() + " · " + r.getDetalles());
                }
            });
            mostrarVentanaLista("Bitácora de auditoría (" + registros.size() + " registros)", lista);
        } catch (RuntimeException e) {
            UiUtil.mostrarError(e.getMessage());
        }
    }

    private static void mostrarMapa(String titulo, Map<String, Long> datos) {
        ListView<String> lista = new ListView<>();
        datos.forEach((clave, valor) -> lista.getItems().add(clave + "  —  " + valor));
        mostrarVentanaLista(titulo, lista);
    }

    private static void mostrarVentanaLista(String titulo, ListView<?> lista) {
        lista.setPrefSize(560, 380);
        Dialog<Void> dialog = DialogoBase.crear(titulo);
        VBox contenido = new VBox(14, lista);
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        UiUtil.animarBotonesDialogo(dialog.getDialogPane());
        dialog.showAndWait();
    }
}
