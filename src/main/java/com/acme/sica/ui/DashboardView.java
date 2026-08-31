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
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard principal: barra superior con la sesión activa, y una grilla de
 * tarjetas tipo "cards" (similar a un panel de mando) con las opciones
 * disponibles. La grilla usa columnas y filas en porcentaje (GridPane), así
 * las tarjetas siempre se reparten TODO el espacio disponible de la ventana
 * —grandes en una pantalla completa/maximizada— sin desbordarse nunca, sin
 * importar cuántas tarjetas tenga el rol de la sesión. Cada tarjeta solo
 * aparece si el rol del usuario tiene el permiso RBAC correspondiente — el
 * dashboard nunca muestra una opción que el backend igual rechazaría.
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
        List<Tarjeta> tarjetas = opcionesDisponibles();
        int total = Math.max(1, tarjetas.size());

        // Columnas/filas calculadas según cuántas opciones tenga el rol, para
        // que la grilla siempre reparta el 100% del ancho y alto disponibles
        // (nunca scroll, nunca desborde) y las tarjetas queden lo más grandes
        // posible en pantallas anchas como 1920x1080.
        int columnas = total <= 3 ? total : (total <= 8 ? 4 : 5);
        int filas = (int) Math.ceil((double) total / columnas);

        GridPane grilla = new GridPane();
        grilla.setHgap(26);
        grilla.setVgap(26);
        grilla.setPadding(new Insets(34));

        for (int c = 0; c < columnas; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / columnas);
            cc.setHgrow(Priority.ALWAYS);
            grilla.getColumnConstraints().add(cc);
        }
        for (int f = 0; f < filas; f++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / filas);
            rc.setVgrow(Priority.ALWAYS);
            grilla.getRowConstraints().add(rc);
        }

        for (int i = 0; i < tarjetas.size(); i++) {
            VBox caja = crearTarjeta(tarjetas.get(i));
            grilla.add(caja, i % columnas, i / columnas);
        }

        grilla.setMaxWidth(Double.MAX_VALUE);
        grilla.setMaxHeight(Double.MAX_VALUE);
        return grilla;
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
        titulo.setMaxWidth(260);

        VBox caja = new VBox(16, icono, titulo);
        caja.setAlignment(Pos.CENTER);
        caja.setMaxWidth(Double.MAX_VALUE);
        caja.setMaxHeight(Double.MAX_VALUE);
        caja.getStyleClass().add("tarjeta-boton");
        caja.setOnMouseClicked(e -> t.accion.ejecutar());
        // Animación al pasar el cursor (crece suavemente) y al hacer clic
        // (sobresale con un pequeño pulso antes de asentarse).
        UiUtil.animarInteraccion(caja, 1.05, 1.11);
        return caja;
    }

    /** Representa una opción del dashboard: icono, título y la acción a ejecutar al hacer clic. */
    private record Tarjeta(String icono, String titulo, Accion accion) {}

    @FunctionalInterface
    private interface Accion {
        void ejecutar();
    }
}
