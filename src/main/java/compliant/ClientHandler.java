package compliant;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

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
        this.scanner = new Scanner(client.getInputStream());
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

        while (!msg.toUpperCase().equals("CLOSE#")) {
            try {
                msg = scanner.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("unexpected loss of connection to client");
                break;
            }

            String command;
            String data;
            try {
                String[] action = msg.split("#", 2);
                System.out.println(Arrays.toString(action));
                command = action[0].toUpperCase();
                System.out.println("Current command is: " + command);
                data = action[1];

                switch (command) {
                    case "SEND" -> {
                        String dataArray[] = data.split("#", 2);
                        String msgTo = dataArray[0].toUpperCase();
                        data = dataArray[1];
                        sendMsgTo(clientName, msgTo, data);
                    }
                    case "ONLINE" -> pw.println("Online users: " + onlineList.getOnlineUsers());
                    case "ASCII" -> pw.println(new Ascii().getRandomArt());
                    case "CLOSE" -> {
                        pw.println("Closing connection...");
                        closeAndRemove();
                    }
                    default -> pw.println("Command not recognised");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                pw.println("Not recognised, try adding '#' after you command.");
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
