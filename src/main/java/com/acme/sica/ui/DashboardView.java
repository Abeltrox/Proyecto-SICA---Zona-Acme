package com.acme.sica.ui;

import com.acme.sica.model.Usuario;
import com.acme.sica.ui.dialogs.AdminDialogs;
import com.acme.sica.ui.dialogs.IngresoDialogs;
import com.acme.sica.ui.dialogs.PersonaDialogs;
import com.acme.sica.ui.dialogs.ReporteDialogs;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard principal: barra superior con la sesión activa, y una grilla de
 * tarjetas tipo "cards" (similar a un panel de mando) con las opciones
 * disponibles. Cada tarjeta solo aparece si el rol del usuario tiene el
 * permiso RBAC correspondiente — el dashboard nunca muestra una opción que
 * el backend igual rechazaría.
 */
public class DashboardView {

    private final MainApp app;
    private final Usuario sesion;

    public DashboardView(MainApp app) {
        this.app = app;
        this.sesion = app.getSesionActual();
    }

    public Parent construir() {
        BorderPane raiz = new BorderPane();
        raiz.getStyleClass().add("dashboard-fondo");
        raiz.setTop(construirBarraSuperior());
        raiz.setCenter(construirGrillaOpciones());
        return raiz;
    }

    private Parent construirBarraSuperior() {
        Label titulo = new Label("SICA · Zona Acme");
        titulo.getStyleClass().add("titulo-app");

        Label subtitulo = new Label(sesion.getNombre() + " — " + sesion.getRol());
        subtitulo.getStyleClass().add("subtitulo-app");

        VBox textos = new VBox(2, titulo, subtitulo);

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        var botonSalir = UiUtil.botonSecundario("Cerrar sesión");
        botonSalir.setStyle("-fx-border-color: white; -fx-text-fill: white;");
        botonSalir.setOnAction(e -> app.cerrarSesion());

        HBox barra = new HBox(16, textos, espaciador, botonSalir);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.getStyleClass().add("barra-superior");
        return barra;
    }

    private Parent construirGrillaOpciones() {
        FlowPane grilla = new FlowPane();
        grilla.setHgap(18);
        grilla.setVgap(18);
        grilla.setPadding(new Insets(28));
        grilla.setPrefWrapLength(1000);

        for (Tarjeta t : opcionesDisponibles()) {
            grilla.getChildren().add(crearTarjeta(t));
        }

        ScrollPane scroll = new ScrollPane(grilla);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        return scroll;
    }

    private List<Tarjeta> opcionesDisponibles() {
        var auth = app.getAutorizacionService();
        List<Tarjeta> tarjetas = new ArrayList<>();

        if (auth.tienePermiso(sesion, "registrar_visita"))
            tarjetas.add(new Tarjeta("🚪", "Registrar ingreso", () -> IngresoDialogs.registrarIngreso(app)));
        if (auth.tienePermiso(sesion, "registrar_salida"))
            tarjetas.add(new Tarjeta("🚶", "Registrar salida", () -> IngresoDialogs.registrarSalida(app)));
        if (auth.tienePermiso(sesion, "aprobar_visita"))
            tarjetas.add(new Tarjeta("✅", "Aprobaciones pendientes", () -> IngresoDialogs.gestionarAprobaciones(app)));
        if (auth.tienePermiso(sesion, "crear_persona"))
            tarjetas.add(new Tarjeta("🧍", "Registrar persona", () -> PersonaDialogs.registrarPersona(app)));
        if (auth.tienePermiso(sesion, "bloquear_persona")) {
            tarjetas.add(new Tarjeta("🚫", "Bloquear persona", () -> PersonaDialogs.bloquearPersona(app)));
            tarjetas.add(new Tarjeta("🔓", "Reactivar persona", () -> PersonaDialogs.reactivarPersona(app)));
        }
        if (auth.tienePermiso(sesion, "registrar_incidente"))
            tarjetas.add(new Tarjeta("⚠️", "Reportar incidente", () -> AdminDialogs.reportarIncidente(app)));
        if (auth.tienePermiso(sesion, "crear_usuario"))
            tarjetas.add(new Tarjeta("👤", "Crear usuario del sistema", () -> AdminDialogs.crearUsuario(app)));
        if (auth.tienePermiso(sesion, "gestionar_empresas"))
            tarjetas.add(new Tarjeta("🏢", "Registrar empresa", () -> AdminDialogs.registrarEmpresa(app)));

        // Reportes de solo lectura: visibles para cualquier sesión activa.
        tarjetas.add(new Tarjeta("📋", "Personas dentro del complejo", () -> ReporteDialogs.personasDentro(app)));
        tarjetas.add(new Tarjeta("📊", "Visitas por estado", () -> ReporteDialogs.conteoPorEstado(app)));
        tarjetas.add(new Tarjeta("🏭", "Visitas por empresa", () -> ReporteDialogs.conteoPorEmpresa(app)));
        if (auth.tienePermiso(sesion, "generar_reporte_auditoria"))
            tarjetas.add(new Tarjeta("🗂️", "Bitácora de auditoría", () -> ReporteDialogs.bitacora(app)));

        return tarjetas;
    }

    private VBox crearTarjeta(Tarjeta t) {
        Label icono = new Label(t.icono);
        icono.getStyleClass().add("tarjeta-icono");
        Label titulo = new Label(t.titulo);
        titulo.getStyleClass().add("tarjeta-titulo");
        titulo.setWrapText(true);
        titulo.setMaxWidth(140);

        VBox caja = new VBox(10, icono, titulo);
        caja.setAlignment(Pos.CENTER);
        caja.setPrefSize(160, 120);
        caja.getStyleClass().add("tarjeta-boton");
        caja.setOnMouseClicked(e -> t.accion.ejecutar());
        return caja;
    }

    /** Representa una opción del dashboard: icono, título y la acción a ejecutar al hacer clic. */
    private record Tarjeta(String icono, String titulo, Accion accion) {}

    @FunctionalInterface
    private interface Accion {
        void ejecutar();
    }
}
