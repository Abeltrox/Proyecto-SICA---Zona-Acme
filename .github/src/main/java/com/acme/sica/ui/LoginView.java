package com.acme.sica.ui;

import com.acme.sica.exception.AccesoDenegadoException;
import com.acme.sica.model.Usuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Pantalla de inicio de sesión: tarjeta blanca centrada sobre fondo verde muy suave. */
public class LoginView {

    private final MainApp app;

    public LoginView(MainApp app) {
        this.app = app;
    }

    public Parent construir() {
        ImageView logo = new ImageView(new Image(
                getClass().getResourceAsStream("/images/logo_zona_acme.png")));
        logo.setFitWidth(420);
        logo.setPreserveRatio(true);
        StackPane logoCentrado = new StackPane(logo);
        logoCentrado.setMaxWidth(Double.MAX_VALUE);

        Label subtitulo = new Label("Sistema Integrado de Control de Acceso · Zona Acme");
        subtitulo.getStyleClass().add("login-subtitulo");

        TextField email = new TextField();
        email.setPromptText("correo@acme.com");
        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña");

        Label mensajeError = new Label();
        mensajeError.setStyle("-fx-text-fill: #D96C6C; -fx-font-size: 12px;");
        mensajeError.setVisible(false);

        var botonIngresar = UiUtil.botonPrimario("Iniciar sesión");
        botonIngresar.setMaxWidth(Double.MAX_VALUE);
        botonIngresar.setOnAction(e -> intentarLogin(email.getText(), password.getText(), mensajeError));
        password.setOnAction(e -> intentarLogin(email.getText(), password.getText(), mensajeError));

        VBox formulario = new VBox(14,
                logoCentrado, subtitulo,
                new javafx.scene.layout.Region() {{ setPrefHeight(10); }},
                UiUtil.campoConEtiqueta("Correo electrónico", email),
                UiUtil.campoConEtiqueta("Contraseña", password),
                mensajeError,
                botonIngresar
        );
        formulario.setAlignment(Pos.CENTER_LEFT);
        formulario.setPadding(new Insets(40));
        formulario.setMaxWidth(380);
        formulario.getStyleClass().add("panel-blanco");

        StackPane contenedor = new StackPane(formulario);
        contenedor.getStyleClass().addAll("pantalla", "pantalla-login");
        contenedor.setPadding(new Insets(40));
        return contenedor;
    }

    private void intentarLogin(String email, String password, Label mensajeError) {
        mensajeError.setVisible(false);
        try {
            Usuario usuario = app.getAutenticacionService().login(email, password);
            app.iniciarSesionExitosa(usuario);
        } catch (AccesoDenegadoException e) {
            mensajeError.setText(e.getMessage());
            mensajeError.setVisible(true);
        } catch (RuntimeException e) {
            mensajeError.setText("Error de conexión con la base de datos: " + e.getMessage());
            mensajeError.setVisible(true);
        }
    }
}
