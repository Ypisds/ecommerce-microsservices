package com.ypisds.ecommerce_microsservices.produto_service.Dto.Response;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CategoriaPostResponseDTOTest {
    
    @Test
    void deveCriarUmaCategoriaPostResponseDTOComSucesso() {
        CategoriaPostResponseDTO dto = new CategoriaPostResponseDTO(1L, "Vendas", "vendas", "categoria-pai");
        
        assertAll(
            () -> assertEquals(dto.id(), 1L),
            () -> assertEquals(dto.nome(), "Vendas"),
            () -> assertEquals(dto.slug(), "vendas"),
            () -> assertEquals(dto.slugCategoriaPai(), "categoria-pai")
        );
    }
}
