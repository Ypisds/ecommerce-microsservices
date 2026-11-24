package com.ypisds.ecommerce_microsservices.produto_service.Dto.Response;

import java.util.List;

public record ProdutoResponseDTO(
    Long id,
    List<ProdutoFotoResponseDTO> fotos,
    String nome,
    String descricao,
    String slug,
    Double preco,
    CategoriaPostResponseDTO categoria,
    boolean ativo
) {
    
}
