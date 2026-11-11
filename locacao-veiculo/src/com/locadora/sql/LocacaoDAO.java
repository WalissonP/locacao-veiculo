package com.locadora.sql;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Locacao;
import com.locadora.relatorioDados.RelatorioDuracao;
import com.locadora.relatorioDados.RelatorioModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocacaoDAO {

    public void registrarLocacao(Locacao locacao) throws SQLException {
        String sql = "INSERT INTO locacao (cliente_id, veiculo_id, data_inicio, data_fim_prevista) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, locacao.getClienteId());
            stmt.setInt(2, locacao.getVeiculoId());
            stmt.setDate(3, Date.valueOf(locacao.getDataInicio()));
            stmt.setDate(4, Date.valueOf(locacao.getDataFimPrevista()));
            stmt.executeUpdate();
        }
    }

    // Consulta para Relatório 1: Locações por Modelo
    public List<RelatorioModelo> obterLocacoesPorModelo() throws SQLException {
        List<RelatorioModelo> lista = new ArrayList<>();
        String sql = "SELECT v.modelo, COUNT(l.id) AS total " +
                     "FROM locacao l " +
                     "JOIN veiculo v ON l.veiculo_id = v.id " +
                     "GROUP BY v.modelo";

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

    // Consulta para Relatório 2: Duração das Locações
    public List<RelatorioDuracao> obterDuracaoLocacoes() throws SQLException {
        List<RelatorioDuracao> lista = new ArrayList<>();
        String sql = "SELECT l.id, v.modelo, c.nome AS cliente, l.data_inicio, l.data_devolucao, " +
                     "DATEDIFF(l.data_devolucao, l.data_inicio) AS duracao_dias " +
                     "FROM locacao l " +
                     "JOIN veiculo v ON l.veiculo_id = v.id " +
                     "JOIN cliente c ON l.cliente_id = c.id " +
                     "WHERE l.data_devolucao IS NOT NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Date dataInicio = rs.getDate("data_inicio");
                Date dataDevolucao = rs.getDate("data_devolucao");
                lista.add(new RelatorioDuracao(
                    rs.getInt("id"),
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
