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
    @DisplayName("Lista todos los estados de disponibilidad")
    void listarTodos() {
        estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO).descripcion("En uso").build());
        estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.RESERVADO).descripcion("Reservado").build());

        List<EstadoDisponibilidad> estados = estadoDisponibilidadRepository.findAll();

        assertEquals(2, estados.size());
    }

    @Test
    @DisplayName("Busca el estado de disponibilidad por recurso")
    void buscarPorDisponibilidadRecurso() {
        estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.BLOQUEO_ACADEMICO).descripcion("Bloqueo academico").build());

        Optional<EstadoDisponibilidad> found = estadoDisponibilidadRepository.findByDisponibilidadRecurso(EstadoDisponibilidadRecurso.BLOQUEO_ACADEMICO);

        assertTrue(found.isPresent());
        assertEquals(EstadoDisponibilidadRecurso.BLOQUEO_ACADEMICO, found.get().getDisponibilidadRecurso());
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
