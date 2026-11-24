package com.ypisds.ecommerce_microsservices.produto_service.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ypisds.ecommerce_microsservices.produto_service.Model.ProdutoFoto;


public interface ProdutoFotoRepository extends JpaRepository<ProdutoFoto, Long> {
    boolean existsByPrincipalAndProdutoId(boolean principal, Long produtoId);

    @Query ("SELECT pf FROM ProdutoFoto pf WHERE pf.produto.id = :produtoId AND pf.principal = true")
    Optional<ProdutoFoto> buscarFotoPrincipalPorProdutoId(@Param("produtoId") Long produtoId);
}
