package com.locadora.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.locadora.controller.ClienteController;
import com.locadora.controller.VeiculoController;
import com.locadora.controller.LocacaoController;

import com.locadora.model.Cliente;
import com.locadora.model.Veiculo;
import com.locadora.model.Locacao;

public final class TelaLocacoes extends JPanel {
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final LocacaoController locacaoController =
            new LocacaoController();
    private final ClienteController clienteController =
            new ClienteController();
    private final VeiculoController veiculoController =
            new VeiculoController();
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
                ComponentesTela.criarBotao(
                    "Cadastrar locação", this::cadastrar, true),
                ComponentesTela.criarBotao(
                    "Finalizar locação", this::finalizar, false),
                ComponentesTela.criarBotao(
                    "Voltar", voltar, false)
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

    // Cadastrar locação
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
                        Cliente cliente = (Cliente) value;
                        label.setText(cliente.getNomeCliente());
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

            JTextField inicio = new JTextField(
                FORMATO_DATA.format(LocalDate.now()), 12
            );

            JTextField fim = new JTextField(
                FORMATO_DATA.format(LocalDate.now().plusDays(1)), 12
            );

            JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));

            formulario.add(new JLabel("Cliente:"));
            formulario.add(comboClientes);

            formulario.add(new JLabel("Veículo:"));
            formulario.add(comboVeiculos);

            formulario.add(new JLabel("Início (dd/MM/yyyy):"));
            formulario.add(inicio);

            formulario.add(new JLabel("Previsão de fim (dd/MM/yyyy):"));
            formulario.add(fim);

            int resultado = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Cadastrar locação",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (resultado != JOptionPane.OK_OPTION) {
                return;
            }

            LocalDate dataInicio = LocalDate.parse(
                inicio.getText().trim(), FORMATO_DATA
            );

            String textoFim = fim.getText().trim();

            LocalDate dataFim = textoFim.isEmpty()
                ? null
                : LocalDate.parse(textoFim, FORMATO_DATA);

            Cliente cliente = (Cliente) comboClientes.getSelectedItem();
            Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();

            Locacao locacao = new Locacao();

            locacao.setCliente(cliente);
            locacao.setVeiculo(veiculo);
            locacao.setDtInicio(dataInicio);
            locacao.setDtPrevista(dataFim);

            locacaoController.registrarLocacao(locacao);

            JOptionPane.showMessageDialog(
                this,
                "Locação cadastrada com sucesso!"
            );

            atualizar();

        } catch (DateTimeParseException e) {
            ComponentesTela.mostrarErro(
                this,
                "Informe as datas no formato dd/MM/yyyy."
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
            ComponentesTela.mostrarErro(
                this,
                "Selecione uma locação para finalizar."
            );
            return;
        }

        if (!"LOCADO".equals(locacao.getStatusLocacaoVeiculo())) {
            ComponentesTela.mostrarErro(
                this,
                "Esta locação já foi finalizada."
            );
            return;
        }

        String dataInformada = JOptionPane.showInputDialog(
            this,
            "Informe a data de devolução (dd/MM/yyyy):",
            FORMATO_DATA.format(LocalDate.now())
        );

        if (dataInformada == null) {
            return;
        }

        try {
            LocalDate dataDevolucao = LocalDate.parse(
                dataInformada.trim(), FORMATO_DATA
            );

            locacaoController.registrarDevolucao(
                locacao.getIdLocacao(),
                dataDevolucao
            );

            JOptionPane.showMessageDialog(
                this,
                "Devolução registrada com sucesso!"
            );

            atualizar();

        } catch (DateTimeParseException e) {
            ComponentesTela.mostrarErro(
                this,
                "Informe a data no formato dd/MM/yyyy."
            );

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