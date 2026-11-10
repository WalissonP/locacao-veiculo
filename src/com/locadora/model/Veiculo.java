package com.locadora.model;

public class Veiculo {
    private int idVeiculo;
    private String placaVeiculo;
    private String modeloVeiculo;
    private String marcaVeiculo;
    private String corVeiculo;

    public Veiculo() {}

    public Veiculo(int idVeiculo, String placaVeiculo, String modeloVeiculo,
                   String marcaVeiculo, String corVeiculo) {
        this.idVeiculo = idVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.modeloVeiculo = modeloVeiculo;
        this.marcaVeiculo = marcaVeiculo;
        this.corVeiculo = corVeiculo;
    }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public String getPlacaVeiculo() { return placaVeiculo; }
    public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }

    public String getModeloVeiculo() { return modeloVeiculo; }
    public void setModeloVeiculo(String modeloVeiculo) { this.modeloVeiculo = modeloVeiculo; }

    public String getMarcaVeiculo() { return marcaVeiculo; }
    public void setMarcaVeiculo(String marcaVeiculo) { this.marcaVeiculo = marcaVeiculo; }

    public String getCorVeiculo() { return corVeiculo; }
    public void setCorVeiculo(String corVeiculo) { this.corVeiculo = corVeiculo; }
}
