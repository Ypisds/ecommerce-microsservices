package com.ypisds.ecommerce_microsservices.produto_service.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.slugify.Slugify;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriasComHierarquiaJaExisteException;
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
    private final Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    @Transactional 
    public CategoriaPostResponseDTO salvarCategoria(CategoriaPostRequestDTO dto)
    {
        logger.info("Executando salvarCategoria em CategoriaService");
        Categoria categoria = categoriaMapper.toEntity(dto);

        // Verificando a unicidade do nome da categoria por "hierarquia" (nome da categoria e id da categoria pai)
        categoriaRepository.findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai())
            .ifPresent(c -> {
                throw new CategoriasComHierarquiaJaExisteException("Já existe uma categoria com o mesmo nome e hierarquia");
            });

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

    public CategoriaPostResponseDTO retornarCategoriaPorSlug(String slug)
    {
        Categoria categoria = categoriaRepository
            .findBySlug(slug)
            .orElseThrow( () -> new CategoriaNaoEncontradaException("Categoria não encontrada"));
          
        return categoriaMapper.toPostResponseDTO(categoria);
    }
}
