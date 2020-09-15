/*
 * Created by Pyatakov Roman BPI 185 on 23.05.2020
 */
package sample;

import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class MessageController {
    Main main;

    public MessageController(Main main) {
        this.main = main;
    }

    public void HandleMessage(String message, Thread listener) {
        System.out.println(message);
        var msg = message.split(" ");
        if (Main.isServer) {
            ServerHandle(msg, (Server) listener);
        } else {
            ClientHandle(msg, (Client) listener);
        }
    }

    private void ServerHandle(String[] message, Server client) {
        switch (message[0]) {
            case "1":
                switch (message[1]) {
                    case "1":
                        main.addToLog("Игра против " + message[2], Font.font("Arial", 14));
                        main.setReadyEnabled();
                        Client.name = message[2];
                        client.send("1 1 " + Server.name);
                        break;
                    case "2":
                        main.addToLog(Client.name + " игрок готов!", Font.font("Arial", 14));
                        Main.readyClient = true;
                        if (Main.readyClient && Main.readyServer) {
                            main.startGame();
                        }
                        break;
                    case "3":
                        main.addToLog(Client.name + " игрок не готов!", Font.font("Arial", 14));
                        Main.readyClient = false;
                        break;

                }
                break;
            case "2":
                int x = Integer.parseInt(message[1]);
                int y = Integer.parseInt(message[2]);
                switch (message[3]) {
                    //меня атакают
                    case "1":
                        main.mapToPlace.fireAtMe(message[1], message[2]);
                        break;
                    //я атаковал
                    case "2":
                        main.mapToAttack.setButtonColor(x,y,"#7109AA");
                        main.addToLog(Server.name + " "+ x + " " + y + " " + " мимо", Font.font("Arial", 12));

                        break;
                    case "3":
                        main.mapToAttack.setButtonColor(x,y,"#AB2B52");
                        main.addToLog(Server.name + " "+ x + " " + y + " " + " попал", Font.font("Arial", FontWeight.BOLD, 12));
                        main.setUnWait();
                        break;
                    case "4":
                        main.mapToAttack.setButtonColor(x,y,"#AB2B52");
                        main.addToLog(Server.name + " "+ x + " " + y + " " + " убил", Font.font("Arial", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 13));
                        main.mapToAttack.nineColorShoots(x,y);
                        main.setUnWait();
                        break;
                    case "5":
                        main.addToLog(Client.name + " - проиграл, поздравляю с победой, так держать!", Font.font("Arial", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
                        main.setWait();
                        break;
                }
                break;
            case "3":
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("CLose game");
                // Header Text: null
                alert.setHeaderText("");
                alert.setContentText(Client.name+ " close game!");
                alert.showAndWait();
                main.GameExit();
                break;
            default:
                break;
        }
    }

    private void ClientHandle(String[] message, Client server) {
        switch (message[0]) {
            case "1":
                switch (message[1]) {
                    case "1":
                        main.addToLog("Игра против " + message[2], Font.font("Arial", 14));
                        Server.name = message[2];
                        break;
                    case "2":
                        main.addToLog(Server.name + " игрок готов!", Font.font("Arial", 14));
                        break;
                    case "3":
                        main.addToLog(Server.name + " игрок не готов!", Font.font("Arial", 14));
                        break;
                    case "4":
                        main.startGame();
                        break;
                }
                break;
            case "2":
                int x = Integer.parseInt(message[1]);
                int y = Integer.parseInt(message[2]);
                switch (message[3]) {
                    //меня атакают
                    case "1":
                        main.mapToPlace.fireAtMe(message[1], message[2]);
                        break;
                    //я атаковал
                    case "2":
                        main.mapToAttack.setButtonColor(x,y,"#7109AA");
                        main.addToLog(Client.name + " " + x + " " + y + " " + " мимо", Font.font("Arial", 12));
                        break;
                    case "3":
                        main.mapToAttack.setButtonColor(x,y,"#AB2B52");
                        main.addToLog(Client.name + " "+ x + " " + y + " " + " попал", Font.font("Arial", FontWeight.BOLD, 12));
                        main.setUnWait();
                        break;
                    case "4":
                        main.mapToAttack.setButtonColor(x,y,"#AB2B52");
                        main.addToLog(Client.name + " "+ x + " " + y + " " + " убил", Font.font("Arial", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 13));
                        main.mapToAttack.nineColorShoots(x,y);
                        main.setUnWait();
                        break;
                    case "5":
                        main.addToLog(Server.name + " - проиграл, поздравляю с победой, так держать!", Font.font("Arial", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
                        main.setWait();
                        break;
                }
                break;
            case "3":
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("CLose game");
                // Header Text: null
                alert.setHeaderText("");
                alert.setContentText(Server.name + " close game!");
                alert.showAndWait();
                main.GameExit();
                break;
            default:
                break;
        }
    }
}
