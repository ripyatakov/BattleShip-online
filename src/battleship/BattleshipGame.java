package battleship;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import sample.Client;
import sample.Main;
import sample.Server;

/**
 * Defines the main game class
 */
public class BattleshipGame {
    //Map settings
    int buttonIndent = 2;
    int rowsOfMap,
            columnsOfMap,
            cellSize;
    final int shipsCount = 10;
    ButtonShip[][] buttonShips;
    Main main;
    Group map;

    public BattleshipGame(int rowsOfMap, int columnsOfMap, int cellSize, int cellIndent, TextField textBox, Main main) {
        buttonIndent = cellIndent;
        this.main = main;
        this.rowsOfMap = rowsOfMap;
        this.columnsOfMap = columnsOfMap;
        this.cellSize = cellSize;
        buttonShips = new ButtonShip[rowsOfMap][columnsOfMap];
        shotedMarkers = new boolean[rowsOfMap][columnsOfMap];
        map = new Group();
        for (int i = 0; i < rowsOfMap; i++) {
            for (int j = 0; j < columnsOfMap; j++) {
                shotedMarkers[i][j] = false;
                buttonShips[i][j] = new ButtonShip(i, j, 2 * cellSize / 7, textBox, this);
                buttonShips[i][j].setLayoutX(j * cellSize + (j + 1) * buttonIndent);
                buttonShips[i][j].setLayoutY(i * cellSize + (i + 1) * buttonIndent);
                buttonShips[i][j].setMaxSize(cellSize, cellSize);
                buttonShips[i][j].setMinSize(cellSize, cellSize);
                buttonShips[i][j].setStyle("-fx-background-color: #028E9B");
                map.getChildren().addAll(buttonShips[i][j]);
            }
        }
    }

    void fireAt(int row, int column) {
        int shootRC = shootAt(row, column);

        switch (shootRC) {
            case 1:
                main.addToLog("Мимо " + row + " " + column, new Font("Arial", 12));
                break;
            case 2:
                main.addToLog("Попал " + row + " " + column, Font.font("Arial", FontWeight.BOLD, 12));
                break;
            case 3:
                main.addToLog("Убил " + row + " " + column, Font.font("Arial", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 13));
                break;
            case 4:
                main.addToLog("Сюда уже стреляли " + row + " " + column, new Font("Arial", 12));
                break;
            default:
                break;
        }
        redrawMap();

    }

    public void fire(String coordinates) {
        //Мы стреляем по полю противника
        //if (!ocean.isGameOver()) {
        if (true) {
            try {
                var ss = coordinates.split(" ");
                int x = Integer.parseInt(ss[0]);
                int y = Integer.parseInt((ss[1]));
                if (!(x > -1 && x < rowsOfMap && y > -1 && y < columnsOfMap))
                    throw new IllegalArgumentException();
                main.send("2 " + coordinates + " 1");
                main.setWait();
            } catch (Exception exc) {
                main.addToLog("Некорректные координаты, повторите ввод...", new Font("Arial", 12));
            }
            //main.updateShipsInfo(shipsCount - ocean.shipsSunk, ocean.shipsSunk, ocean.shotsFired);
//            if (ocean.isGameOver()){
//                main.addToLog("Поздравляю с победой! Хорошая игра!",Font.font("Arial",FontWeight.BOLD,16));
//                ((Button)main.fireGroup.getChildren().get(1)).setText("Перезапустить");
//                ((Button)main.fireGroup.getChildren().get(1)).setOnAction(e->{
//                    try {
//                        main.startGame();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                });
//            }
        }
    }

    public void fireAtMe(String xx, String yy) {
        try {
            int x = Integer.parseInt(xx);
            int y = Integer.parseInt(yy);
            if (!(x > -1 && x < rowsOfMap && y > -1 && y < columnsOfMap))
                throw new IllegalArgumentException();
            ocean.shootAt(x, y, true);
            sunkRedraw();
            redrawMap();
            String message = "2 " + xx + " " + yy + " ";
            String name = (Main.isServer)?Client.name:Server.name;
            switch (ocean.ships[x][y].getSymbol(x, y).charAt(0)) {
                case '.':
                    break;
                case 'x':
                    setButtonColor(x, y, "black");
                    message += "4";
                    main.addToLog(name + " "+ x + " " + y + " " + " убил", Font.font("Arial", 12));
                    main.setWait();
                    break;
                case 'S':
                    setButtonColor(x, y, "#AB2B52");
                    message += "3";
                    main.addToLog(name + " "+ x + " " + y + " " + " попал", Font.font("Arial", 12));
                    main.setWait();
                    break;
                case '-':

                    setButtonColor(x, y, "#7109AA");
                    message += "2";
                    main.addToLog(name + " " +  x + " " + y + " " + " промазал", Font.font("Arial", 12));
                    main.setUnWait();
                    break;
                default:
                    break;
            }
            main.send(message);
            if (ocean.isGameOver()) {
                message = "2 " +  x + " " + y + " 5";
                main.addToLog(name + " потопил все ваши корабли и победил :(", Font.font("Arial", 12));
                main.send(message);
                main.setWait();
            }


        } catch (Exception e) {

        }
    }


