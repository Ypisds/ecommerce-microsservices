package com.ypisds.ecommerce_microsservices.produto_service.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.ProdutoPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.ProdutoResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Produto;

@Mapper(componentModel = "spring", uses = {CategoriaMapper.class, ProdutoFotoMapper.class})
public interface ProdutoMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "dhCriacao", ignore = true)
    @Mapping(target = "dhUltimaAlteracao", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "fotos", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    Produto toEntity(ProdutoPostRequestDTO dto);

    ProdutoResponseDTO toResponseDTO(Produto produto);

}
