package com.ypisds.ecommerce_microsservices.produto_service.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaTreeResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
            ),
            @ApiResponse (
                responseCode = "409",
                description = "Category with the same name and hierarchy already exists",
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

    @GetMapping("{slug}")
    @Operation(summary = "Get a category by slug")
    @ApiResponses (
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Category found",
                content = {
                    @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoriaPostResponseDTO.class)
                    )
                }
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Category not found",
                content = @Content
            )
        }
    )
    public ResponseEntity<CategoriaPostResponseDTO> retornarCategoriaPorSlug(@PathVariable String slug)
    {
        logger.info("Executing GET /api/categories/{}", slug);

        CategoriaPostResponseDTO responseDTO = categoriaService.retornarCategoriaPorSlug(slug);
        return ResponseEntity
                .status(200)
                .body(responseDTO);
    }

    @PutMapping("{slug}")
    @Operation(summary = "Update a category by slug")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Category updated successfully",
                content = {
                    @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoriaPostResponseDTO.class)
                    )
                }
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid hierarchy (category cannot be its own father)",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Category or category's father not found",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Category with the same name and hierarchy already exists",
                content = @Content
            )
        }
    )
    public ResponseEntity<CategoriaPostResponseDTO> atualizarCategoria(
        @PathVariable String slug,
        @RequestBody @Valid CategoriaPostRequestDTO dto
    )
    {
        logger.info("Executing PUT /api/categories/{}", slug);

        CategoriaPostResponseDTO responseDTO = categoriaService.atualizarCategoria(slug, dto);
        return ResponseEntity
                .status(200)
                .body(responseDTO);
    }

    @DeleteMapping("{slug}")
    @Operation(summary = "Delete a category by slug")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204",
                description = "Category deleted successfully",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Category not found",
                content = @Content
            )
        }
    )
    public ResponseEntity<Void> deletarCategoria(@PathVariable String slug)
    {
        logger.info("Executing DELETE /api/categories/{}", slug);

        categoriaService.deletarCategoria(slug);
        return ResponseEntity
                .status(204)
                .build();
    }

    @GetMapping("")
    @Operation(summary = "Get all categories in a hierarchical tree structure")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Categories tree retrieved successfully",
                content = {
                    @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = CategoriaTreeResponseDTO.class))
                    )
                }
            )
        }
    )
    public ResponseEntity<List<CategoriaTreeResponseDTO>> listarCategoriasEmArvore()
    {
        logger.info("Executing GET /api/categories");

        List<CategoriaTreeResponseDTO> responseDTO = categoriaService.listarCategoriasEmArvore();
        return ResponseEntity
                .status(200)
                .body(responseDTO);
    }
}
