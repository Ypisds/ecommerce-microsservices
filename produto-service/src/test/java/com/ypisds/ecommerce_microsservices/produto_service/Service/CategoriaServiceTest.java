package com.ypisds.ecommerce_microsservices.produto_service.Service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.github.slugify.Slugify;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.FatherCategoryNotFoundException;
import com.ypisds.ecommerce_microsservices.produto_service.Mapper.CategoriaMapper;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Categoria;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CategoriaServiceTest {

    @Mock 
    private CategoriaMapper categoriaMapper;

    @Mock 
    private CategoriaRepository categoriaRepository;

    @Mock 
    private Slugify slugify;

    @InjectMocks
    private CategoriaService categoriaService;

    @BeforeEach 
    void setup()
    {

    }
    
    @Test
    void deveCriarUmaCategoriaSemCategoriaPaiComSucesso()
    {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", null);
        Categoria categoria = new Categoria(null, "Vendas", null, null, null, null);
        CategoriaPostResponseDTO responseDTO = new CategoriaPostResponseDTO(1L, "Vendas", "vendas-1", null);

        Mockito.when(categoriaMapper.toEntity(dto)).thenReturn(categoria);
        Mockito.when(categoriaRepository.save(any(Categoria.class))).thenReturn(new Categoria
            (1L, "Vendas", "temporario", LocalDateTime.now(), LocalDateTime.now(), null))
            .thenReturn(new Categoria(
                1L, "Vendas", "vendas-1", LocalDateTime.now(), LocalDateTime.now(), null
            ));
        Mockito.when(slugify.slugify(any(String.class))).thenReturn("vendas-1");
        Mockito.when(categoriaMapper.toPostResponseDTO(any(Categoria.class))).thenReturn(responseDTO);

        CategoriaPostResponseDTO response = categoriaService.salvarCategoria(dto);

        assertAll(
            () -> assertEquals(response.id(), 1L),
            () -> assertEquals(response.nome(), "Vendas"),
            () -> assertEquals(response.slug(), "vendas-1"),
            () -> assertEquals(responseDTO.slugCategoriaPai(), null)
        );

        Mockito.verify(categoriaMapper, times(1)).toEntity(dto);
        Mockito.verify(categoriaMapper, times(1)).toPostResponseDTO(any(Categoria.class));
        Mockito.verify(categoriaRepository, times(2)).save(any(Categoria.class));
        Mockito.verify(slugify, only()).slugify(any(String.class));
        Mockito.verify(categoriaRepository, never()).findById(any());
    }

    @Test
    void deveCriarUmaCategoriaComCategoriaPaiComSucesso()
    {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 1L);
        Categoria categoria = new Categoria(null, "Vendas", null, null, null, null);
        Categoria categoriaPai = new Categoria(1L, "Eletrônicos", "eletronicos-1", LocalDateTime.now(), LocalDateTime.now(), null);
        CategoriaPostResponseDTO responseDTO = new CategoriaPostResponseDTO(2L, "Vendas", "vendas-1", "eletronicos");

        Mockito.when(categoriaMapper.toEntity(dto)).thenReturn(categoria);
        Mockito.when(categoriaRepository.findById(1L)).thenReturn(java.util.Optional.of(categoriaPai));
        Mockito.when(categoriaRepository.save(any(Categoria.class))).thenReturn(new Categoria
            (2L, "Vendas", "temporario", LocalDateTime.now(), LocalDateTime.now(), categoriaPai))
            .thenReturn(new Categoria(
                2L, "Vendas", "vendas-1", LocalDateTime.now(), LocalDateTime.now(), categoriaPai
            ));
        Mockito.when(slugify.slugify(any(String.class))).thenReturn("vendas-1");
        Mockito.when(categoriaMapper.toPostResponseDTO(any(Categoria.class))).thenReturn(responseDTO);

        CategoriaPostResponseDTO response = categoriaService.salvarCategoria(dto);

        assertAll(
            () -> assertEquals(response.id(), 2L),
            () -> assertEquals(response.nome(), "Vendas"),
            () -> assertEquals(response.slug(), "vendas-1"),
            () -> assertEquals(responseDTO.slugCategoriaPai(), "eletronicos")
        );

        Mockito.verify(categoriaMapper, times(1)).toEntity(dto);
        Mockito.verify(categoriaMapper, times(1)).toPostResponseDTO(any(Categoria.class));
        Mockito.verify(categoriaRepository, times(2)).save(any(Categoria.class));
        Mockito.verify(slugify, only()).slugify(any(String.class));
        Mockito.verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarErroQuandoCategoriaPaiNaoEncontrada()
    {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 1L);
        Categoria categoria = new Categoria(null, "Vendas", null, null, null, null);

        Mockito.when(categoriaMapper.toEntity(dto)).thenReturn(categoria);
        Mockito.when(categoriaRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        String message = assertThrows(FatherCategoryNotFoundException.class, () -> {
            categoriaService.salvarCategoria(dto);
            fail("Deveria ter retornado um erro aqui");
        }).getMessage();

        assertEquals("Category's father not found", message);

        Mockito.verify(categoriaMapper, times(1)).toEntity(dto);
        Mockito.verify(categoriaRepository, times(1)).findById(1L);
        Mockito.verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void deveRetornarUmaCategoriaPorSlugComSucesso()
    {
        String slug = "vendas-1";
        Categoria categoria = new Categoria(1L, "Vendas", slug, LocalDateTime.now(), LocalDateTime.now(), null);
        CategoriaPostResponseDTO responseDTO = new CategoriaPostResponseDTO(1L, "Vendas", slug, null);

        Mockito.when(categoriaRepository.findBySlug(slug)).thenReturn(java.util.Optional.of(categoria));
        Mockito.when(categoriaMapper.toPostResponseDTO(categoria)).thenReturn(responseDTO);

        CategoriaPostResponseDTO response = categoriaService.retornarCategoriaPorSlug(slug);

        assertAll(
            () -> assertEquals(response.id(), 1L),
            () -> assertEquals(response.nome(), "Vendas"),
            () -> assertEquals(response.slug(), slug),
            () -> assertEquals(response.slugCategoriaPai(), null)
        );

        Mockito.verify(categoriaRepository, only()).findBySlug(slug);
        Mockito.verify(categoriaMapper, only()).toPostResponseDTO(categoria);
    }

    @Test 
    void deveRetornarCategoriaNaoEncontradaExceptionQuandoSlugDaCategoriaNaoExistir()
    {
        String slug = "vendas-1";

        Mockito.when(categoriaRepository.findBySlug(slug)).thenReturn(java.util.Optional.empty());

        String message = assertThrows(com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException.class, () -> {
            categoriaService.retornarCategoriaPorSlug(slug);
            fail("Deveria ter retornado um erro aqui");
        }).getMessage();

        assertEquals("Categoria não encontrada", message);

        Mockito.verify(categoriaRepository, only()).findBySlug(slug);
        Mockito.verify(categoriaMapper, never()).toPostResponseDTO(any(Categoria.class));
    }

    @Test 
    void deveRetornarErroQuandoCategoriaHierarquiaJaExiste()
    {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 1L);
        Categoria categoria = new Categoria(null, "Vendas", null, null, null, null);
        Categoria categoriaExistente = new Categoria(2L, "Vendas", "vendas-2", LocalDateTime.now(), LocalDateTime.now(), null);

        Mockito.when(categoriaMapper.toEntity(dto)).thenReturn(categoria);
        Mockito.when(categoriaRepository.findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai()))
            .thenReturn(java.util.Optional.of(categoriaExistente));

        String message = assertThrows(com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriasComHierarquiaJaExisteException.class, () -> {
            categoriaService.salvarCategoria(dto);
            fail("Deveria ter retornado um erro aqui");
        }).getMessage();

        assertEquals("Já existe uma categoria com o mesmo nome e hierarquia", message);

        Mockito.verify(categoriaMapper, times(1)).toEntity(dto);
        Mockito.verify(categoriaRepository, only()).findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai());
        Mockito.verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void deveAtualizarUmaCategoriaComSucessoAlterandoNomeECategoriaPai()
    {
        String slugAntigo = "vendas-1";
        String novoSlug = "promocoes-1";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Promoções", 2L);

        Categoria categoriaExistente = new Categoria(1L, "Vendas", slugAntigo, LocalDateTime.now(), LocalDateTime.now(), null);
        Categoria novaCategoriaPai = new Categoria(2L, "Eletrônicos", "eletronicos-2", LocalDateTime.now(), LocalDateTime.now(), null);
        Categoria categoriaAtualizada = new Categoria(1L, "Promoções", novoSlug, LocalDateTime.now(), LocalDateTime.now(), novaCategoriaPai);
        CategoriaPostResponseDTO responseDTO = new CategoriaPostResponseDTO(1L, "Promoções", novoSlug, "eletronicos-2");

        Mockito.when(categoriaRepository.findBySlug(slugAntigo)).thenReturn(java.util.Optional.of(categoriaExistente));
        Mockito.when(categoriaRepository.findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai())).thenReturn(java.util.Optional.empty());
        Mockito.when(categoriaRepository.findById(2L)).thenReturn(java.util.Optional.of(novaCategoriaPai));
        Mockito.when(slugify.slugify("Promoções 1")).thenReturn(novoSlug);
        Mockito.when(categoriaRepository.save(categoriaExistente)).thenReturn(categoriaAtualizada);
        Mockito.when(categoriaMapper.toPostResponseDTO(categoriaAtualizada)).thenReturn(responseDTO);

        CategoriaPostResponseDTO response = categoriaService.atualizarCategoria(slugAntigo, dto);

        assertAll(
            () -> assertEquals(1L, response.id()),
            () -> assertEquals("Promoções", response.nome()),
            () -> assertEquals(novoSlug, response.slug()),
            () -> assertEquals("eletronicos-2", response.slugCategoriaPai())
        );

        Mockito.verify(categoriaRepository, times(1)).findBySlug(slugAntigo);
        Mockito.verify(categoriaRepository, times(1)).findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai());
        Mockito.verify(categoriaRepository, times(1)).findById(2L);
        Mockito.verify(slugify, times(1)).slugify("Promoções 1");
        Mockito.verify(categoriaRepository, times(1)).save(categoriaExistente);
        Mockito.verify(categoriaMapper, times(1)).toPostResponseDTO(categoriaAtualizada);
    }

    @Test
    void deveAtualizarUmaCategoriaMantendoOMesmoNomeSemRecalcularSlugERemovendoCategoriaPai()
    {
        String slugAtual = "vendas-1";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", null);

        Categoria categoriaPaiAntiga = new Categoria(2L, "Eletrônicos", "eletronicos-2", LocalDateTime.now(), LocalDateTime.now(), null);
        Categoria categoriaExistente = new Categoria(1L, "Vendas", slugAtual, LocalDateTime.now(), LocalDateTime.now(), categoriaPaiAntiga);
        CategoriaPostResponseDTO responseDTO = new CategoriaPostResponseDTO(1L, "Vendas", slugAtual, null);

        Mockito.when(categoriaRepository.findBySlug(slugAtual)).thenReturn(java.util.Optional.of(categoriaExistente));
        // Simula o repositório retornando a própria categoria na verificação de hierarquia
        Mockito.when(categoriaRepository.findByNomeAndCategoriaPaiId("Vendas", null)).thenReturn(java.util.Optional.of(categoriaExistente));
        Mockito.when(categoriaRepository.save(categoriaExistente)).thenReturn(categoriaExistente);
        Mockito.when(categoriaMapper.toPostResponseDTO(categoriaExistente)).thenReturn(responseDTO);

        CategoriaPostResponseDTO response = categoriaService.atualizarCategoria(slugAtual, dto);

        assertAll(
            () -> assertEquals(1L, response.id()),
            () -> assertEquals("Vendas", response.nome()),
            () -> assertEquals(slugAtual, response.slug()),
            () -> assertEquals(null, response.slugCategoriaPai()),
            () -> assertEquals(null, categoriaExistente.getCategoriaPai())
        );

        Mockito.verify(categoriaRepository, times(1)).findBySlug(slugAtual);
        Mockito.verify(categoriaRepository, times(1)).findByNomeAndCategoriaPaiId("Vendas", null);
        Mockito.verify(categoriaRepository, never()).findById(any());
        Mockito.verify(slugify, never()).slugify(any());
        Mockito.verify(categoriaRepository, times(1)).save(categoriaExistente);
        Mockito.verify(categoriaMapper, times(1)).toPostResponseDTO(categoriaExistente);
    }

    @Test
    void deveRetornarErroAoAtualizarQuandoCategoriaNaoEncontrada()
    {
        String slug = "inexistente-99";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", null);

        Mockito.when(categoriaRepository.findBySlug(slug)).thenReturn(java.util.Optional.empty());

        String message = assertThrows(com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException.class, () -> {
            categoriaService.atualizarCategoria(slug, dto);
            fail("Deveria ter retornado um erro aqui");
        }).getMessage();

        assertEquals("Categoria não encontrada", message);

        Mockito.verify(categoriaRepository, only()).findBySlug(slug);
        Mockito.verify(categoriaRepository, never()).save(any(Categoria.class));
        Mockito.verify(categoriaMapper, never()).toPostResponseDTO(any(Categoria.class));
    }

    @Test
    void deveRetornarErroAoAtualizarQuandoOutraCategoriaComMesmaHierarquiaJaExiste()
    {
        String slug = "vendas-1";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Eletrônicos", 2L);

        Categoria categoriaAtual = new Categoria(1L, "Vendas", slug, LocalDateTime.now(), LocalDateTime.now(), null);
        Categoria outraCategoriaConflitante = new Categoria(3L, "Eletrônicos", "eletronicos-3", LocalDateTime.now(), LocalDateTime.now(), null);

        Mockito.when(categoriaRepository.findBySlug(slug)).thenReturn(java.util.Optional.of(categoriaAtual));
        Mockito.when(categoriaRepository.findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai()))
            .thenReturn(java.util.Optional.of(outraCategoriaConflitante));

        String message = assertThrows(com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriasComHierarquiaJaExisteException.class, () -> {
            categoriaService.atualizarCategoria(slug, dto);
            fail("Deveria ter retornado um erro aqui");
        }).getMessage();

        assertEquals("Já existe uma categoria com o mesmo nome e hierarquia", message);

        Mockito.verify(categoriaRepository, times(1)).findBySlug(slug);
        Mockito.verify(categoriaRepository, times(1)).findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai());
        Mockito.verify(categoriaRepository, never()).findById(any());
        Mockito.verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void deveRetornarErroAoAtualizarQuandoCategoriaForPaiDeSiMesma()
    {
        String slug = "vendas-1";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 1L);

        Categoria categoriaAtual = new Categoria(1L, "Vendas", slug, LocalDateTime.now(), LocalDateTime.now(), null);

        Mockito.when(categoriaRepository.findBySlug(slug)).thenReturn(java.util.Optional.of(categoriaAtual));
        Mockito.when(categoriaRepository.findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai())).thenReturn(java.util.Optional.empty());

        String message = assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.atualizarCategoria(slug, dto);
            fail("Deveria ter retornado um erro aqui");
        }).getMessage();

        assertEquals("Uma categoria não pode ser pai dela mesma", message);

        Mockito.verify(categoriaRepository, times(1)).findBySlug(slug);
        Mockito.verify(categoriaRepository, times(1)).findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai());
        Mockito.verify(categoriaRepository, never()).findById(any());
        Mockito.verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void deveRetornarErroAoAtualizarQuandoCategoriaPaiNaoForEncontrada()
    {
        String slug = "vendas-1";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 99L);

        Categoria categoriaAtual = new Categoria(1L, "Vendas", slug, LocalDateTime.now(), LocalDateTime.now(), null);

        Mockito.when(categoriaRepository.findBySlug(slug)).thenReturn(java.util.Optional.of(categoriaAtual));
        Mockito.when(categoriaRepository.findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai())).thenReturn(java.util.Optional.empty());
        Mockito.when(categoriaRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        String message = assertThrows(FatherCategoryNotFoundException.class, () -> {
            categoriaService.atualizarCategoria(slug, dto);
            fail("Deveria ter retornado um erro aqui");
        }).getMessage();

        assertEquals("Category's father not found", message);

        Mockito.verify(categoriaRepository, times(1)).findBySlug(slug);
        Mockito.verify(categoriaRepository, times(1)).findByNomeAndCategoriaPaiId(dto.nome(), dto.idCategoriaPai());
        Mockito.verify(categoriaRepository, times(1)).findById(99L);
        Mockito.verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void deveDeletarUmaCategoriaComSucesso()
    {
        String slug = "vendas-1";
        Categoria categoria = new Categoria(1L, "Vendas", slug, LocalDateTime.now(), LocalDateTime.now(), null);

        Mockito.when(categoriaRepository.findBySlug(slug)).thenReturn(java.util.Optional.of(categoria));

        categoriaService.deletarCategoria(slug);

        Mockito.verify(categoriaRepository, times(1)).findBySlug(slug);
        Mockito.verify(categoriaRepository, times(1)).delete(categoria);
    }

    @Test
    void deveRetornarErroAoDeletarQuandoCategoriaNaoEncontrada()
    {
        String slug = "inexistente-99";

        Mockito.when(categoriaRepository.findBySlug(slug)).thenReturn(java.util.Optional.empty());

        String message = assertThrows(com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException.class, () -> {
            categoriaService.deletarCategoria(slug);
            fail("Deveria ter retornado um erro aqui");
        }).getMessage();

        assertEquals("Categoria não encontrada", message);

        Mockito.verify(categoriaRepository, only()).findBySlug(slug);
        Mockito.verify(categoriaRepository, never()).delete(any(Categoria.class));
    }
}
