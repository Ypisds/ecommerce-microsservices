package com.ypisds.ecommerce_microsservices.produto_service.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.slugify.Slugify;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaTreeResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriasComHierarquiaJaExisteException;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.FatherCategoryNotFoundException;
import com.ypisds.ecommerce_microsservices.produto_service.Mapper.CategoriaMapper;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Categoria;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.CategoriaRepository;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CategoriaPostResponseDTO atualizarCategoria(String slug, CategoriaPostRequestDTO dto) {
        logger.info("Executando atualizarCategoria para o slug: {}", slug);

        Categoria categoria = categoriaRepository
            .findBySlug(slug)
            .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada"));

        // Valida unicidade na hierarquia, ignorando a própria categoria que está sendo editada
        categoriaRepository.findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai())
            .ifPresent(c -> {
                if (!c.getId().equals(categoria.getId())) {
                    throw new CategoriasComHierarquiaJaExisteException("Já existe uma categoria com o mesmo nome e hierarquia");
                }
            });

        // Atualiza ou remove a categoria pai
        if (dto.idCategoriaPai() != null) {
            if (dto.idCategoriaPai().equals(categoria.getId())) {
                throw new IllegalArgumentException("Uma categoria não pode ser pai dela mesma");
            }

            Categoria categoriaPai = categoriaRepository
                .findById(dto.idCategoriaPai())
                .orElseThrow(() -> new FatherCategoryNotFoundException("Category's father not found"));
            categoria.setCategoriaPai(categoriaPai);
        } else {
            categoria.setCategoriaPai(null);
        }

        // Atualiza o nome e recalcula o slug seguindo o padrão (nome + id) caso o nome tenha mudado
        if (!categoria.getNome().equals(dto.nome())) {
            categoria.setNome(dto.nome());
            categoria.setSlug(slugify.slugify(dto.nome() + " " + categoria.getId()));
        }

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return categoriaMapper.toPostResponseDTO(categoriaSalva);
    }

    @Transactional
    public void deletarCategoria(String slug) {
        logger.info("Executando deletarCategoria para o slug: {}", slug);

        Categoria categoria = categoriaRepository
            .findBySlug(slug)
            .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada"));

        categoriaRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaTreeResponseDTO> listarCategoriasEmArvore() {
        logger.info("Executando listarCategoriasEmArvore em CategoriaService");

        List<Categoria> todasCategorias = categoriaRepository.findAll();

        // 1. Cria um mapa indexado pelo ID da categoria
        Map<Long, CategoriaTreeResponseDTO> mapaCategorias = new HashMap<>();
        for (Categoria cat : todasCategorias) {
            mapaCategorias.put(
                cat.getId(),
                new CategoriaTreeResponseDTO(cat.getId(), cat.getNome(), cat.getSlug(), new ArrayList<>())
            );
        }

        // 2. Conecta cada filho ao seu respectivo pai em memória
        List<CategoriaTreeResponseDTO> raizes = new ArrayList<>();
        for (Categoria cat : todasCategorias) {
            CategoriaTreeResponseDTO dtoAtual = mapaCategorias.get(cat.getId());

            if (cat.getCategoriaPai() == null) {
                raizes.add(dtoAtual);
            } else {
                CategoriaTreeResponseDTO paiDto = mapaCategorias.get(cat.getCategoriaPai().getId());
                if (paiDto != null) {
                    paiDto.subcategorias().add(dtoAtual);
                }
            }
        }

        return raizes;
    }
}
