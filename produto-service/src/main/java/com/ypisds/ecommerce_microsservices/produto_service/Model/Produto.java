package com.ypisds.ecommerce_microsservices.produto_service.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PRODUTO")
@AllArgsConstructor
@NoArgsConstructor 
@Getter
@Setter
@EqualsAndHashCode
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUTO")
    private Long id;

    @Column(name = "NM_PRODUTO", length = 150, nullable = false)
    private String nome;

    @Column(name = "NM_SLUG_PRODUTO", length = 250, unique = true, nullable = false)
    private String slug;

    @Column(name = "NM_DESCRICAO", length = 500, nullable = false)
    private String descricao;

    @Column(name = "PRECO", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "IN_ATIVO", nullable = false)
    private boolean isAtivo = true;

    @ManyToOne
    @JoinColumn(
        name = "ID_CATEGORIA",
        nullable = false,
        foreignKey = @ForeignKey(name= "FK_PRODUTO_1")
    )
    private Categoria categoria;

    @Column(name = "DT_DATA_CRIACAO", nullable = false)
    private LocalDateTime dhCriacao;

    @Column(name = "DT_DATA_ULTIMA_ALTERACAO", nullable = false)
    private LocalDateTime dhUltimaAlteracao;

    @PrePersist
    protected void onCreate()
    {
        LocalDateTime now = LocalDateTime.now();
        this.dhCriacao = now;
        this.dhUltimaAlteracao = now;
    }

    @PreUpdate 
    protected void onUpdate()
    {
        this.dhUltimaAlteracao = LocalDateTime.now();
    }
    
}
