package com.locadora.relatorioDados;

import java.time.LocalDate;

public class RelatorioDuracao {
    private int locacaoId;
    private String modeloVeiculo;
    private String nomeCliente;
    private LocalDate dataInicio;
    private LocalDate dataDevolucao;
    private long duracaoDias;

    public RelatorioDuracao() {}

    public RelatorioDuracao(int locacaoId, String modeloVeiculo, String nomeCliente, LocalDate dataInicio, LocalDate dataDevolucao, long duracaoDias) {
        this.locacaoId = locacaoId;
        this.modeloVeiculo = modeloVeiculo;
        this.nomeCliente = nomeCliente;
        this.dataInicio = dataInicio;
        this.dataDevolucao = dataDevolucao;
        this.duracaoDias = duracaoDias;
    }

    public int getLocacaoId() { return locacaoId; }
    public void setLocacaoId(int locacaoId) { this.locacaoId = locacaoId; }

    public String getModeloVeiculo() { return modeloVeiculo; }
    public void setModeloVeiculo(String modeloVeiculo) { this.modeloVeiculo = modeloVeiculo; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public long getDuracaoDias() { return duracaoDias; }
    public void setDuracaoDias(long duracaoDias) { this.duracaoDias = duracaoDias; }
}
