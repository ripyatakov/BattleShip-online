package battleship;
import java.util.Random;

/**
 * Defines the game "board" class
 */
public class Ocean {

    final int maxCellsPerShip = 4;

    int rowsOfMap, columnsOfMap;
    int [][] mapInfo;

    Ship[][] ships ;
    public boolean[][] shooted;
    int shotsFired;

    int hitCount;

    int shipsSunk;

    /**
     * Set the main settings
     */
    public Ocean(int rowsOfMap, int columnsOfMap){
        this.rowsOfMap = rowsOfMap;
        this.columnsOfMap = columnsOfMap;
        ships = new Ship[rowsOfMap][columnsOfMap];
        shooted = new boolean[rowsOfMap][columnsOfMap];
        mapInfo = new int[rowsOfMap][columnsOfMap];
        shotsFired = 0;
        hitCount = 0;
        shipsSunk = 0;
        for (int i = 0; i < rowsOfMap; i++)
        {
            for (int j = 0; j < columnsOfMap; j++)
            {
                ships[i][j] = new EmptySea(rowsOfMap, columnsOfMap);
                shooted[i][j] = false;
                mapInfo[i][j] = 0;

            }
        }
    }

    /**
     * Shows the occeping of cell [row, column]
     * @param row
     * @param column
     * @return Occupied?true:false
     */
    public boolean isOccupied(int row, int column){
        return  !(ships[row][column].getShipType().equals("EmptySea"));
    }

    /**
     * The main method for placement ships in Ocean
     */
    public void placeAllShipsRandomly(){
        Random rnd = new Random();
        int posX;
        int posY;
        boolean horizontal;
        for (int i = 1; i <= maxCellsPerShip; i++)
            for(int j = 1; j <= i; j++){
                Ship cur;
                switch (i)
                {
                    case
                        1 : cur = new Battleship(rowsOfMap,columnsOfMap); break;
                    case
                        2 : cur = new Cruiser(rowsOfMap,columnsOfMap); break;
                    case
                        3 : cur = new Destroyer(rowsOfMap,columnsOfMap); break;
                    case
                        4 : cur = new Submarine(rowsOfMap,columnsOfMap); break;

                    default: cur = new Submarine(rowsOfMap,columnsOfMap); break;
                }
                do {
                    posX = rnd.nextInt(rowsOfMap);
                    posY = rnd.nextInt(columnsOfMap);
                    horizontal = rnd.nextBoolean();
                    }   while (!cur.okToPlaceShipAt(posY,posX,horizontal,this));
                cur.placeShipAt(posY,posX,horizontal,this);
            }
    }

    /**
     * Method for shoot at the cell
     * @param row
     * @param column
     * @return <true>shoot on ship</true> <false>shoot on empty sea or sunk ship</false>
     */
    public boolean shootAt(int row, int column, boolean isShoot){
        if (ships[row][column].isSunk()) return false;
        shooted[row][column] = true;
        boolean rtrn = ships[row][column].shootAt(row,column);
        if (isShoot) {
            shotsFired++;
        }
        hitCount += rtrn?1:0;
        if (ships[row][column].isSunk())
            shipsSunk++;
        return rtrn;
    }

    /**
     * Getters of shots fired
     * @return
     */
    public int getShotsFired(){
        return shotsFired;
    }
    /**
     * Getters of hit count
     * @return
     */
    public int getHitCount(){
        return hitCount;
    }
    /**
     * Getters of ships sunk
     * @return
     */
    public int getShipsSunk() {
        return shipsSunk;
    }

    /**
     * Shows the end of the game
     * @return
     */
    public boolean isGameOver(){
        return shipsSunk == 10;
    }

    /**
     * Getters for ships
     * @return
     */
    public Ship[][] getShipArray(){
        return ships;
    }

    /**
     * Method for print game "desk"
     */
    public void print(){
        String answer = "  ";
        for (int i = 0; i < columnsOfMap; i++){
            answer += i + " ";
        }
        for (int j = 0; j < rowsOfMap; j ++) {
            answer += "\n";
            for (int i = -1; i < columnsOfMap; i++) {
                if (i < 0) {
                    answer += j + " ";
                } else {
                    answer += ships[j][i].getSymbol(j, i) + " ";
                }
            }
        }
        System.out.println(answer);
    }
}
