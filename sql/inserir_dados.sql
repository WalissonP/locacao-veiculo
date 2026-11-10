-- Inserindo clientes
INSERT INTO cliente (nome_cliente, email_cliente, telefone_cliente)
VALUES
    ('Ana Silva', 'ana@email.com', '27999999999'),
    ('João Santos', 'joao@email.com', '27988888888'),
    ('Maria Oliveira', 'maria@email.com', '27977777777');


-- Inserindo veículos
INSERT INTO veiculos (placa_veiculo, modelo_veiculo, marca_veiculo, cor_veiculo)
VALUES
    ('ABC1D23', 'Onix', 'Chevrolet', 'Branco'),
    ('DEF4G56', 'HB20', 'Hyundai', 'Preto'),
    ('GHI7J89', 'Corolla', 'Toyota', 'Prata');


-- Inserindo locações
INSERT INTO locacao (
    idVeiculo,
    idCliente,
    dtinicio,
    dtPrevista,
    dtDevolucao,
    status_locacao_veiculo
)
VALUES
    (1, 1, '2026-11-01', '2026-11-05', '2026-11-05', 'DISPONIVEL'),
    (2, 2, '2026-11-08', '2026-11-12', NULL, 'LOCADO');