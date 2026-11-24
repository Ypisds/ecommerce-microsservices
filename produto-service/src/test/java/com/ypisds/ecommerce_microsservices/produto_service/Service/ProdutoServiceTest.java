package com.ypisds.ecommerce_microsservices.produto_service.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.github.slugify.Slugify;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.ProdutoPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.ProdutoFotoResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.ProdutoResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException;
import com.ypisds.ecommerce_microsservices.produto_service.Mapper.ProdutoMapper;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Categoria;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Produto;
import com.ypisds.ecommerce_microsservices.produto_service.Model.ProdutoFoto;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.CategoriaRepository;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoFotoService produtoFotoService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private Slugify slugify;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private ProdutoPostRequestDTO requestDTO;
    private MultipartFile arquivoMock;
    private Categoria categoria;
    private Produto produtoMapeado;

    @BeforeEach
    void setUp() {
        requestDTO = new ProdutoPostRequestDTO(
            "Notebook Gamer",
            "Notebook com RTX 4060",
            BigDecimal.valueOf(6500.00),
            1L
        );

        arquivoMock = new MockMultipartFile(
            "file",
            "notebook.png",
            "image/png",
            "bytes-da-imagem".getBytes()
        );

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Informática");
        categoria.setSlug("informatica-1");

        produtoMapeado = new Produto();
        produtoMapeado.setNome("Notebook Gamer");
        produtoMapeado.setDescricao("Notebook com RTX 4060");
        produtoMapeado.setPreco(BigDecimal.valueOf(6500.00));
    }

    @Test
    void deveSalvarProdutoComSlugEFotoComSucesso() throws IOException {
        ProdutoFoto fotoSalva = new ProdutoFoto();
        fotoSalva.setId(50L);
        fotoSalva.setUrl("uuid_notebook.png");
        fotoSalva.setPrincipal(true);

        ProdutoResponseDTO responseEsperado = new ProdutoResponseDTO(
            10L,
            List.of(new ProdutoFotoResponseDTO(50L, "uuid_notebook.png", true, LocalDateTime.now(), LocalDateTime.now())),
            "Notebook Gamer",
            "Notebook com RTX 4060",
            "notebook-gamer-10",
            6500.00,
            new CategoriaPostResponseDTO(1L, "Informática", "informatica-1", null),
            true
        );

        when(produtoMapper.toEntity(requestDTO)).thenReturn(produtoMapeado);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            if (p.getId() == null) {
                p.setId(10L);
            }
            return p;
        });
        when(slugify.slugify("Notebook Gamer 10")).thenReturn("notebook-gamer-10");
        when(produtoFotoService.uploadFoto(arquivoMock, produtoMapeado)).thenReturn(fotoSalva);
        when(produtoMapper.toResponseDTO(produtoMapeado)).thenReturn(responseEsperado);

        ProdutoResponseDTO resultado = produtoService.salvarProduto(requestDTO, arquivoMock);

        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
        assertEquals("notebook-gamer-10", resultado.slug());
        assertEquals("notebook-gamer-10", produtoMapeado.getSlug());
        assertEquals(categoria, produtoMapeado.getCategoria());
        assertTrue(produtoMapeado.getFotos().contains(fotoSalva));

        // Garante a ordem exata das operações: salva produto -> gera slug -> faz upload -> salva novamente -> mapeia DTO
        InOrder inOrder = inOrder(produtoRepository, slugify, produtoFotoService, produtoMapper);
        inOrder.verify(produtoRepository).save(produtoMapeado);
        inOrder.verify(slugify).slugify("Notebook Gamer 10");
        inOrder.verify(produtoFotoService).uploadFoto(arquivoMock, produtoMapeado);
        inOrder.verify(produtoRepository).save(produtoMapeado);
        inOrder.verify(produtoMapper).toResponseDTO(produtoMapeado);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoForEncontrada() throws IOException {
        when(produtoMapper.toEntity(requestDTO)).thenReturn(produtoMapeado);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        CategoriaNaoEncontradaException ex = assertThrows(
            CategoriaNaoEncontradaException.class,
            () -> produtoService.salvarProduto(requestDTO, arquivoMock)
        );

        assertEquals("Categoria não encontrada", ex.getMessage());
        verify(produtoRepository, never()).save(any());
        verify(produtoFotoService, never()).uploadFoto(any(), any());
        verify(slugify, never()).slugify(any());
    }

    @Test
    void devePropagarIOExceptionQuandoFalharUploadDaFoto() throws IOException {
        when(produtoMapper.toEntity(requestDTO)).thenReturn(produtoMapeado);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(slugify.slugify("Notebook Gamer 10")).thenReturn("notebook-gamer-10");
        when(produtoFotoService.uploadFoto(arquivoMock, produtoMapeado))
            .thenThrow(new IOException("Erro ao enviar imagem para o MinIO"));

        assertThrows(
            IOException.class,
            () -> produtoService.salvarProduto(requestDTO, arquivoMock)
        );

        // O primeiro save (para gerar o ID) ocorreu, mas o segundo save não deve ocorrer
        verify(produtoRepository, times(1)).save(produtoMapeado);
        verify(produtoMapper, never()).toResponseDTO(any());
    }
}