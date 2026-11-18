package com.locadora.dao;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Cadastrar cliente
    public void salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente " +
                     "(nome_cliente, email_cliente, telefone_cliente) " +
                     "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNomeCliente());
            stmt.setString(2, cliente.getEmailCliente());
            stmt.setString(3, cliente.getTelefoneCliente());

            stmt.executeUpdate();
        }
    }

    // Listar todos os clientes
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT id_cliente, nome_cliente, email_cliente, telefone_cliente " +
                     "FROM cliente ORDER BY id_cliente ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nome_cliente"),
                    rs.getString("email_cliente"),
                    rs.getString("telefone_cliente")
                ));
            }
        }

        return clientes;
    }

    // Atualizar cliente
    public boolean atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET " +
                     "nome_cliente = ?, " +
                     "email_cliente = ?, " +
                     "telefone_cliente = ? " +
                     "WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNomeCliente());
            stmt.setString(2, cliente.getEmailCliente());
            stmt.setString(3, cliente.getTelefoneCliente());
            stmt.setInt(4, cliente.getIdCliente());

            return stmt.executeUpdate() > 0;
        }
    }

    // Verificar se o cliente possui alguma locação
    public boolean temLocacao(int idCliente) throws SQLException {
        String sql = "SELECT EXISTS (" +
                     "SELECT 1 FROM locacao WHERE id_cliente = ?" +
                     ")";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        }

        return false;
    }

    // Excluir cliente
    public boolean excluir(int idCliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            return stmt.executeUpdate() > 0;
        }
    }

    // Verificar se o cliente está cadastrado
    public boolean existeCliente(int idCliente) throws SQLException {
        String sql = "SELECT EXISTS (" +
                     "SELECT 1 FROM cliente WHERE id_cliente = ?" +
                     ")";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        }

        return false;
    }
}