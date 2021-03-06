package server;

import compliant.Ascii;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable{
    private Socket socket;
    private PrintWriter pw;
    private Scanner scanner;
    private BlockingQueue<MessageHandler> queue;
    private OnlineList onlineList;

    private String clientName = "anon";

    private int id;
    private static int counter = 1;

    public ClientHandler(Socket client) throws IOException {
        this.socket = client;
        this.pw = new PrintWriter(client.getOutputStream(), true);
        this.scanner = new Scanner(client.getInputStream(), StandardCharsets.UTF_8);
    }

    public ClientHandler(Socket client, BlockingQueue<MessageHandler> queue) throws IOException {
        this.socket = client;
        this.pw = new PrintWriter(client.getOutputStream(), true);
        this.scanner = new Scanner(client.getInputStream());
        this.queue = queue;

        this.onlineList = new OnlineList();
    }

    public ClientHandler(Socket client, PrintWriter pw, Scanner scanner, BlockingQueue<MessageHandler> queue, String clientName) throws IOException {
        this.socket = client;
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

        try {
            clientName = scanner.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("unexpected loss of connection to client");
        }
        onlineList.add(this);

        pw.println("Hello " + this.clientName + " and welcome. \n---------------------------------------\n" +
                "Online users: " + onlineList.getOnlineUsers()+ "\n---------------------------------------\n");

        while (!msg.toUpperCase().equals("/CLOSE")) {
            try {
                msg = scanner.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("unexpected loss of connection to client");
                break;
            }

            String command;
            String data;
            try {
                String[] action = msg.split(" ", 2);
                command = action[0].toUpperCase();
                data = action[1];

                switch (command) {
                    case "/ALL" ->
                            sendToAll(clientName, "ALL", data);
                    case "/MSG" -> {
                        String[] dataArray = data.split(" ", 2);
                        String msgTo = dataArray[0];
                        data = dataArray[1];
                        sendMsgTo(clientName, msgTo, data);
                    }
                    case "/ONLINE" -> pw.println("Online users: " + onlineList.getOnlineUsers());
                    case "/ASCII" -> pw.println(new Ascii().getRandomArt());
                    case "#ALL" ->
                            sendToAll(clientName, "ALL", data);
                    case "#MSG" -> {
                        String[] dataArray = data.split(" ", 2);
                        String msgTo = dataArray[0];
                        data = dataArray[1];
                        sendMsgTo(clientName, msgTo, data);
                    }
                    case "#ONLINE" -> pw.println("Online users: " + onlineList.getOnlineUsers());
                    case "#ASCII" -> pw.println(new Ascii().getRandomArt());
                    default -> pw.println("Not recognised");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                pw.println("Not recognised");
            }

        }

        pw.println("Closing connection...");
        closeAndRemove();
    }

    public String insertName() {
        try {
            String tmpClientName = null;
            tmpClientName = scanner.nextLine();
            for (int i = 0; i < onlineList.getOnlineList().size(); i++) {

            }
        } catch (NoSuchElementException e) {
            System.out.println("unexpected loss of connection to client");
        }
        return null;
    }

    public void sendToAll(String clientName, String msgTo, String input) {
        System.out.println(clientName + " to ALL " + input);
        queue.add(new MessageHandler(clientName, msgTo, input));
    }

    public void sendMsgTo(String clientName,String msgTo, String input) {
        System.out.println(clientName + " to " + msgTo + ": " + input);
        queue.add(new MessageHandler(clientName, msgTo, input));
    }

    public void closeAndRemove() {
        try {
            onlineList.remove(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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