package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable{
    Socket client;
    PrintWriter pw;
    Scanner scanner;
    BlockingQueue<MessageHandler> queue;

    String clientName = "anon";

    private int id;
    private static int counter = 1;

    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        this.pw = new PrintWriter(client.getOutputStream(), true);
        this.scanner = new Scanner(client.getInputStream());
    }

    public ClientHandler(Socket client, BlockingQueue<MessageHandler> queue) throws IOException {
        this.client = client;
        this.pw = new PrintWriter(client.getOutputStream(), true);
        this.scanner = new Scanner(client.getInputStream());
        this.queue = queue;
    }

    public ClientHandler(Socket client, PrintWriter pw, Scanner scanner, BlockingQueue<MessageHandler> queue, String clientName) throws IOException {
        this.client = client;
        this.pw = new PrintWriter(client.getOutputStream());
        this.scanner = new Scanner(client.getInputStream());
        this.queue = queue;

        this.clientName = clientName;
        this.id = counter;
        counter++;
    }

    public void protocol() {
        String msg = "";
        pw.println("What is your name?: ");
        clientName = scanner.nextLine();
        pw.println("Hello " + this.clientName + " and welcome. \n---------------------------------------");
        while (!msg.equals("CLOSE#")) {
            try {
                msg = scanner.nextLine();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }

            String data;
            try {
                String[] action = msg.split("#", 2);
                data = action[1];

                switch(action[0]) {
                    case "ALL":
                        //INSERT MESSAGE IN SHARED RESOURCE
                        sendToAll(clientName, data);
                        break;
                    case "MSG":
                        pw.println("Who do you want to send your message to");
                        String msgTo = scanner.nextLine();
                        sendMsgTo(clientName, msgTo, data);
                        break;
                    case "CLOSE":
                        pw.println("Closing connection...");
                        break;
                    default:
                        pw.println("Not recognised");
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                pw.println("Not recognised");
            }

        }

        try {
            pw.println("Closing connection...");
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(String clientName, String input) {
    }

    public void sendMsgTo(String clientName,String msgTo, String input) {
        MessageHandler message = new MessageHandler(clientName, msgTo, input);
    }

    public PrintWriter getPw() {
        return this.pw;
    }

    public Scanner getScanner() {
        return this.scanner;
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public void run() {
        this.protocol();
        System.out.println("LOST CONNECTION TO " + Thread.currentThread().getName());
    }
}
