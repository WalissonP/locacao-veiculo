package com.locadora.view;

import com.locadora.controller.UsuarioController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.sql.SQLException;

public class TelaLogin extends JFrame {

    private static final Color NAVY = new Color(29, 47, 65);
    private static final Color BLUE = new Color(36, 91, 190);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    private final UsuarioController usuarioController;

    private final JTextField campoUsuario = new JTextField();
    private final JPasswordField campoSenha = new JPasswordField();

    private final JButton botaoEntrar = new JButton("Entrar");
    private final JButton botaoVisualizarSenha = new JButton();

    private boolean senhaVisivel = false;

    public TelaLogin() {

        usuarioController = new UsuarioController();

        setTitle("Login - Velozzi Locadora");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setIconImage(new ImageIcon("assets/imagens/logo.png").getImage());

        JPanel principal = new JPanel(new GridLayout(1, 2));
        principal.add(criarPainelMarca());
        principal.add(criarPainelLogin());

        add(principal);

        getRootPane().setDefaultButton(botaoEntrar);
    }

    // Painel lateral com a identidade visual da locadora
    private JPanel criarPainelMarca() {

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(NAVY);
        painel.setBorder(new EmptyBorder(70, 35, 70, 35));

        JLabel logo = new JLabel();

        ImageIcon imagem = new ImageIcon("assets/imagens/logo.png");

        Image imagemRedimensionada = imagem.getImage().getScaledInstance(
            100, 100, Image.SCALE_SMOOTH
        );

        logo.setIcon(new ImageIcon(imagemRedimensionada));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nome = new JLabel("VELOZZI LOCADORA");
        nome.setFont(new Font("SansSerif", Font.BOLD, 23));
        nome.setForeground(Color.WHITE);
        nome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descricao = new JLabel(
            "<html><div style='text-align:center;'>"
            + "Gestão de veículos, clientes e locações "
            + "em um só lugar."
            + "</div></html>"
        );

        descricao.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descricao.setForeground(new Color(220, 230, 242));
        descricao.setAlignmentX(Component.CENTER_ALIGNMENT);

        painel.add(Box.createVerticalGlue());
        painel.add(logo);
        painel.add(Box.createVerticalStrut(20));
        painel.add(nome);
        painel.add(Box.createVerticalStrut(12));
        painel.add(descricao);
        painel.add(Box.createVerticalGlue());

        return painel;
    }

