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
