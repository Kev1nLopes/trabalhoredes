package View;

import Model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

public class Screen extends JFrame implements ActionListener {


    private JTextField text;
    private JTextPane painelEsquerdo = new JTextPane();
    private JTextPane painelDireito = new JTextPane();
    private Usuario usuario;
    public Screen(String nome){
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        System.out.println(usuario.getNome());




        setTitle("Chat udp");
        setSize(800, 500);
        //Quando clicar em fechar, fechar mesmo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Não permitir mudar o tamanho da tela
        setResizable(false);
        //Ir para o meio da tela ao iniciar
        setLocationRelativeTo(null);
        setLayout(null);

        JButton jButton = new JButton("Enviar");
        jButton.setBounds(580, 430, 200, 30);
        jButton.setVisible(true);
        jButton.setFont(new Font("Arial", Font.BOLD, 20));
        jButton.setForeground(new Color(200,200,200));
        jButton.setBackground(new Color(0,0,0));
        jButton.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
        jButton.addActionListener(this);

        JLabel jLabel = new JLabel("Mensagem");
        jLabel.setBounds(280, 400, 100, 30);
        jLabel.setFont(new Font("Arial", Font.ITALIC, 20));

        text = new JTextField();
        text.setFont(new Font("Arial", Font.ITALIC, 20));
        text.setVisible(true);
        text.setEnabled(true);



        painelDireito.setBounds(this.getX() / 3 + 15, 0, this.getX() / 2 + 30, 500);

        painelEsquerdo.setBounds(0, 0, this.getX() / 3, 500);
        jLabel.setBounds(10, 400, this.painelDireito.getX() - 100, 30);
        text.setBounds(10, 430, this.painelDireito.getX() - 100, 30);


        add(painelEsquerdo);
        add(painelDireito);

        painelDireito.add(jButton);
        painelDireito.add(jLabel);
        painelDireito.add(text);
        painelDireito.setVisible(true);
        painelEsquerdo.setVisible(true);

        setVisible(true);




    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, text.getText().toString(), "Sucesso", JOptionPane.QUESTION_MESSAGE);
    }
}


