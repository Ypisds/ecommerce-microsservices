ALTER TABLE produto_foto ADD CONSTRAINT UK_PRODUTO_FOTO_1 UNIQUE (ds_url);

CREATE UNIQUE INDEX IF NOT EXISTS uk_imagem_principal_por_produto
    ON produto_foto (produto_id)
    WHERE principal IS TRUE