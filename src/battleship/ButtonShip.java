/*
 * Created by Pyatakov Roman BPI 185 on 09.04.2020
 */
package battleship;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import sample.Main;

public class ButtonShip extends Button {
    int     row,
            column;
    TextField textBox;
    public ButtonShip(int row, int column, int FontSize, TextField textBox, BattleshipGame game){
        super();
        this.textBox = textBox;
        this.row = row;
        this.column = column;
        this.setText(row + " " + column);

        this.setFont(new Font("Calibri",FontSize));
        if (textBox != null) {
            this.setOnAction(e -> {
                if (!Main.isWait) {
                    textBox.setText(this.getText());
                    game.fire(this.getText());
                }
            });
        } else{
            //Расстановка кораблей
            //устал дебажить оставил только рандом(
//            this.setOnMouseClicked(e ->{
//                if (e.getButton() == MouseButton.SECONDARY){
//                    Ship.deleteShipAt(row,column,game.ocean, game.main);
//                    game.redrawPlaceMap();
//                } else
//                if (e.getButton() == MouseButton.PRIMARY) {
//                    Ship.deleteShipAt(row, column, game.ocean, game.main);
//                    game.redrawPlaceMap();
//                }
//
//            });
//
//            this.setOnMouseMoved(e ->{
//                if (row != game.main.lastRow || column != game.main.lastColumn){
//                    this.setText("h");
//                    if (game.main.lastRow != -1 && game.main.lastColumn != -1){
//                        Ship.deleteShipAt(game.main.lastRow,game.main.lastColumn,game.ocean, game.main);
//                        game.main.lastRow = -1;
//                        game.main.lastColumn = -1;
//                    }
//                    if (game.ocean.ships[row][column].okToPlaceShipAt(row,column,Main.curShipHorizontal, game.ocean)){
//                        game.main.lastRow = row;
//                        game.main.lastColumn = column;
//                        Main.curShip.placeShipAt(row,column,Main.curShipHorizontal,game.ocean);
//                    }
//                    game.redrawPlaceMap();
//
//                }
//            });
        }
    }
}
