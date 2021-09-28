package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

class Reader extends Thread {
    boolean keepRunning = true;
    Scanner scannerFromServer;
    public Reader(Scanner sc) {
        this.scannerFromServer = sc;
    }

    @Override
    public void run() {
        while(keepRunning) {
            try {
                System.out.println(scannerFromServer.nextLine());
            } catch (NoSuchElementException ns) {
                System.out.println("CLIENT CONNECTION CLOSED...");
                break;
            }
        }

    }
}

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        main.startClient();
    }

    public void startClient() {
        Scanner keyboard = new Scanner(System.in);
        String msg = "";

        try {
            Socket socket = new Socket("localhost", 8285);
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            Reader reader = new Reader(scanner);
            reader.start();

            while (!msg.equals("CLOSE#")) {
                msg = keyboard.nextLine();
                pw.println(msg);

            }
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
