package com.ypisds.ecommerce_microsservices.produto_service.Dto.Request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class CategoriaPostRequestDTOTest {

    private final Validator validator =
        Validation.buildDefaultValidatorFactory().getValidator();

    @Test 
    void deveCriarUmaCategoriaPostRequestDTOComSucesso() {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 1L);

        assertAll(
            () -> assertEquals(dto.nome(), "Vendas"),
            () -> assertEquals(dto.idCategoriaPai(), 1L)
        );
    }

    @Test
    void deveRetornarExceptionQuandoNomeForNulo()
    {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO(null, 1L);

        var violations = validator.validate(dto);

        assertAll(
            () -> assertEquals(violations.size(), 1),
            () -> assertEquals(violations.iterator().next().getMessage(), "Nome da categoria não pode ser nulo")
        );
    }


}
