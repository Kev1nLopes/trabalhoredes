package View;

import Model.UserServer;
import Model.Usuario;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ClientThread;
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Screen extends JFrame implements ActionListener {

    private JTextField text;

    private DatagramSocket _socket;

    private Usuario _user;

    private JTextPane painelEsquerdo = new JTextPane();

    private ArrayList<String> messages  = new ArrayList<String>();

    private byte[] recebeMensagens;
    private JTextArea painelDireito = new JTextArea();

    private ArrayList<String> userServerString = new ArrayList<>();

    public Screen(String nome, DatagramSocket socket, Usuario user){

        _socket = socket;
        _user = user;





        setTitle("Chat udp " + _user.getNome());
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

        painelEsquerdo.setBounds(0, 30, this.getX() / 3, 470);
        jLabel.setBounds(10, 400, this.painelDireito.getX() - 100, 30);
        text.setBounds(10, 430, this.painelDireito.getX() - 100, 30);

        String[] statusListString = {"DISPONIVEL", "NAO_PERTURBE", "VOLTO_LOGO"};
        JComboBox statusList = new JComboBox(statusListString);
        statusList.setBounds(0, 0, this.getX() / 3, 30);
        statusList.setSelectedIndex(0);
        statusList.addActionListener(this::changeUserStatus);
        add(statusList);

        add(painelEsquerdo);
        add(painelDireito);

        JButton btnSair = new JButton();
        btnSair.setText("Sair");
        btnSair.setBounds(( this.getX() / 3 ) / 3, 380, 100, 30);
        btnSair.addActionListener(this::logout);

        btnSair.setVisible(true);



        painelDireito.add(jButton);

        painelDireito.add(jLabel);
        painelDireito.add(text);
        painelEsquerdo.add(btnSair);
        painelDireito.setVisible(true);
        painelEsquerdo.setVisible(true);
        statusList.setVisible(true);

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

    public void logout(ActionEvent e){
        try{
            byte[] data = "logout".getBytes();
            DatagramPacket logoutPacket = new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), 8085);
            _socket.send(logoutPacket);
            _socket.close();
            System.exit(0);
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public void clearPanel(JTextPane textPane){
        StyledDocument doc = textPane.getStyledDocument();
        ElementIterator iterator = new ElementIterator(doc.getDefaultRootElement());

        Element element;
        while ((element = iterator.next()) != null) {
            AttributeSet attributes = element.getAttributes();
            if (attributes.getAttribute(StyleConstants.ComponentElementName) != null) {
                int startOffset = element.getStartOffset();
                int endOffset = element.getEndOffset();
                MutableAttributeSet emptyAttributes = new SimpleAttributeSet();
                doc.setCharacterAttributes(startOffset, endOffset - startOffset, emptyAttributes, false);
            }
        }
    }

    public static void updateJTextPane(JTextPane textPane) {
        StyledDocument doc = textPane.getStyledDocument();

        try {
            // Remove o conteúdo existente
            doc.remove(0, doc.getLength());


            // Adiciona novo conteúdo

        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Revalida o JTextPane para atualizar a interface do usuário
        textPane.revalidate();
    }


    public void changeUserStatus(ActionEvent e){
       JComboBox cb = (JComboBox) e.getSource();
       String valor = (String) cb.getSelectedItem();
       System.out.println(valor);
       try{
            byte[] message = ("status " + this._user.getNome() + ":" + valor).getBytes();
            DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), 8085);
            _socket.send(packet);
       }catch(IOException ex){
            new RuntimeException(ex);
       }
    }


    public void recebeSonda(){
        try{
            System.out.println("recebendo sonda");
            recebeMensagens = new byte[1024];
            DatagramPacket recebeSonda = new DatagramPacket(recebeMensagens, recebeMensagens.length);
            _socket.receive(recebeSonda);
            ObjectMapper om = new ObjectMapper();
            String mensagem = new String(recebeSonda.getData(), 0, recebeSonda.getLength());


            if(mensagem.contains("[") && mensagem.contains("{")){
                System.out.println("chegou um json");
                System.out.println(mensagem);

                Gson gson = new Gson();
                String[] objetos = gson.fromJson(mensagem, String[].class);
                final int width = this.getX() / 3;

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int marginTop = 0;
                        for (var u: objetos
                        ) {
                            System.out.println(u);
                            UserServer us = gson.fromJson(u, UserServer.class);
                            userServerString.add(u);
                            JButton button = new JButton();
                            button.setText(us.getNome() + " - " + us.getStatus());
                            button.setBounds(0, marginTop, width, 30);
                            marginTop += 30;

                            painelEsquerdo.add(button);
                            button.setVisible(true);
                        }

                    }
                });
                painelEsquerdo.setVisible(true);


                

            }else{
                System.out.println("Mensagem recebida de " + recebeSonda.getAddress().getHostAddress());
                painelDireito.append(mensagem + "\n");

            }

        }catch (IOException e){

        }
    }
}


