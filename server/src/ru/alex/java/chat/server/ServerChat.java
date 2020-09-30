package ru.alex.java.chat.server;


import ru.alex.java.chat.network.TCPActions;
import ru.alex.java.chat.network.TCPConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ServerChat implements TCPActions  {
    public static void main(String[] args) {
        new ServerChat();
    }

    private final List<TCPConnection> connectionList = new ArrayList<>();

    private ServerChat()  {
        System.out.println("Server is running...");
        try {
            ServerSocket serverSocket = new ServerSocket(8001);
            while (true) {
                try {
                    new TCPConnection(this,serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onReady(TCPConnection tcpConnection) {
        connectionList.add(tcpConnection);
        sendToAllClients("Client connected: " + tcpConnection);

    }

    @Override
    public synchronized void receiveMessage(TCPConnection tcpConnection, String value) {
        sendToAllClients(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connectionList.remove(tcpConnection);
        System.out.println("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void receiveException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection: " + e);
    }

    private void sendToAllClients(String value) {
        System.out.println(value);
        final int listSize  = connectionList.size();
        for (TCPConnection tcpConnection : connectionList) tcpConnection.sendString(value);
    }
}