    public Group getMap() {
        return map;
    }

    Ocean ocean;

    public void startGame() {
        ocean = new Ocean(rowsOfMap, columnsOfMap);
        int score = 0;
        ocean.placeAllShipsRandomly();
    }

    /**
     * @param row
     * @param column
     * @return 1 - мимо 2 - попал 3 - попал и убил 4 - нельзя стрелять
     */
    public int shootAt(int row, int column) {
        int sunked = ocean.shipsSunk;
        if (hited(row, column)) {
            return 4;
        }
        boolean penetration = ocean.shootAt(row, column, true);
        if (penetration) {
            if (sunked != ocean.shipsSunk) {
                sunkRedraw();
                return 3;
            }
            return 2;
        } else {
            return 1;
        }
    }

    public boolean hited(int row, int column) {
        return ocean.shooted[row][column];
    }

    /**
     * Method for getting numbers for shoot
     *
     * @return new int []{row, column}
     */
    public void setButtonColor(int row, int column, String c) {
        buttonShips[row][column].setStyle("-fx-background-color: " + c);
    }

    public void redrawMap() {
        for (int i = 0; i < rowsOfMap; i++) {
            for (int j = 0; j < columnsOfMap; j++) {
                char symb = ocean.ships[i][j].getSymbol(i, j).charAt(0);
                switch (symb) {
                    case '.':
                        break;
                    case 'x':
                        setButtonColor(i, j, "black");
                        break;
                    case 'S':
                        setButtonColor(i, j, "#AB2B52");
                        break;
                    case '-':
                        setButtonColor(i, j, "#7109AA");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void redrawPlaceMap() {
        for (int i = 0; i < rowsOfMap; i++) {
            for (int j = 0; j < columnsOfMap; j++) {
                try {
                    var a = (EmptySea) ocean.ships[i][j];
                    setButtonColor(i, j, "#028E9B");
                } catch (Exception e) {
                    setButtonColor(i, j, "green");
                }
//                char symb = ocean.ships[i][j].getSymbol(i,j).charAt(0);
//                switch (symb){
//                    case 'x': setButtonColor(i,j,"green");break;
//                    default: setButtonColor(i,j,"#028E9B"); break;
//                }
            }
        }
    }

    private void sunkRedraw() {
        for (int i = 0; i < rowsOfMap; i++) {
            for (int j = 0; j < columnsOfMap; j++) {
                if (ocean.ships[i][j].getSymbol(i, j).charAt(0) == 'x') {
                    nineShoots(i, j);
                }
            }
        }
    }

    private void nineShoots(int row, int column) {
        for (int i = Math.max(0, row - 1); i <= Math.min(rowsOfMap - 1, row + 1); i++) {
            for (int j = Math.max(0, column - 1); j <= Math.min(columnsOfMap - 1, column + 1); j++) {
                ocean.shootAt(i, j, false);
            }
        }
    }
    private boolean[][] shotedMarkers;
    public void nineColorShoots(int row, int column){

        if (shotedMarkers[row][column])
            return;

        shotedMarkers[row][column] = true;
        for (int i = Math.max(0, row - 1); i <= Math.min(rowsOfMap - 1, row + 1); i++) {
            for (int j = Math.max(0, column - 1); j <= Math.min(columnsOfMap - 1, column + 1); j++) {
                if (!(row == i && column == j)) {
                    if ((buttonShips[i][j].getStyle().split(" "))[1].equals("#AB2B52")){
                        nineColorShoots(i, j);
                    } else {
                        setButtonColor(i, j, "#7109AA");
                    }
                }
            }
        }
    }

}
