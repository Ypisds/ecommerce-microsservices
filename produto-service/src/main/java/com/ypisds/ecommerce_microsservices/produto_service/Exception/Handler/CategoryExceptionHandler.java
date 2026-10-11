package com.ypisds.ecommerce_microsservices.produto_service.Exception.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ypisds.ecommerce_microsservices.produto_service.Controller.CategoriaController;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.FatherCategoryNotFoundException;

@RestControllerAdvice(assignableTypes = CategoriaController.class)
public class CategoryExceptionHandler {
    
    @ExceptionHandler(FatherCategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleFatherCategoryNotFoundEntityException(FatherCategoryNotFoundException e)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .body(e.getMessage());
    }
}
