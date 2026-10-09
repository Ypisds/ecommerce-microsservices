package com.ypisds.ecommerce_microsservices.produto_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ypisds.ecommerce_microsservices.produto_service.Model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
}
