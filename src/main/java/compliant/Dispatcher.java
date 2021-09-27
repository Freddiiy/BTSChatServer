package compliant;

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
            MessageHandler messageObject;
            String message = "";
            String messageFrom = "";
            String messageTo = "";
            while (true) {
                messageObject = messages.take();
                message = messageObject.getMessage();
                messageFrom = messageObject.getMessageFrom();
                messageTo = messageObject.getMessageTo();

                if (!messageTo.equals("ALL"))
                    for (ClientHandler client : clients) {
                        if(client.getClientName().equalsIgnoreCase(messageTo)) {
                            client.getPw().println("Whisper from " + messageFrom + ": " + message);
                        } else {
                            if (client.getClientName().equalsIgnoreCase(messageFrom)) {
                                client.getPw().println(messageFrom + " could not be found.");
                            }
                        }
                } else if(messageTo.equals("ALL")) {
                    for (ClientHandler client : clients) {
                        client.getPw().println(messageFrom + " to ALL: " + message);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
