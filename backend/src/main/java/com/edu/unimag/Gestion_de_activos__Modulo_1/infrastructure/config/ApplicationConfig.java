package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.config;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.EstadoActualizadoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.HistorialRecursoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.RecursoQueryPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.TransactionPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.ValidacionExternaPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.TipoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cableado de los casos de uso de aplicación.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public GestionRecursos gestionRecursos(ActivoRepository activoRepository,
                                            EspacioRepository espacioRepository,
                                            EstadoDisponibilidadRepository estadoDisponibilidadRepository,
                                            EstadoActivoRepository estadoActivoRepository,
                                            FacultadRepository facultadRepository,
                                            UbicacionUniversitariaRepository ubicacionRepository,
                                            TipoActivoRepository tipoActivoRepository,
                                            RecursoQueryPort recursoQueryPort,
                                            ValidacionExternaPort validacionExternaPort,
                                            EstadoActualizadoPort estadoActualizadoPort,
                                            HistorialRecursoPort historialRecursoPort,
                                            TransactionPort transactionPort) {
        return new GestionRecursos(
                activoRepository,
                espacioRepository,
                estadoDisponibilidadRepository,
                estadoActivoRepository,
                facultadRepository,
                ubicacionRepository,
                tipoActivoRepository,
                recursoQueryPort,
                validacionExternaPort,
                estadoActualizadoPort,
                historialRecursoPort,
                transactionPort);
    }
}
