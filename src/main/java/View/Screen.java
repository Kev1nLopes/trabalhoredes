package View;

import Model.Usuario;
import org.example.ClientThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Screen extends JFrame implements ActionListener {

    private JTextField text;

    private DatagramSocket _socket;

    private Usuario _user;

    private JTextPane painelEsquerdo = new JTextPane();

    private ArrayList<String> messages  = new ArrayList<String>();

    private byte[] recebeMensagens;
    private JTextArea painelDireito = new JTextArea();

    public Screen(String nome, DatagramSocket socket, Usuario user){

        _socket = socket;
        _user = user;





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
        jButton.setBounds(300, 430, 150, 30);
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



        while(true){
            recebeSonda();
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            synchronized (text){
                ClientThread clientThread = new ClientThread(_socket, _user.getNome() + ": " + text.getText());
                clientThread.start();
                painelDireito.append(_user.getNome() + ": " + text.getText() + "\n");
                text.setText(null);
            }
        }catch (RuntimeException ex){

        }

    }


    public void recebeSonda(){
        try{
            System.out.println("recebendo sonda");
            recebeMensagens = new byte[1024];
            DatagramPacket recebeSonda = new DatagramPacket(recebeMensagens, recebeMensagens.length);
            _socket.receive(recebeSonda);

            String mensagem = new String(recebeSonda.getData(), 0, recebeSonda.getLength());
            System.out.println("Mensagem recebida de " + recebeSonda.getAddress().getHostAddress());
            painelDireito.append(mensagem + "\n");


        }catch (IOException e){

        }
    }
}


