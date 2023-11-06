package org.example;

import View.Screen;

import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientThread extends Thread{

    private final DatagramSocket _socket;
    private final Screen _screen;

    private byte[] incoming = new byte[1024];

    public ClientThread(DatagramSocket socket, Screen screen){
        this._socket = socket;
        this._screen = screen;
    }

    public void run(){

        System.out.println("starting thread");
        while(true){
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try{
                _socket.receive(packet);
            }catch (IOException e){
                throw new RuntimeException(e);
            }

            String message = new String(packet.getData(), 0, packet.getLength()) + "\n";
            System.out.println(message);

        }
    }
}
