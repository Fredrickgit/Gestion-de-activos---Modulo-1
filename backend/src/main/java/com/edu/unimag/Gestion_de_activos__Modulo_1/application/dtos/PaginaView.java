package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.List;

public record PaginaView<T>(
        List<T> content,
        int page,
        int size,
        long totalElements
) {
}
