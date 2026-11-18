package com.locadora.view;

import com.locadora.controller.ClienteController;
import com.locadora.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public final class TelaClientes extends JPanel {

    private final ClienteController clienteController = new ClienteController();

    private final JLabel resumo = ComponentesTela.criarResumo();

    private final DefaultTableModel modelo =
            ComponentesTela.criarModelo("ID", "Nome", "E-mail", "Telefone");

    private final JTable tabela = ComponentesTela.criarTabela(modelo);

    public TelaClientes(Runnable voltar) {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(ComponentesTela.criarPainelCadastro(
                "Cadastre e mantenha os clientes da locadora.",
                resumo,
                tabela,
                ComponentesTela.criarBotao("Cadastrar", this::cadastrar, true),
                ComponentesTela.criarBotao("Alterar", this::alterar, false),
                ComponentesTela.criarBotao("Excluir", this::excluir, false),
                ComponentesTela.criarBotao("Voltar", voltar, false)),
                BorderLayout.CENTER);

        atualizar();
    }

    // Listar clientes cadastrados no banco
    public void atualizar() {
        modelo.setRowCount(0);

        try {
            List<Cliente> clientes = clienteController.listarTodos();

            for (Cliente cliente : clientes) {
                modelo.addRow(new Object[]{
                        cliente.getIdCliente(),
                        cliente.getNomeCliente(),
                        cliente.getEmailCliente(),
                        cliente.getTelefoneCliente()
                });
            }

            resumo.setText("Clientes cadastrados: " + clientes.size());

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                    this,
                    "Não foi possível carregar os clientes: " + e.getMessage()
            );
        }
    }

    // Cadastrar cliente
    private void cadastrar() {
        String[] valores = ComponentesTela.abrirFormulario(
                this,
                "Cadastrar cliente",
                new String[]{"Nome", "E-mail", "Telefone"},
                null
        );

        if (valores == null) {
            return;
        }

        try {
            Cliente cliente = new Cliente();

            cliente.setNomeCliente(valores[0].trim());
            cliente.setEmailCliente(valores[1].trim());
            cliente.setTelefoneCliente(valores[2].trim());

            clienteController.cadastrar(cliente);

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente cadastrado com sucesso!"
            );

            atualizar();

        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                    this,
                    "Erro ao cadastrar cliente: " + e.getMessage()
            );
        }
    }

    // Alterar cliente selecionado
    private void alterar() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            ComponentesTela.mostrarErro(
                    this,
                    "Selecione um cliente para alterar."
            );
            return;
        }

        linha = tabela.convertRowIndexToModel(linha);

        int id = (int) modelo.getValueAt(linha, 0);

        String[] valoresAtuais = {
                modelo.getValueAt(linha, 1).toString(),
                modelo.getValueAt(linha, 2).toString(),
                modelo.getValueAt(linha, 3).toString()
        };

        String[] valores = ComponentesTela.abrirFormulario(
                this,
                "Alterar cliente",
                new String[]{"Nome", "E-mail", "Telefone"},
                valoresAtuais
        );

        if (valores == null) {
            return;
        }

        try {
            Cliente cliente = new Cliente();

            cliente.setIdCliente(id);
            cliente.setNomeCliente(valores[0].trim());
            cliente.setEmailCliente(valores[1].trim());
            cliente.setTelefoneCliente(valores[2].trim());

            clienteController.atualizar(cliente);

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente atualizado com sucesso!"
            );

            atualizar();

        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                    this,
                    "Erro ao atualizar cliente: " + e.getMessage()
            );
        }
    }

    // Excluir cliente selecionado
    private void excluir() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            ComponentesTela.mostrarErro(
                    this,
                    "Selecione um cliente para excluir."
            );
            return;
        }

        linha = tabela.convertRowIndexToModel(linha);

        int id = (int) modelo.getValueAt(linha, 0);

        String nome = modelo.getValueAt(linha, 1).toString();

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o cliente " + nome + "?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            clienteController.excluir(id);

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente excluído com sucesso!"
            );

            atualizar();

        } catch (IllegalArgumentException e) {
            ComponentesTela.mostrarErro(this, e.getMessage());

        } catch (SQLException e) {
            ComponentesTela.mostrarErro(
                    this,
                    "Erro ao excluir cliente: " + e.getMessage()
            );
        }
    }
}