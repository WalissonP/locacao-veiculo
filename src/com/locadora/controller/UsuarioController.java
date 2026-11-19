package com.locadora.controller;

import com.locadora.dao.UsuarioDAO;
import com.locadora.model.Usuario;

import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    private static final int ITERACOES = 210000;
    private static final int TAMANHO_CHAVE = 256;
    private static final int TAMANHO_SALT = 16;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // Autenticar administrador
    public Usuario autenticar(String nomeUsuario, String senha) throws SQLException {

        validarCredenciais(nomeUsuario, senha);

        Usuario usuario = usuarioDAO.buscarPorNomeUsuario(nomeUsuario.trim());

        if (usuario == null) {
            return null;
        }

        if (!verificarSenha(senha, usuario.getSenhaUsuario())) {
            return null;
        }

        return usuario;
    }

    // Validar os dados informados no login
    private void validarCredenciais(String nomeUsuario, String senha) {

        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Informe o nome de usuário."
            );
        }

        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException(
                "Informe a senha."
            );
        }
    }

    // Verificar se a senha digitada corresponde ao hash armazenado
    private boolean verificarSenha(String senha, String senhaHash) {

        try {
            String[] partes = senhaHash.split("\\$");

            if (partes.length != 4 || !partes[0].equals("pbkdf2")) {
                return false;
            }

            int iteracoes = Integer.parseInt(partes[1]);

            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashArmazenado = Base64.getDecoder().decode(partes[3]);

            KeySpec especificacao = new PBEKeySpec(
                senha.toCharArray(),
                salt,
                iteracoes,
                TAMANHO_CHAVE
            );

            SecretKeyFactory fabrica = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            );

            byte[] hashCalculado = fabrica.generateSecret(especificacao).getEncoded();

            ((PBEKeySpec) especificacao).clearPassword();

            return MessageDigest.isEqual(hashArmazenado, hashCalculado);

        } catch (Exception e) {
            return false;
        }
    }

    // Gerar hash seguro para a senha do administrador
    public static String gerarHashSenha(String senha) {

        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException(
                "A senha não pode estar vazia."
            );
        }

        try {
            byte[] salt = new byte[TAMANHO_SALT];
            new SecureRandom().nextBytes(salt);

            KeySpec especificacao = new PBEKeySpec(
                senha.toCharArray(),
                salt,
                ITERACOES,
                TAMANHO_CHAVE
            );

            SecretKeyFactory fabrica = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            );

            byte[] hash = fabrica.generateSecret(especificacao).getEncoded();

            ((PBEKeySpec) especificacao).clearPassword();

            return "pbkdf2$" + ITERACOES + "$" +
                Base64.getEncoder().encodeToString(salt) + "$" +
                Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                "Não foi possível gerar o hash da senha.", e
            );
        }
    }
}