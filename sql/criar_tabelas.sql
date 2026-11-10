CREATE TABLE cliente (
	idCliente SERIAL PRIMARY KEY,
	nome_cliente VARCHAR(100) NOT NULL,
	email_cliente VARCHAR(255) NOT NULL,
	telefone_cliente VARCHAR(15) NOT NULL
)

CREATE TABLE veiculos (
	idVeiculo SERIAL PRIMARY KEY,
	placa_veiculo VARCHAR(12) NOT NULL,
	modelo_veiculo VARCHAR(100) NOT NULL,
	marca_veiculo VARCHAR(100) NOT NULL,
	cor_veiculo VARCHAR(100) NOT NULL
)

CREATE TABLE locacao (
    idLocacao SERIAL PRIMARY KEY,
    idVeiculo INT NOT NULL,
    idCliente INT NOT NULL,
    dtinicio DATE NOT NULL,
    dtPrevista DATE NULL,
    dtDevolucao DATE NULL,
    status_locacao_veiculo VARCHAR(50) NOT NULL DEFAULT 'DISPONIVEL',

    CHECK (status_locacao_veiculo IN ('LOCADO', 'DISPONIVEL')),
	
    FOREIGN KEY (idVeiculo) REFERENCES veiculos(idVeiculo),
    FOREIGN KEY (idCliente) REFERENCES cliente(idCliente)
);