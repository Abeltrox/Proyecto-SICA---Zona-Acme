package com.acme.sica.service;

import com.acme.sica.exception.EntidadNoEncontradaException;
import com.acme.sica.model.Empresa;
import com.acme.sica.model.RegistroAuditoria;
import com.acme.sica.model.Usuario;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.repository.EmpresaRepository;
import com.acme.sica.repository.PersonaRepository;

import java.util.List;

public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PersonaRepository personaRepository;
    private final AutorizacionService autorizacionService;
    private final AuditoriaRepository auditoriaRepository;

    public EmpresaService(EmpresaRepository empresaRepository, PersonaRepository personaRepository,
                           AutorizacionService autorizacionService, AuditoriaRepository auditoriaRepository) {
        this.empresaRepository = empresaRepository;
        this.personaRepository = personaRepository;
        this.autorizacionService = autorizacionService;
        this.auditoriaRepository = auditoriaRepository;
    }

    public Empresa registrarEmpresa(Usuario operador, String nombre, String contacto) {
        autorizacionService.verificarPermiso(operador, "gestionar_empresas");

        Empresa empresa = new Empresa();
        empresa.setNombre(nombre);
        empresa.setContactoPrincipal(contacto);
        empresaRepository.guardar(empresa);

        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "CREACION_EMPRESA", "empresas",
                empresa.getId(), "Empresa registrada: " + nombre));
        return empresa;
    }

    public List<Empresa> listarEmpresas() {
        return empresaRepository.listarTodas();
    }

    /**
     * Elimina una empresa. Antes de tocar la base de datos, valida en la capa
     * de negocio que ninguna persona (trabajador) siga asociada a ella: es
     * una regla de integridad más clara para el usuario que dejar que salte
     * la excepción cruda de la restricción de llave foránea.
     */
    public void eliminarEmpresa(Usuario operador, int empresaId) {
        autorizacionService.verificarPermiso(operador, "gestionar_empresas");

        Empresa empresa = empresaRepository.buscarPorId(empresaId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empresa", String.valueOf(empresaId)));

        long personasAsociadas = personaRepository.listarTodas().stream()
                .filter(p -> p.getEmpresa() != null && p.getEmpresa().getId() == empresaId)
                .count();
        if (personasAsociadas > 0) {
            throw new IllegalStateException("No se puede eliminar " + empresa.getNombre() + ": tiene "
                    + personasAsociadas + " persona(s) registrada(s) asociada(s). Reasígnalas o elimínalas primero.");
        }

        empresaRepository.eliminar(empresaId);
        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "ELIMINACION_EMPRESA", "empresas",
                empresaId, "Empresa eliminada: " + empresa.getNombre()));
    }
}
