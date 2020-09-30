package ru.alex.java.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TCPConnection {
    private final Thread regThread;
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPActions eventListener;

    public TCPConnection(TCPActions eventListener,String ipAddress, int portNumber) throws IOException {
        this(eventListener,new Socket(ipAddress, portNumber));
    }

    public TCPConnection(TCPActions eventListener, Socket socket) throws IOException  {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        regThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onReady(TCPConnection.this);
                    while (!regThread.isInterrupted()) {
                        eventListener.receiveMessage(TCPConnection.this, in.readLine());

                    }
                } catch (IOException e) {
                     eventListener.receiveException(TCPConnection.this,e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);

                }

            }
        });
        regThread.start();
    }

    public void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.receiveException(TCPConnection.this, e);
            disconnect();
        }
    }

    private void disconnect() {
        regThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.receiveException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
