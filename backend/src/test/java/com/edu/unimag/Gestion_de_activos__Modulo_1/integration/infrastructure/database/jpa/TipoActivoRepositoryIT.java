package com.edu.unimag.Gestion_de_activos__Modulo_1.integration.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.TipoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.TestcontainersConfiguration;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class TipoActivoRepositoryIT {

    @Autowired
    private TipoActivoRepository tipoActivoRepository;

    @Test
    @DisplayName("Guarda y recupera un tipo de activo")
    void guardarYRecuperarPorId() {
        TipoActivo tipo = tipoActivoRepository.save(
                TipoActivo.builder().nombre("Proyector QA").descripcion("Proyector de prueba").plazoDevolucion(30).build());

        Optional<TipoActivo> found = tipoActivoRepository.findById(tipo.getId());

        assertTrue(found.isPresent());
        assertEquals("Proyector QA", found.get().getNombre());
        assertEquals(30, found.get().getPlazoDevolucion());
    }

    @Test
    @DisplayName("Lista todos los tipos de activo")
    void listarTodos() {
        tipoActivoRepository.save(TipoActivo.builder().nombre("Equipo QA 1").plazoDevolucion(15).build());
        tipoActivoRepository.save(TipoActivo.builder().nombre("Equipo QA 2").plazoDevolucion(15).build());

        List<TipoActivo> tipos = tipoActivoRepository.findAll();

        assertTrue(tipos.stream().anyMatch(tipo -> "Equipo QA 1".equals(tipo.getNombre())));
        assertTrue(tipos.stream().anyMatch(tipo -> "Equipo QA 2".equals(tipo.getNombre())));
    }

    @Test
    @DisplayName("Busca tipos por nombre parcial ignorando mayusculas")
    void buscarPorNombreParcial() {
        tipoActivoRepository.save(TipoActivo.builder().nombre("Microfono QA").plazoDevolucion(15).build());

        List<TipoActivo> tipos = tipoActivoRepository.findByNombreContainingIgnoreCase("MICRO");

        assertEquals(1, tipos.size());
    }

    @Test
    @DisplayName("Elimina un tipo de activo")
    void eliminarTipoActivo() {
        TipoActivo tipo = tipoActivoRepository.save(TipoActivo.builder().nombre("Camaras QA").plazoDevolucion(30).build());

        tipoActivoRepository.deleteById(tipo.getId());

        assertFalse(tipoActivoRepository.existsById(tipo.getId()));
    }
}
