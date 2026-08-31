package com.acme.sica.ui.dialogs;

import com.acme.sica.model.Empresa;
import com.acme.sica.model.Rol;
import com.acme.sica.model.Usuario;
import com.acme.sica.model.Visita;
import com.acme.sica.ui.MainApp;
import com.acme.sica.ui.UiUtil;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public final class AdminDialogs {

    private AdminDialogs() {}

    public static void crearUsuario(MainApp app) {
        Dialog<Void> dialog = DialogoBase.crear("Crear usuario del sistema");

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre completo");
        TextField email = new TextField();
        email.setPromptText("correo@acme.com");
        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña temporal");

        ComboBox<Rol> rol = new ComboBox<>();
        // Catálogo fijo de roles del sistema (coincide con data.sql):
        rol.getItems().setAll(new Rol(1, "Superusuario"), new Rol(2, "Supervisor de Seguridad"),
                new Rol(3, "Guarda de Seguridad"), new Rol(4, "Funcionario de Empresa"));
        rol.setValue(rol.getItems().get(2));
        rol.setMaxWidth(Double.MAX_VALUE);

        VBox contenido = new VBox(16,
                UiUtil.campoConEtiqueta("Nombre completo", nombre),
                UiUtil.campoConEtiqueta("Correo electrónico", email),
                UiUtil.campoConEtiqueta("Contraseña temporal", password),
                UiUtil.campoConEtiqueta("Rol", rol));
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Crear usuario", () -> {
            app.getUsuarioService().crearUsuario(app.getSesionActual(), nombre.getText().trim(),
                    email.getText().trim(), password.getText(), rol.getValue().getId());
            UiUtil.mostrarExito("Usuario creado", email.getText().trim() + " ya puede iniciar sesión.");
        });

        dialog.showAndWait();
    }

    public static void registrarEmpresa(MainApp app) {
        Dialog<Void> dialog = DialogoBase.crear("Registrar empresa");

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre de la empresa");
        TextField contacto = new TextField();
        contacto.setPromptText("Contacto principal");

        VBox contenido = new VBox(16,
                UiUtil.campoConEtiqueta("Nombre de la empresa", nombre),
                UiUtil.campoConEtiqueta("Contacto principal", contacto));
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Registrar", () -> {
            Empresa empresa = app.getEmpresaService().registrarEmpresa(app.getSesionActual(),
                    nombre.getText().trim(), contacto.getText().trim());
            UiUtil.mostrarExito("Empresa registrada", empresa.toString());
        });

        dialog.showAndWait();
    }

    /** La visita relacionada (opcional) se elige de un desplegable en vez de escribir su ID. */
    public static void reportarIncidente(MainApp app) {
        Dialog<Void> dialog = DialogoBase.crear("Reportar incidente");

        List<Visita> visitas = app.getVisitaRepository().listarTodas();
        ComboBox<Visita> visita = new ComboBox<>();
        visita.getItems().addAll(visitas);
        visita.setPromptText("Ninguna (opcional)");
        visita.setMaxWidth(Double.MAX_VALUE);
        visita.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Visita v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getPersona().getNombre() + " — " + v.getEstadoVisita());
            }
        });
        visita.setButtonCell(visita.getCellFactory().call(null));

        TextArea descripcion = new TextArea();
        descripcion.setPromptText("Describe lo ocurrido...");
        descripcion.setPrefRowCount(4);

        VBox contenido = new VBox(16,
                UiUtil.campoConEtiqueta("Visita relacionada (opcional)", visita),
                UiUtil.campoConEtiqueta("Descripción del incidente", descripcion));
        contenido.setPadding(new Insets(24));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Reportar", () -> {
            Integer id = visita.getValue() == null ? null : visita.getValue().getId();
            app.getIncidenteService().reportarIncidente(app.getSesionActual(), id, descripcion.getText().trim());
            UiUtil.mostrarExito("Incidente registrado", "El incidente quedó registrado en la bitácora.");
        });

        dialog.showAndWait();
    }
}
