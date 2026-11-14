package com.edu.unimag.Gestion_de_activos__Modulo_1.integration.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;
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
class UbicacionUniversitariaRepositoryIT {

    @Autowired
    private UbicacionUniversitariaRepository ubicacionRepository;

    @Test
    @DisplayName("Guarda y recupera una ubicacion universitaria")
    void guardarYRecuperarPorId() {
        UbicacionUniversitaria ubicacion = ubicacionRepository.save(
                UbicacionUniversitaria.builder().nombre("Biblioteca QA").descripcion("Sede de pruebas").build());

        Optional<UbicacionUniversitaria> found = ubicacionRepository.findById(ubicacion.getId());

        assertTrue(found.isPresent());
        assertEquals("Biblioteca QA", found.get().getNombre());
    }

    @Test
    @DisplayName("Lista todas las ubicaciones")
    void listarTodas() {
        ubicacionRepository.save(UbicacionUniversitaria.builder().nombre("Sede QA 1").build());
        ubicacionRepository.save(UbicacionUniversitaria.builder().nombre("Sede QA 2").build());

        List<UbicacionUniversitaria> ubicaciones = ubicacionRepository.findAll();

        assertEquals(2, ubicaciones.size());
    }

    @Test
    @DisplayName("Busca la ubicacion por nombre")
    void buscarPorNombre() {
        ubicacionRepository.save(UbicacionUniversitaria.builder().nombre("Sede QA 3").build());

        Optional<UbicacionUniversitaria> found = ubicacionRepository.findByNombre("Sede QA 3");

        assertTrue(found.isPresent());
        assertEquals("Sede QA 3", found.get().getNombre());
    }

    @Test
    @DisplayName("Elimina una ubicacion")
    void eliminarUbicacion() {
        UbicacionUniversitaria ubicacion = ubicacionRepository.save(UbicacionUniversitaria.builder().nombre("Sede QA 4").build());

        ubicacionRepository.deleteById(ubicacion.getId());

        assertFalse(ubicacionRepository.existsById(ubicacion.getId()));
    }
}
