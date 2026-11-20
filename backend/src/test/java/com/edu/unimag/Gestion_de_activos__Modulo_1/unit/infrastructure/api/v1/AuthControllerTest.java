package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.AuthMeResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1.AuthController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    private final AuthController controller = new AuthController();

    @Test
    @DisplayName("Devuelve el usuario y el rol sin el prefijo ROLE_")
    void devuelveUsuarioYRol() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_DIRECCION")));

        AuthMeResponse response = controller.me(authentication);

        assertEquals("admin", response.usuario());
        assertEquals("DIRECCION", response.rol());
    }

    @Test
    @DisplayName("Devuelve el rol de monitor cuando la cuenta es de solo lectura")
    void devuelveRolMonitor() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "monitor",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_MONITOR")));

        AuthMeResponse response = controller.me(authentication);

        assertEquals("monitor", response.usuario());
        assertEquals("MONITOR", response.rol());
    }
}
