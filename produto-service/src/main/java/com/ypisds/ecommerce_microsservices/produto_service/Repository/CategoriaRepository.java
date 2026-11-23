package com.ypisds.ecommerce_microsservices.produto_service.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ypisds.ecommerce_microsservices.produto_service.Model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
    Optional<Categoria> findBySlug(String slug);

    @Query("select c from Categoria c where c.nome = :nome and ((c.categoriaPai is null and :idCategoriaPai is null) or (c.categoriaPai.id = :idCategoriaPai))")
    Optional<Categoria> findByNomeAndCategoriaPaiId(@Param("nome") String nome, @Param("idCategoriaPai") Long idCategoriaPai);
}
