package com.ypisds.ecommerce_microsservices.produto_service.Service;

import org.springframework.stereotype.Service;

import com.github.slugify.Slugify;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.FatherCategoryNotFoundException;
import com.ypisds.ecommerce_microsservices.produto_service.Mapper.CategoriaMapper;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Categoria;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.CategoriaRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaService {
    
    private final Slugify slugify;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional 
    public CategoriaPostResponseDTO salvarCategoria(CategoriaPostRequestDTO dto)
    {
        Categoria categoria = categoriaMapper.toEntity(dto);

        if(dto.idCategoriaPai() != null)
        {
            Categoria categoriaPai = categoriaRepository
                .findById(dto.idCategoriaPai())
                .orElseThrow(() -> new FatherCategoryNotFoundException("Category's father not found"));
            categoria.setCategoriaPai(categoriaPai);
        }

        categoria.setSlug("temporario");

        categoria = categoriaRepository.save(categoria);

        categoria.setSlug(slugify.slugify(
            categoria.getNome() + " " + categoria.getId()
        ));

        categoria = categoriaRepository.save(categoria);

        return categoriaMapper.toPostResponseDTO(categoria);
      
    }
}
