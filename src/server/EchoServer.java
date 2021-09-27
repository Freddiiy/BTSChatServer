package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class EchoServer {
    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void startServer() throws IOException {
        //TODO: lav message queue
        //TODO: lav listen til clienthandlers
        CopyOnWriteArrayList<ClientHandler> clients= new CopyOnWriteArrayList<>();
        BlockingQueue<MessageHandler> messageHandler = new ArrayBlockingQueue<MessageHandler>(10);

        Dispatcher dispatcher = new Dispatcher(messageHandler, clients);
        dispatcher.start();

        //TODO: instatiate dispatcher as thread with the shared rescourses
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        while (true) {
            Socket client = serverSocket.accept(); //Blocking call
            //TODO: make cl with shared rescourse
            ClientHandler cl = new ClientHandler(client, messageHandler);
            clients.add(cl);
            //TODO: put cl i listen
            executorService.execute(cl);
        }
    }
}
