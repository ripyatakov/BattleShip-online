package battleship;

/**
 * Defines the empty game cell
 */
public class EmptySea extends Ship {
    /**
     * Base settings for EmptySea
     */
    public EmptySea(int rowsOfMap, int columnsOfMap){
        super(rowsOfMap, columnsOfMap);
        this.rowsOfMap = rowsOfMap;
        this.columnsOfMap = columnsOfMap;
        length = 1;
        for (int i = 0; i < length; i++){
            hit[i] = false;
        }
    }

    /**
     *
     * @param row Row
     * @param column Column
     * @return curcell is shooted <true>-</true> <false>.</false>
     */
    @Override
    public String getSymbol(int row, int column){
        if (hit[0]) return "-";
        return ".";
    }
    /**
     * Shoot on Empty sea cell
     */
    @Override
    public boolean shootAt(int row, int column){
        hit[0] = true;
        return false;
    }

    /**
     * @return Anyway False
     */
    @Override
    public boolean isSunk(){
        return false;
    }

}
