package com.ypisds.ecommerce_microsservices.produto_service.Service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.slugify.Slugify;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.ProdutoPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.ProdutoResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException;
import com.ypisds.ecommerce_microsservices.produto_service.Mapper.ProdutoMapper;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Categoria;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Produto;
import com.ypisds.ecommerce_microsservices.produto_service.Model.ProdutoFoto;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.CategoriaRepository;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.ProdutoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor
@Slf4j 
public class ProdutoService {
    
    private final ProdutoRepository produtoRepository;
    private final ProdutoFotoService produtoFotoService;
    private final CategoriaRepository categoriaRepository;
    private final Slugify slugify;
    private final ProdutoMapper produtoMapper;

    @Transactional(rollbackOn = Exception.class)
    public ProdutoResponseDTO salvarProduto(ProdutoPostRequestDTO produtoPostRequestDTO, MultipartFile file) throws IOException {
        Produto produto = produtoMapper.toEntity(produtoPostRequestDTO);
        Categoria categoria = categoriaRepository.findById(produtoPostRequestDTO.categoriaId())
                            .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada"));
        produto.setCategoria(categoria);
        produto.setSlug("temporario");

        produto = produtoRepository.save(produto);
        produto.setSlug(
            slugify.slugify(produto.getNome() + " " + produto.getId())
        );

        // Salvando a foto
        ProdutoFoto produtoFoto = produtoFotoService.uploadFoto(file, produto);

        produto = produtoRepository.save(produto);

        produto.getFotos().add(produtoFoto);

        return produtoMapper.toResponseDTO(produto);
    }

}
