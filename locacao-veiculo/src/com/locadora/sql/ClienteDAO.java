package com.locadora.sql;

import com.locadora.config.DatabaseConnection;
import com.locadora.exception.GerenciadorDeErros;
import com.locadora.exception.Validador;
import com.locadora.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    
    public void salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, cpf, telefone) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.executeUpdate();
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone")
                ));
            }
        }
        return clientes;
    }


    public void inserir(Cliente cliente) {
        try {
            // Valida os dados antes de mandar para o banco
            Validador.validarCliente(cliente);
        } catch (Exception e) {
            //  Captura qualquer erro (seja de validação ou de SQL como CPF duplicado)
            // e converte para uma mensagem amigável
            String mensagem = GerenciadorDeErros.tratarExcecao(e);
            throw new RuntimeException(mensagem);
        }
    }

    
}
