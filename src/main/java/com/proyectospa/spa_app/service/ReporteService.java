package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.model.Turno;
import com.proyectospa.spa_app.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private TurnoRepository turnoRepo;

    public List<Map<String, Object>> reportePagosAgrupadoPorProfesional(LocalDate desde, LocalDate hasta) {
        // Filtramos turnos pagados en el rango de fechas
        List<Turno> turnos = turnoRepo.findAll().stream()
                .filter(t -> !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta))
                .filter(Turno::isPagado)
                .collect(Collectors.toList());

        // Agrupamos por profesional
        Map<Integer, List<Turno>> turnosPorProfesional = turnos.stream()
                .collect(Collectors.groupingBy(t -> t.getProfesional().getId()));

        // Construimos la lista con totales por profesional
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Map.Entry<Integer, List<Turno>> entry : turnosPorProfesional.entrySet()) {
            Integer profesionalId = entry.getKey();
            List<Turno> listaTurnos = entry.getValue();

            double totalRecaudado = listaTurnos.stream().mapToDouble(Turno::getMonto).sum();
            int cantidadTurnos = listaTurnos.size();
            String nombreProfesional = listaTurnos.get(0).getProfesional().getNombre();

            Map<String, Object> map = new HashMap<>();
            map.put("profesionalId", profesionalId);
            map.put("profesionalNombre", nombreProfesional);
            map.put("cantidadTurnos", cantidadTurnos);
            map.put("totalRecaudado", totalRecaudado);

            resultado.add(map);
        }

        return resultado;
    }

    public List<Map<String, Object>> reportePagosAgrupadoPorServicio(LocalDate desde, LocalDate hasta) {
    List<Turno> turnos = turnoRepo.findAll().stream()
            .filter(t -> !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta))
            .filter(Turno::isPagado)
            .collect(Collectors.toList());

    Map<Integer, List<Turno>> turnosPorServicio = turnos.stream()
            .collect(Collectors.groupingBy(t -> t.getServicio().getId()));

    List<Map<String, Object>> resultado = new ArrayList<>();

    for (Map.Entry<Integer, List<Turno>> entry : turnosPorServicio.entrySet()) {
        Integer servicioId = entry.getKey();
        List<Turno> listaTurnos = entry.getValue();

        double totalRecaudado = listaTurnos.stream().mapToDouble(Turno::getMonto).sum();
        int cantidadTurnos = listaTurnos.size();
        String nombreServicio = listaTurnos.get(0).getServicio().getNombre();

        Map<String, Object> map = new HashMap<>();
        map.put("servicioId", servicioId);
        map.put("servicioNombre", nombreServicio);
        map.put("cantidadTurnos", cantidadTurnos);
        map.put("totalRecaudado", totalRecaudado);

        resultado.add(map);
    }

    return resultado;
}

}
