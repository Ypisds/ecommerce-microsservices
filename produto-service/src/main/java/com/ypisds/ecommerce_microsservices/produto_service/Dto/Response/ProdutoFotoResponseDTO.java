package com.ypisds.ecommerce_microsservices.produto_service.Dto.Response;

import java.time.LocalDateTime;

public record ProdutoFotoResponseDTO(
    Long id,
    String url,
    boolean principal,
    LocalDateTime dhCriacao,
    LocalDateTime dhUltimaAlteracao
)
{}
