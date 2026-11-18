package com.locadora.dao;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Cliente;
import com.locadora.model.Faturamento;
import com.locadora.model.Locacao;
import com.locadora.model.Veiculo;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FaturamentoDAO {

    // Salvar usando conexão externa (mantém locação + faturamento na mesma transação)
    public void salvar(Faturamento faturamento, Connection conn) throws SQLException {
        String sql = "INSERT INTO faturamento " +
                     "(id_locacao, valor_diaria, qtd_diarias, valor_total, " +
                     "forma_pagamento, status_pagamento, dt_pagamento) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, faturamento.getLocacao().getIdLocacao());
            stmt.setBigDecimal(2, faturamento.getValorDiaria());
            stmt.setInt(3, faturamento.getQtdDiarias());
            stmt.setBigDecimal(4, faturamento.getValorTotal());
            stmt.setString(5, faturamento.getFormaPagamento());
            stmt.setString(6, faturamento.getStatusPagamento());

            if (faturamento.getDtPagamento() != null) {
                stmt.setDate(7, Date.valueOf(faturamento.getDtPagamento()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.executeUpdate();
        }
    }

    // Salvar abrindo conexão própria
    public void salvar(Faturamento faturamento) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            salvar(faturamento, conn);
        }
    }

    // Listar todos os faturamentos com dados de Locação, Cliente e Veículo
    public List<Faturamento> listarTodos() throws SQLException {
        List<Faturamento> lista = new ArrayList<>();

        String sql = "SELECT " +
                     "f.id_faturamento, f.valor_diaria, f.qtd_diarias, f.valor_total, " +
                     "f.forma_pagamento, f.status_pagamento, f.dt_pagamento, " +
                     "l.id_locacao, l.dt_inicio, l.dt_prevista, l.dt_devolucao, l.status_locacao_veiculo, " +
                     "c.id_cliente, c.nome_cliente, c.email_cliente, c.telefone_cliente, " +
                     "v.id_veiculo, v.placa_veiculo, v.modelo_veiculo, v.marca_veiculo, v.cor_veiculo " +
                     "FROM faturamento f " +
                     "JOIN locacao l ON f.id_locacao = l.id_locacao " +
                     "JOIN cliente c ON l.id_cliente = c.id_cliente " +
                     "JOIN veiculos v ON l.id_veiculo = v.id_veiculo " +
                     "ORDER BY f.id_faturamento DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nome_cliente"),
                    rs.getString("email_cliente"),
                    rs.getString("telefone_cliente")
                );

                Veiculo veiculo = new Veiculo(
                    rs.getInt("id_veiculo"),
                    rs.getString("placa_veiculo"),
                    rs.getString("modelo_veiculo"),
                    rs.getString("marca_veiculo"),
                    rs.getString("cor_veiculo")
                );

                Date dtInicio = rs.getDate("dt_inicio");
                Date dtPrevista = rs.getDate("dt_prevista");
                Date dtDevolucao = rs.getDate("dt_devolucao");

                Locacao locacao = new Locacao(
                    rs.getInt("id_locacao"),
                    veiculo,
                    cliente,
                    dtInicio != null ? dtInicio.toLocalDate() : null,
                    dtPrevista != null ? dtPrevista.toLocalDate() : null,
                    dtDevolucao != null ? dtDevolucao.toLocalDate() : null,
                    rs.getString("status_locacao_veiculo")
                );

                Date dtPagamento = rs.getDate("dt_pagamento");

                Faturamento fat = new Faturamento(
                    rs.getInt("id_faturamento"),
                    locacao,
                    rs.getBigDecimal("valor_diaria"),
                    rs.getInt("qtd_diarias"),
                    rs.getBigDecimal("valor_total"),
                    rs.getString("forma_pagamento"),
                    rs.getString("status_pagamento"),
                    dtPagamento != null ? dtPagamento.toLocalDate() : null
                );

                lista.add(fat);
            }
        }

        return lista;
    }

    // Dar baixa em um faturamento PENDENTE
    public boolean registrarPagamento(int idFaturamento, LocalDate dataPagamento) throws SQLException {
        String sql = "UPDATE faturamento " +
                     "SET status_pagamento = 'PAGO', dt_pagamento = ? " +
                     "WHERE id_faturamento = ? AND status_pagamento = 'PENDENTE'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(dataPagamento));
            stmt.setInt(2, idFaturamento);

            return stmt.executeUpdate() > 0;
        }
    }

    // Cancelar um faturamento PENDENTE
    public boolean cancelarFaturamento(int idFaturamento) throws SQLException {
        String sql = "UPDATE faturamento " +
                     "SET status_pagamento = 'CANCELADO' " +
                     "WHERE id_faturamento = ? AND status_pagamento = 'PENDENTE'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFaturamento);

            return stmt.executeUpdate() > 0;
        }
    }

    // Soma dos pagamentos realizados no mês atual (para o card do Dashboard)
    public BigDecimal obterTotalPagoMesAtual() throws SQLException {
        String sql = "SELECT COALESCE(SUM(valor_total), 0) AS total " +
                     "FROM faturamento " +
                     "WHERE status_pagamento = 'PAGO' " +
                     "AND EXTRACT(MONTH FROM dt_pagamento) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                     "AND EXTRACT(YEAR FROM dt_pagamento) = EXTRACT(YEAR FROM CURRENT_DATE)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        }

        return BigDecimal.ZERO;
    }
}
