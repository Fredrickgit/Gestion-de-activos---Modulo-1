package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.external;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.Modulo2Client;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.Modulo3Client;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.ValidacionExternaAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class Modulo3IntegrationTest {

    private final UUID recursoId = UUID.randomUUID();
    private final Modulo2Client modulo2 = mock(Modulo2Client.class);
    private final RestClient.Builder builder = RestClient.builder().baseUrl("https://modulo3.test");
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

    private ApplicationContextRunner context() {
        return new ApplicationContextRunner()
                .withBean(Modulo2Client.class, () -> modulo2)
                .withBean(Modulo3Client.class, () -> new Modulo3Client(builder.build()))
                .withBean(ValidacionExternaAdapter.class)
                .withPropertyValues("external.validation.enabled=false",
                        "external.modulo3.enabled=true");
    }

    @Test
    void consultaM3ConM2DesactivadoYReconoceNovedades() {
        for (boolean pendiente : new boolean[]{false, true}) {
            server.reset();
            server.expect(requestTo("https://modulo3.test/api/v1/novedades/pendientes?recursoId=" + recursoId))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess("""
                            {"recursoId":"%s","tieneNovedadPendiente":%s}
                            """.formatted(recursoId, pendiente), MediaType.APPLICATION_JSON));

            context().run(ctx -> {
                var adapter = ctx.getBean(ValidacionExternaAdapter.class);
                assertThat(adapter.tieneReservaActiva(recursoId)).contains(false);
                assertThat(adapter.tieneNovedadPendiente(recursoId)).contains(pendiente);
            });
            server.verify();
        }
        verifyNoInteractions(modulo2);
    }

    @Test
    void errorDeM3NoSeInterpretaComoAusenciaDeNovedades() {
        server.expect(requestTo("https://modulo3.test/api/v1/novedades/pendientes?recursoId=" + recursoId))
                .andRespond(withServerError());
        context().run(ctx -> assertThat(ctx.getBean(ValidacionExternaAdapter.class)
                .tieneNovedadPendiente(recursoId)).isEmpty());
        server.verify();
    }

    @Test
    void conservaInterruptorGlobalCuandoNoHayConfiguracionPorModulo() {
        new ApplicationContextRunner()
                .withBean(Modulo2Client.class, () -> modulo2)
                .withBean(Modulo3Client.class, () -> new Modulo3Client(builder.build()))
                .withBean(ValidacionExternaAdapter.class)
                .withPropertyValues("external.validation.enabled=false")
                .run(ctx -> {
                    var adapter = ctx.getBean(ValidacionExternaAdapter.class);
                    assertThat(adapter.tieneReservaActiva(recursoId)).contains(false);
                    assertThat(adapter.tieneNovedadPendiente(recursoId)).contains(false);
                });
        verifyNoInteractions(modulo2);
        server.verify();
    }
}
