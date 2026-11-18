package com.locadora.dao;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Locacao;
import com.locadora.relatorioDados.RelatorioDuracao;
import com.locadora.relatorioDados.RelatorioModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocacaoDAO {

    public int registrarLocacao(Locacao locacao, Connection conn) throws SQLException {

        String sql = "INSERT INTO locacao " +
                     "(id_veiculo, id_cliente, dt_inicio, dt_prevista, " +
                     "dt_devolucao, status_locacao_veiculo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, locacao.getVeiculo().getIdVeiculo());
            stmt.setInt(2, locacao.getCliente().getIdCliente());
            stmt.setDate(3, Date.valueOf(locacao.getDtInicio()));

            if (locacao.getDtPrevista() != null) {
                stmt.setDate(4, Date.valueOf(locacao.getDtPrevista()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            if (locacao.getDtDevolucao() != null) {
                stmt.setDate(5, Date.valueOf(locacao.getDtDevolucao()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, locacao.getStatusLocacaoVeiculo());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Falha ao obter o ID da locação cadastrada.");
    }

    public int registrarLocacao(Locacao locacao) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return registrarLocacao(locacao, conn);
        }
    }

    // Listar todas as locações
    public List<Locacao> listarTodas() throws SQLException {
        List<Locacao> locacoes = new ArrayList<>();

        String sql = "SELECT " +
                     "l.id_locacao, l.dt_inicio, l.dt_prevista, " +
                     "l.dt_devolucao, l.status_locacao_veiculo, " +
                     "c.id_cliente AS cliente_id, " +
                     "c.nome_cliente, c.email_cliente, c.telefone_cliente, " +
                     "v.id_veiculo AS veiculo_id, " +
                     "v.placa_veiculo, v.modelo_veiculo, " +
                     "v.marca_veiculo, v.cor_veiculo " +
                     "FROM locacao l " +
                     "JOIN cliente c ON l.id_cliente = c.id_cliente " +
                     "JOIN veiculos v ON l.id_veiculo = v.id_veiculo " +
                     "ORDER BY l.id_locacao ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Date dataInicio = rs.getDate("dt_inicio");
                Date dataPrevista = rs.getDate("dt_prevista");
                Date dataDevolucao = rs.getDate("dt_devolucao");

                Locacao locacao = new Locacao(
                    rs.getInt("id_locacao"),

                    new com.locadora.model.Veiculo(
                        rs.getInt("veiculo_id"),
                        rs.getString("placa_veiculo"),
                        rs.getString("modelo_veiculo"),
                        rs.getString("marca_veiculo"),
                        rs.getString("cor_veiculo")
                    ),

                    new com.locadora.model.Cliente(
                        rs.getInt("cliente_id"),
                        rs.getString("nome_cliente"),
                        rs.getString("email_cliente"),
                        rs.getString("telefone_cliente")
                    ),

                    dataInicio != null ? dataInicio.toLocalDate() : null,
                    dataPrevista != null ? dataPrevista.toLocalDate() : null,
                    dataDevolucao != null ? dataDevolucao.toLocalDate() : null,

                    rs.getString("status_locacao_veiculo")
                );

                locacoes.add(locacao);
            }
        }

        return locacoes;
    }

    // Verificar se o cliente existe
    public boolean existeCliente(int idCliente) throws SQLException {
        String sql = "SELECT 1 FROM cliente WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Verificar se o veículo existe
    public boolean existeVeiculo(int idVeiculo) throws SQLException {
        String sql = "SELECT 1 FROM veiculos WHERE id_veiculo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVeiculo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Verificar se o veículo está disponível
    public boolean veiculoDisponivel(int idVeiculo) throws SQLException {
        String sql = "SELECT 1 FROM locacao " +
                     "WHERE id_veiculo = ? " +
                     "AND status_locacao_veiculo = 'LOCADO' " +
                     "AND dt_devolucao IS NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVeiculo);

            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        }
    }

    // Registrar devolução do veículo
    public int registrarDevolucao(int idLocacao, Date dataDevolucao) throws SQLException {
        String sql = "UPDATE locacao " +
                     "SET dt_devolucao = ?, " +
                     "status_locacao_veiculo = 'DISPONIVEL' " +
                     "WHERE id_locacao = ? " +
                     "AND status_locacao_veiculo = 'LOCADO' " +
                     "AND dt_devolucao IS NULL " +
                     "AND ? >= dt_inicio";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, dataDevolucao);
            stmt.setInt(2, idLocacao);
            stmt.setDate(3, dataDevolucao);

            return stmt.executeUpdate();
        }
    }

    // Relatório 1: Locações por modelo de veículo
    public List<RelatorioModelo> obterLocacoesPorModelo() throws SQLException {
        List<RelatorioModelo> lista = new ArrayList<>();

        String sql = "SELECT v.modelo_veiculo AS modelo, " +
                     "COUNT(l.id_locacao) AS total " +
                     "FROM locacao l " +
                     "JOIN veiculos v ON l.id_veiculo = v.id_veiculo " +
                     "GROUP BY v.modelo_veiculo";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new RelatorioModelo(
                    rs.getString("modelo"),
                    rs.getInt("total")
                ));
            }
        }

        return lista;
    }

    // Relatório 2: Duração das locações
    public List<RelatorioDuracao> obterDuracaoLocacoes() throws SQLException {
        List<RelatorioDuracao> lista = new ArrayList<>();

        String sql = "SELECT l.id_locacao, " +
                     "v.modelo_veiculo AS modelo, " +
                     "c.nome_cliente AS cliente, " +
                     "l.dt_inicio, " +
                     "l.dt_devolucao, " +
                     "(l.dt_devolucao - l.dt_inicio) AS duracao_dias " +
                     "FROM locacao l " +
                     "JOIN veiculos v ON l.id_veiculo = v.id_veiculo " +
                     "JOIN cliente c ON l.id_cliente = c.id_cliente " +
                     "WHERE l.dt_devolucao IS NOT NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date dataInicio = rs.getDate("dt_inicio");
                Date dataDevolucao = rs.getDate("dt_devolucao");

                lista.add(new RelatorioDuracao(
                    rs.getInt("id_locacao"),
                    rs.getString("modelo"),
                    rs.getString("cliente"),
                    dataInicio != null ? dataInicio.toLocalDate() : null,
                    dataDevolucao != null ? dataDevolucao.toLocalDate() : null,
                    rs.getLong("duracao_dias")
                ));
            }
        }

        return lista;
    }
}
