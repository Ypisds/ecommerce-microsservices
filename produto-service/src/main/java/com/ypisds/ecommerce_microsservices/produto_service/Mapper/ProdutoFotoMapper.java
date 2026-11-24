package com.ypisds.ecommerce_microsservices.produto_service.Mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.ProdutoFotoResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Model.ProdutoFoto;

@Mapper(componentModel = "spring")
public interface ProdutoFotoMapper {

    ProdutoFotoResponseDTO toResponseDTO(ProdutoFoto foto);

    List<ProdutoFotoResponseDTO> toResponseDTOList(List<ProdutoFoto> fotos);
}