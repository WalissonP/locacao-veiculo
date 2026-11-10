package com.locadora.exception;

import com.locadora.model.Cliente;
import com.locadora.model.Veiculo;
import com.locadora.model.Locacao;

public class Validador {

    public static void validarCliente(Cliente cliente) throws ValidacaoException {
        if (cliente == null) {
            throw new ValidacaoException("Os dados do cliente não podem ser nulos.");
        }
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do cliente é obrigatório.");
        }
        if (cliente.getCpf() == null || !cliente.getCpf().matches("\\d{11}")) {
            throw new ValidacaoException("CPF inválido. O CPF deve conter exatamente 11 dígitos numéricos.");
        }
    }

    public static void validarVeiculo(Veiculo veiculo) throws ValidacaoException {
        if (veiculo == null) {
            throw new ValidacaoException("Os dados do veículo não podem ser nulos.");
        }
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            throw new ValidacaoException("A placa do veículo é obrigatória.");
        }
        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            throw new ValidacaoException("O modelo do veículo é obrigatório.");
        }
    }

    public static void validarLocacao(Locacao locacao) throws ValidacaoException {
        if (locacao == null) {
            throw new ValidacaoException("Os dados da locação não podem ser nulos.");
        }
        if (locacao.getDataInicio() != null && locacao.getDataFimPrevista() != null) {
            if (locacao.getDataFimPrevista().isBefore(locacao.getDataInicio())) {
                throw new ValidacaoException("A data fim prevista não pode ser anterior à data de início.");
            }
        } else {
            throw new ValidacaoException("As datas de início e término da locação são obrigatórias.");
        }
    }
}