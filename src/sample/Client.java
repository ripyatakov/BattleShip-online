package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends Thread {

    private static Socket clientSocket; //сокет для общения
    private static ServerSocket server; // серверсокет
    private static BufferedReader in; // поток чтения из сокета
    private static PrintWriter out; // поток записи в сокет

    public static String name;
    public static boolean ready;

    public Client(int port, String ip) throws IOException {
        clientSocket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // и отправлять
        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Server listener");
        try {
            try {
                try {

                    String txt = "";
                    do {
                        final String text = in.readLine();
                        if (text != null)
                            javafx.application.Platform.runLater(() -> {
                                Main.messageController.HandleMessage(text, this);
                            });
                        txt = (text == null)?"da":text;
                    } while (!txt.equals("bye"));

                } finally { // в любом случае сокет будет закрыт
                    clientSocket.close();
                    // потоки тоже хорошо бы закрыть
                    in.close();
                    out.close();
                }
            } finally {
                System.out.println("Сервер закрыт!");
                //server.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    public void send(String message){
        try {
            // не долго думая отвечает клиенту
            out.println(message);// выталкиваем все из буфера
        } catch (Exception e){
            try {
                in.close();
                out.close();
            }
            catch (Exception e1){}
        }
    }
    public void close(){
        try {
            in.close();
            out.close();
        } catch (Exception e) {

        } finally {
            javafx.application.Platform.runLater(() -> {
                Main.showAlertMessage("Сервер разорвал соединение");
            });

        }
    }
}