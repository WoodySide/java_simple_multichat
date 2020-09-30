package ru.alex.java.chat.network;

public interface TCPActions {

    void onReady(TCPConnection tcpConnection);
    void receiveMessage(TCPConnection tcpConnection, String value);
    void onDisconnect(TCPConnection tcpConnection);
    void receiveException(TCPConnection tcpConnection, Exception e);

}
