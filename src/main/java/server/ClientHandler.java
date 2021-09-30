package server;

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
                            //INSERT MESSAGE IN SHARED RESOURCE
                            sendToAll(clientName, "ALL", data);
                    case "/MSG" -> {
                        String[] dataArray = data.split(" ", 2);
                        String msgTo = dataArray[0];
                        data = dataArray[1];
                        sendMsgTo(clientName, msgTo, data);
                    }
                    case "/ONLINE" -> pw.println("Online users: " + onlineList.getOnlineUsers());
                    default -> pw.println("Not recognised");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                pw.println("Not recognised");
            }

        }

        pw.println("Closing connection...");
        closeAndRemove();
    }

    public void webProtocol() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            Scanner s = new Scanner(in, StandardCharsets.UTF_8);

            String data = s.useDelimiter("\\r\\n\\r\\n").next();
            Matcher get = Pattern.compile("^GET").matcher(data);
            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8)))
                        + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
                out.write(response, 0, response.length);

                ArrayList<Integer> byteArray = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    byteArray.add(in.read());
                    System.out.println(i + " byteArray: " + byteArray.get(i));
                }


                byte[] decoded = new byte[byteArray.get(1) - 128];
                byte[] encoded = new byte[] { (byte)(int) byteArray.get(6), (byte)(int) byteArray.get(7), (byte)(int) byteArray.get(8), (byte)(int) byteArray.get(9), (byte)(int) byteArray.get(10), (byte)(int) byteArray.get(11) };
                byte[] key = new byte[] { (byte)(int) byteArray.get(2), (byte)(int) byteArray.get(3), (byte)(int) byteArray.get(4), (byte)(int) byteArray.get(5) };
                for (int i = 0; i < encoded.length; i++) {
                    decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
                }
                System.out.println(Arrays.toString(decoded));

                char[] decodedCharArray = new char[decoded.length];
                for (int i = 0; i < decoded.length; i++) {
                    decodedCharArray[i] = (char)decoded[i];
                }
                System.out.println(Arrays.toString(decodedCharArray));

                String decodedString = new String(decodedCharArray);
                System.out.println(decodedString);

                //send back
                char[] backCharArray = decodedString.toCharArray();
                byte[] backByteArray = new byte[backCharArray.length];
                for (int i = 0; i < backCharArray.length; i++) {
                    backByteArray[i] = (byte)backCharArray[i];
                }
                System.out.println(Arrays.toString(backByteArray));
                out.write(backByteArray);
            }
        } catch (NoSuchAlgorithmException | IOException e) {
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
        //this.webProtocol();
        System.out.println("LOST CONNECTION TO " + Thread.currentThread().getName());
    }
}