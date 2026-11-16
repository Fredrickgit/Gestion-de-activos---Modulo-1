package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.config;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.config.JacksonConfig;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers.RecursoApiMapper;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica el contrato de seguridad de REST.md §6:
 * <ul>
 *   <li>sin credenciales → 401 con cuerpo JSON;</li>
 *   <li>rol DIRECCION → puede consultar, crear y cambiar estado;</li>
 *   <li>rol MONITOR → solo consulta (403 al modificar).</li>
 * </ul>
 *
 * Se usa la rebanada web (sin base de datos ni Kafka) y se mockean los casos
 * de uso y el mapeador.
 */
@WebMvcTest(properties = {
        "app.security.user=direccion",
        "app.security.password=direccion-pass",
        "app.security.monitor-user=monitor",
        "app.security.monitor-password=monitor-pass"
})
@Import({SecurityConfig.class, JacksonConfig.class})
class SecurityConfigTest {

    private static final String DIRECCION = "direccion";
    private static final String DIRECCION_PASS = "direccion-pass";
    private static final String MONITOR = "monitor";
    private static final String MONITOR_PASS = "monitor-pass";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GestionRecursos gestionRecursos;

    @MockitoBean
    private RecursoApiMapper recursoApiMapper;

    @Test
    void peticionSinCredencialesDevuelve401Json() throws Exception {
        mockMvc.perform(get("/api/v1/recursos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void credencialesInvalidasDevuelven401() throws Exception {
        mockMvc.perform(get("/api/v1/recursos").with(httpBasic(DIRECCION, "incorrecta")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void direccionPuedeConsultar() throws Exception {
        mockMvc.perform(get("/api/v1/recursos").with(httpBasic(DIRECCION, DIRECCION_PASS)))
                .andExpect(status().isOk());
    }

    @Test
    void rutaInexistenteDevuelve404NoError500() throws Exception {
        mockMvc.perform(get("/api/v1/ruta-que-no-existe").with(httpBasic(DIRECCION, DIRECCION_PASS)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void monitorPuedeConsultar() throws Exception {
        mockMvc.perform(get("/api/v1/recursos").with(httpBasic(MONITOR, MONITOR_PASS)))
                .andExpect(status().isOk());
    }

    @Test
    void monitorNoPuedeCrearEspacios() throws Exception {
        mockMvc.perform(post("/api/v1/espacios")
                        .with(httpBasic(MONITOR, MONITOR_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laboratorio",
                                  "codigo": "ESP-001",
                                  "aforoMaximo": 10,
                                  "facultadId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                                  "equipamiento": ["b1f0d2c3-4e5a-4b7c-8d9e-0f1a2b3c4d5e"]
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void monitorNoPuedeCambiarEstado() throws Exception {
        mockMvc.perform(patch("/api/v1/recursos/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11/estado")
                        .with(httpBasic(MONITOR, MONITOR_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nuevoEstado": "EN_MANTENIMIENTO",
                                  "motivo": "Prueba",
                                  "usuarioResponsable": "monitor"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void direccionPuedeCrearEspacios() throws Exception {
        mockMvc.perform(post("/api/v1/espacios")
                        .with(httpBasic(DIRECCION, DIRECCION_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laboratorio",
                                  "codigo": "ESP-001",
                                  "aforoMaximo": 10,
                                  "facultadId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
                                  "equipamiento": ["b1f0d2c3-4e5a-4b7c-8d9e-0f1a2b3c4d5e"]
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void direccionPuedeCambiarEstado() throws Exception {
        mockMvc.perform(patch("/api/v1/recursos/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11/estado")
                        .with(httpBasic(DIRECCION, DIRECCION_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nuevoEstado": "EN_MANTENIMIENTO",
                                  "motivo": "Prueba",
                                  "usuarioResponsable": "direccion"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
