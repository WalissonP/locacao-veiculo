package com.locadora.view;

import com.locadora.controller.FaturamentoController;
import com.locadora.model.Faturamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class TelaFaturamento extends JPanel {
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat FORMATO_MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final FaturamentoController faturamentoController = new FaturamentoController();
    private final JLabel resumo = ComponentesTela.criarResumo();
    private final JComboBox<String> filtroStatus =
            new JComboBox<>(new String[]{"Todos", "PENDENTE", "PAGO", "CANCELADO"});

    private final DefaultTableModel modelo = ComponentesTela.criarModelo(
            "ID", "Locação", "Cliente", "Veículo",
            "Valor Diária", "Diárias", "Valor Total",
            "Forma Pagto.", "Status", "Data Pagto."
    );
    private final JTable tabela = ComponentesTela.criarTabela(modelo);
    private List<Faturamento> faturamentosExibidos = new ArrayList<>();

    public TelaFaturamento(Runnable voltar) {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        // Cabeçalho com filtro de status
        JPanel topo = new JPanel(new BorderLayout(16, 0));
        topo.setOpaque(false);
        topo.add(ComponentesTela.criarCabecalho(
                "Consulte os registros financeiros e acompanhe os pagamentos das locações.",
                resumo
        ), BorderLayout.CENTER);

        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelFiltro.setOpaque(false);
        JLabel labelFiltro = new JLabel("Filtrar status:");
        labelFiltro.setFont(new Font("SansSerif", Font.BOLD, 13));
        labelFiltro.setForeground(ComponentesTela.TEXT);
        filtroStatus.addActionListener(e -> atualizar());
        painelFiltro.add(labelFiltro);
        painelFiltro.add(filtroStatus);
        topo.add(painelFiltro, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acoes.setOpaque(false);
        acoes.add(ComponentesTela.criarBotao("Registrar pagamento", this::registrarPagamento, true));
        acoes.add(ComponentesTela.criarBotao("Cancelar cobrança", this::cancelarCobranca, false));
        acoes.add(ComponentesTela.criarBotao("Voltar", voltar, false));
        add(acoes, BorderLayout.SOUTH);

        atualizar();
    }

    public void atualizar() {
        modelo.setRowCount(0);
        faturamentosExibidos.clear();

        String statusSelecionado = (String) filtroStatus.getSelectedItem();

        try {
            List<Faturamento> todos = faturamentoController.listarTodos();

            BigDecimal totalPago = BigDecimal.ZERO;
            BigDecimal totalPendente = BigDecimal.ZERO;

            for (Faturamento fat : todos) {
                if ("PAGO".equalsIgnoreCase(fat.getStatusPagamento())) {
                    totalPago = totalPago.add(fat.getValorTotal());
                } else if ("PENDENTE".equalsIgnoreCase(fat.getStatusPagamento())) {
                    totalPendente = totalPendente.add(fat.getValorTotal());
                }

                boolean correspondeFiltro = "Todos".equals(statusSelecionado)
                        || fat.getStatusPagamento().equalsIgnoreCase(statusSelecionado);

                if (correspondeFiltro) {
                    faturamentosExibidos.add(fat);
                    modelo.addRow(new Object[]{
                        fat.getIdFaturamento(),
                        "#" + fat.getLocacao().getIdLocacao(),
                        fat.getLocacao().getCliente().getNomeCliente(),
                        fat.getLocacao().getVeiculo().getModeloVeiculo()
                            + " - " + fat.getLocacao().getVeiculo().getPlacaVeiculo(),
                        FORMATO_MOEDA.format(fat.getValorDiaria()),
                        fat.getQtdDiarias(),
                        FORMATO_MOEDA.format(fat.getValorTotal()),
                        fat.getFormaPagamento(),
                        fat.getStatusPagamento(),
                        formatarData(fat.getDtPagamento())
                    });
                }
            }

            resumo.setText(
                "Recebido (PAGO): " + FORMATO_MOEDA.format(totalPago) +
                "    |    A receber (PENDENTE): " + FORMATO_MOEDA.format(totalPendente)
            );

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Não foi possível carregar os faturamentos: " + e.getMessage()
            );
        }
    }

    private void registrarPagamento() {
        Faturamento selecionado = obterSelecionado();

        if (selecionado == null) {
            ComponentesTela.mostrarErro(this, "Selecione um registro de faturamento na tabela.");
            return;
        }

        if (!"PENDENTE".equalsIgnoreCase(selecionado.getStatusPagamento())) {
            ComponentesTela.mostrarErro(
                this,
                "Apenas faturamentos com status PENDENTE podem receber baixa."
            );
            return;
        }

        String dataInformada = JOptionPane.showInputDialog(
            this,
            "Informe a data do pagamento (dd/mm/aaaa):",
            FORMATO_DATA.format(LocalDate.now())
        );

        if (dataInformada == null) {
            return;
        }

        try {
            LocalDate dataPagamento = LocalDate.parse(dataInformada.trim(), FORMATO_DATA);
            faturamentoController.registrarPagamento(selecionado.getIdFaturamento(), dataPagamento);

            JOptionPane.showMessageDialog(
                this,
                "Pagamento registrado com sucesso!"
            );

            atualizar();

        } catch (DateTimeParseException e) {
            ComponentesTela.mostrarErro(this, "Informe a data no formato dd/mm/aaaa.");
        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());
        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Erro ao registrar pagamento: " + e.getMessage()
            );
        }
    }

    private void cancelarCobranca() {
        Faturamento selecionado = obterSelecionado();

        if (selecionado == null) {
            ComponentesTela.mostrarErro(this, "Selecione um registro de faturamento na tabela.");
            return;
        }

        if (!"PENDENTE".equalsIgnoreCase(selecionado.getStatusPagamento())) {
            ComponentesTela.mostrarErro(
                this,
                "Apenas faturamentos PENDENTES podem ser cancelados."
            );
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente cancelar o faturamento #" + selecionado.getIdFaturamento() + "?",
            "Confirmar cancelamento",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            faturamentoController.cancelarFaturamento(selecionado.getIdFaturamento());
            JOptionPane.showMessageDialog(this, "Faturamento cancelado com sucesso!");
            atualizar();
        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());
        } catch (SQLException e) {
            ComponentesTela.mostrarErro(this, "Erro ao cancelar faturamento: " + e.getMessage());
        }
    }

    private Faturamento obterSelecionado() {
        int indice = tabela.getSelectedRow();
        if (indice < 0) {
            return null;
        }
        int indiceModelo = tabela.convertRowIndexToModel(indice);
        if (indiceModelo >= faturamentosExibidos.size()) {
            return null;
        }
        return faturamentosExibidos.get(indiceModelo);
    }

    private String formatarData(LocalDate data) {
        return data == null ? "-" : FORMATO_DATA.format(data);
    }
}
