package org.example;

import Model.UserServer;
import Model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

    //Portas inicial
    private static ArrayList<Integer> users = new ArrayList<>();
    //Vou criar uma array de usuarios e vou mapear com objectMapper

    private static ArrayList<String> usersMap = new ArrayList<>();


    private static InetAddress address;

    static {
        try{
            address = InetAddress.getByName("localhost");

        }catch(UnknownHostException e){
            throw new RuntimeException(e);
        }
    }


    //Manda para todos, exceto o que acabou de mandar o pacote
    private static void broadcast(byte[] data, DatagramPacket userPacket){

        try{
            for (String u : usersMap){

                ObjectMapper om = new ObjectMapper();
                UserServer us = om.readValue(u, UserServer.class);
                if(us.getPort() != userPacket.getPort()){
                    System.out.println(us.getStatus());
                    System.out.println("mostrando o status");
                    String status = us.getStatus().toString();
                    if(status == "NAO_PERTURBE"){
                        System.out.println("ignorando usuario" + us.getNome() + us.getStatus());
                        return;
                    }
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, us.getPort());
                    socket.send(packet);
                }



            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Manda pra todos inclusive para o cliente atual
    private static void broadcastAll(byte[] data, DatagramPacket userPacket){

        try{
            for (String u : usersMap){
                ObjectMapper om = new ObjectMapper();
                    UserServer us = om.readValue(u, UserServer.class);
                    System.out.println(us.getStatus());
                    String status = us.getStatus().toString();
                    if(us.getStatus().equals("NAO_PERTURBE")){
                        System.out.println("ignorando usuario" + us.getNome() + us.getStatus());
                        return;
                    }
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, us.getPort());
                    socket.send(packet);

            }

        } catch (IOException e) {
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
            ObjectMapper om = new ObjectMapper();
            String mensagem = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            messages.add(mensagem);

            //Ver maneira de inicializar usuario
            if(mensagem.contains("Entrou")){
                String username = mensagem.substring(mensagem.indexOf(";") + 1);
                System.out.println(username + " entrou");
                users.add(packet.getPort());
                System.out.println(packet.getPort());
                UserServer newUser = new UserServer(username, Usuario.StatusUsuario.DISPONIVEL, InetAddress.getByName("localhost"), packet.getPort());

                String json = om.writeValueAsString(newUser);
                usersMap.add(json);
                DatagramPacket responseConfirm = new DatagramPacket((username + " entrou").getBytes(), (username + " entrou").getBytes().length , address, packet.getPort());
                socket.send(responseConfirm);
                broadcast((username + " entrou").getBytes(), packet);
                String allUsersJson = om.writeValueAsString(usersMap);
                byte[] usersMapByte = allUsersJson.getBytes();
                broadcastAll(usersMapByte, packet);

            } else if (mensagem.contains("status")) {
                System.out.println("Mudando status de algum usuario");
                System.out.println(mensagem);
                for (String user: usersMap) {


                    UserServer u = om.readValue(user, UserServer.class);
                    if(u.getPort() == packet.getPort()){
                        int index = usersMap.indexOf(user);
                        System.out.println("achei o cabeça" + u.getNome());
                        if(mensagem.contains("NAO_PERTURBE")){
                            u.setStatus(Usuario.StatusUsuario.NAO_PERTURBE);
                        }else if(mensagem.contains("DISPONIVEL")){
                            u.setStatus(Usuario.StatusUsuario.DISPONIVEL);
                        }else{
                            u.setStatus(Usuario.StatusUsuario.VOLTO_LOGO);
                        }
                        String newJson = om.writeValueAsString(u);
                        usersMap.set(index, newJson);
                    }
                }
                System.out.println(usersMap);


                    String allUsersJson = om.writeValueAsString(usersMap);

                    byte[] usersMapByte = allUsersJson.getBytes();
                    broadcastAll(usersMapByte, packet);



                
            }else if(mensagem.contains("logout")){
                for (String u: usersMap
                     ) {

                    UserServer us = om.readValue(u, UserServer.class);
                    if(us.getPort() == packet.getPort()){
                        System.out.println("Deslogando o " + us.getNome());
                        int index = usersMap.indexOf(u);
                        usersMap.remove(index);
                        broadcast((us.getNome() + " saiu").getBytes(), packet);
                    }
                }

            } else{
               int userPort = packet.getPort();
               byte[] byteMessage = mensagem.getBytes();
               broadcast(byteMessage, packet);

            }
        }

    }

}
