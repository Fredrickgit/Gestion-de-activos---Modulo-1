package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.AuthMeResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Información de la sesión autenticada mediante HTTP Basic.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String ROLE_PREFIX = "ROLE_";

    @GetMapping("/me")
    public AuthMeResponse me(Authentication authentication) {
        String rol = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .findFirst()
                .orElse("MONITOR");

        return new AuthMeResponse(authentication.getName(), rol);
    }
}
