package com.ypisds.ecommerce_microsservices.produto_service.Dto.Request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoPostRequestDTO
(
    @NotBlank String nome,
    @NotBlank String descricao,
    @NotNull BigDecimal preco,
    @NotNull Long categoriaId
) 
{}
