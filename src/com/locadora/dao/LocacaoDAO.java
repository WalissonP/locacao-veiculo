
package com.locadora.sql;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Locacao;
import com.locadora.model.Cliente;
import com.locadora.model.Veiculo;
import com.locadora.relatorioDados.RelatorioDuracao;
import com.locadora.relatorioDados.RelatorioModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocacaoDAO {

    public void registrarLocacao(Locacao locacao) throws SQLException {

        String sql = "INSERT INTO locacao " +
                     "(idveiculo, idcliente, dtinicio, dtprevista, " +
                     "dtdevolucao, status_locacao_veiculo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        }
    }

    // Relatório 1: Locações por modelo de veículo
    public List<RelatorioModelo> obterLocacoesPorModelo()
            throws SQLException {

        List<RelatorioModelo> lista = new ArrayList<>();

        String sql = "SELECT v.modelo_veiculo AS modelo, " +
                     "COUNT(l.idlocacao) AS total " +
                     "FROM locacao l " +
                     "JOIN veiculos v ON l.idveiculo = v.idveiculo " +
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
    public List<RelatorioDuracao> obterDuracaoLocacoes()
            throws SQLException {

        List<RelatorioDuracao> lista = new ArrayList<>();

        String sql = "SELECT l.idlocacao, " +
                     "v.modelo_veiculo AS modelo, " +
                     "c.nome_cliente AS cliente, " +
                     "l.dtinicio, " +
                     "l.dtdevolucao, " +
                     "(l.dtdevolucao - l.dtinicio) AS duracao_dias " +
                     "FROM locacao l " +
                     "JOIN veiculos v ON l.idveiculo = v.idveiculo " +
                     "JOIN cliente c ON l.idcliente = c.idcliente " +
                     "WHERE l.dtdevolucao IS NOT NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Date dataInicio = rs.getDate("dtinicio");
                Date dataDevolucao = rs.getDate("dtdevolucao");

                lista.add(new RelatorioDuracao(
                    rs.getInt("idlocacao"),
                    rs.getString("modelo"),
                    rs.getString("cliente"),
                    dataInicio != null
                        ? dataInicio.toLocalDate() : null,
                    dataDevolucao != null
                        ? dataDevolucao.toLocalDate() : null,
                    rs.getLong("duracao_dias")
                ));
            }
        }

        return lista;
    }
}