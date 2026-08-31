package com.acme.sica.ui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Utilidades compartidas por todas las pantallas JavaFX: crea controles ya
 * estilizados con las clases CSS del tema (verde suave + blanco), estandariza
 * las alertas de éxito/error para no repetir código en cada diálogo, y agrega
 * las animaciones de hover/click a los botones y tarjetas del dashboard.
 */
public final class UiUtil {

    private UiUtil() {}

    public static Button botonPrimario(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("boton-primario");
        animarInteraccion(b);
        return b;
    }

    public static Button botonSecundario(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("boton-secundario");
        animarInteraccion(b);
        return b;
    }

    public static Button botonPeligro(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("boton-peligro");
        animarInteraccion(b);
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

        animarBotonesDialogo(pane);
    }

    // ---------------- Animaciones de hover / clic ----------------

    /**
     * Anima cualquier nodo "tipo botón": al pasar el cursor crece suavemente,
     * y al hacer clic da un pequeño "pulso" hacia arriba (sobresale) antes de
     * asentarse en el tamaño de hover. JavaFX CSS no soporta transiciones
     * animadas nativas (a diferencia del CSS web), por eso se arma con
     * ScaleTransition en vez de solo pseudo-clases :hover/:pressed.
     */
    public static void animarInteraccion(Node nodo) {
        animarInteraccion(nodo, 1.045, 1.09);
    }

    public static void animarInteraccion(Node nodo, double escalaHover, double escalaClic) {
        ScaleTransition crecer = crearEscala(nodo, 160, escalaHover);
        ScaleTransition encoger = crearEscala(nodo, 160, 1.0);
        ScaleTransition pulso = crearEscala(nodo, 90, escalaClic);
        ScaleTransition asentar = crearEscala(nodo, 140, escalaHover);

        nodo.setOnMouseEntered(e -> {
            detener(encoger, pulso, asentar);
            crecer.playFromStart();
        });
        nodo.setOnMouseExited(e -> {
            detener(crecer, pulso, asentar);
            encoger.playFromStart();
        });
        nodo.setOnMousePressed(e -> {
            detener(crecer, encoger, asentar);
            pulso.playFromStart();
        });
        nodo.setOnMouseReleased(e -> {
            pulso.stop();
            boolean sigueDentro = nodo.contains(e.getX(), e.getY());
            (sigueDentro ? asentar : encoger).playFromStart();
        });
    }

    /** Aplica la animación a todos los botones ya generados de un DialogPane (OK/Cancelar/Aprobar/etc.). */
    public static void animarBotonesDialogo(DialogPane pane) {
        for (ButtonType tipo : pane.getButtonTypes()) {
            Node boton = pane.lookupButton(tipo);
            if (boton != null) animarInteraccion(boton, 1.035, 1.07);
        }
    }

    private static ScaleTransition crearEscala(Node nodo, int milisegundos, double escala) {
        ScaleTransition st = new ScaleTransition(Duration.millis(milisegundos), nodo);
        st.setToX(escala);
        st.setToY(escala);
        st.setInterpolator(Interpolator.EASE_BOTH);
        return st;
    }

    private static void detener(Animation... animaciones) {
        for (Animation a : animaciones) a.stop();
    }
}
