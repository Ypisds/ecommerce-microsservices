package com.ypisds.ecommerce_microsservices.produto_service.Model;

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
@Table(name = "CATEGORIA")
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Categoria {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CATEGORIA")
    private Long id;

    @Column(name = "NM_CATEGORIA", length = 50, nullable = false)
    private String nome;

    @Column(name = "NM_SLUG_CATEGORIA", length = 100, nullable = false, unique = true)
    private String slug;

    @Column(name = "DT_DATA_CRIACAO", nullable = false)
    private LocalDateTime dhCriacao;

    @Column(name = "DT_DATA_ULTIMA_ALTERACAO", nullable = false)
    private LocalDateTime dhUltimaAlteracao;

    @ManyToOne
    @JoinColumn(
        nullable = true,
        name = "ID_CATEGORIA_PAI",
        foreignKey = @ForeignKey(name = "FK_CATEGORIA_1")
    )
    private Categoria categoriaPai;

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
