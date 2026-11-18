package com.locadora.controller;

import com.locadora.model.Cliente;
import com.locadora.dao.ClienteDAO;

import java.sql.SQLException;
import java.util.List;

public class ClienteController {
    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    // Cadastrar cliente
    public void cadastrar(Cliente cliente) throws SQLException {
        validarCliente(cliente);
        clienteDAO.salvar(cliente);
    }

    // Listar todos os clientes
    public List<Cliente> listarTodos() throws SQLException {
        return clienteDAO.listarTodos();
    }

    // Alterar cliente
    public void atualizar(Cliente cliente) throws SQLException {
        validarCliente(cliente);

        if (cliente.getIdCliente() <= 0) {
            throw new IllegalArgumentException(
                "O ID do cliente deve ser válido."
            );
        }

        if (!clienteDAO.atualizar(cliente)) {
            throw new IllegalArgumentException(
                "Cliente não encontrado."
            );
        }
    }

    // Excluir cliente
    public void excluir(int idCliente) throws SQLException {
        if (idCliente <= 0) {
            throw new IllegalArgumentException(
                "O ID do cliente deve ser válido."
            );
        }

        if (!clienteDAO.existeCliente(idCliente)) {
            throw new IllegalArgumentException(
                "Cliente não encontrado."
            );
        }

        if (clienteDAO.temLocacao(idCliente)) {
            throw new IllegalArgumentException(
                "Não é possível excluir este cliente, pois ele possui locações registradas."
            );
        }

        if (!clienteDAO.excluir(idCliente)) {
            throw new IllegalArgumentException(
                "Não foi possível excluir o cliente."
            );
        }
    }

    // Validar os dados do cliente
    private void validarCliente(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                "Os dados do cliente não foram informados."
            );
        }

        if (cliente.getNomeCliente() == null ||
            cliente.getNomeCliente().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O nome do cliente é obrigatório."
            );
        }

        if (cliente.getNomeCliente().trim().length() > 100) {
            throw new IllegalArgumentException(
                "O nome do cliente deve ter no máximo 100 caracteres."
            );
        }

        if (cliente.getEmailCliente() == null ||
            cliente.getEmailCliente().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O e-mail do cliente é obrigatório."
            );
        }

        if (cliente.getEmailCliente().trim().length() > 255) {
            throw new IllegalArgumentException(
                "O e-mail deve ter no máximo 255 caracteres."
            );
        }

        if (cliente.getTelefoneCliente() == null ||
            cliente.getTelefoneCliente().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O telefone do cliente é obrigatório."
            );
        }

        if (cliente.getTelefoneCliente().trim().length() > 15) {
            throw new IllegalArgumentException(
                "O telefone deve ter no máximo 15 caracteres."
            );
        }
    }
}