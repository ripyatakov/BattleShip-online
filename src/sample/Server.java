package sample;

import com.sun.media.jfxmediaimpl.platform.Platform;
import javafx.application.Application;
import javafx.scene.control.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private static BufferedReader in; // поток чтения из сокета
    private static PrintWriter out; // поток записи в сокет
    private int port;

    public static String name;
    public static boolean ready;

    public Server(int port) throws IOException {
        this.port = port;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client listener");

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);
            Socket socket = serverSocket.accept();
            System.out.println("New client connected");

            InputStream input = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            out = new PrintWriter(output, true);
            String txt = "";
            do {
                final String text = in.readLine();
                if (text != null) {
                    javafx.application.Platform.runLater(() -> {
                        Main.messageController.HandleMessage(text, this);
                    });
                }
                txt = (text == null) ? "da" : text;
            } while (!txt.equals("bye"));

            socket.close();

        } catch ( IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex){
        }

    }

    public void send(String message) {
        try {
            out.println(message);
        } catch (Exception e) {

        }
    }


}