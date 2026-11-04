package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TipoActivoMapper {

    public com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo toDomain(
            com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo model) {
        if (model == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo.builder()
// blabla
    }

    public com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo toModel(
            com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo domain) {
        if (domain == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo.builder()
// blabla
    }

}
