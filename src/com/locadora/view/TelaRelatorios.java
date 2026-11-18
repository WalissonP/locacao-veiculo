package com.locadora.view;

import com.locadora.controller.LocacaoController;
import com.locadora.model.Locacao;
import com.locadora.relatorioDados.RelatorioDuracao;
import com.locadora.relatorioDados.RelatorioModelo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class TelaRelatorios extends JPanel {

    private final LocacaoController locacaoController = new LocacaoController();

    private final JLabel resumo = ComponentesTela.criarResumo();

    private final DefaultTableModel modeloVeiculos =
            ComponentesTela.criarModelo("Modelo", "Total de locações");

    private final DefaultTableModel modeloDuracao =
            ComponentesTela.criarModelo(
                    "ID locação",
                    "Cliente",
                    "Veículo",
                    "Data de início",
                    "Data de devolução",
                    "Duração (dias)"
            );

    private final DefaultTableModel modeloResumo =
            ComponentesTela.criarModelo(
                    "ID locação",
                    "Cliente",
                    "Veículo",
                    "Início",
                    "Previsão de devolução",
                    "Devolução",
                    "Status"
            );

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaRelatorios(Runnable voltar) {

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        add(ComponentesTela.criarCabecalho(
                "Relatórios de locações da locadora.",
                resumo
        ), BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();

        abas.addTab(
                "Locações por modelo",
                new JScrollPane(ComponentesTela.criarTabela(modeloVeiculos))
        );

        abas.addTab(
                "Duração das locações",
                new JScrollPane(ComponentesTela.criarTabela(modeloDuracao))
        );

        abas.addTab(
                "Resumo das locações",
                new JScrollPane(ComponentesTela.criarTabela(modeloResumo))
        );

        add(abas, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acoes.setOpaque(false);

        acoes.add(ComponentesTela.criarBotao(
                "Atualizar relatórios",
                this::atualizar,
                true
        ));

        acoes.add(ComponentesTela.criarBotao(
                "Voltar",
                voltar,
                false
        ));

        add(acoes, BorderLayout.SOUTH);

        atualizar();
    }

    // Atualizar todos os relatórios
    public void atualizar() {

        try {
            List<Locacao> locacoes = locacaoController.listarTodas();

            int ativas = 0;
            int finalizadas = 0;

            for (Locacao locacao : locacoes) {

                if ("LOCADO".equalsIgnoreCase(
                        locacao.getStatusLocacaoVeiculo())) {
                    ativas++;

                } else if ("DISPONIVEL".equalsIgnoreCase(
                        locacao.getStatusLocacaoVeiculo())) {
                    finalizadas++;
                }
            }

            resumo.setText(
                    "Total de locações: " + locacoes.size()
                    + "    |    Ativas: " + ativas
                    + "    |    Finalizadas: " + finalizadas
            );

            atualizarLocacoesPorModelo();
            atualizarDuracaoLocacoes();
            atualizarResumoLocacoes(locacoes);

        } catch (SQLException e) {

            ComponentesTela.mostrarErro(
                    this,
                    "Não foi possível atualizar os relatórios: "
                    + e.getMessage()
            );
        }
    }

    // Relatório 1: Locações agrupadas por modelo
    private void atualizarLocacoesPorModelo() throws SQLException {

        modeloVeiculos.setRowCount(0);

        List<RelatorioModelo> lista =
                locacaoController.obterLocacoesPorModelo();

        for (RelatorioModelo relatorio : lista) {

            modeloVeiculos.addRow(new Object[]{
                    relatorio.getModelo(),
                    relatorio.getTotalLocacoes()
            });
        }
    }

    // Relatório 2: Duração das locações finalizadas
    private void atualizarDuracaoLocacoes() throws SQLException {

        modeloDuracao.setRowCount(0);

        List<RelatorioDuracao> lista =
                locacaoController.obterDuracaoLocacoes();

        for (RelatorioDuracao relatorio : lista) {

            modeloDuracao.addRow(new Object[]{
                    relatorio.getLocacaoId(),
                    relatorio.getNomeCliente(),
                    relatorio.getModeloVeiculo(),
                    formatarData(relatorio.getDataInicio()),
                    formatarData(relatorio.getDataDevolucao()),
                    relatorio.getDuracaoDias()
            });
        }
    }

    // Relatório 3: Resumo de todas as locações
    private void atualizarResumoLocacoes(List<Locacao> locacoes) {

        modeloResumo.setRowCount(0);

        for (Locacao locacao : locacoes) {

            modeloResumo.addRow(new Object[]{
                    locacao.getIdLocacao(),
                    locacao.getCliente().getNomeCliente(),
                    locacao.getVeiculo().getModeloVeiculo(),
                    formatarData(locacao.getDtInicio()),
                    formatarData(locacao.getDtPrevista()),
                    formatarData(locacao.getDtDevolucao()),
                    locacao.getStatusLocacaoVeiculo()
            });
        }
    }

    // Formatar datas
    private String formatarData(java.time.LocalDate data) {

        if (data == null) {
            return "-";
        }

        return data.format(FORMATO_DATA);
    }
}