package com.locadora.dao;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Veiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {
    // Cadastrar veículo
    public void salvar(Veiculo veiculo) throws SQLException {
        String sql = "INSERT INTO veiculos " +
                     "(placa_veiculo, modelo_veiculo, marca_veiculo, cor_veiculo) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, veiculo.getPlacaVeiculo());
            stmt.setString(2, veiculo.getModeloVeiculo());
            stmt.setString(3, veiculo.getMarcaVeiculo());
            stmt.setString(4, veiculo.getCorVeiculo());

            stmt.executeUpdate();
        }
    }

    // Listar todos os veículos
    public List<Veiculo> listarTodos() throws SQLException {
        List<Veiculo> veiculos = new ArrayList<>();

        String sql = "SELECT id_veiculo, placa_veiculo, modelo_veiculo, " +
                     "marca_veiculo, cor_veiculo FROM veiculos";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                veiculos.add(new Veiculo(
                    rs.getInt("id_veiculo"),
                    rs.getString("placa_veiculo"),
                    rs.getString("modelo_veiculo"),
                    rs.getString("marca_veiculo"),
                    rs.getString("cor_veiculo")
                ));
            }
        }

        return veiculos;
    }

    // Atualizar veículo
    public boolean atualizar(Veiculo veiculo) throws SQLException {
        String sql = "UPDATE veiculos SET " +
                     "placa_veiculo = ?, " +
                     "modelo_veiculo = ?, " +
                     "marca_veiculo = ?, " +
                     "cor_veiculo = ? " +
                     "WHERE id_veiculo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, veiculo.getPlacaVeiculo());
            stmt.setString(2, veiculo.getModeloVeiculo());
            stmt.setString(3, veiculo.getMarcaVeiculo());
            stmt.setString(4, veiculo.getCorVeiculo());
            stmt.setInt(5, veiculo.getIdVeiculo());

            return stmt.executeUpdate() > 0;
        }
    }

    // Excluir veículo
    public boolean excluir(int idVeiculo) throws SQLException {
        String sql = "DELETE FROM veiculos WHERE id_veiculo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVeiculo);

            return stmt.executeUpdate() > 0;
        }
    }
}
