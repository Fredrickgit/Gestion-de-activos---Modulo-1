package com.edu.unimag.Gestion_de_activos__Modulo_1.integration.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
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
class FacultadRepositoryIT {

    @Autowired
    private FacultadRepository facultadRepository;

    @Test
    @DisplayName("Guarda y recupera una facultad por identificador")
    void guardarYRecuperarPorId() {
        Facultad facultad = facultadRepository.save(
                Facultad.builder().nombre("Facultad QA").descripcion("Facultad de pruebas").build());

        Optional<Facultad> found = facultadRepository.findById(facultad.getId());

        assertTrue(found.isPresent());
        assertEquals("Facultad QA", found.get().getNombre());
        assertEquals("Facultad de pruebas", found.get().getDescripcion());
    }

    @Test
    @DisplayName("Lista todas las facultades")
    void listarTodas() {
        facultadRepository.save(Facultad.builder().nombre("Facultad QA 1").build());
        facultadRepository.save(Facultad.builder().nombre("Facultad QA 2").build());

        List<Facultad> facultades = facultadRepository.findAll();

        assertEquals(2, facultades.size());
    }

    @Test
    @DisplayName("Consulta la existencia de una facultad")
    void existePorId() {
        Facultad facultad = facultadRepository.save(Facultad.builder().nombre("Facultad QA 3").build());

        assertTrue(facultadRepository.existsById(facultad.getId()));
        assertFalse(facultadRepository.existsById(java.util.UUID.randomUUID()));
    }

    @Test
    @DisplayName("Busca la facultad por nombre")
    void buscarPorNombre() {
        facultadRepository.save(Facultad.builder().nombre("Facultad QA 4").build());

        Optional<Facultad> found = facultadRepository.findByNombre("Facultad QA 4");

        assertTrue(found.isPresent());
        assertEquals("Facultad QA 4", found.get().getNombre());
    }

    @Test
    @DisplayName("Verifica la existencia por nombre")
    void existePorNombre() {
        facultadRepository.save(Facultad.builder().nombre("Facultad QA 5").build());

        assertTrue(facultadRepository.existsByNombre("Facultad QA 5"));
        assertFalse(facultadRepository.existsByNombre("Facultad QA 5X"));
    }

    @Test
    @DisplayName("Elimina una facultad")
    void eliminarFacultad() {
        Facultad facultad = facultadRepository.save(Facultad.builder().nombre("Facultad QA 6").build());

        facultadRepository.deleteById(facultad.getId());

        assertFalse(facultadRepository.existsById(facultad.getId()));
    }
}
