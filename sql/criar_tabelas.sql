CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nome_usuario VARCHAR(100) NOT NULL,
    senha_usuario VARCHAR(255) NOT NULL
);

CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nome_cliente VARCHAR(100) NOT NULL,
    email_cliente VARCHAR(255) NOT NULL,
    telefone_cliente VARCHAR(15) NOT NULL
);

CREATE TABLE veiculos (
    id_veiculo SERIAL PRIMARY KEY,
    placa_veiculo VARCHAR(12) NOT NULL,
    modelo_veiculo VARCHAR(100) NOT NULL,
    marca_veiculo VARCHAR(100) NOT NULL,
    cor_veiculo VARCHAR(100) NOT NULL
);

CREATE TABLE locacao (
    id_locacao SERIAL PRIMARY KEY,
    id_veiculo INT NOT NULL,
    id_cliente INT NOT NULL,
    dt_inicio DATE NOT NULL,
    dt_prevista DATE NULL,
    dt_devolucao DATE NULL,
    status_locacao_veiculo VARCHAR(50) NOT NULL DEFAULT 'DISPONIVEL',

    CHECK (status_locacao_veiculo IN ('LOCADO', 'DISPONIVEL')),

    FOREIGN KEY (id_veiculo) REFERENCES veiculos(id_veiculo),
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE faturamento (
    id_faturamento SERIAL PRIMARY KEY,
    id_locacao INT NOT NULL,
    valor_diaria NUMERIC(10,2) NOT NULL,
    qtd_diarias INT NOT NULL,
    valor_total NUMERIC(10,2) NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    status_pagamento VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    dt_pagamento DATE NULL,

    CHECK (valor_diaria >= 0),
    CHECK (qtd_diarias > 0),
    CHECK (valor_total >= 0),
    CHECK (forma_pagamento IN ('PIX', 'DINHEIRO', 'CARTAO_CREDITO', 'CARTAO_DEBITO')),
    CHECK (status_pagamento IN ('PENDENTE', 'PAGO', 'CANCELADO')),

    FOREIGN KEY (id_locacao) REFERENCES locacao(id_locacao)
);