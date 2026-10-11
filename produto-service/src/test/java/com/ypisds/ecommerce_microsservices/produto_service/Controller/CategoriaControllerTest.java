package com.ypisds.ecommerce_microsservices.produto_service.Controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ypisds.ecommerce_microsservices.produto_service.Dto.Request.CategoriaPostRequestDTO;
import com.ypisds.ecommerce_microsservices.produto_service.Dto.Response.CategoriaPostResponseDTO;
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

        Mockito.when(categoriaService.salvarCategoria(dto)).thenThrow(new FatherCategoryNotFoundException("Category's father not found"));

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
            )
        .andExpect(status().isNotFound())
        .andExpect(content().string("Category's father not found"));
    }
}
