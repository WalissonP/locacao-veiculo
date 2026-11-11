package com.locadora.model;

public class Veiculo {
    private int id;
    private String placa;
    private String modelo;
    private String marca;
    private String status; // Ex: "DISPONIVEL", "ALUGADO", "MANUTENCAO"

    public Veiculo() {}

    public Veiculo(int id, String placa, String modelo, String marca, String status) {
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
