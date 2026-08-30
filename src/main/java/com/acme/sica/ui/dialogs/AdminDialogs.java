package com.acme.sica.ui.dialogs;

import com.acme.sica.model.Empresa;
import com.acme.sica.model.Rol;
import com.acme.sica.model.Usuario;
import com.acme.sica.ui.MainApp;
import com.acme.sica.ui.UiUtil;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

        VBox contenido = new VBox(12,
                UiUtil.campoConEtiqueta("Nombre completo", nombre),
                UiUtil.campoConEtiqueta("Correo electrónico", email),
                UiUtil.campoConEtiqueta("Contraseña temporal", password),
                UiUtil.campoConEtiqueta("Rol", rol));
        contenido.setPadding(new Insets(6));
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

        VBox contenido = new VBox(12,
                UiUtil.campoConEtiqueta("Nombre de la empresa", nombre),
                UiUtil.campoConEtiqueta("Contacto principal", contacto));
        contenido.setPadding(new Insets(6));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Registrar", () -> {
            Empresa empresa = app.getEmpresaService().registrarEmpresa(app.getSesionActual(),
                    nombre.getText().trim(), contacto.getText().trim());
            UiUtil.mostrarExito("Empresa registrada", empresa.toString());
        });

        dialog.showAndWait();
    }

    public static void reportarIncidente(MainApp app) {
        Dialog<Void> dialog = DialogoBase.crear("Reportar incidente");

        TextField visitaId = new TextField();
        visitaId.setPromptText("ID de visita relacionada (opcional)");
        TextArea descripcion = new TextArea();
        descripcion.setPromptText("Describe lo ocurrido...");
        descripcion.setPrefRowCount(4);

        VBox contenido = new VBox(12,
                UiUtil.campoConEtiqueta("ID de visita relacionada (opcional)", visitaId),
                UiUtil.campoConEtiqueta("Descripción del incidente", descripcion));
        contenido.setPadding(new Insets(6));
        dialog.getDialogPane().setContent(contenido);

        DialogoBase.agregarBotones(dialog, "Reportar", () -> {
            Integer id = visitaId.getText().isBlank() ? null : Integer.parseInt(visitaId.getText().trim());
            app.getIncidenteService().reportarIncidente(app.getSesionActual(), id, descripcion.getText().trim());
            UiUtil.mostrarExito("Incidente registrado", "El incidente quedó registrado en la bitácora.");
        });

        dialog.showAndWait();
    }
}
