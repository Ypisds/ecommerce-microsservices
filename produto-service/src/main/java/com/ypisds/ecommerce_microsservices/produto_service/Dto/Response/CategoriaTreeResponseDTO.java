package com.ypisds.ecommerce_microsservices.produto_service.Dto.Response;

import java.util.List;

public record CategoriaTreeResponseDTO
(
    Long id,
    String nome,
    String slug,
    List<CategoriaTreeResponseDTO> subcategorias
) 
{}
