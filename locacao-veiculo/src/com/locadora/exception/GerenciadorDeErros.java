package com.locadora.exception;

import java.sql.SQLException;
import java.time.format.DateTimeParseException;

public class GerenciadorDeErros {

    public static String tratarExcecao(Throwable erro) {
        if (erro instanceof SQLException) {
            return tratarErroBancoDeDados((SQLException) erro);
        }

        if (erro instanceof ValidacaoException || erro instanceof RegraDeNegocioException) {
            return "Atenção: " + erro.getMessage();
        }

        if (erro instanceof NumberFormatException) {
            return "Erro de Formato: Preencha os campos numéricos apenas com números válidos.";
        }
        
        if (erro instanceof DateTimeParseException) {
            return "Erro de Data: Informe a data no formato correto (Ex: DD/MM/AAAA).";
        }

        if (erro instanceof NullPointerException) {
            return "Erro Interno: Um dado obrigatório da operação não foi informado.";
        }

        return "Erro inesperado no sistema: " + (erro.getMessage() != null ? erro.getMessage() : "Consulte o suporte.");
    }

    private static String tratarErroBancoDeDados(SQLException e) {
        int codigoErro = e.getErrorCode();

        switch (codigoErro) {
            case 1062:
            case 23505:
                return "Registro duplicado: Já existe um cadastro com esses dados.";
            case 1451:
            case 23503:
                return "Não é possível excluir este registro pois ele possui histórico no sistema.";
            default:
                return "Erro no Banco de Dados: " + e.getMessage();
        }
    }
}