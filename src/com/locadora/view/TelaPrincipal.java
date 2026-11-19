package com.locadora.view;

import com.locadora.controller.ClienteController;
import com.locadora.controller.FaturamentoController;
import com.locadora.controller.LocacaoController;
import com.locadora.controller.VeiculoController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TelaPrincipal extends JFrame {
    private static final Color NAVY = new Color(29, 47, 65);
    private static final Color BLUE = new Color(36, 91, 190);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BACKGROUND = new Color(247, 249, 252);

    private final ClienteController clienteController = new ClienteController();
    private final VeiculoController veiculoController = new VeiculoController();
    private final LocacaoController locacaoController = new LocacaoController();
    private final FaturamentoController faturamentoController = new FaturamentoController();
    private final CardLayout layoutTelas = new CardLayout();
    private final JPanel telas = new JPanel(layoutTelas);
    private final JLabel tituloTela = new JLabel("Dashboard");
    private final JLabel totalVeiculos = new JLabel("0");
    private final JLabel totalClientes = new JLabel("0");
    private final JLabel totalLocacoes = new JLabel("0");
    private final JLabel totalFaturamentoMes = new JLabel("R$ 0,00");
    private final List<JButton> botoesMenu = new ArrayList<>();
    private final Icon iconeLogo;
    private final Icon iconePainel;
    private final Icon iconeVeiculo;
    private final Icon iconeVeiculoMenu;
    private final Icon iconeCliente;
    private final Icon iconeLocacao;
    private final Icon iconeRelatorio;
    private final Icon iconeFaturamento;
    private final Icon iconeSair;
    private TelaVeiculos telaVeiculos;
    private TelaClientes telaClientes;
    private TelaLocacoes telaLocacoes;
    private TelaFaturamento telaFaturamento;
    private TelaRelatorios telaRelatorios;
    

    public TelaPrincipal() {
        iconeLogo = carregarIcone("logo.png", 40);
        iconePainel = carregarIcone("painel.png", 20);
        iconeVeiculo = carregarIcone("carros.png", 32);
        iconeVeiculoMenu = carregarIcone("carros.png", 20);
        iconeCliente = carregarIcone("cliente.png", 20);
        iconeLocacao = carregarIcone("locacao.png", 20);
        iconeRelatorio = carregarIcone("relatorio-de-negocios.png", 20);
        iconeFaturamento = carregarIcone("faturamento.png", 20);
        iconeSair = carregarIcone("sair.png", 20);

        setIconImage(new ImageIcon("assets/imagens/logo.png").getImage());
        setTitle("Velozzi Locadora");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        root.add(criarMenuLateral(), BorderLayout.WEST);

        JPanel area = new JPanel(new BorderLayout());
        area.setOpaque(false);
        area.add(criarCabecalho(), BorderLayout.NORTH);
        telas.add(criarDashboard(), "Dashboard");
        telaVeiculos = new TelaVeiculos(() -> mostrarTela("Dashboard", "Dashboard", 0));
        telaClientes = new TelaClientes(() -> mostrarTela("Dashboard", "Dashboard", 0));
        telaLocacoes = new TelaLocacoes(() -> mostrarTela("Dashboard", "Dashboard", 0));
        telaFaturamento = new TelaFaturamento(() -> mostrarTela("Dashboard", "Dashboard", 0));
        telaRelatorios = new TelaRelatorios(() -> mostrarTela("Dashboard", "Dashboard", 0));
        telas.add(telaVeiculos, "Veículos");
        telas.add(telaClientes, "Clientes");
        telas.add(telaLocacoes, "Locações");
        telas.add(telaFaturamento, "Faturamento");
        telas.add(telaRelatorios, "Relatórios");
        area.add(telas, BorderLayout.CENTER);
        root.add(area, BorderLayout.CENTER);
        add(root);
        atualizarDashboard();
    }

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel(new BorderLayout(0, 30));
        menu.setPreferredSize(new Dimension(190, 0));
        menu.setBackground(NAVY);
        menu.setBorder(BorderFactory.createEmptyBorder(28, 14, 24, 14));

        JLabel marca = new JLabel(
            "<html><b style='font-size:15px'>VELOZZI LOCADORA</b></html>"
        );

        marca.setIcon(iconeLogo);
        marca.setIconTextGap(8);
        marca.setForeground(Color.WHITE);

        menu.add(marca, BorderLayout.NORTH);

        JPanel itens = new JPanel(new GridLayout(6, 1, 0, 8));
        itens.setOpaque(false);
        itens.add(criarBotaoMenu("Dashboard", iconePainel, 0, "Dashboard"));
        itens.add(criarBotaoMenu("Veículos", iconeVeiculoMenu, 1, "Veículos"));
        itens.add(criarBotaoMenu("Clientes", iconeCliente, 2, "Clientes"));
        itens.add(criarBotaoMenu("Locações", iconeLocacao, 3, "Locações"));
        itens.add(criarBotaoMenu("Faturamento", iconeFaturamento, 4, "Faturamento"));
        itens.add(criarBotaoMenu("Relatórios", iconeRelatorio, 5, "Relatórios"));
        menu.add(itens, BorderLayout.CENTER);

        JButton sair = new JButton("Sair", new IconComFundo(iconeSair));
        sair.setIconTextGap(8);
        estilizarBotaoMenu(sair, false);
        sair.addActionListener(evento -> System.exit(0));
        menu.add(sair, BorderLayout.SOUTH);
        return menu;
    }

    private JButton criarBotaoMenu(String texto, Icon icone, int indice, String nomeTela) {
        JButton botao = new JButton(texto, new IconComFundo(icone));
        botao.setIconTextGap(8);
        estilizarBotaoMenu(botao, indice == 0);
        botao.addActionListener(evento -> mostrarTela(nomeTela, texto, indice));
        botoesMenu.add(botao);
        return botao;
    }

    private void estilizarBotaoMenu(JButton botao, boolean selecionado) {
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 14));
        botao.setForeground(Color.WHITE);
        botao.setBackground(selecionado ? BLUE : NAVY);
        botao.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 4));
        botao.setFocusPainted(false);
        botao.setOpaque(true);
    }

    private void mostrarTela(String nomeTela, String titulo, int indiceMenu) {
        tituloTela.setText(titulo);
        for (int i = 0; i < botoesMenu.size(); i++) {
            botoesMenu.get(i).setBackground(i == indiceMenu ? BLUE : NAVY);
        }
        atualizarDashboard();
        telaVeiculos.atualizar();
        telaClientes.atualizar();
        telaLocacoes.atualizar();
        telaFaturamento.atualizar();
        telaRelatorios.atualizar();
        layoutTelas.show(telas, nomeTela);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Color.WHITE);
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));
        tituloTela.setFont(new Font("SansSerif", Font.BOLD, 20));
        tituloTela.setForeground(TEXT);
        JLabel usuario = new JLabel(" Administrador ");
        usuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        usuario.setForeground(TEXT);
        cabecalho.add(tituloTela, BorderLayout.WEST);
        cabecalho.add(usuario, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setOpaque(false);
        JPanel conteudo = new JPanel(new BorderLayout(0, 22));
        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));
        conteudo.add(criarBoasVindas(), BorderLayout.NORTH);
        conteudo.add(criarPainelPrincipal(), BorderLayout.CENTER);
        dashboard.add(conteudo, BorderLayout.CENTER);
        return dashboard;
    }

    private JPanel criarBoasVindas() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);
        JLabel titulo = new JLabel("Bem-vindo!");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(TEXT);
        JLabel subtitulo = new JLabel("Visão geral da locadora.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitulo.setForeground(MUTED);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(5));
        painel.add(subtitulo);
        return painel;
    }

    private JPanel criarPainelPrincipal() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setOpaque(false);
        cards.add(criarCard(iconeVeiculo, totalVeiculos, "Veículos cadastrados", BLUE));
        cards.add(criarCard(iconeCliente, totalClientes, "Clientes cadastrados", new Color(42, 150, 88)));
        cards.add(criarCard(iconeLocacao, totalLocacoes, "Locações efetuadas", new Color(221, 145, 25)));
        cards.add(criarCard(
            iconeFaturamento,
            totalFaturamentoMes,
            "Faturamento (mês)",
            new Color(126, 70, 185)
        ));
        painel.add(cards, BorderLayout.NORTH);
        return painel;
    }

    private JPanel criarCard(Icon icone, JLabel valor, String legenda, Color cor) {
        return criarCard(new JLabel(icone, SwingConstants.CENTER), valor, legenda, cor);
    }

    private JPanel criarCard(JLabel iconeLabel, JLabel valorLabel, String legenda, Color cor) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(18, 16, 16, 16)));
        iconeLabel.setPreferredSize(new Dimension(48, 48));
        iconeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        iconeLabel.setForeground(cor);
        iconeLabel.setOpaque(true);
        iconeLabel.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 24));
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        valorLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valorLabel.setForeground(TEXT);
        JLabel legendaLabel = new JLabel(legenda);
        legendaLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        legendaLabel.setForeground(MUTED);
        textos.add(valorLabel);
        textos.add(Box.createVerticalStrut(5));
        textos.add(legendaLabel);
        card.add(iconeLabel, BorderLayout.WEST);
        card.add(textos, BorderLayout.CENTER);
        return card;
    }

    private void atualizarDashboard() {
        try {
            totalVeiculos.setText(String.valueOf(veiculoController.listarTodos().size()));
        } catch (SQLException e) {
            totalVeiculos.setText("--");
            e.printStackTrace();
        }

        try {
            totalClientes.setText(String.valueOf(clienteController.listarTodos().size()));
        } catch (SQLException e) {
            totalClientes.setText("--");
            e.printStackTrace();
        }

        try {
            totalLocacoes.setText(String.valueOf(locacaoController.listarTodas().size()));
        } catch (SQLException e) {
            totalLocacoes.setText("--");
            e.printStackTrace();
        }

        try {
            NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            totalFaturamentoMes.setText(moeda.format(faturamentoController.obterTotalPagoMesAtual()));
        } catch (SQLException e) {
            totalFaturamentoMes.setText("--");
            e.printStackTrace();
        }
    }

    private Icon carregarIcone(String nomeArquivo, int tamanho) {
        File arquivo = new File("assets/imagens", nomeArquivo);
        try {
            BufferedImage imagem = ImageIO.read(arquivo);
            if (imagem == null) {
                throw new IOException("Formato de imagem nao reconhecido.");
            }
            Image imagemRedimensionada = imagem.getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH);
            return new ImageIcon(imagemRedimensionada);
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel carregar o icone: " + arquivo.getPath(), e);
        }
    }

    private static class IconComFundo implements Icon {
        private static final int PADDING = 4;
        private final Icon icone;

        private IconComFundo(Icon icone) {
            this.icone = icone;
        }

        @Override
        public int getIconWidth() {
            return icone.getIconWidth() + PADDING * 2;
        }

        @Override
        public int getIconHeight() {
            return icone.getIconHeight() + PADDING * 2;
        }

        @Override
        public void paintIcon(Component componente, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(241, 245, 249));
            g2.fillRoundRect(x, y, getIconWidth(), getIconHeight(), 8, 8);
            icone.paintIcon(componente, g2, x + PADDING, y + PADDING);
            g2.dispose();
        }
    }
}
