package com.locadora.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.locadora.controller.VeiculoController;
import com.locadora.model.Veiculo;

public final class TelaVeiculos extends JPanel {
    private final VeiculoController veiculoController = new VeiculoController();
    private final JLabel resumo = ComponentesTela.criarResumo();
    private final DefaultTableModel modelo =
            ComponentesTela.criarModelo("ID", "Placa", "Modelo", "Marca", "Cor");
    private final JTable tabela = ComponentesTela.criarTabela(modelo);
    private List<Veiculo> veiculos = new ArrayList<>();

    public TelaVeiculos(Runnable voltar) {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(ComponentesTela.criarPainelCadastro(
                "Cadastre e mantenha os veículos da locadora.",
                resumo,
                tabela,
                ComponentesTela.criarBotao("Cadastrar", this::cadastrar, true),
                ComponentesTela.criarBotao("Alterar", this::alterar, false),
                ComponentesTela.criarBotao("Excluir", this::excluir, false),
                ComponentesTela.criarBotao("Voltar", voltar, false)),
                BorderLayout.CENTER);

        atualizar();
    }

    // Atualizar tabela com os veículos do banco
    public void atualizar() {
        modelo.setRowCount(0);

        try {
            veiculos = veiculoController.listarTodos();

            for (Veiculo veiculo : veiculos) {
                modelo.addRow(new Object[]{
                    veiculo.getIdVeiculo(),
                    veiculo.getPlacaVeiculo(),
                    veiculo.getModeloVeiculo(),
                    veiculo.getMarcaVeiculo(),
                    veiculo.getCorVeiculo()
                });
            }

            resumo.setText("Veículos cadastrados: " + veiculos.size());

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Não foi possível carregar os veículos: " + e.getMessage()
            );
        }
    }

    // Cadastrar veículo
    private void cadastrar() {
        String[] valores = ComponentesTela.abrirFormulario(
                this,
                "Cadastrar veículo",
                new String[]{"Placa", "Modelo", "Marca", "Cor"},
                null
        );

        if (valores == null) {
            return;
        }

        try {
            Veiculo veiculo = new Veiculo(0, valores[0], valores[1],
                                          valores[2], valores[3]);

            veiculoController.cadastrar(veiculo);
            atualizar();

        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Não foi possível cadastrar o veículo: " + e.getMessage()
            );
        }
    }

    // Alterar veículo
    private void alterar() {
        Veiculo veiculo = selecionado();

        if (veiculo == null) {
            ComponentesTela.mostrarErro(
                this,
                "Selecione um veículo para alterar."
            );
            return;
        }

        String[] valores = ComponentesTela.abrirFormulario(
                this,
                "Alterar veículo",
                new String[]{"Placa", "Modelo", "Marca", "Cor"},
                new String[]{
                    veiculo.getPlacaVeiculo(),
                    veiculo.getModeloVeiculo(),
                    veiculo.getMarcaVeiculo(),
                    veiculo.getCorVeiculo()
                }
        );

        if (valores == null) {
            return;
        }

        try {
            veiculo.setPlacaVeiculo(valores[0]);
            veiculo.setModeloVeiculo(valores[1]);
            veiculo.setMarcaVeiculo(valores[2]);
            veiculo.setCorVeiculo(valores[3]);

            veiculoController.atualizar(veiculo);
            atualizar();

            JOptionPane.showMessageDialog(
                this,
                "Veículo alterado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Não foi possível alterar o veículo: " + e.getMessage()
            );
        }
    }

    // Excluir veículo
    private void excluir() {
        Veiculo veiculo = selecionado();

        if (veiculo == null) {
            ComponentesTela.mostrarErro(
                this,
                "Selecione um veículo para excluir."
            );
            return;
        }

        if (!ComponentesTela.confirmarExclusao(
                this,
                "Deseja excluir o veículo selecionado?")) {
            return;
        }

        try {
            veiculoController.excluir(veiculo.getIdVeiculo());
            atualizar();

        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                this,
                "Não foi possível excluir o veículo. " +
                "Verifique se ele possui locações vinculadas."
            );
        }
    }

    // Obter veículo selecionado na tabela
    private Veiculo selecionado() {
        int indice = tabela.getSelectedRow();

        if (indice < 0) {
            return null;
        }

        int indiceModelo = tabela.convertRowIndexToModel(indice);

        if (indiceModelo >= veiculos.size()) {
            return null;
        }

        return veiculos.get(indiceModelo);
    }
}