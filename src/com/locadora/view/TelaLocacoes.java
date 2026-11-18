package com.locadora.view;

import com.locadora.controller.ClienteController;
import com.locadora.controller.LocacaoController;
import com.locadora.controller.VeiculoController;
import com.locadora.model.Cliente;
import com.locadora.model.Locacao;
import com.locadora.model.Veiculo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class TelaLocacoes extends JPanel {
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final LocacaoController locacaoController = new LocacaoController();
    private final ClienteController clienteController = new ClienteController();
    private final VeiculoController veiculoController = new VeiculoController();
    private final JLabel resumo = ComponentesTela.criarResumo();
    private final DefaultTableModel modelo =
            ComponentesTela.criarModelo(
                "ID", "Cliente", "Veículo",
                "Início", "Previsão de fim",
                "Devolução", "Status"
            );
    private final JTable tabela = ComponentesTela.criarTabela(modelo);
    private List<Locacao> locacoes = new ArrayList<>();

    public TelaLocacoes(Runnable voltar) {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(ComponentesTela.criarPainelCadastro(
                "Cadastre clientes e veículos primeiro. " +
                "Um veículo não pode ter mais de uma locação ativa.",
                resumo,
                tabela,
                ComponentesTela.criarBotao("Cadastrar locação", this::cadastrar, true),
                ComponentesTela.criarBotao("Finalizar locação", this::finalizar, false),
                ComponentesTela.criarBotao("Voltar", voltar, false)
            ),
            BorderLayout.CENTER
        );

        atualizar();
    }

    // Atualizar tabela com as locações do banco
    public void atualizar() {
        modelo.setRowCount(0);

        try {
            locacoes = locacaoController.listarTodas();

            int totalAtivas = 0;

            for (Locacao locacao : locacoes) {
                if ("LOCADO".equals(locacao.getStatusLocacaoVeiculo())) {
                    totalAtivas++;
                }

                modelo.addRow(new Object[]{
                    locacao.getIdLocacao(),
                    locacao.getCliente().getNomeCliente(),
                    locacao.getVeiculo().getModeloVeiculo()
                        + " - " + locacao.getVeiculo().getPlacaVeiculo(),
                    formatarData(locacao.getDtInicio()),
                    formatarData(locacao.getDtPrevista()),
                    formatarData(locacao.getDtDevolucao()),
                    locacao.getStatusLocacaoVeiculo()
                });
            }

            resumo.setText(
                "Locações ativas: " + totalAtivas +
                "    |    Total efetuadas: " + locacoes.size()
            );

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Não foi possível carregar as locações: " + e.getMessage()
            );
        }
    }

    // Cadastrar locação + faturamento
    private void cadastrar() {
        try {
            List<Cliente> clientes = clienteController.listarTodos();
            List<Veiculo> veiculos = veiculoController.listarTodos();

            if (clientes.isEmpty() || veiculos.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Cadastre pelo menos um cliente e um veículo " +
                    "antes de cadastrar uma locação.",
                    "Pré-requisitos da locação",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            JComboBox<Cliente> comboClientes =
                    new JComboBox<>(clientes.toArray(new Cliente[0]));
            JComboBox<Veiculo> comboVeiculos =
                    new JComboBox<>(veiculos.toArray(new Veiculo[0]));

            comboClientes.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                    );

                    if (value instanceof Cliente) {
                        label.setText(((Cliente) value).getNomeCliente());
                    }

                    return label;
                }
            });

            comboVeiculos.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                    );

                    if (value instanceof Veiculo) {
                        Veiculo veiculo = (Veiculo) value;
                        label.setText(
                            veiculo.getMarcaVeiculo() + " " +
                            veiculo.getModeloVeiculo() + " - " +
                            veiculo.getPlacaVeiculo()
                        );
                    }

                    return label;
                }
            });

            JTextField inicio = new JTextField(FORMATO_DATA.format(LocalDate.now()), 12);
            JTextField fim = new JTextField(FORMATO_DATA.format(LocalDate.now().plusDays(1)), 12);
            JTextField campoValorDiaria = new JTextField("120.00", 12);

            JTextField campoQtdDiarias = new JTextField("1", 12);
            campoQtdDiarias.setEditable(false);
            campoQtdDiarias.setBackground(new Color(241, 245, 249));

            JTextField campoValorTotal = new JTextField("120.00", 12);
            campoValorTotal.setEditable(false);
            campoValorTotal.setBackground(new Color(241, 245, 249));

            JComboBox<String> comboFormaPagamento = new JComboBox<>(
                new String[]{"PIX", "DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO"}
            );

            JCheckBox checkJaPago = new JCheckBox("Cliente já realizou o pagamento");
            JTextField campoDataPagamento = new JTextField(FORMATO_DATA.format(LocalDate.now()), 12);
            campoDataPagamento.setEnabled(false);

            checkJaPago.addActionListener(e -> {
                boolean pago = checkJaPago.isSelected();
                campoDataPagamento.setEnabled(pago);
                if (pago && campoDataPagamento.getText().trim().isEmpty()) {
                    campoDataPagamento.setText(FORMATO_DATA.format(LocalDate.now()));
                }
            });

            Runnable recalcular = () -> {
                try {
                    LocalDate dtIni = LocalDate.parse(inicio.getText().trim(), FORMATO_DATA);
                    String txtFim = fim.getText().trim();
                    LocalDate dtFim = txtFim.isEmpty() ? null : LocalDate.parse(txtFim, FORMATO_DATA);

                    int qtd = locacaoController.calcularQuantidadeDiarias(dtIni, dtFim);
                    campoQtdDiarias.setText(String.valueOf(qtd));

                    String txtDiaria = campoValorDiaria.getText().trim().replace(",", ".");
                    BigDecimal diaria = new BigDecimal(txtDiaria);
                    BigDecimal total = diaria.multiply(BigDecimal.valueOf(qtd));
                    campoValorTotal.setText(String.format(Locale.US, "%.2f", total));
                } catch (Exception ignored) {
                    campoValorTotal.setText("-");
                }
            };

            DocumentListener listenerCalculo = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { recalcular.run(); }
                @Override
                public void removeUpdate(DocumentEvent e) { recalcular.run(); }
                @Override
                public void changedUpdate(DocumentEvent e) { recalcular.run(); }
            };

            inicio.getDocument().addDocumentListener(listenerCalculo);
            fim.getDocument().addDocumentListener(listenerCalculo);
            campoValorDiaria.getDocument().addDocumentListener(listenerCalculo);
            recalcular.run();

            JPanel formulario = new JPanel(new GridLayout(10, 2, 8, 8));

            formulario.add(new JLabel("Cliente:"));
            formulario.add(comboClientes);

            formulario.add(new JLabel("Veículo:"));
            formulario.add(comboVeiculos);

            formulario.add(new JLabel("Início (dd/mm/aaaa):"));
            formulario.add(inicio);

            formulario.add(new JLabel("Previsão de fim (dd/mm/aaaa):"));
            formulario.add(fim);

            formulario.add(new JLabel("Valor da diária (R$):"));
            formulario.add(campoValorDiaria);

            formulario.add(new JLabel("Qtd. de diárias (automático):"));
            formulario.add(campoQtdDiarias);

            formulario.add(new JLabel("Valor total (R$):"));
            formulario.add(campoValorTotal);

            formulario.add(new JLabel("Forma de pagamento:"));
            formulario.add(comboFormaPagamento);

            formulario.add(new JLabel("Status do pagamento:"));
            formulario.add(checkJaPago);

            formulario.add(new JLabel("Data do pagamento (dd/mm/aaaa):"));
            formulario.add(campoDataPagamento);

            int resultado = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Cadastrar locação e faturamento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (resultado != JOptionPane.OK_OPTION) {
                return;
            }

            LocalDate dataInicio = LocalDate.parse(inicio.getText().trim(), FORMATO_DATA);
            String textoFim = fim.getText().trim();
            LocalDate dataFim = textoFim.isEmpty() ? null : LocalDate.parse(textoFim, FORMATO_DATA);

            BigDecimal valorDiaria;
            try {
                valorDiaria = new BigDecimal(
                    campoValorDiaria.getText().trim().replace(",", ".")
                );
            } catch (NumberFormatException ex) {
                ComponentesTela.mostrarErro(
                    this,
                    "Informe um valor de diária numérico válido (ex: 120.00)."
                );
                return;
            }

            boolean jaPago = checkJaPago.isSelected();
            LocalDate dataPagamento = null;
            if (jaPago) {
                dataPagamento = LocalDate.parse(campoDataPagamento.getText().trim(), FORMATO_DATA);
            }

            Locacao locacao = new Locacao();
            locacao.setCliente((Cliente) comboClientes.getSelectedItem());
            locacao.setVeiculo((Veiculo) comboVeiculos.getSelectedItem());
            locacao.setDtInicio(dataInicio);
            locacao.setDtPrevista(dataFim);

            String formaPagamento = (String) comboFormaPagamento.getSelectedItem();

            locacaoController.registrarLocacaoComFaturamento(
                locacao, valorDiaria, formaPagamento, jaPago, dataPagamento
            );

            JOptionPane.showMessageDialog(
                this,
                "Locação e faturamento cadastrados com sucesso!"
            );

            atualizar();

        } catch (DateTimeParseException e) {
            ComponentesTela.mostrarErro(
                this,
                "Informe as datas no formato dd/mm/aaaa."
            );
        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());
        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Não foi possível cadastrar a locação: " + e.getMessage()
            );
        }
    }

    // Finalizar locação
    private void finalizar() {
        Locacao locacao = selecionado();

        if (locacao == null) {
            ComponentesTela.mostrarErro(this, "Selecione uma locação para finalizar.");
            return;
        }

        if (!"LOCADO".equals(locacao.getStatusLocacaoVeiculo())) {
            ComponentesTela.mostrarErro(this, "Esta locação já foi finalizada.");
            return;
        }

        String dataInformada = JOptionPane.showInputDialog(
            this,
            "Informe a data de devolução (dd/mm/aaaa):",
            FORMATO_DATA.format(LocalDate.now())
        );

        if (dataInformada == null) {
            return;
        }

        try {
            LocalDate dataDevolucao = LocalDate.parse(dataInformada.trim(), FORMATO_DATA);

            locacaoController.registrarDevolucao(locacao.getIdLocacao(), dataDevolucao);

            JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso!");

            atualizar();

        } catch (DateTimeParseException e) {
            ComponentesTela.mostrarErro(this, "Informe a data no formato dd/mm/aaaa.");
        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());
        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Não foi possível registrar a devolução: " + e.getMessage()
            );
        }
    }

    // Obter locação selecionada
    private Locacao selecionado() {
        int indice = tabela.getSelectedRow();

        if (indice < 0) {
            return null;
        }

        int indiceModelo = tabela.convertRowIndexToModel(indice);

        if (indiceModelo >= locacoes.size()) {
            return null;
        }

        return locacoes.get(indiceModelo);
    }

    // Formatar datas para exibição
    private String formatarData(LocalDate data) {
        return data == null ? "-" : FORMATO_DATA.format(data);
    }
}
