package com.example.network;

/**
 * Интерфейс, в котором находятся различные события tcpConnection
 * onConnectionReady - соединение совершилось
 * onReceiveString - принять строку
 * onDisconnect - дисконект соединения
 * onException - исключение
 */
public interface TCPConnectionEvents {

    void onConnectionReady(TCPConnection tcpConnection);

    void onReceiveString(TCPConnection tcpConnection, String line);

    void onDisconnect(TCPConnection tcpConnection);

    void onException(TCPConnection tcpConnection, Exception e);
}
