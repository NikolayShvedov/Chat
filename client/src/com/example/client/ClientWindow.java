package com.example.client;

import com.example.network.TCPConnection;
import com.example.network.TCPConnectionEvents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

/**
 * Клиентское окно
 * SERVER_HOST - адрес сервера
 * PORT - порт
 * WIDTH - ширина окна
 * HEIGHT - высота окна
 * chatWindow - окно чата
 * nickName - поле для ввода имени клиента
 * nick - метка для указания на поле ввода имени
 * message - метка для указания на поле ввода сообщения
 * inputMessage - поле для написания и отправки сообщения
 * connection - соединение с сервером
 */
public class ClientWindow extends JFrame implements ActionListener, TCPConnectionEvents {

    private static final String SERVER_HOST = "localhost";
    private static final int PORT = 3443;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow(600, 400);
            }
        });
    }

    private final JTextArea chatWindow = new JTextArea();
    private final JTextField nickName = new JTextField("Введите ваше имя: ");
    private final JTextField inputMessage = new JTextField("Введите ваше сообщение: ");

    private TCPConnection connection;

    private ClientWindow(int WIDTH, int HEIGHT){
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        chatWindow.setEditable(false);
        chatWindow.setLineWrap(true);
        add(chatWindow, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        inputMessage.addActionListener(this);
        bottomPanel.add(nickName, BorderLayout.WEST);
        bottomPanel.add(inputMessage, BorderLayout.CENTER);

        setVisible(true);
        // при фокусе поле сообщения очищается
        inputMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputMessage.setText("");
            }
        });
        // при фокусе поле сообщения очищается
        nickName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                nickName.setText("");
            }
        });
        setVisible(true);
        try {
            connection = new TCPConnection(this, SERVER_HOST, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = inputMessage.getText();
        if (msg.equals(""))
        {
            return;
        }
        inputMessage.setText(null);
        connection.sendMessage(nickName.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String line) {
        printMessage(line);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close");

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
         printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.append(message + "\n");
                chatWindow.setCaretPosition(chatWindow.getDocument().getLength());
            }
        });
    }

}
