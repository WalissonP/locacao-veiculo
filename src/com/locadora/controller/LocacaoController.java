package com.locadora.controller;

import com.locadora.model.Locacao;
import com.locadora.dao.LocacaoDAO;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class LocacaoController {

    private final LocacaoDAO locacaoDAO;

    public LocacaoController() {
        this.locacaoDAO = new LocacaoDAO();
    }

    // Registrar uma nova locação
    public void registrarLocacao(Locacao locacao) throws SQLException {

        validarLocacao(locacao);

        int idCliente = locacao.getCliente().getIdCliente();
        int idVeiculo = locacao.getVeiculo().getIdVeiculo();

        if (!locacaoDAO.existeCliente(idCliente)) {
            throw new IllegalArgumentException(
                "O cliente informado não está cadastrado."
            );
        }

        if (!locacaoDAO.existeVeiculo(idVeiculo)) {
            throw new IllegalArgumentException(
                "O veículo informado não está cadastrado."
            );
        }

        if (!locacaoDAO.veiculoDisponivel(idVeiculo)) {
            throw new IllegalArgumentException(
                "O veículo já está alugado."
            );
        }

        // Toda nova locação começa como LOCADO.
        locacao.setStatusLocacaoVeiculo("LOCADO");
        locacao.setDtDevolucao(null);

        locacaoDAO.registrarLocacao(locacao);
    }

    // Registrar devolução
    public void registrarDevolucao(
            int idLocacao, LocalDate dataDevolucao) throws SQLException {

        if (idLocacao <= 0) {
            throw new IllegalArgumentException(
                "O ID da locação deve ser válido."
            );
        }

        if (dataDevolucao == null) {
            throw new IllegalArgumentException(
                "A data de devolução é obrigatória."
            );
        }

        int resultado = locacaoDAO.registrarDevolucao(
            idLocacao,
            Date.valueOf(dataDevolucao)
        );

        if (resultado == 0) {
            throw new IllegalArgumentException(
                "Não foi possível registrar a devolução. " +
                "Verifique se a locação existe, está ativa " +
                "e se a data de devolução é válida."
            );
        }
    }

    // Validar os dados da locação
    private void validarLocacao(Locacao locacao) {

        if (locacao == null) {
            throw new IllegalArgumentException(
                "Os dados da locação não foram informados."
            );
        }

        if (locacao.getCliente() == null ||
            locacao.getCliente().getIdCliente() <= 0) {
            throw new IllegalArgumentException(
                "Um cliente válido deve ser informado."
            );
        }

        if (locacao.getVeiculo() == null ||
            locacao.getVeiculo().getIdVeiculo() <= 0) {
            throw new IllegalArgumentException(
                "Um veículo válido deve ser informado."
            );
        }

        if (locacao.getDtInicio() == null) {
            throw new IllegalArgumentException(
                "A data de início da locação é obrigatória."
            );
        }

        if (locacao.getDtPrevista() != null &&
            locacao.getDtPrevista().isBefore(locacao.getDtInicio())) {
            throw new IllegalArgumentException(
                "A data prevista não pode ser anterior à data de início."
            );
        }

        if (locacao.getDtDevolucao() != null) {
            throw new IllegalArgumentException(
                "A data de devolução deve ser informada somente " +
                "no momento da devolução do veículo."
            );
        }
    }
}
