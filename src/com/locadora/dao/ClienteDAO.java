
package com.locadora.sql;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

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

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT * FROM cliente";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("idcliente"),
                    rs.getString("nome_cliente"),
                    rs.getString("email_cliente"),
                    rs.getString("telefone_cliente")
                ));
            }
        }

        return clientes;
    }
}