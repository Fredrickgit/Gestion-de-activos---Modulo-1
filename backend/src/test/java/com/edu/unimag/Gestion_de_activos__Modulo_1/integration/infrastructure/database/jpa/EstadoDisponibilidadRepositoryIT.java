package com.edu.unimag.Gestion_de_activos__Modulo_1.integration.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class EstadoDisponibilidadRepositoryIT {

    @Autowired
    private EstadoDisponibilidadRepository estadoDisponibilidadRepository;

    @Test
    @DisplayName("Guarda y recupera un estado de disponibilidad")
    void guardarYRecuperarPorId() {
        EstadoDisponibilidad estado = estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE).descripcion("Disponible").build());

        Optional<EstadoDisponibilidad> found = estadoDisponibilidadRepository.findById(estado.getId());

        assertTrue(found.isPresent());
        assertEquals(EstadoDisponibilidadRecurso.DISPONIBLE, found.get().getDisponibilidadRecurso());
        assertEquals("Disponible", found.get().getDescripcion());
    }

    @Test
    @DisplayName("Solo expone los tres estados operativos")
    void listarTodos() {
        List<EstadoDisponibilidad> estados = estadoDisponibilidadRepository.findAll();

        assertEquals(3, estados.size());
        assertTrue(estados.stream().anyMatch(estado -> estado.getDisponibilidadRecurso() == EstadoDisponibilidadRecurso.DISPONIBLE));
        assertTrue(estados.stream().anyMatch(estado -> estado.getDisponibilidadRecurso() == EstadoDisponibilidadRecurso.EN_USO));
        assertTrue(estados.stream().anyMatch(estado -> estado.getDisponibilidadRecurso() == EstadoDisponibilidadRecurso.EN_MANTENIMIENTO));
    }

    @Test
    @DisplayName("Busca los estados operativos por su valor")
    void buscarPorDisponibilidadRecurso() {
        for (EstadoDisponibilidadRecurso valor : EstadoDisponibilidadRecurso.values()) {
            Optional<EstadoDisponibilidad> found = estadoDisponibilidadRepository.findByDisponibilidadRecurso(valor);
            assertTrue(found.isPresent());
            assertEquals(valor, found.get().getDisponibilidadRecurso());
        }
    }

    @Test
    @DisplayName("Elimina un estado de disponibilidad")
    void eliminarEstadoDisponibilidad() {
        EstadoDisponibilidad estado = estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE).descripcion("Disponible").build());

        estadoDisponibilidadRepository.deleteById(estado.getId());

        assertFalse(estadoDisponibilidadRepository.existsById(estado.getId()));
    }
}
