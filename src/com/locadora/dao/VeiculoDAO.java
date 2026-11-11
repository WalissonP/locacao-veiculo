package com.locadora.sql;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Veiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

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

    public List<Veiculo> listarTodos() throws SQLException {
        List<Veiculo> veiculos = new ArrayList<>();

        String sql = "SELECT * FROM veiculos";

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
}