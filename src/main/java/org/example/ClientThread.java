package org.example;

import View.Screen;

import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientThread extends Thread{

    private final DatagramSocket _socket;

    private String _message;


    public ClientThread(DatagramSocket socket, String message){
        this._socket = socket;
        this._message = message;
    }

    public void run(){

        System.out.println("starting thread");

        byte[] byteMessage = (_message + "\n").getBytes();
        try {
            DatagramPacket sendPacket = new DatagramPacket(byteMessage, byteMessage.length, InetAddress.getByName("localhost"), 8085);
            _socket.send(sendPacket);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }



}
