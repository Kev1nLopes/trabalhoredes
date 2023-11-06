package org.example;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ChatServer {

    private static byte[] incoming = new byte[1024];

    private static final int PORT = 8085;

    private static DatagramSocket socket;

    private static ArrayList<String> messages = new ArrayList<String>();

    static{
        try{
            socket = new DatagramSocket(PORT);
        }catch (SocketException e){
            throw new RuntimeException(e);
        }
    }


    private static ArrayList<Integer> users = new ArrayList<>();

    private static  ArrayList<String> userNames = new ArrayList<>();

    private static InetAddress address;

    static {
        try{
            address = InetAddress.getByName("localhost");

        }catch(UnknownHostException e){
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {

        System.out.println("Server started on port" + PORT);

        while(true){
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try{
                socket.receive(packet);
            }catch(IOException e){
                throw new RuntimeException(e);
            }

            String mensagem = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            messages.add(mensagem);
            for (String message: messages
            ) {
                System.out.println(message);
            }


            //Ver maneira de inicializar usuario
            if(mensagem.contains("Entrou")){

                String username = mensagem.substring(mensagem.indexOf(";") + 1);
                System.out.println(username + " entrou");
                users.add(packet.getPort());
                System.out.println(packet.getPort());
                userNames.add(username);
                DatagramPacket responseConfirm = new DatagramPacket((username + " entrou").getBytes(), (username + " entrou").getBytes().length , address, packet.getPort());
                socket.send(responseConfirm);

            }else{
               int userPort = packet.getPort();
               byte[] byteMessage = mensagem.getBytes();

               for(int forward_port : users){
                   if(forward_port != userPort){
                       System.out.println("Enviando para outros");
                       DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forward_port);
                       try{
                           socket.send(forward);
                       }catch (IOException e){
                           throw new RuntimeException(e);
                       }
                   }
               }
            }
        }

    }

}
