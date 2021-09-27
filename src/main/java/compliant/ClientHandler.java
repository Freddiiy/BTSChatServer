package compliant;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable {
    private Socket client;
    private PrintWriter pw;
    private Scanner scanner;
    private BlockingQueue<MessageHandler> queue;
    private OnlineList onlineList;

    private String clientName = "anon";

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

        this.onlineList = new OnlineList();
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
        onlineList.add(this);

        while (!msg.toUpperCase().equals("CLOSE#")) {
            try {
                msg = scanner.nextLine();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                System.out.println("Unexpected loss of connection to client");
                break;
            }

            String[] nameSplit = msg.split("#", 2);
            if (!nameSplit[0].equals("CONNECT#")) {
                try {
                    pw.println("Didnt write CONNECT#'name'. Try to reconnect");
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

            clientName = nameSplit[1];
            System.out.println("Client name: " + clientName);
            msg = "";

            String command;
            String data;
            try {
                String[] action = msg.split("#", 2);
//                System.out.println(Arrays.toString(action));
                command = action[0].toUpperCase();
                data = action[1];

                switch (command) {
                    case "SEND" -> {
                        String[] dataArray = data.split("#", 2);

                        System.out.println(Arrays.toString(dataArray));

                        String msgTo = dataArray[0].toUpperCase();
                        data = dataArray[1];
                        sendMsgTo(clientName, msgTo, data);
                    }
                    case "ONLINE" -> pw.println("Online users: " + onlineList.getOnlineUsers());
                    case "CLOSE" -> {
                        try {
                            pw.println("Closing connection...");
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    default -> pw.println("Not recognised");
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

    public void sendMsgTo(String clientName, String msgTo, String input) {
        System.out.println(clientName + "#" + msgTo + "#" + input);
        queue.add(new MessageHandler(clientName, msgTo, input));
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
