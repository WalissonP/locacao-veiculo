package com.locadora.controller;

import com.locadora.dao.FaturamentoDAO;
import com.locadora.model.Faturamento;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FaturamentoController {
    private final FaturamentoDAO faturamentoDAO;

    public FaturamentoController() {
        this.faturamentoDAO = new FaturamentoDAO();
    }

    public List<Faturamento> listarTodos() throws SQLException {
        return faturamentoDAO.listarTodos();
    }

    public void registrarPagamento(int idFaturamento, LocalDate dataPagamento) throws SQLException {
        if (idFaturamento <= 0) {
            throw new IllegalArgumentException("Selecione um faturamento válido.");
        }

        if (dataPagamento == null) {
            throw new IllegalArgumentException("A data de pagamento é obrigatória.");
        }

        if (!faturamentoDAO.registrarPagamento(idFaturamento, dataPagamento)) {
            throw new IllegalArgumentException(
                "Não foi possível registrar o pagamento. Verifique se o status está PENDENTE."
            );
        }
    }

    public void cancelarFaturamento(int idFaturamento) throws SQLException {
        if (idFaturamento <= 0) {
            throw new IllegalArgumentException("Selecione um faturamento válido.");
        }

        if (!faturamentoDAO.cancelarFaturamento(idFaturamento)) {
            throw new IllegalArgumentException(
                "Não foi possível cancelar. Apenas cobranças PENDENTES podem ser canceladas."
            );
        }
    }

    public BigDecimal obterTotalPagoMesAtual() throws SQLException {
        return faturamentoDAO.obterTotalPagoMesAtual();
    }
}
