package com.locadora.model;

import java.time.LocalDate;

public class Locacao {
    private int idLocacao;
    private Veiculo veiculo;
    private Cliente cliente;
    private LocalDate dtInicio;
    private LocalDate dtPrevista;
    private LocalDate dtDevolucao;
    private String statusLocacaoVeiculo;

    public Locacao() {}

    public Locacao(int idLocacao, Veiculo veiculo, Cliente cliente,
                   LocalDate dtInicio, LocalDate dtPrevista,
                   LocalDate dtDevolucao, String statusLocacaoVeiculo) {
        this.idLocacao = idLocacao;
        this.veiculo = veiculo;
        this.cliente = cliente;
        this.dtInicio = dtInicio;
        this.dtPrevista = dtPrevista;
        this.dtDevolucao = dtDevolucao;
        this.statusLocacaoVeiculo = statusLocacaoVeiculo;
    }

    public int getIdLocacao() { return idLocacao; }
    public void setIdLocacao(int idLocacao) { this.idLocacao = idLocacao; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }

    public LocalDate getDtPrevista() { return dtPrevista; }
    public void setDtPrevista(LocalDate dtPrevista) { this.dtPrevista = dtPrevista; }

    public LocalDate getDtDevolucao() { return dtDevolucao; }
    public void setDtDevolucao(LocalDate dtDevolucao) { this.dtDevolucao = dtDevolucao; }

    public String getStatusLocacaoVeiculo() { return statusLocacaoVeiculo; }
    public void setStatusLocacaoVeiculo(String statusLocacaoVeiculo) {
        this.statusLocacaoVeiculo = statusLocacaoVeiculo;
    }
}