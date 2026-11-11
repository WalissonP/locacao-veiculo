package com.locadora.controller;

import com.locadora.relatorioDados.RelatorioDuracao;
import com.locadora.relatorioDados.RelatorioModelo;
import com.locadora.sql.LocacaoDAO;

import java.sql.SQLException;
import java.util.List;

public class RelatorioController {
    private LocacaoDAO locacaoDAO;

    public RelatorioController() {
        this.locacaoDAO = new LocacaoDAO();
    }

    public List<RelatorioModelo> gerarRelatorioPorModelo() throws SQLException {
        return locacaoDAO.obterLocacoesPorModelo();
    }

    public List<RelatorioDuracao> gerarRelatorioPorDuracao() throws SQLException {
        return locacaoDAO.obterDuracaoLocacoes();
    }
}
