package com.locadora.controller;

import com.locadora.model.Veiculo;
import com.locadora.dao.VeiculoDAO;

import java.sql.SQLException;
import java.util.List;

public class VeiculoController {

    private final VeiculoDAO veiculoDAO;

    public VeiculoController() {
        this.veiculoDAO = new VeiculoDAO();
    }

    // Cadastrar veículo
    public void cadastrar(Veiculo veiculo) throws SQLException {

        validarVeiculo(veiculo);

        veiculoDAO.salvar(veiculo);
    }

    // Listar todos os veículos
    public List<Veiculo> listarTodos() throws SQLException {

        return veiculoDAO.listarTodos();
    }

    // Validar os dados do veículo
    private void validarVeiculo(Veiculo veiculo) {

        if (veiculo == null) {
            throw new IllegalArgumentException(
                "Os dados do veículo não foram informados."
            );
        }

        if (veiculo.getPlacaVeiculo() == null ||
            veiculo.getPlacaVeiculo().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "A placa do veículo é obrigatória."
            );
        }

        if (veiculo.getPlacaVeiculo().trim().length() > 12) {
            throw new IllegalArgumentException(
                "A placa deve ter no máximo 12 caracteres."
            );
        }

        if (veiculo.getModeloVeiculo() == null ||
            veiculo.getModeloVeiculo().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O modelo do veículo é obrigatório."
            );
        }

        if (veiculo.getModeloVeiculo().trim().length() > 100) {
            throw new IllegalArgumentException(
                "O modelo deve ter no máximo 100 caracteres."
            );
        }

        if (veiculo.getMarcaVeiculo() == null ||
            veiculo.getMarcaVeiculo().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "A marca do veículo é obrigatória."
            );
        }

        if (veiculo.getMarcaVeiculo().trim().length() > 100) {
            throw new IllegalArgumentException(
                "A marca deve ter no máximo 100 caracteres."
            );
        }

        if (veiculo.getCorVeiculo() == null ||
            veiculo.getCorVeiculo().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "A cor do veículo é obrigatória."
            );
        }

        if (veiculo.getCorVeiculo().trim().length() > 100) {
            throw new IllegalArgumentException(
                "A cor deve ter no máximo 100 caracteres."
            );
        }
    }
}