    // Painel com os campos de autenticação
    private JPanel criarPainelLogin() {

        // Painel principal da área de login
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(new EmptyBorder(50, 45, 50, 45));

        // Largura padrão dos componentes
        int largura = 416;

        // Formulário com alinhamento à esquerda
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(Color.WHITE);
        formulario.setPreferredSize(new Dimension(largura, 340));
        formulario.setMaximumSize(new Dimension(largura, 340));

        // Título
        JLabel titulo = new JLabel("Bem-vindo!");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        titulo.setMaximumSize(new Dimension(largura, 40));

        // Subtítulo
        JLabel subtitulo = new JLabel("Entre com sua conta.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(MUTED);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setMaximumSize(new Dimension(largura, 25));

        // Rótulo do usuário
        JLabel labelUsuario = criarLabel("Usuário");

        // Campo de usuário
        configurarCampo(campoUsuario, largura);

        // Rótulo da senha
        JLabel labelSenha = criarLabel("Senha");

        // Botão do olho
        botaoVisualizarSenha.setIcon(new IconOlho(false));
        botaoVisualizarSenha.setToolTipText("Mostrar senha");
        botaoVisualizarSenha.setPreferredSize(new Dimension(42, 50));
        botaoVisualizarSenha.setMinimumSize(new Dimension(42, 50));
        botaoVisualizarSenha.setMaximumSize(new Dimension(42, 50));
        botaoVisualizarSenha.setBackground(Color.WHITE);
        botaoVisualizarSenha.setBorderPainted(false);
        botaoVisualizarSenha.setFocusPainted(false);
        botaoVisualizarSenha.setContentAreaFilled(false);
        botaoVisualizarSenha.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botaoVisualizarSenha.addActionListener(
            e -> alternarVisibilidadeSenha()
        );

        // Campo de senha com o olho integrado
        JPanel painelSenha = new JPanel(new BorderLayout());
        painelSenha.setBackground(Color.WHITE);
        painelSenha.setBorder(BorderFactory.createLineBorder(BORDER));
        painelSenha.setPreferredSize(new Dimension(largura, 50));
        painelSenha.setMinimumSize(new Dimension(largura, 50));
        painelSenha.setMaximumSize(new Dimension(largura, 50));
        painelSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoSenha.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campoSenha.setForeground(TEXT);
        campoSenha.setBackground(Color.WHITE);
        campoSenha.setCaretColor(BLUE);
        campoSenha.setBorder(new EmptyBorder(10, 12, 10, 5));

        painelSenha.add(campoSenha, BorderLayout.CENTER);
        painelSenha.add(botaoVisualizarSenha, BorderLayout.EAST);

        // Botão entrar
        botaoEntrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        botaoEntrar.setForeground(Color.WHITE);
        botaoEntrar.setBackground(BLUE);
        botaoEntrar.setFocusPainted(false);
        botaoEntrar.setBorderPainted(false);
        botaoEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoEntrar.setPreferredSize(new Dimension(largura, 45));
        botaoEntrar.setMinimumSize(new Dimension(largura, 45));
        botaoEntrar.setMaximumSize(new Dimension(largura, 45));
        botaoEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoEntrar.addActionListener(e -> entrar());

        // Montagem do formulário
        formulario.add(titulo);
        formulario.add(Box.createVerticalStrut(8));

        formulario.add(subtitulo);
        formulario.add(Box.createVerticalStrut(35));

        formulario.add(labelUsuario);
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(campoUsuario);
        formulario.add(Box.createVerticalStrut(22));

        formulario.add(labelSenha);
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(painelSenha);
        formulario.add(Box.createVerticalStrut(30));

        formulario.add(botaoEntrar);

        // Centraliza o formulário na área branca
        // Os componentes internos permanecem alinhados à esquerda
        painel.add(formulario);

        return painel;
    }

    // Criar rótulos dos campos
    private JLabel criarLabel(String texto) {

        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    // Configurar aparência dos campos
    private void configurarCampo(JTextField campo, int largura) {

        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(TEXT);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(BLUE);

        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(10, 12, 10, 12)
        ));

        campo.setPreferredSize(new Dimension(largura, 50));
        campo.setMinimumSize(new Dimension(largura, 50));
        campo.setMaximumSize(new Dimension(largura, 50));

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // Mostrar ou ocultar a senha
    private void alternarVisibilidadeSenha() {

        senhaVisivel = !senhaVisivel;

        if (senhaVisivel) {
            campoSenha.setEchoChar((char) 0);
        } else {
            campoSenha.setEchoChar('•');
        }

        botaoVisualizarSenha.setIcon(new IconOlho(senhaVisivel));

        botaoVisualizarSenha.setToolTipText(
            senhaVisivel ? "Ocultar senha" : "Mostrar senha"
        );
    }

    // Ícone de olho desenhado em Java
    private static class IconOlho implements Icon {

        private final boolean visivel;

        public IconOlho(boolean visivel) {
            this.visivel = visivel;
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }

        @Override
        public void paintIcon(Component componente, Graphics graphics, int x, int y) {

            Graphics2D g2 = (Graphics2D) graphics.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(MUTED);
            g2.setStroke(new BasicStroke(1.7f));

            // Contorno do olho
            g2.draw(new Ellipse2D.Double(x + 2, y + 5, 16, 10));

            // Pupila
            g2.fill(new Ellipse2D.Double(x + 7, y + 7, 6, 6));

            // Traço para indicar senha oculta
            if (!visivel) {
                g2.drawLine(x + 3, y + 17, x + 17, y + 3);
            }

            g2.dispose();
        }
    }

    // Validar credenciais e abrir a tela principal
    private void entrar() {

        String nomeUsuario = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword());

        try {

            botaoEntrar.setEnabled(false);
            botaoEntrar.setText("Entrando...");

            var usuario = usuarioController.autenticar(nomeUsuario, senha);

            if (usuario != null) {

                TelaPrincipal telaPrincipal = new TelaPrincipal();
                telaPrincipal.setVisible(true);

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "Usuário ou senha incorretos.",
                    "Falha no login",
                    JOptionPane.ERROR_MESSAGE
                );

                campoSenha.setText("");
                campoSenha.requestFocus();
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Atenção",
                JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Não foi possível realizar o login.\nVerifique a conexão com o banco de dados.",
                "Erro de conexão",
                JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();

        } finally {

            botaoEntrar.setEnabled(true);
            botaoEntrar.setText("Entrar");
        }
    }
}