package com.acme.sica.ui;

import com.acme.sica.decorator.AccesoServiceAuditoriaDecorator;
import com.acme.sica.factory.RepositoryFactory;
import com.acme.sica.model.Usuario;
import com.acme.sica.observer.FuncionarioConsolaObserver;
import com.acme.sica.observer.GestorNotificaciones;
import com.acme.sica.repository.*;
import com.acme.sica.service.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicación gráfica (JavaFX). Cumple el mismo rol de
 * "composition root" que App.java (versión consola): arma todos los
 * repositorios (Factory Method), envuelve AccesoService con el decorador de
 * auditoría, y expone los servicios a las pantallas. Las reglas de negocio,
 * el RBAC, los patrones de diseño y la capa de persistencia son EXACTAMENTE
 * los mismos que en la versión de consola — lo único que cambia es la capa
 * de presentación (View/Controller de la arquitectura MVC).
 */
public class MainApp extends Application {

    private Stage stage;
    private Usuario sesionActual;

    // ---- Repositorios ----
    private UsuarioRepository usuarioRepository;
    private RolRepository rolRepository;
    private PersonaRepository personaRepository;
    private EmpresaRepository empresaRepository;
    private VisitaRepository visitaRepository;
    private IncidenteRepository incidenteRepository;
    private AuditoriaRepository auditoriaRepository;
    private EstadoRepository estadoRepository;

    // ---- Servicios ----
    private AutorizacionService autorizacionService;
    private AutenticacionService autenticacionService;
    private AccesoServiceI accesoService;
    private PersonaService personaService;
    private IncidenteService incidenteService;
    private ReporteService reporteService;
    private UsuarioService usuarioService;
    private EmpresaService empresaService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        inicializarRepositoriosYServicios();

        stage.setTitle("SICA - Zona Acme");
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo_zona_acme.png")));
        mostrarLogin();
        // Arranca maximizada para aprovechar toda la pantalla (p. ej. 1920x1080):
        // así las tarjetas del dashboard quedan lo más grandes posible sin desbordar.
        stage.setMaximized(true);
        stage.show();
    }

    private void inicializarRepositoriosYServicios() {
        usuarioRepository = RepositoryFactory.crearUsuarioRepository();
        rolRepository = RepositoryFactory.crearRolRepository();
        personaRepository = RepositoryFactory.crearPersonaRepository();
        empresaRepository = RepositoryFactory.crearEmpresaRepository();
        visitaRepository = RepositoryFactory.crearVisitaRepository();
        incidenteRepository = RepositoryFactory.crearIncidenteRepository();
        auditoriaRepository = RepositoryFactory.crearAuditoriaRepository();
        estadoRepository = RepositoryFactory.crearEstadoRepository();

        GestorNotificaciones.getInstancia().suscribir(new FuncionarioConsolaObserver());
        GestorNotificaciones.getInstancia().suscribir(NotificacionToast.getInstancia());

        autorizacionService = new AutorizacionService();
        autenticacionService = new AutenticacionService(usuarioRepository, auditoriaRepository);

        AccesoServiceI accesoServiceBase = new AccesoService(personaRepository, visitaRepository,
                estadoRepository, autorizacionService);
        accesoService = new AccesoServiceAuditoriaDecorator(accesoServiceBase, auditoriaRepository);

        personaService = new PersonaService(personaRepository, estadoRepository, autorizacionService, auditoriaRepository);
        incidenteService = new IncidenteService(incidenteRepository, visitaRepository, autorizacionService, auditoriaRepository);
        reporteService = new ReporteService(visitaRepository, auditoriaRepository, autorizacionService);
        usuarioService = new UsuarioService(usuarioRepository, rolRepository, autorizacionService, auditoriaRepository);
        empresaService = new EmpresaService(empresaRepository, autorizacionService, auditoriaRepository);
    }

    // ---------------- Navegación ----------------

    public void mostrarLogin() {
        sesionActual = null;
        cambiarEscena(new LoginView(this).construir(), "SICA - Iniciar sesión");
    }

    public void iniciarSesionExitosa(Usuario usuario) {
        this.sesionActual = usuario;
        mostrarDashboard();
    }

    public void mostrarDashboard() {
        cambiarEscena(new DashboardView(this).construir(), "SICA - " + sesionActual.getNombre());
    }

    public void cerrarSesion() {
        mostrarLogin();
    }

    private void cambiarEscena(Parent raiz, String titulo) {
        Scene escena = new Scene(raiz, 1100, 720);
        escena.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        stage.setTitle(titulo);
        stage.setScene(escena);
    }

    // ---------------- Getters usados por las pantallas ----------------

    public Usuario getSesionActual() { return sesionActual; }
    public Stage getStage() { return stage; }

    public UsuarioRepository getUsuarioRepository() { return usuarioRepository; }
    public PersonaRepository getPersonaRepository() { return personaRepository; }
    public EmpresaRepository getEmpresaRepository() { return empresaRepository; }
    public VisitaRepository getVisitaRepository() { return visitaRepository; }

    public AutenticacionService getAutenticacionService() { return autenticacionService; }
    public AutorizacionService getAutorizacionService() { return autorizacionService; }
    public AccesoServiceI getAccesoService() { return accesoService; }
    public PersonaService getPersonaService() { return personaService; }
    public IncidenteService getIncidenteService() { return incidenteService; }
    public ReporteService getReporteService() { return reporteService; }
    public UsuarioService getUsuarioService() { return usuarioService; }
    public EmpresaService getEmpresaService() { return empresaService; }
}
