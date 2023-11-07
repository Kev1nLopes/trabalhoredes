package org.example;

import Model.Usuario;
import View.Screen;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;

public class ChatClient extends JFrame {

    private static  DatagramSocket socket;

    static{
        try{
            socket = new DatagramSocket();
        }catch (SocketException e){
            throw new RuntimeException(e);
        }
    }

    private static InetAddress address;


    static {
        try{
            address = InetAddress.getByName("localhost");
        }catch (UnknownHostException e){
            throw new RuntimeException(e);
        }
    }



    private static final int SERVER_PORT = 8085;
    private static final TextField inputBox = new TextField();

    public static void main(String[] args ) throws IOException{


        JTextField field = new JTextField();

        while(field.getText().length() == 0){
            JOptionPane.showMessageDialog(null, field, "Bem vindo ao chat p2p", JOptionPane.PLAIN_MESSAGE);
            if(field.getText().length()>  0){
                byte[] uuid = ("Entrou ;" + field.getText()).getBytes();
                DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT);
                socket.send(initialize);
                new Screen(field.getText(), socket, new Usuario(field.getText(), Usuario.StatusUsuario.DISPONIVEL, InetAddress.getByName("localhost")));

            }else{
                JOptionPane.showMessageDialog(null, "Por favor informe seu nome infeliz", "Erro", JOptionPane.ERROR_MESSAGE);

            }
        }
     }


}
