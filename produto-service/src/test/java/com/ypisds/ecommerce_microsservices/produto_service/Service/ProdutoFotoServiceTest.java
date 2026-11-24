package com.ypisds.ecommerce_microsservices.produto_service.Service;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ypisds.ecommerce_microsservices.produto_service.Component.MinIOService;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.ProdutoFotoPrincipalNaoEncontradaException;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Produto;
import com.ypisds.ecommerce_microsservices.produto_service.Model.ProdutoFoto;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.ProdutoFotoRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProdutoFotoServiceTest {

    private static final String BUCKET_NAME = "produto-fotos";

    @Mock
    private ProdutoFotoRepository produtoFotoRepository;

    @Mock
    private MinIOService minIOService;

    @InjectMocks
    private ProdutoFotoService produtoFotoService;

    private Produto produto;
    private MultipartFile arquivoMock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(produtoFotoService, "autoCreateBucket", true);
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook Gamer");

        arquivoMock = new MockMultipartFile(
            "file",
            "notebook.png",
            "image/png",
            "conteudo-falso-da-imagem".getBytes()
        );
    }

    // =========================================================================
    // TESTES DE INICIALIZAÇÃO (@PostConstruct)
    // =========================================================================

    @Test
    void deveCriarBucketNaInicializacaoSeNaoExistir() {
        produtoFotoService.init();

        verify(minIOService, times(1)).createBucketIfNotExists(BUCKET_NAME);
    }

    // =========================================================================
    // TESTES DE UPLOAD DE FOTO (uploadFoto)
    // =========================================================================

    @Test
    void deveFazerUploadEDefinirComoPrincipalQuandoProdutoAindaNaoPossuiFotoPrincipal() throws IOException {
        String keyGerada = "uuid-123_notebook.png";

        when(minIOService.uploadFile(BUCKET_NAME, arquivoMock)).thenReturn(keyGerada);
        when(produtoFotoRepository.existsByPrincipalAndProdutoId(true, 1L)).thenReturn(false);
        when(produtoFotoRepository.save(any(ProdutoFoto.class))).thenAnswer(invocation -> {
            ProdutoFoto foto = invocation.getArgument(0);
            foto.setId(10L);
            return foto;
        });

        ProdutoFoto resultado = produtoFotoService.uploadFoto(arquivoMock, produto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(keyGerada, resultado.getUrl());
        assertEquals(produto, resultado.getProduto());
        assertTrue(resultado.isPrincipal(), "A primeira foto do produto deve ser marcada como principal");

        // Garante que o objeto passado ao repository tinha exatamente os valores esperados
        ArgumentCaptor<ProdutoFoto> captor = ArgumentCaptor.forClass(ProdutoFoto.class);
        verify(produtoFotoRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().isPrincipal());
        assertEquals(keyGerada, captor.getValue().getUrl());
    }

    @Test
    void deveFazerUploadEDefinirComoSecundariaQuandoProdutoJaPossuiFotoPrincipal() throws IOException {
        String keyGerada = "uuid-456_notebook-lateral.png";

        when(minIOService.uploadFile(BUCKET_NAME, arquivoMock)).thenReturn(keyGerada);
        when(produtoFotoRepository.existsByPrincipalAndProdutoId(true, 1L)).thenReturn(true);
        when(produtoFotoRepository.save(any(ProdutoFoto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProdutoFoto resultado = produtoFotoService.uploadFoto(arquivoMock, produto);

        assertNotNull(resultado);
        assertEquals(keyGerada, resultado.getUrl());
        assertEquals(produto, resultado.getProduto());
        assertFalse(resultado.isPrincipal(), "Fotos subsequentes não devem ser marcadas como principal");

        verify(produtoFotoRepository, times(1)).save(any(ProdutoFoto.class));
    }

    @Test
    void devePropagarIOExceptionENaoSalvarNoBancoQuandoFalharUploadNoMinIO() throws IOException {
        when(minIOService.uploadFile(BUCKET_NAME, arquivoMock)).thenThrow(new IOException("Falha de rede com MinIO"));

        assertThrows(IOException.class, () -> produtoFotoService.uploadFoto(arquivoMock, produto));

        verify(produtoFotoRepository, never()).existsByPrincipalAndProdutoId(any(Boolean.class), any());
        verify(produtoFotoRepository, never()).save(any());
    }

    // =========================================================================
    // TESTES DE BUSCA DE PRESIGNED URL (getPrincipalPresignedUrlByProdutoId)
    // =========================================================================

    @Test
    void deveRetornarPresignedUrlDaFotoPrincipalComSucesso() {
        Long produtoId = 1L;
        String keyFoto = "uuid-123_notebook.png";
        String presignedUrlEsperada = "http://localhost:9000/produto-fotos/uuid-123_notebook.png?X-Amz-Signature=abc";

        ProdutoFoto fotoPrincipal = new ProdutoFoto();
        fotoPrincipal.setId(10L);
        fotoPrincipal.setUrl(keyFoto);
        fotoPrincipal.setPrincipal(true);
        fotoPrincipal.setProduto(produto);

        when(produtoFotoRepository.buscarFotoPrincipalPorProdutoId(produtoId)).thenReturn(Optional.of(fotoPrincipal));
        when(minIOService.createPresignedGetUrl(BUCKET_NAME, keyFoto)).thenReturn(presignedUrlEsperada);

        String urlRetornada = produtoFotoService.getPrincipalPresignedUrlByProdutoId(produtoId);

        assertEquals(presignedUrlEsperada, urlRetornada);
        verify(produtoFotoRepository, times(1)).buscarFotoPrincipalPorProdutoId(produtoId);
        verify(minIOService, times(1)).createPresignedGetUrl(BUCKET_NAME, keyFoto);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoPossuirFotoPrincipal() {
        Long produtoId = 99L;

        when(produtoFotoRepository.buscarFotoPrincipalPorProdutoId(produtoId)).thenReturn(Optional.empty());

        assertThrows(
            ProdutoFotoPrincipalNaoEncontradaException.class,
            () -> produtoFotoService.getPrincipalPresignedUrlByProdutoId(produtoId)
        );

        verify(minIOService, never()).createPresignedGetUrl(any(), any());
    }
}