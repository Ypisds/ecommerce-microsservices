package com.ypisds.ecommerce_microsservices.produto_service.Service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ypisds.ecommerce_microsservices.produto_service.Component.MinIOService;
import com.ypisds.ecommerce_microsservices.produto_service.Exception.Exceptions.ProdutoFotoPrincipalNaoEncontradaException;
import com.ypisds.ecommerce_microsservices.produto_service.Model.Produto;
import com.ypisds.ecommerce_microsservices.produto_service.Model.ProdutoFoto;
import com.ypisds.ecommerce_microsservices.produto_service.Repository.ProdutoFotoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor
@Slf4j
public class ProdutoFotoService {
    
    private static final String bucketName = "produto-fotos";

    private final ProdutoFotoRepository produtoFotoRepository;

    private final MinIOService minIOService;

    @Value ("${minio.auto-create-bucket:true}")
    private boolean autoCreateBucket;

    @PostConstruct
    public void init() {
        if (autoCreateBucket) {
            minIOService.createBucketIfNotExists(bucketName);
        }
    }

    @Transactional(rollbackFor = IOException.class)
    public ProdutoFoto uploadFoto(MultipartFile file, Produto produto) throws IOException {
        String url = minIOService.uploadFile(bucketName, file);
        boolean principal = !produtoFotoRepository.existsByPrincipalAndProdutoId(true, produto.getId());

        ProdutoFoto produtoFoto = new ProdutoFoto();
        produtoFoto.setUrl(url);
        produtoFoto.setProduto(produto);
        produtoFoto.setPrincipal(principal);

        return produtoFotoRepository.save(produtoFoto);
    }

    public String getPrincipalPresignedUrlByProdutoId(Long produtoId) {
        ProdutoFoto fotoPrincipal = produtoFotoRepository.buscarFotoPrincipalPorProdutoId(produtoId)
                            .orElseThrow(() -> new ProdutoFotoPrincipalNaoEncontradaException(produtoId));

        return minIOService.createPresignedGetUrl(bucketName, fotoPrincipal.getUrl());
    }
}
