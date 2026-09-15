package com.ypisds.ecommerce_microsservices.produto_service.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProdutoController {

    @GetMapping(value = "/diagnostics", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> diagnostics()
    {
        return new ResponseEntity<String>("Products works!", HttpStatus.OK);
    }
}
