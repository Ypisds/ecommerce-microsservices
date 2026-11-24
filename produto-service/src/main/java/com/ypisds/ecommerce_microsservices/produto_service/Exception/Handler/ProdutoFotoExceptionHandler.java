package com.ypisds.ecommerce_microsservices.produto_service.Exception.Handler;

import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.ProdutoFotoPrincipalNaoEncontradaException;


@RestControllerAdvice
public class ProdutoFotoExceptionHandler {
    
    @ExceptionHandler(ProdutoFotoPrincipalNaoEncontradaException.class)
    public ProblemDetail handleProdutoFotoPrincipalNaoEncontradaException(ProdutoFotoPrincipalNaoEncontradaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Foto principal do produto não encontrada");
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

}