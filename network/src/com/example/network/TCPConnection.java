package com.example.network;

import com.example.server.ChatServer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Класс для реализации TCP соединения сервера с пользователями.
 * thread - поток, который отвечает за передачу сообщений
 * socket - клиентский сокет
 * in - входящее сообщение
 * out - исходящее сообщение
 * tspConnectionEvent - переменная интерфейса, в котором описаны некоторые события, связанные с соединением
 */
public class TCPConnection {

    private final Thread thread;
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionEvents tspConnectionEvent;

    public TCPConnection(TCPConnectionEvents tspConnectionEvent, String IP, int PORT) throws IOException {
        this(tspConnectionEvent, new Socket(IP, PORT));
    }

    public TCPConnection(TCPConnectionEvents tspConnectionEvent, Socket socket) throws IOException {
        this.socket = socket;
        this.tspConnectionEvent = tspConnectionEvent;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tspConnectionEvent.onConnectionReady(TCPConnection.this);
                    while (!thread.isInterrupted())
                    {
                        tspConnectionEvent.onReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    tspConnectionEvent.onException(TCPConnection.this, e);
                } finally {
                    tspConnectionEvent.onDisconnect(TCPConnection.this);
                }
            }
        });
        thread.start();
    }

    /**
     * Метод для отправки сообщения
     * @param message - строка, которая будет отправлена
     */
    public synchronized void sendMessage(String message) {
        try{
            out.write(message + "\r\n");
            out.flush();
        } catch (IOException e) {
            disconnect();
        }
    }

    /**
     * метод для разрыва соединения
     */
    public synchronized void disconnect() {
        thread.interrupt();
        try{
            socket.close();
        } catch (IOException e) {
            tspConnectionEvent.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
