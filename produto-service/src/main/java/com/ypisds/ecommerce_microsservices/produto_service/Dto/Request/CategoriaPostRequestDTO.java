package com.ypisds.ecommerce_microsservices.produto_service.Dto.Request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaPostRequestDTO
(
    @NotBlank String nome,
    Long idCategoriaPai
) 
{}
