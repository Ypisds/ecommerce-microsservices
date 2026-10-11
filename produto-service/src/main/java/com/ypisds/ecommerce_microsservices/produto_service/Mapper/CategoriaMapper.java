package com.ypisds.ecommerce_microsservices.produto_service.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Categoria;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "dhCriacao", ignore = true)
    @Mapping(target = "dhUltimaAlteracao", ignore = true)
    @Mapping(target = "categoriaPai", ignore = true)
    Categoria toEntity(CategoriaPostRequestDTO dto);

    @Mapping(source = "categoriaPai.slug", target = "slugCategoriaPai")
    CategoriaPostResponseDTO toPostResponseDTO(Categoria categoria);
}
