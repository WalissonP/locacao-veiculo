package com.locadora.dao;

import com.locadora.config.DatabaseConnection;
import com.locadora.model.Usuario;

import java.sql.*;

public class UsuarioDAO {

    // Buscar usuário pelo nome de usuário
    public Usuario buscarPorNomeUsuario(String nomeUsuario) throws SQLException {
        String sql = "SELECT id_usuario, nome_usuario, senha_usuario " +
                     "FROM usuarios WHERE nome_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nome_usuario"),
                        rs.getString("senha_usuario")
                    );
                }
            }
        }

        return null;
    }
}