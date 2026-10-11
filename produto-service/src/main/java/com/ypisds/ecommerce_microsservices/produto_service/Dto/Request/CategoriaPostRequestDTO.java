package com.ypisds.ecommerce_microsservices.produto_service.Dto.Request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaPostRequestDTO
(
    @NotBlank(message = "Nome da categoria não pode ser nulo") String nome,
    Long idCategoriaPai
) 
{}
