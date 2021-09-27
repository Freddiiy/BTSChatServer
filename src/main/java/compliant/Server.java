package compliant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void startServer() throws IOException {
        //TODO: lav message queue
        //TODO: lav listen til clienthandlers
        CopyOnWriteArrayList<ClientHandler> clients= new CopyOnWriteArrayList<ClientHandler>();
        BlockingQueue<MessageHandler> messageHandler = new ArrayBlockingQueue<MessageHandler>(10);

        Dispatcher dispatcher = new Dispatcher(messageHandler, clients);
        dispatcher.start();

        //TODO: instatiate dispatcher as thread with the shared rescourses
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        while (true) {
            Socket client = serverSocket.accept(); //Blocking call
            ClientHandler cl = new ClientHandler(client, messageHandler);
            //TODO: make cl with shared rescourse
            clients.add(cl);
            //TODO: put cl i listen
            executorService.execute(cl);
        }
    }
}
