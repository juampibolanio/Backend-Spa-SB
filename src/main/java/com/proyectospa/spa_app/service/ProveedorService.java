package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.model.Proveedor;
import com.proyectospa.spa_app.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public Proveedor guardarProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Optional<Proveedor> buscarPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    public void eliminarProveedor(Integer id) {
        proveedorRepository.deleteById(id);
    }
}
