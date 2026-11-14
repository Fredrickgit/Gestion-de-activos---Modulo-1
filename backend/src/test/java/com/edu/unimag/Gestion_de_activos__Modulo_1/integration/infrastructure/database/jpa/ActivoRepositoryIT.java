package com.edu.unimag.Gestion_de_activos__Modulo_1.integration.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.TipoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
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
class ActivoRepositoryIT {

    @Autowired
    private ActivoRepository activoRepository;

    @Autowired
    private EstadoActivoRepository estadoActivoRepository;

    @Autowired
    private EstadoDisponibilidadRepository estadoDisponibilidadRepository;

    @Autowired
    private TipoActivoRepository tipoActivoRepository;

    private UUID estadoFisicoId;
    private UUID disponibilidadId;
    private UUID tipoActivoId;

    @BeforeEach
    void setUpCatalogos() {
        EstadoActivo estado = estadoActivoRepository.save(
                EstadoActivo.builder().estado(com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo.OPTIMO)
                        .descripcion("Estado optimo").build());
        EstadoDisponibilidad disponibilidad = estadoDisponibilidadRepository.save(
                EstadoDisponibilidad.builder().disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE).descripcion("Disponible").build());
        TipoActivo tipo = tipoActivoRepository.save(TipoActivo.builder().nombre("Tipo QA").plazoDevolucion(30).build());

        this.estadoFisicoId = estado.getId();
        this.disponibilidadId = disponibilidad.getId();
        this.tipoActivoId = tipo.getId();
    }

    @Test
    @DisplayName("Guarda y recupera un activo por identificador")
    void guardarYRecuperarPorId() {
        Activo activo = activoRepository.save(Activo.builder()
                .nombre("Laptop QA")
                .codigo("ACT-QA-001")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(estadoFisicoId)
                .disponibilidadId(disponibilidadId)
                .tipoActivoId(tipoActivoId)
                .plazoDevolucion(30)
                .build());

        Optional<Activo> found = activoRepository.findById(activo.getId());

        assertTrue(found.isPresent());
        assertEquals("Laptop QA", found.get().getNombre());
        assertEquals("ACT-QA-001", found.get().getCodigo());
    }

    @Test
    @DisplayName("Lista todos los activos")
    void listarTodos() {
        activoRepository.save(Activo.builder()
                .nombre("Equipo QA 1")
                .codigo("ACT-QA-002")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(estadoFisicoId)
                .disponibilidadId(disponibilidadId)
                .tipoActivoId(tipoActivoId)
                .plazoDevolucion(30)
                .build());
        activoRepository.save(Activo.builder()
                .nombre("Equipo QA 2")
                .codigo("ACT-QA-003")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(estadoFisicoId)
                .disponibilidadId(disponibilidadId)
                .tipoActivoId(tipoActivoId)
                .plazoDevolucion(30)
                .build());

        List<Activo> activos = activoRepository.findAll();

        assertEquals(2, activos.size());
    }

    @Test
    @DisplayName("Busca el activo por codigo")
    void buscarPorCodigo() {
        activoRepository.save(Activo.builder()
                .nombre("Equipo QA 3")
                .codigo("ACT-QA-004")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(estadoFisicoId)
                .disponibilidadId(disponibilidadId)
                .tipoActivoId(tipoActivoId)
                .plazoDevolucion(30)
                .build());

        Optional<Activo> found = activoRepository.findByCodigo("ACT-QA-004");

        assertTrue(found.isPresent());
        assertEquals("Equipo QA 3", found.get().getNombre());
    }

    @Test
    @DisplayName("Marca un activo como dado de baja")
    void marcarBaja() {
        Activo activo = activoRepository.save(Activo.builder()
                .nombre("Equipo QA 5")
                .codigo("ACT-QA-005")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(estadoFisicoId)
                .disponibilidadId(disponibilidadId)
                .tipoActivoId(tipoActivoId)
                .plazoDevolucion(30)
                .build());

        int filasAfectadas = activoRepository.marcarBaja(activo.getId());

        assertEquals(1, filasAfectadas);
    }
}
