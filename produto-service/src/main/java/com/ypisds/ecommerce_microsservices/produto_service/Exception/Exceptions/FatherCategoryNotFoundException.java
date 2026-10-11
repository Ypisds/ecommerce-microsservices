package com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions;

public class FatherCategoryNotFoundException extends RuntimeException {
    public FatherCategoryNotFoundException(String message)
    {
        super(message);
    }
}
