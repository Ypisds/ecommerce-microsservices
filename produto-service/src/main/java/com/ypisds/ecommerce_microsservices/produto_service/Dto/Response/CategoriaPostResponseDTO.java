package com.ypisds.ecommerce_microsservices.produto_service.Dto.Response;

public record CategoriaPostResponseDTO
(
    Long id,
    String nome,
    String slug,
    String slugCategoriaPai
) 
{}
