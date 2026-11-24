package com.ypisds.ecommerce_microsservices.produto_service.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table (name = "PRODUTO_FOTO")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter
@EqualsAndHashCode(of = "id")
public class ProdutoFoto {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "ID_PRODUTO_FOTO")
    private Long id;

    @Column (name = "DS_URL", nullable = false, length = 500, unique = true)
    private String url;

    @Column (name = "IN_PRINCIPAL", nullable = false)
    private boolean principal = false;
    
    @ManyToOne(targetEntity = Produto.class)
    @JoinColumn (name = "ID_PRODUTO", nullable = false)
    private Produto produto;

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
    