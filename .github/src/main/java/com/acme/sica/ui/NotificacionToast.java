package com.acme.sica.ui;

import com.acme.sica.model.Visita;
import com.acme.sica.observer.ObservadorNotificacion;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

/**
 * PATRÓN Observer (mismo patrón ya usado en la versión de consola, aquí con
 * un observador concreto distinto): en vez de imprimir en consola, muestra un
 * "toast" verde flotante en la esquina de la pantalla cuando llega una visita
 * pendiente de aprobación. Es la prueba de que el patrón Observer permite
 * cambiar el canal de notificación (consola -> GUI) sin tocar AccesoService
 * ni las estrategias de Flujo (OCP).
 */
public class NotificacionToast implements ObservadorNotificacion {

    private static final NotificacionToast INSTANCIA = new NotificacionToast();

    private NotificacionToast() {}

    public static NotificacionToast getInstancia() {
        return INSTANCIA;
    }

    @Override
    public void notificarVisitaPendiente(Visita visita) {
        Platform.runLater(() -> mostrarToast(
                "Nueva visita pendiente: " + visita.getPersona().getNombre()));
    }

    private void mostrarToast(String mensaje) {
        Label label = new Label("🔔  " + mensaje);
        label.getStyleClass().add("chip-estado");
        label.setStyle(label.getStyle() + "-fx-font-size: 13px; -fx-padding: 12 20 12 20;");

        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-color: transparent;");

        Stage toast = new Stage(StageStyle.TRANSPARENT);
        toast.setAlwaysOnTop(true);
        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(NotificacionToast.class.getResource("/css/theme.css").toExternalForm());
        toast.setScene(scene);

        toast.setX(javafx.stage.Screen.getPrimary().getVisualBounds().getMaxX() - 340);
        toast.setY(40);
        toast.show();

        PauseTransition espera = new PauseTransition(Duration.seconds(4));
        espera.setOnFinished(e -> toast.close());
        espera.play();
    }
}
