package ru.alex.java.chat.client;

import ru.alex.java.chat.network.TCPActions;
import ru.alex.java.chat.network.TCPConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class ClientWindow extends JFrame implements TCPActions, ActionListener {

    private static final String IP_ADDRESS = "192.168.1.43";
    private static final int PORT = 8001;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientWindow());
    }
    private final  JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("woody_side");
    private final JTextField getFieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        getFieldInput.addActionListener(this);
        add(fieldNickname, BorderLayout.NORTH);
        add(getFieldInput, BorderLayout.SOUTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDRESS,PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = getFieldInput.getText();
        if(message.equals("")) return;
        getFieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + message);
    }

    @Override
    public void onReady(TCPConnection tcpConnection) {
        printMessage("Connection is ready...");
    }

    @Override
    public void receiveMessage(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Connection close");
    }

    @Override
    public void receiveException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
