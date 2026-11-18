package com.locadora.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Faturamento {
    private int idFaturamento;
    private Locacao locacao;
    private BigDecimal valorDiaria;
    private int qtdDiarias;
    private BigDecimal valorTotal;
    private String formaPagamento;
    private String statusPagamento;
    private LocalDate dtPagamento;

    public Faturamento() {}

    public Faturamento(int idFaturamento, Locacao locacao, BigDecimal valorDiaria,
                       int qtdDiarias, BigDecimal valorTotal, String formaPagamento,
                       String statusPagamento, LocalDate dtPagamento) {
        this.idFaturamento = idFaturamento;
        this.locacao = locacao;
        this.valorDiaria = valorDiaria;
        this.qtdDiarias = qtdDiarias;
        this.valorTotal = valorTotal;
        this.formaPagamento = formaPagamento;
        this.statusPagamento = statusPagamento;
        this.dtPagamento = dtPagamento;
    }

    public int getIdFaturamento() { return idFaturamento; }
    public void setIdFaturamento(int idFaturamento) { this.idFaturamento = idFaturamento; }

    public Locacao getLocacao() { return locacao; }
    public void setLocacao(Locacao locacao) { this.locacao = locacao; }

    public BigDecimal getValorDiaria() { return valorDiaria; }
    public void setValorDiaria(BigDecimal valorDiaria) { this.valorDiaria = valorDiaria; }

    public int getQtdDiarias() { return qtdDiarias; }
    public void setQtdDiarias(int qtdDiarias) { this.qtdDiarias = qtdDiarias; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }

    public LocalDate getDtPagamento() { return dtPagamento; }
    public void setDtPagamento(LocalDate dtPagamento) { this.dtPagamento = dtPagamento; }
}
