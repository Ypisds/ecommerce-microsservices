package com.ypisds.ecommerce_microsservices.produto_service.Exception.Handler;

import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ypisds.ecommerce_microsservices.produto_service.Controller.CategoriaController;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriasComHierarquiaJaExisteException;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.FatherCategoryNotFoundException;

@RestControllerAdvice(assignableTypes = CategoriaController.class)
public class CategoryExceptionHandler {

    @ExceptionHandler({
        CategoriaNaoEncontradaException.class,
        FatherCategoryNotFoundException.class
    })
    public ProblemDetail handleNotFoundExceptions(RuntimeException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Recurso não encontrado");
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(CategoriasComHierarquiaJaExisteException.class)
    public ProblemDetail handleConflictException(CategoriasComHierarquiaJaExisteException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("Conflito de hierarquia de categoria");
        problemDetail.setType(URI.create("https://api.ecommerce.com/errors/conflict"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}