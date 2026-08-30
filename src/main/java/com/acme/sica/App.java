package com.acme.sica;

import com.acme.sica.controller.*;
import com.acme.sica.decorator.AccesoServiceAuditoriaDecorator;
import com.acme.sica.factory.RepositoryFactory;
import com.acme.sica.model.Usuario;
import com.acme.sica.observer.FuncionarioConsolaObserver;
import com.acme.sica.observer.GestorNotificaciones;
import com.acme.sica.repository.*;
import com.acme.sica.service.*;
import com.acme.sica.view.ConsolaView;

/**
 * Punto de entrada de la aplicación de consola SICA.
 * Actúa como "composition root": aquí, y solo aquí, se instancian las
 * implementaciones concretas (repositorios JDBC vía Factory, servicios,
 * el decorador de auditoría, el observer) y se inyectan por constructor en
 * los controladores. El resto del código nunca hace "new XyzJDBC()" directamente.
 */
public class App {

    public static void main(String[] args) {
        // ---- Repositorios (Factory Method) ----
        UsuarioRepository usuarioRepository = RepositoryFactory.crearUsuarioRepository();
        RolRepository rolRepository = RepositoryFactory.crearRolRepository();
        PersonaRepository personaRepository = RepositoryFactory.crearPersonaRepository();
        EmpresaRepository empresaRepository = RepositoryFactory.crearEmpresaRepository();
        VisitaRepository visitaRepository = RepositoryFactory.crearVisitaRepository();
        IncidenteRepository incidenteRepository = RepositoryFactory.crearIncidenteRepository();
        AuditoriaRepository auditoriaRepository = RepositoryFactory.crearAuditoriaRepository();
        EstadoRepository estadoRepository = RepositoryFactory.crearEstadoRepository();

        // ---- Observer: el funcionario en consola se suscribe a notificaciones ----
        GestorNotificaciones.getInstancia().suscribir(new FuncionarioConsolaObserver());

        // ---- Servicios ----
        AutorizacionService autorizacionService = new AutorizacionService();
        AutenticacionService autenticacionService = new AutenticacionService(usuarioRepository, auditoriaRepository);

        AccesoServiceI accesoServiceBase = new AccesoService(personaRepository, visitaRepository,
                estadoRepository, autorizacionService);
        // ---- Decorator: se envuelve el servicio base con auditoría automática ----
        AccesoServiceI accesoService = new AccesoServiceAuditoriaDecorator(accesoServiceBase, auditoriaRepository);

        PersonaService personaService = new PersonaService(personaRepository, estadoRepository,
                autorizacionService, auditoriaRepository);
        IncidenteService incidenteService = new IncidenteService(incidenteRepository, visitaRepository,
                autorizacionService, auditoriaRepository);
        ReporteService reporteService = new ReporteService(visitaRepository, auditoriaRepository, autorizacionService);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository, rolRepository,
                autorizacionService, auditoriaRepository);
        EmpresaService empresaService = new EmpresaService(empresaRepository, autorizacionService, auditoriaRepository);

        // ---- Vista y controladores ----
        ConsolaView view = new ConsolaView();
        AutenticacionController authController = new AutenticacionController(autenticacionService, view);
        AccesoController accesoController = new AccesoController(accesoService, visitaRepository, view);
        PersonaController personaController = new PersonaController(personaService, empresaRepository, view);
        AdministracionController adminController = new AdministracionController(usuarioService, empresaService,
                incidenteService, view);
        ReporteController reporteController = new ReporteController(reporteService, view);

        // ---- Ciclo principal ----
        view.mostrar("=================================================");
        view.mostrar("  SICA - Sistema Integrado de Control de Acceso");
        view.mostrar("  Complejo Empresarial Zona Acme");
        view.mostrar("=================================================");

        Usuario sesion = null;
        while (sesion == null) {
            sesion = authController.iniciarSesion();
        }

        boolean salir = false;
        while (!salir) {
            mostrarMenu(view, sesion);
            int opcion = view.leerEntero("Opción");
            switch (opcion) {
                case 1 -> accesoController.registrarIngreso(sesion);
                case 2 -> accesoController.registrarSalida(sesion);
                case 3 -> accesoController.gestionarAprobaciones(sesion);
                case 4 -> personaController.registrarPersona(sesion);
                case 5 -> personaController.bloquearPersona(sesion);
                case 6 -> personaController.reactivarPersona(sesion);
                case 7 -> adminController.reportarIncidente(sesion);
                case 8 -> adminController.crearUsuario(sesion);
                case 9 -> adminController.registrarEmpresa(sesion);
                case 10 -> reporteController.mostrarPersonasDentro();
                case 11 -> reporteController.mostrarConteoPorEstado();
                case 12 -> reporteController.mostrarConteoPorEmpresa();
                case 13 -> reporteController.mostrarBitacora(sesion);
                case 0 -> salir = true;
                default -> view.mostrarError("Opción no válida.");
            }
            if (!salir) view.pausa();
        }

        view.mostrar("Sesión finalizada. ¡Hasta luego!");
    }

    private static void mostrarMenu(ConsolaView view, Usuario sesion) {
        view.mostrar("\n===== MENÚ SICA (" + sesion.getNombre() + " - " + sesion.getRol() + ") =====");
        view.mostrar(" 1. Registrar ingreso");
        view.mostrar(" 2. Registrar salida");
        view.mostrar(" 3. Gestionar aprobaciones pendientes");
        view.mostrar(" 4. Registrar persona");
        view.mostrar(" 5. Bloquear persona");
        view.mostrar(" 6. Reactivar persona");
        view.mostrar(" 7. Reportar incidente");
        view.mostrar(" 8. Crear usuario del sistema");
        view.mostrar(" 9. Registrar empresa");
        view.mostrar("10. Reporte: personas dentro del complejo");
        view.mostrar("11. Reporte: conteo de visitas por estado");
        view.mostrar("12. Reporte: conteo de visitas por empresa");
        view.mostrar("13. Ver bitácora de auditoría");
        view.mostrar(" 0. Salir");
    }
}
