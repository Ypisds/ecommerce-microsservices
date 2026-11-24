package com.ypisds.ecommerce_microsservices.produto_service.Exception.Handler;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ypisds.ecommerce_microsservices.produto_service.Controller.ProdutoController;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.ProdutoFotoPrincipalNaoEncontradaException;


@RestControllerAdvice(assignableTypes = ProdutoController.class)
public class ProdutoFotoExceptionHandler {
    
    @ExceptionHandler(ProdutoFotoPrincipalNaoEncontradaException.class)
    public ProblemDetail handleProdutoFotoPrincipalNaoEncontradaException(ProdutoFotoPrincipalNaoEncontradaException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Foto principal do produto não encontrada");
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
    
    @ExceptionHandler(IOException.class)
    public ProblemDetail handleIOException(IOException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Erro ao processar a foto do produto");
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/internal-server-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

}