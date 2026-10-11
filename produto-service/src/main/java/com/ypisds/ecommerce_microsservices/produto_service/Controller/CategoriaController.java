package com.ypisds.ecommerce_microsservices.produto_service.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Tag(name = "Categories", description = "Endpoints about categories")
public class CategoriaController {
    
    private final CategoriaService categoriaService;
    private final static Logger logger = LoggerFactory.getLogger(CategoriaController.class);

    @PostMapping("")
    @Operation(summary = "Create a category")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Successfull creation of a category",
                content = {
                    @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoriaPostResponseDTO.class)
                    )
                }
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Category's father not found",
                content = @Content
            )
        }
    )
    public ResponseEntity<CategoriaPostResponseDTO> criarCategoria(@RequestBody @Valid CategoriaPostRequestDTO dto)
    {
        logger.info("Executing POST /api/categories");

        CategoriaPostResponseDTO responseDTO = categoriaService.salvarCategoria(dto);
        return ResponseEntity
                .status(201)
                .body(responseDTO);
    }
}
