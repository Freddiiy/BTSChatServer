package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Dispatcher extends Thread {
    BlockingQueue<MessageHandler> messages;
    CopyOnWriteArrayList<ClientHandler> clients;

    public Dispatcher(BlockingQueue<MessageHandler> messages, CopyOnWriteArrayList<ClientHandler> clients) {
        this.messages = messages;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            String outMsg = "";
            while (true) {
                outMsg = messages.take().getName;
                System.out.println("to ALL: " + outMsg);
                String[] nameSplit = outMsg.split(":", 2);

                String clientName = nameSplit[0];
                outMsg = nameSplit[1];
                for (ClientHandler client : clients) {
                    if (!client.getClientName().equals(clientName))
                    client.getPw().println(clientName + ": " + outMsg);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
