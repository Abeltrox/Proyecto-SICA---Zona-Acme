package com.acme.sica.service;

import com.acme.sica.model.Empresa;
import com.acme.sica.model.RegistroAuditoria;
import com.acme.sica.model.Usuario;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.repository.EmpresaRepository;

import java.util.List;

public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final AutorizacionService autorizacionService;
    private final AuditoriaRepository auditoriaRepository;

    public EmpresaService(EmpresaRepository empresaRepository, AutorizacionService autorizacionService,
                           AuditoriaRepository auditoriaRepository) {
        this.empresaRepository = empresaRepository;
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
}
