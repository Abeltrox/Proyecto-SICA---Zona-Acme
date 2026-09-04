package com.acme.sica.ui.dialogs;

import com.acme.sica.model.Empresa;
import com.acme.sica.model.Persona;
import com.acme.sica.model.TipoPersona;
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
        documento.setPromptText("Documento de identidad (máx. 10 dígitos)");
        UiUtil.restringirSoloDigitos(documento, 10);
        TextField placa = new TextField();
        placa.setPromptText("Ej. GTL251 (carro) o HGK20H (moto)");
        UiUtil.restringirComoPlaca(placa, 7);

        VBox contenido = new VBox(16,
                UiUtil.campoConEtiqueta("Documento de identidad", documento),
                UiUtil.campoConEtiqueta("Placa de vehículo (opcional)", placa));
        contenido.setPadding(new Insets(24));
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

    /**
     * Ingreso explícito para "Trabajador con carné olvidado" — el guarda elige, de una lista filtrada a
     * trabajadores activos, quién llegó sin su carné. Reutiliza AccesoService.registrarIngreso(), que ya
     * detecta este caso automáticamente (Strategy FlujoCarnetOlvidado) y deja la visita "Pendiente de
     * Aprobación" hasta que el Funcionario de Empresa la apruebe.
     */
    public static void registrarCarnetOlvidado(MainApp app) {
        List<Persona> trabajadores = app.getPersonaRepository().listarTodas().stream()
                .filter(p -> p.getTipoPersona() == TipoPersona.Trabajador && p.puedeIngresar())
                .toList();
        registrarIngresoExplicito(app, "Ingreso por carné olvidado", "Trabajador sin carné", trabajadores,
                "No hay trabajadores activos registrados en el sistema.",
                "Quedará pendiente de aprobación por el Funcionario de Empresa correspondiente.");
    }

    /**
     * Ingreso explícito para "Invitado no anunciado" — el guarda elige, de una lista filtrada a invitados
     * activos, quién llegó sin visita pre-aprobada. Reutiliza AccesoService.registrarIngreso(), que detecta
     * el caso automáticamente (Strategy FlujoInvitadoNoAnunciado) y deja la visita pendiente de aprobación.
     */
    public static void registrarInvitadoNoAnunciado(MainApp app) {
        List<Persona> invitados = app.getPersonaRepository().listarTodas().stream()
                .filter(p -> p.getTipoPersona() == TipoPersona.Invitado && p.puedeIngresar())
                .toList();
        registrarIngresoExplicito(app, "Ingreso — invitado no anunciado", "Invitado", invitados,
                "No hay invitados activos registrados en el sistema.",
                "Quedará pendiente de aprobación por el Funcionario de Empresa que visita.");
    }

    private static void registrarIngresoExplicito(MainApp app, String titulo, String etiquetaCampo,
                                                    List<Persona> candidatas, String mensajeVacio, String nota) {
        if (candidatas.isEmpty()) {
            UiUtil.mostrarExito(titulo, mensajeVacio);
            return;
        }

        Dialog<Void> dialog = DialogoBase.crear(titulo);

        ComboBox<Persona> persona = new ComboBox<>();
        persona.getItems().addAll(candidatas);
        persona.setPromptText("Selecciona una persona");
        persona.setMaxWidth(Double.MAX_VALUE);

        TextField placa = new TextField();
        placa.setPromptText("Ej. GTL251 (carro) o HGK20H (moto)");
        UiUtil.restringirComoPlaca(placa, 7);

        VBox contenido = new VBox(16,
                UiUtil.campoConEtiqueta(etiquetaCampo, persona),
                UiUtil.campoConEtiqueta("Placa de vehículo (opcional)", placa),
                new Label(nota));
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Registrar", () -> {
            Persona seleccionada = persona.getValue();
            if (seleccionada == null) throw new IllegalArgumentException("Selecciona una persona de la lista.");
            Visita visita = app.getAccesoService().registrarIngreso(seleccionada.getDocumentoIdentidad(),
                    app.getSesionActual(), placa.getText().isBlank() ? null : placa.getText().trim());
            UiUtil.mostrarExito("Ingreso procesado", visita.getPersona().getNombre()
                    + " → estado: " + visita.getEstadoVisita());
        });

        dialog.showAndWait();
    }

    /** El operador elige de una lista quién sale (todas las visitas actualmente "Dentro"), sin escribir ningún ID. */
    public static void registrarSalida(MainApp app) {
        List<Visita> abiertas = app.getVisitaRepository().listarTodas().stream()
                .filter(Visita::estaAbierta)
                .toList();

        if (abiertas.isEmpty()) {
            UiUtil.mostrarExito("Nadie dentro", "En este momento no hay personas registradas dentro del complejo.");
            return;
        }

        Dialog<Void> dialog = DialogoBase.crear("Registrar salida");

        ComboBox<Visita> visita = new ComboBox<>();
        visita.getItems().addAll(abiertas);
        visita.setPromptText("Selecciona quién sale");
        visita.setMaxWidth(Double.MAX_VALUE);
        visita.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Visita v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getPersona().getNombre() + " — entró " + v.getFechaEntrada());
            }
        });
        visita.setButtonCell(visita.getCellFactory().call(null));

        VBox contenido = new VBox(16, UiUtil.campoConEtiqueta("Persona que sale", visita));
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Registrar salida", () -> {
            Visita seleccionada = visita.getValue();
            if (seleccionada == null) throw new IllegalArgumentException("Selecciona una persona de la lista.");
            Visita v = app.getAccesoService().registrarSalida(seleccionada.getId(), app.getSesionActual());
            UiUtil.mostrarExito("Salida registrada", v.getPersona().getNombre() + " ha salido del complejo.");
        });

        dialog.showAndWait();
    }

    /** El operador elige la empresa de un desplegable (no escribe el ID) y luego elige la visita de una lista. */
    public static void gestionarAprobaciones(MainApp app) {
        gestionarAprobaciones(app, false);
    }

    /**
     * Igual que {@link #gestionarAprobaciones(MainApp)}, pero solo muestra visitas pendientes de personas
     * tipo Invitado (deja fuera a los trabajadores con carné olvidado). Es el botón extra pedido para que
     * el Superusuario (y cualquier rol con permiso 'aprobar_visita') pueda aprobar invitados específicamente.
     */
    public static void gestionarAprobacionesInvitados(MainApp app) {
        gestionarAprobaciones(app, true);
    }

    private static void gestionarAprobaciones(MainApp app, boolean soloInvitados) {
        List<Empresa> empresas = app.getEmpresaRepository().listarTodas();
        if (empresas.isEmpty()) {
            UiUtil.mostrarExito("Sin empresas", "Todavía no hay empresas registradas en el sistema.");
            return;
        }

        ComboBox<Empresa> empresaCombo = new ComboBox<>();
        empresaCombo.getItems().addAll(empresas);
        empresaCombo.setPromptText("Selecciona una empresa");
        empresaCombo.setMaxWidth(Double.MAX_VALUE);

        Dialog<Empresa> seleccion = DialogoBase.crear(soloInvitados ? "Aprobaciones pendientes — invitados" : "Aprobaciones pendientes");
        VBox contenidoSeleccion = new VBox(16, UiUtil.campoConEtiqueta("Empresa", empresaCombo));
        contenidoSeleccion.setPadding(new Insets(24));
        seleccion.getDialogPane().setContent(contenidoSeleccion);
        seleccion.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        UiUtil.animarBotonesDialogo(seleccion.getDialogPane());
        seleccion.setResultConverter(boton -> boton == ButtonType.OK ? empresaCombo.getValue() : null);

        Optional<Empresa> empresaElegida = seleccion.showAndWait();
        if (empresaElegida.isEmpty()) return;

        List<Visita> pendientes = app.getVisitaRepository().listarPendientesPorFuncionario(empresaElegida.get().getId());
        if (soloInvitados) {
            pendientes = pendientes.stream()
                    .filter(v -> v.getPersona().getTipoPersona() == TipoPersona.Invitado)
                    .toList();
        }
        if (pendientes.isEmpty()) {
            UiUtil.mostrarExito("Sin pendientes",
                    (soloInvitados ? "No hay invitados pendientes de aprobación para " : "No hay visitas pendientes de aprobación para ")
                            + empresaElegida.get().getNombre() + ".");
            return;
        }

        ListView<Visita> lista = new ListView<>();
        lista.getItems().addAll(pendientes);
        lista.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Visita v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getPersona()
                        + " — llegó " + v.getFechaEntrada());
            }
        });
        lista.setPrefHeight(240);

        Dialog<Void> dialog = DialogoBase.crear("Pendientes — " + empresaElegida.get().getNombre());
        VBox contenido = new VBox(14, new Label("Selecciona una visita y decide:"), lista);
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);

        ButtonType aprobarTipo = new ButtonType("Aprobar");
        ButtonType rechazarTipo = new ButtonType("Rechazar");
        dialog.getDialogPane().getButtonTypes().addAll(aprobarTipo, rechazarTipo, ButtonType.CANCEL);
        UiUtil.animarBotonesDialogo(dialog.getDialogPane());

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
