package com.ypisds.ecommerce_microsservices.produto_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ypisds.ecommerce_microsservices.produto_service.Model.ProdutoFoto;

public interface ProdutoFotoRepository extends JpaRepository<ProdutoFoto, Long> {
    
}
