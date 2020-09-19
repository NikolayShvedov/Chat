package com.example.server;

import com.example.network.TCPConnection;
import com.example.network.TCPConnectionEvents;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Класса для запуска сервера и создания соединения
 * connections - список соединений
 * PORT - порт сервера
 * clients_count - количество клиента в чате, статичное поле
 */
public class ChatServer implements TCPConnectionEvents {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private static final String IP = "46.73.9.86";
    static final int PORT = 3443;
    private static int clients_count = 0;

    private ChatServer(){
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)){

            while(true){
                try {
                    new TCPConnection(this, serverSocket.accept());
                }catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
        clients_count++;
        sendToAllConnections("Клиентов в чате = " + clients_count);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String line) {
        sendToAllConnections(line);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
        clients_count--;
        sendToAllConnections("Клиентов в чате = " + clients_count);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        final int connectionsSize = connections.size();
        for(int i = 0; i < connectionsSize; i++){
            connections.get(i).sendMessage(value);
        }
    }
}
