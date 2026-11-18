package com.locadora.controller;

import com.locadora.config.DatabaseConnection;
import com.locadora.dao.FaturamentoDAO;
import com.locadora.dao.LocacaoDAO;
import com.locadora.model.Faturamento;
import com.locadora.model.Locacao;
import com.locadora.relatorioDados.RelatorioDuracao;
import com.locadora.relatorioDados.RelatorioModelo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class LocacaoController {
    private static final List<String> FORMAS_PAGAMENTO_VALIDAS =
            Arrays.asList("PIX", "DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO");

    private final LocacaoDAO locacaoDAO;
    private final FaturamentoDAO faturamentoDAO;

    public LocacaoController() {
        this.locacaoDAO = new LocacaoDAO();
        this.faturamentoDAO = new FaturamentoDAO();
    }

    // Listar todas as locações
    public List<Locacao> listarTodas() throws SQLException {
        return locacaoDAO.listarTodas();
    }

    // Calcular quantidade de diárias (mínimo 1 para atender CHECK (qtd_diarias > 0))
    public int calcularQuantidadeDiarias(LocalDate dtInicio, LocalDate dtPrevista) {
        if (dtInicio == null || dtPrevista == null) {
            return 1;
        }
        long dias = ChronoUnit.DAYS.between(dtInicio, dtPrevista);
        return (dias <= 0) ? 1 : (int) dias;
    }

    // Registrar uma nova locação junto com seu faturamento
    public void registrarLocacaoComFaturamento(
            Locacao locacao,
            BigDecimal valorDiaria,
            String formaPagamento,
            boolean jaPago,
            LocalDate dtPagamento) throws SQLException {

        validarLocacao(locacao);
        validarFaturamento(valorDiaria, formaPagamento, jaPago, dtPagamento);

        int idCliente = locacao.getCliente().getIdCliente();
        int idVeiculo = locacao.getVeiculo().getIdVeiculo();

        if (!locacaoDAO.existeCliente(idCliente)) {
            throw new IllegalArgumentException("O cliente informado não está cadastrado.");
        }

        if (!locacaoDAO.existeVeiculo(idVeiculo)) {
            throw new IllegalArgumentException("O veículo informado não está cadastrado.");
        }

        if (!locacaoDAO.veiculoDisponivel(idVeiculo)) {
            throw new IllegalArgumentException("O veículo já está alugado.");
        }

        // Toda nova locação começa como LOCADO
        locacao.setStatusLocacaoVeiculo("LOCADO");
        locacao.setDtDevolucao(null);

        int qtdDiarias = calcularQuantidadeDiarias(locacao.getDtInicio(), locacao.getDtPrevista());
        BigDecimal valorTotal = valorDiaria.multiply(BigDecimal.valueOf(qtdDiarias));

        Faturamento faturamento = new Faturamento();
        faturamento.setValorDiaria(valorDiaria);
        faturamento.setQtdDiarias(qtdDiarias);
        faturamento.setValorTotal(valorTotal);
        faturamento.setFormaPagamento(formaPagamento);

        if (jaPago) {
            faturamento.setStatusPagamento("PAGO");
            faturamento.setDtPagamento(dtPagamento);
        } else {
            faturamento.setStatusPagamento("PENDENTE");
            faturamento.setDtPagamento(null);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idLocacaoGerado = locacaoDAO.registrarLocacao(locacao, conn);
                locacao.setIdLocacao(idLocacaoGerado);
                faturamento.setLocacao(locacao);

                faturamentoDAO.salvar(faturamento, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Registrar devolução
    public void registrarDevolucao(int idLocacao, LocalDate dataDevolucao) throws SQLException {
        if (idLocacao <= 0) {
            throw new IllegalArgumentException("O ID da locação deve ser válido.");
        }

        if (dataDevolucao == null) {
            throw new IllegalArgumentException("A data de devolução é obrigatória.");
        }

        int resultado = locacaoDAO.registrarDevolucao(idLocacao, Date.valueOf(dataDevolucao));

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
            throw new IllegalArgumentException("Os dados da locação não foram informados.");
        }

        if (locacao.getCliente() == null || locacao.getCliente().getIdCliente() <= 0) {
            throw new IllegalArgumentException("Um cliente válido deve ser informado.");
        }

        if (locacao.getVeiculo() == null || locacao.getVeiculo().getIdVeiculo() <= 0) {
            throw new IllegalArgumentException("Um veículo válido deve ser informado.");
        }

        if (locacao.getDtInicio() == null) {
            throw new IllegalArgumentException("A data de início da locação é obrigatória.");
        }

        if (locacao.getDtPrevista() != null && locacao.getDtPrevista().isBefore(locacao.getDtInicio())) {
            throw new IllegalArgumentException("A data prevista não pode ser anterior à data de início.");
        }

        if (locacao.getDtDevolucao() != null) {
            throw new IllegalArgumentException(
                "A data de devolução deve ser informada somente no momento da devolução do veículo."
            );
        }
    }

    // Validar os dados do faturamento
    private void validarFaturamento(
            BigDecimal valorDiaria,
            String formaPagamento,
            boolean jaPago,
            LocalDate dtPagamento) {

        if (valorDiaria == null || valorDiaria.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Informe um valor de diária maior que zero.");
        }

        if (formaPagamento == null || !FORMAS_PAGAMENTO_VALIDAS.contains(formaPagamento)) {
            throw new IllegalArgumentException("Selecione uma forma de pagamento válida.");
        }

        if (jaPago && dtPagamento == null) {
            throw new IllegalArgumentException(
                "Informe a data de pagamento para locações já pagas."
            );
        }
    }

    // Relatório de locações por modelo
    public List<RelatorioModelo> obterLocacoesPorModelo() throws SQLException {
        return locacaoDAO.obterLocacoesPorModelo();
    }

    // Relatório de duração das locações
    public List<RelatorioDuracao> obterDuracaoLocacoes() throws SQLException {
        return locacaoDAO.obterDuracaoLocacoes();
    }
}
