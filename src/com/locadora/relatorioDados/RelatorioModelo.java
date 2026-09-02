package com.locadora.relatorioDados;

public class RelatorioModelo {
    private String modelo;
    private int totalLocacoes;

    public RelatorioModelo() {}

    public RelatorioModelo(String modelo, int totalLocacoes) {
        this.modelo = modelo;
        this.totalLocacoes = totalLocacoes;
    }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getTotalLocacoes() { return totalLocacoes; }
    public void setTotalLocacoes(int totalLocacoes) { this.totalLocacoes = totalLocacoes; }
}
