package com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions;

public class ProdutoFotoPrincipalNaoEncontradaException extends RuntimeException {
    public ProdutoFotoPrincipalNaoEncontradaException(Long idProduto) {
        super("Foto principal não encontrada para o produto: " + idProduto);
    }
    
}
