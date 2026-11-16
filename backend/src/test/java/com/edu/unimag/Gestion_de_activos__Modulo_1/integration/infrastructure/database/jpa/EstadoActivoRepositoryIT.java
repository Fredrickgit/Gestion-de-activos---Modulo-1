package com.edu.unimag.Gestion_de_activos__Modulo_1.integration.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
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
class EstadoActivoRepositoryIT {

    @Autowired
    private EstadoActivoRepository estadoActivoRepository;

    @Test
    @DisplayName("Guarda y recupera un estado activo")
    void guardarYRecuperarPorId() {
        EstadoActivo estado = estadoActivoRepository.save(
                EstadoActivo.builder().estado(EstadoFisicoActivo.OPTIMO).descripcion("Estado optimo").build());

        Optional<EstadoActivo> found = estadoActivoRepository.findById(estado.getId());

        assertTrue(found.isPresent());
        assertEquals(EstadoFisicoActivo.OPTIMO, found.get().getEstado());
        assertEquals("Estado optimo", found.get().getDescripcion());
    }

    @Test
    @DisplayName("Lista todos los estados activos")
    void listarTodos() {
        estadoActivoRepository.save(EstadoActivo.builder().estado(EstadoFisicoActivo.NUEVO).descripcion("Nuevo").build());
        estadoActivoRepository.save(EstadoActivo.builder().estado(EstadoFisicoActivo.MAL_ESTADO).descripcion("Mal estado").build());

        List<EstadoActivo> estados = estadoActivoRepository.findAll();

        assertEquals(2, estados.size());
    }

    @Test
    @DisplayName("Busca el estado activo por identificador de estado fisico")
    void buscarPorEstado() {
        estadoActivoRepository.save(EstadoActivo.builder().estado(EstadoFisicoActivo.AVERIADO).descripcion("Averiado").build());

        Optional<EstadoActivo> found = estadoActivoRepository.findByEstado(EstadoFisicoActivo.AVERIADO);

        assertTrue(found.isPresent());
        assertEquals(EstadoFisicoActivo.AVERIADO, found.get().getEstado());
    }

    @Test
    @DisplayName("Elimina un estado activo")
    void eliminarEstadoActivo() {
        EstadoActivo estado = estadoActivoRepository.save(
                EstadoActivo.builder().estado(EstadoFisicoActivo.NUEVO).descripcion("Nuevo").build());

        estadoActivoRepository.deleteById(estado.getId());

        assertFalse(estadoActivoRepository.existsById(estado.getId()));
    }
}
