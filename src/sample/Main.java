package sample;

import battleship.BattleshipGame;
import battleship.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;

import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    public static boolean isServer = false;
    static MessageController messageController;
    final int cellSize = 40;
    final int cellIndent = 2;
    final int rowsOfMap = 10;
    final int columnsOfMap = 10;
    //Games groupws
    private ScrollPane logLabel;
    private FlowPane fp;
    private FlowPane currentGameInfo;
    private FlowPane shipButtons;
    public FlowPane fireGroup;
    static Client client;
    static Server server;
    BattleshipGame mapToAttack;
    BattleshipGame mapToPlace;
    public int[] cellShip = new int[4];
    public static boolean isWait = false;
    static boolean readyClient = false;
    static boolean readyServer = false;

    public static int curShipSize = 1;
    public static boolean curShipHorizontal = true;
    public static Ship curShip;

    public  int lastRow = -1;
    public  int lastColumn = -1;

    private void initializeFireGroup() {
        fireGroup = new FlowPane(10000, 10);
        TextField textBox = new TextField("0 0");
        textBox.setFont(new Font("Calibri", 16));
        textBox.setAlignment(Pos.BASELINE_CENTER);

        Button buttonFire = new Button();
        textBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    mapToAttack.fire(textBox.getText());
                }
            }
        });
        buttonFire.setText("Огонь!");
        buttonFire.setFont(new Font("Arial", 18));

        buttonFire.setPrefSize(cellSize * 6 + cellIndent * 4, cellSize);
        textBox.setPrefSize(cellSize * 6 + cellIndent * 4, cellSize);

        buttonFire.setOnAction(e -> {
            mapToAttack.fire(textBox.getText());
        });

        fireGroup.setLayoutX(columnsOfMap * cellSize + (columnsOfMap + 1) * cellIndent);
        fireGroup.setLayoutY(rowsOfMap * cellSize + (rowsOfMap + 1) * cellIndent);
        fireGroup.getChildren().addAll(textBox, buttonFire);

    }

    private void initializeGameInfo() {
        currentGameInfo = new FlowPane(10000, 5);
        currentGameInfo.setLayoutX(columnsOfMap * cellSize + (columnsOfMap + 1) * cellIndent);
        currentGameInfo.setLayoutY(cellIndent);
        Label l1 = new Label();
        l1.setText("Вы вошли в игру как " + (isServer?Server.name:Client.name));
        Label l2 = new Label();
        l2.setText("Ваш противник " + (!isServer?Server.name:Client.name));
        Label l3 = new Label();

        Button b4 = new Button();
        b4.setPrefSize(cellSize*2,cellSize);
        b4.setText("CLOSE GAME");
        b4.setOnAction(e -> {
            send("3");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CLose game");
            // Header Text: null
            alert.setHeaderText("");
            alert.setContentText(Main.isServer?Server.name:Client.name + " close game!");
            alert.showAndWait();
            GameExit();
        });

        l1.setCursor(Cursor.HAND);
        l2.setCursor(Cursor.HAND);
        l3.setCursor(Cursor.HAND);
        l1.setFont(new Font("Calibri", 16));
        l2.setFont(new Font("Calibri", 16));
        l3.setFont(new Font("Calibri", 16));

        currentGameInfo.getChildren().addAll(l1, l2, b4);
    }

    private void initializeLogLabel() {
        logLabel = new ScrollPane();

        logLabel.setPrefSize(columnsOfMap * cellSize + (columnsOfMap - 1) * cellIndent, 3 * cellSize + (3 - 1) * cellIndent);
        logLabel.setLayoutX(cellIndent);
        logLabel.setLayoutY(rowsOfMap * cellSize + (rowsOfMap + 1) * cellIndent);

        logLabel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        logLabel.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        fp = new FlowPane(10000, 2);

        logLabel.setContent(fp);
    }

    private void initializeStartFields() {
        FlowPane inputGroup = new FlowPane(10000, cellIndent * 3);

        inputGroup.setLayoutX(cellIndent * 5);
        inputGroup.setLayoutY(cellIndent * 5);

        //Nickname
        Label msg1 = new Label();
        msg1.setFont(Font.font("Arial", 14));
        msg1.setText("Nickname:");

        TextField nickname = new TextField(isServer?"server":"client");
        nickname.setFont(Font.font("Arial", cellSize / 2));
        nickname.setPrefSize(cellSize * 7 - 10 * cellIndent, cellSize);
        nickname.setAlignment(Pos.CENTER);

        //Port
        Label msg2 = new Label();
        msg2.setFont(Font.font("Arial", 14));
        msg2.setText("Port:");

        TextField port = new TextField("80");
        port.setFont(Font.font("Arial", cellSize / 2));
        port.setPrefSize(cellSize * 7 - 10 * cellIndent, cellSize);
        port.setAlignment(Pos.CENTER);
        inputGroup.getChildren().addAll(msg1, nickname, msg2, port);

        //Button start
        Button start = new Button();
        start.setPrefSize(cellSize * 7 - 10 * cellIndent, cellSize);
        start.setFont(Font.font("Arial", cellSize / 2));
        start.setText("START");

        start.setOnAction(e -> {
            try {
                primaryStage.setTitle("Ждем клиента...");
                server = new Server(Integer.parseInt(port.getText()));
                Server.name = nickname.getText();
                Server.ready = false;
                server.start();
                initializePlaceGroup();
            } catch (IOException ex) {
                showAlertMessage("Неудалось подключиться к серверу!");
            } catch (Exception ex) {
                showAlertMessage("Неправильные данные, повторите ввод!");
            }
        });
        //IP if needed
        if (!isServer) {
            Label msg3 = new Label();
            msg3.setFont(Font.font("Arial", 14));
            msg3.setText("IP:");

            TextField ip = new TextField();
            ip.setFont(Font.font("Arial", cellSize / 2));
            ip.setPrefSize(cellSize * 7 - 10 * cellIndent, cellSize);
            ip.setAlignment(Pos.CENTER);
            inputGroup.getChildren().addAll(msg3, ip);
            start.setOnAction(e -> {
                try {
                    primaryStage.setTitle("Ждем сервер...");
                    client = new Client(Integer.parseInt(port.getText()), ip.getText());
                    client.start();
                    Client.name = nickname.getText();
                    Client.ready = false;
                    client.send("1 1 " + nickname.getText());
                    initializePlaceGroup();
                } catch (IOException ex) {
                    showAlertMessage("Неудалось подключиться к серверу!");
                } catch (Exception ex) {
                    showAlertMessage("Неправильные данные, повторите ввод!");
                }
            });
        }

        inputGroup.getChildren().addAll(start);


        primaryStage.setScene(new Scene(inputGroup, cellSize * 7, cellSize * 8));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void initializePlaceGroup() {
        initializeLogLabel();
        //оставил только рандом
        //placeShipsButtons();
        mapToPlace = new BattleshipGame(rowsOfMap, columnsOfMap, cellSize, cellIndent,
                null, this);
        mapToPlace.startGame();
        mapToPlace.redrawPlaceMap();
        Group root = new Group();
        //root.getChildren().addAll(mapToPlace.getMap(), shipButtons);
        root.getChildren().addAll(mapToPlace.getMap());
//        mapToPlace.getMap().setScaleX(0.5);
//        mapToPlace.getMap().setScaleY(0.5);
//        mapToPlace.getMap().setLayoutX((cellSize+ cellIndent) * (columnsOfMap-2) + cellIndent);
        primaryStage.setTitle("BattleShip");

        //Randomly button
        Button randomButton = new Button();
        randomButton.setPrefSize((columnsOfMap + 6) * (cellSize + cellIndent) - columnsOfMap * (cellSize + cellIndent) - 2 * cellIndent, 3 * cellSize / 2);
        randomButton.setLayoutX(columnsOfMap * (cellSize + cellIndent) + cellIndent);
        randomButton.setLayoutY(rowsOfMap * (cellSize + cellIndent) + cellIndent);
        randomButton.setFont(Font.font("Arial", cellSize / 2));
        randomButton.setText("Random");

        randomButton.setOnAction(e -> {
                    cellShip[3] = 0;
                    cellShip[2] = 0;
                    cellShip[1] = 0;
                    cellShip[0] = 0;
                    mapToPlace.startGame();
                    mapToPlace.redrawPlaceMap();
                }
        );

        //Ready button
        Button ready = new Button();
        ready.setPrefSize((columnsOfMap + 6) * (cellSize + cellIndent) - columnsOfMap * (cellSize + cellIndent) - 2 * cellIndent, 3 * cellSize / 2);
        ready.setLayoutX(columnsOfMap * (cellSize + cellIndent) + cellIndent);
        ready.setLayoutY((rowsOfMap + 3) * cellSize + (rowsOfMap + 3 + 1) * cellIndent - cellIndent - 3 * cellSize / 2);
        ready.setFont(Font.font("Arial", cellSize / 2));
        ready.setText("Ready");

        if (isServer) {
            ready.setDisable(true);
            btn = ready;
            //server
            ready.setOnAction(e -> {
                if (canToReady()) {
                    readyServer = !readyServer;
                    randomButton.setDisable(readyServer);
                    if (readyServer) {
                        send("1 2 ready");
                        if (Main.readyClient && Main.readyServer) {
                            startGame();
                        }
                    } else {
                        send("1 3 notready");
                    }
                } else
                {
                    showAlertMessage("All ships must be placed!!!");
                }
            });
        } else {
            //client

            ready.setOnAction(e -> {
                if (canToReady()) {
                    readyClient = !readyClient;
                    randomButton.setDisable(readyClient);
                    if (readyClient) {
                        send("1 2 ready");
                    } else {
                        send("1 3 notready");
                    }
                } else
                {
                    showAlertMessage("All ships must be placed!!!");
                }
            });
        }

        root.getChildren().addAll(logLabel, randomButton, ready);
        primaryStage.setScene(new Scene(root, (columnsOfMap + 6) * (cellSize + cellIndent), (rowsOfMap + 3) * cellSize + (rowsOfMap + 3 + 1) * cellIndent));

    }
    private Button btn;
    void setReadyEnabled(){
        btn.setDisable(false);
    }
    private void placeShipsButtons(){
        cellShip = new int[4];

        cellShip[0] = cellShip[1] = cellShip[2] = cellShip[3] = 0;

        shipButtons = new FlowPane(1000,cellIndent);
        shipButtons.setLayoutX((cellSize + cellIndent) * columnsOfMap + cellIndent);
        shipButtons.setLayoutY(cellIndent);

        curShip = new Submarine(rowsOfMap,columnsOfMap);
        curShipSize = 1;
        curShipHorizontal = true;

        //4
        Button ship4 = new Button();
        ship4.setPrefSize((cellSize + cellIndent)*4 - cellIndent,cellSize);
        ship4.setText("→");
        ship4.setOnAction(e -> {
            if (curShip.getShipType().equals("battleship")){
                curShipHorizontal = !curShipHorizontal;

            } else
            {
                curShipSize = 4;
                curShip = new Battleship(rowsOfMap,columnsOfMap);
            }
            if (curShipHorizontal)
                ship4.setText("→");
            else
                ship4.setText("↓");
        });
        //3
        Button ship3 = new Button();
        ship3.setPrefSize((cellSize + cellIndent)*3 - cellIndent,cellSize);
        ship3.setText("→");
        ship3.setOnAction(e -> {
            if (curShip.getShipType().equals("cruiser")){
                curShipHorizontal = !curShipHorizontal;

            } else
            {
                curShipSize = 3;
                curShip = new Cruiser(rowsOfMap,columnsOfMap);
            }

            if (curShipHorizontal)
                ship3.setText("→");
            else
                ship3.setText("↓");
        });
        //2
        Button ship2 = new Button();
        ship2.setPrefSize((cellSize + cellIndent)*2 - cellIndent,cellSize);
        ship2.setText("→");
        ship2.setOnAction(e -> {
            if (curShip.getShipType().equals("destroyer")){
                curShipHorizontal = !curShipHorizontal;

            } else
            {
                curShipSize = 2;
                curShip = new Destroyer(rowsOfMap,columnsOfMap);
            }

            if (curShipHorizontal)
                ship2.setText("→");
            else
                ship2.setText("↓");
        });

        //1
        Button ship1 = new Button();
        ship1.setPrefSize((cellSize + cellIndent)*1 - cellIndent,cellSize);
        ship1.setText("→");
        ship1.setOnAction(e -> {
            if (curShip.getShipType().equals("submarine")){
                curShipHorizontal = !curShipHorizontal;


            } else
            {
                curShipSize = 1;
                curShip = new Submarine(rowsOfMap,columnsOfMap);
            }

            if (curShipHorizontal)
                ship1.setText("→");
            else
                ship1.setText("↓");
        });

        shipButtons.getChildren().addAll(ship1,ship2,ship3,ship4);
    }

    private boolean canToReady(){
        return cellShip[3] == 0 && cellShip[2] == 0 && cellShip[1] == 0 && cellShip[0] == 0;
    }

    static void showAlertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");

        alert.setContentText(message);

        alert.showAndWait();
    }

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        messageController = new MessageController(this);
        this.primaryStage = primaryStage;
        primaryStage.setTitle(isServer?"Server":"Client");
        initializeStartFields();
        //startGame();


    }

    public void startGame() {
        Platform.runLater( () -> {
                    if (isServer) {
                        send("1 4 start");
                        setWait();
                    } else {
                        setUnWait();
                    }
                }
        );

        initializeLogLabel();

        initializeGameInfo();

        initializeFireGroup();

        mapToPlace.getMap().setScaleX(0.5);
        mapToPlace.getMap().setScaleY(0.5);
        mapToPlace.getMap().setLayoutX((cellSize+ cellIndent) * (columnsOfMap-2) + cellIndent);

        mapToAttack = new BattleshipGame(rowsOfMap, columnsOfMap, cellSize, cellIndent,
                (TextField) fireGroup.getChildren().get(0), this);
        mapToAttack.startGame();
        Group root = mapToAttack.getMap();
        primaryStage.setTitle("BattleShip");

        primaryStage.setScene(new Scene(root, (columnsOfMap + 6) * (cellSize + cellIndent), (rowsOfMap + 3) * cellSize + (rowsOfMap + 3 + 1) * cellIndent));

        updateShipsInfo(0, 0, 0);

        root.getChildren().addAll(logLabel, currentGameInfo, fireGroup, mapToPlace.getMap());

        primaryStage.setResizable(false);

        primaryStage.show();
        welcomeSpich();
        updateShipsInfo(10, 0, 0);
    }

    public void addToLog(String text, Font font) {
        // Date date = new Date();
        //SimpleDateFormat formatForDateNow = new SimpleDateFormat("HH:mm:ss: ");
        Label loggedMessaged = new Label();
        loggedMessaged.setFont(font);
        loggedMessaged.setText(text);
        fp.getChildren().add(0, loggedMessaged);
    }

    public void updateShipsInfo(int full, int sunked, int shoted) {
        //((Label) currentGameInfo.getChildren().get(0)).setText("Выживших кораблей: " + (full));
        //((Label) currentGameInfo.getChildren().get(1)).setText("Потопленных кораблей: " + (sunked));
        //((Label) currentGameInfo.getChildren().get(2)).setText("Количество выстрелов: " + (shoted));
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }
        isServer = args[0].equals("Server");

        launch(args);
    }

    private void welcomeSpich() {
        addToLog("Удачи и веселой игры! \t\tавтор: Пятаков Роман БПИ185", Font.font("Arial", 14));
        addToLog("или же после ввода в текстовое поле нажмите Enter/Огонь! ", Font.font("Arial", 14));
        addToLog("для выстрела воспользуйтесь нажатием ЛКМ на клетку поля", Font.font("Arial", 14));
        addToLog("Добро пожаловать в игру Морсокой Бой", Font.font("Arial", FontWeight.BOLD, 15));

    }

    public void send(String message) {
        if (isServer) {
            server.send(message);
        } else {
            client.send(message);
        }
    }
    public void setWait(){
        isWait = true;
        var buttonFire = fireGroup.getChildren().get(1);
        buttonFire.setDisable(true);
    }
    public void setUnWait(){
        isWait = false;
        var buttonFire = fireGroup.getChildren().get(1);
        buttonFire.setDisable(false);
    }
    void GameExit(){
        Platform.exit();
    }
}
