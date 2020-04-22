package lab.second;

package lab.second;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

class ClientStarterWorkerThread extends Thread {

    private int ID;
    private DataInputStream inputStream;

    public ClientStarterWorkerThread(int clientID, DataInputStream inputStream) {
        this.ID = clientID;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        // todo: Handle listening to messages
    }
}

public class ClientStarer {

    private int ID;
    //todo: init other required variables here

    ClientStarer(int id, String host, int port) throws IOException {
        this.ID = id;
        // todo: Connect to server and send client ID

        // todo: Listen for incoming messages
    }

    // todo: Implement the sending message mechanism
    void sendMessage(int idReceiver, String message) throws IOException {

    }

    // todo: end communication - send END to server
    private void endCommunication() throws IOException {

    }

    // todo: listen for icoming messages from the server.
    // It should start a separate thread to handle listening
    // and not block the execution
    // Should start a new ClientStarterWorkerThread
    private void listen() {

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //todo: Initialize and start 3 clients

        ClientStarer client1;
        ClientStarer client2;
        ClientStarer client3;

        // Simulate chat
        client1.sendMessage(2, "Hello from client 1");
        Thread.sleep(1000);
        client2.sendMessage(3, "Hello from client 2");
        Thread.sleep(1000);
        client1.sendMessage(3, "Hello from client 1");
        Thread.sleep(1000);
        client3.sendMessage(1, "Hello from client 3");
        Thread.sleep(1000);
        client3.sendMessage(2, "Hello from client 3");

        // Exit the chatroom
        client1.endCommunication();
        client2.endCommunication();
        client3.endCommunication();
    }
}

public class TCPServer {

    private ServerSocket server;
    private HashMap<Integer, Socket> activeConnections;

    // todo: Get the required connection
    public Socket getConnection(int id) {

    }

    // todo: Add connected client to the hash map
    void addConnection(int id, Socket connection) {

    }

    synchronized void endConnection(int id){
        activeConnections.remove(id);
    }

    //todo: Initialize server
    TCPServer(int port) throws IOException {

    }

    // todo: Handle server listening
    // todo: For each connection, start a separate
    // todo: thread (ServerWorkerThread) to handle the communication
    void listen() throws IOException {

    }

    public static void main(String[] args) throws IOException {
        // todo: Start server
    }
}