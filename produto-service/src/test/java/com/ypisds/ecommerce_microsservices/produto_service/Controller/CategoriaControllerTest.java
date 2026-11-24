package com.ypisds.ecommerce_microsservices.produto_service.Controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaTreeResponseDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriaNaoEncontradaException;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.CategoriasComHierarquiaJaExisteException;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.FatherCategoryNotFoundException;
import com.ypisds.ecommerce_microsservices.produto_service.Service.CategoriaService;

import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private CategoriaService categoriaService;

    // =========================================================================
    // TESTES DE CRIAÇÃO (POST /categories)
    // =========================================================================

    @Test
    void deveCriarUmaCategoriaSemCategoriaPaiComSucesso() throws Exception {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", null);

        Mockito.when(categoriaService.salvarCategoria(dto)).thenReturn(new CategoriaPostResponseDTO(
            1L, "Vendas", "vendas-1", null
        ));

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.nome").value("Vendas"))
        .andExpect(jsonPath("$.slug").value("vendas-1"))
        .andExpect(jsonPath("$.slugCategoriaPai").value((String) null));
    }

    @Test
    void deveCriarUmaCategoriaComCategoriaPaiComSucesso() throws Exception {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 1L);

        Mockito.when(categoriaService.salvarCategoria(dto)).thenReturn(new CategoriaPostResponseDTO(
            2L, "Vendas", "vendas-2", "vendas-1"
        ));

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(2L))
        .andExpect(jsonPath("$.nome").value("Vendas"))
        .andExpect(jsonPath("$.slug").value("vendas-2"))
        .andExpect(jsonPath("$.slugCategoriaPai").value("vendas-1"));
    }

    @Test
    void deveRetornarErroAoTentarCriarUmaCategoriaComCategoriaPaiInexistente() throws Exception {
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 1L);

        Mockito.when(categoriaService.salvarCategoria(dto))
            .thenThrow(new FatherCategoryNotFoundException("Category's father not found"));

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
        .andExpect(jsonPath("$.detail").value("Category's father not found"))
        .andExpect(jsonPath("$.type").value("https://api.ecommerce.com/errors/not-found"))
        .andExpect(jsonPath("$.timestamp").exists());
    }

    // =========================================================================
    // TESTES DE BUSCA POR SLUG (GET /categories/{slug})
    // =========================================================================

    @Test
    void deveRetornarCategoriaPorSlugComSucesso() throws Exception {
        String slug = "vendas-1";

        Mockito.when(categoriaService.retornarCategoriaPorSlug(slug)).thenReturn(new CategoriaPostResponseDTO(
            1L, "Vendas", slug, null
        ));

        mockMvc.perform(get("/categories/{slug}", slug)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.nome").value("Vendas"))
        .andExpect(jsonPath("$.slug").value(slug))
        .andExpect(jsonPath("$.slugCategoriaPai").value((String) null));
    }

    @Test
    void deveRetornarErroQuandoCategoriaPorSlugNaoForEncontrada() throws Exception {
        String slug = "inexistente-99";

        Mockito.when(categoriaService.retornarCategoriaPorSlug(slug))
            .thenThrow(new CategoriaNaoEncontradaException("Categoria não encontrada"));

        mockMvc.perform(get("/categories/{slug}", slug)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
        .andExpect(jsonPath("$.detail").value("Categoria não encontrada"))
        .andExpect(jsonPath("$.type").value("https://api.ecommerce.com/errors/not-found"))
        .andExpect(jsonPath("$.timestamp").exists());
    }

    // =========================================================================
    // TESTES DE ATUALIZAÇÃO (PUT /categories/{slug})
    // =========================================================================

    @Test
    void deveAtualizarUmaCategoriaComSucesso() throws Exception {
        String slugAntigo = "vendas-1";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Promoções", 2L);

        Mockito.when(categoriaService.atualizarCategoria(slugAntigo, dto)).thenReturn(new CategoriaPostResponseDTO(
            1L, "Promoções", "promocoes-1", "eletronicos-2"
        ));

        mockMvc.perform(put("/categories/{slug}", slugAntigo)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.nome").value("Promoções"))
        .andExpect(jsonPath("$.slug").value("promocoes-1"))
        .andExpect(jsonPath("$.slugCategoriaPai").value("eletronicos-2"));
    }

    @Test
    void deveRetornarErroAoTentarAtualizarCategoriaInexistente() throws Exception {
        String slug = "inexistente-99";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Promoções", null);

        Mockito.when(categoriaService.atualizarCategoria(slug, dto))
            .thenThrow(new CategoriaNaoEncontradaException("Categoria não encontrada"));

        mockMvc.perform(put("/categories/{slug}", slug)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
        .andExpect(jsonPath("$.detail").value("Categoria não encontrada"))
        .andExpect(jsonPath("$.type").value("https://api.ecommerce.com/errors/not-found"))
        .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornarErroAoTentarAtualizarCategoriaComCategoriaPaiInexistente() throws Exception {
        String slug = "vendas-1";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Vendas", 99L);

        Mockito.when(categoriaService.atualizarCategoria(slug, dto))
            .thenThrow(new FatherCategoryNotFoundException("Category's father not found"));

        mockMvc.perform(put("/categories/{slug}", slug)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
        .andExpect(jsonPath("$.detail").value("Category's father not found"))
        .andExpect(jsonPath("$.type").value("https://api.ecommerce.com/errors/not-found"))
        .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornarErroAoTentarAtualizarCategoriaComHierarquiaDuplicada() throws Exception {
        String slug = "vendas-1";
        CategoriaPostRequestDTO dto = new CategoriaPostRequestDTO("Eletrônicos", 2L);

        Mockito.when(categoriaService.atualizarCategoria(slug, dto))
            .thenThrow(new CategoriasComHierarquiaJaExisteException("Já existe uma categoria com o mesmo nome e hierarquia"));

        mockMvc.perform(put("/categories/{slug}", slug)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.title").value("Conflito de hierarquia de categoria"))
        .andExpect(jsonPath("$.detail").value("Já existe uma categoria com o mesmo nome e hierarquia"))
        .andExpect(jsonPath("$.type").value("https://api.ecommerce.com/errors/conflict"))
        .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornarBadRequestQuandoPayloadDeAtualizacaoForInvalido() throws Exception {
        String slug = "vendas-1";
        CategoriaPostRequestDTO dtoInvalido = new CategoriaPostRequestDTO("", null);

        mockMvc.perform(put("/categories/{slug}", slug)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dtoInvalido))
        )
        .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(categoriaService);
    }

    // =========================================================================
    // TESTES DE EXCLUSÃO (DELETE /categories/{slug})
    // =========================================================================

    @Test
    void deveDeletarUmaCategoriaComSucesso() throws Exception {
        String slug = "vendas-1";

        Mockito.doNothing().when(categoriaService).deletarCategoria(slug);

        mockMvc.perform(delete("/categories/{slug}", slug))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));

        Mockito.verify(categoriaService, Mockito.times(1)).deletarCategoria(slug);
    }

    @Test
    void deveRetornarErroAoTentarDeletarCategoriaInexistente() throws Exception {
        String slug = "inexistente-99";

        Mockito.doThrow(new CategoriaNaoEncontradaException("Categoria não encontrada"))
            .when(categoriaService).deletarCategoria(slug);

        mockMvc.perform(delete("/categories/{slug}", slug))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
        .andExpect(jsonPath("$.detail").value("Categoria não encontrada"))
        .andExpect(jsonPath("$.type").value("https://api.ecommerce.com/errors/not-found"))
        .andExpect(jsonPath("$.timestamp").exists());
    }

    // =========================================================================
    // TESTES DE LISTAGEM EM ÁRVORE (GET /categories)
    // =========================================================================

    @Test
    void deveListarCategoriasEmArvoreComSucesso() throws Exception {
        var neto = new CategoriaTreeResponseDTO(
            3L, "Notebooks Gamer", "notebooks-gamer-3", List.of()
        );
        var filho = new CategoriaTreeResponseDTO(
            2L, "Notebooks", "notebooks-2", List.of(neto)
        );
        var raiz = new CategoriaTreeResponseDTO(
            1L, "Eletrônicos", "eletronicos-1", List.of(filho)
        );

        Mockito.when(categoriaService.listarCategoriasEmArvore()).thenReturn(List.of(raiz));

        mockMvc.perform(get("/categories")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].nome").value("Eletrônicos"))
        .andExpect(jsonPath("$[0].slug").value("eletronicos-1"))
        .andExpect(jsonPath("$[0].subcategorias[0].id").value(2L))
        .andExpect(jsonPath("$[0].subcategorias[0].nome").value("Notebooks"))
        .andExpect(jsonPath("$[0].subcategorias[0].subcategorias[0].id").value(3L))
        .andExpect(jsonPath("$[0].subcategorias[0].subcategorias[0].nome").value("Notebooks Gamer"));
    }

    @Test
    void deveRetornarListaVaziaAoListarCategoriasQuandoNaoHouverRegistros() throws Exception {
        Mockito.when(categoriaService.listarCategoriasEmArvore()).thenReturn(List.of());

        mockMvc.perform(get("/categories")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
    }
}