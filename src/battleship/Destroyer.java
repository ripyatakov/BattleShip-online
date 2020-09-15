package battleship;
/**
 * Defines the two-deck ship
 */
public class Destroyer extends Ship {
    /**
     * Base settings for Destroyer
     */
    public Destroyer(int rowsOfMap, int columnsOfMap){
        super(rowsOfMap, columnsOfMap);
        length = 2;
        for (int i = 0; i < length; i++){
            hit[i] = false;
        }
    }
    /**
     * returns Ship type
     */
    @Override
    public String getShipType(){
        return "destroyer";
    }
}
