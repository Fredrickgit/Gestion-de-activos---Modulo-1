package com.edu.unimag.Gestion_de_activos__Modulo_1.integration.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.TestcontainersConfiguration;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class EspacioRepositoryIT {

    @Autowired
    private EspacioRepository espacioRepository;

    @Autowired
    private FacultadRepository facultadRepository;

    @Autowired
    private UbicacionUniversitariaRepository ubicacionRepository;

    @Autowired
    private EstadoDisponibilidadRepository estadoDisponibilidadRepository;

    @Test
    @DisplayName("Guarda y recupera un espacio por identificador")
    void guardarYRecuperarPorId() {
        Facultad facultad = facultadRepository.save(Facultad.builder().nombre("Facultad QA Espacio").build());
        UbicacionUniversitaria ubicacion = ubicacionRepository.save(UbicacionUniversitaria.builder().nombre("Ubicacion QA Espacio").build());
        EstadoDisponibilidad disponibilidad = estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE).build());

        Espacio espacio = espacioRepository.save(Espacio.builder()
                .nombre("Sala QA")
                .codigo("ESP-QA-001")
                .aforo(10)
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(5)
                .facultadId(facultad.getId())
                .disponibilidadId(disponibilidad.getId())
                .ubicacionId(ubicacion.getId())
                .build());

        Optional<Espacio> found = espacioRepository.findById(espacio.getId());

        assertTrue(found.isPresent());
        assertEquals("Sala QA", found.get().getNombre());
        assertEquals(10, found.get().getAforo());
    }

    @Test
    @DisplayName("Lista todos los espacios")
    void listarTodos() {
        Facultad facultad = facultadRepository.save(Facultad.builder().nombre("Facultad QA Espacio 2").build());
        UbicacionUniversitaria ubicacion = ubicacionRepository.save(UbicacionUniversitaria.builder().nombre("Ubicacion QA Espacio 2").build());
        EstadoDisponibilidad disponibilidad = estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE).build());

        espacioRepository.save(Espacio.builder()
                .nombre("Sala QA 2")
                .codigo("ESP-QA-002")
                .aforo(5)
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(5)
                .facultadId(facultad.getId())
                .disponibilidadId(disponibilidad.getId())
                .ubicacionId(ubicacion.getId())
                .build());

        List<Espacio> espacios = espacioRepository.findAll();

        assertEquals(1, espacios.size());
    }

    @Test
    @DisplayName("Filtra espacios por categoria")
    void filtrarPorCategoria() {
        Facultad facultad = facultadRepository.save(Facultad.builder().nombre("Facultad QA Espacio 3").build());
        UbicacionUniversitaria ubicacion = ubicacionRepository.save(UbicacionUniversitaria.builder().nombre("Ubicacion QA Espacio 3").build());
        EstadoDisponibilidad disponibilidad = estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE).build());

        espacioRepository.save(Espacio.builder()
                .nombre("Sala QA 3")
                .codigo("ESP-QA-003")
                .aforo(3)
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(5)
                .facultadId(facultad.getId())
                .disponibilidadId(disponibilidad.getId())
                .ubicacionId(ubicacion.getId())
                .build());

        List<Espacio> espacios = espacioRepository.findByCategoria(CategoriaRecurso.ESPACIO);

        assertEquals(1, espacios.size());
    }
}
