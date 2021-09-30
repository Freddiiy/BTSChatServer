package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebProtocol {
    private Socket socket;

    public WebProtocol(Socket socket) {
        this.socket = socket;
    }

    public void protocol() {
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


                byte[] decoded = new byte[byteArray.get(1) - 128]; //gets the size of the message by taking the second byte in the byte array and -128 with it
                int j=0;
                System.out.println("size: " + byteArray.size());
                byte[] encoded = new byte[byteArray.size()-6];
                for (Integer b : byteArray) {
                    encoded[j++] = (byte)(int)b;
                }
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
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public void oldProtocol() {
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
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }
}
