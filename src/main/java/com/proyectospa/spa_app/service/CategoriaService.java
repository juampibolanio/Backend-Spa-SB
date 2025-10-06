package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.CategoriaDTO;
import com.proyectospa.spa_app.model.Categoria;
import com.proyectospa.spa_app.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Listar todas las categorías
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Guardar una nueva categoría
    public CategoriaDTO guardar(CategoriaDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        Categoria guardada = categoriaRepository.save(categoria);
        dto.setId(guardada.getId());
        return dto;
    }

    // Eliminar categoría por ID
    public void eliminar(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe la categoría con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    // Método auxiliar para convertir Entity DTO
    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        return dto;
    }
}
