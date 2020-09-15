package Tests;

import battleship.Ocean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OceanTest {
    Ocean ocean;
    final int rows = 10;
    final int columns = 10;
    @BeforeEach
    void Initialize(){
        ocean = new Ocean(rows ,columns);
        ocean.placeAllShipsRandomly();
    }


    @Test
    void fireAllMap() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                ocean.shootAt(i,j,true);
            }
        }
        Assertions.assertEquals(10,ocean.getShipsSunk());
        Assertions.assertEquals(100,ocean.getShotsFired());
        Assertions.assertEquals(true,ocean.isGameOver());
    }

    @Test
    void shootAt() {
        int expected = ocean.getShotsFired();
        ocean.shootAt(5,5,false);
        Assertions.assertEquals(0,ocean.getShotsFired());
        Assertions.assertEquals(expected,ocean.getShotsFired());
    }

    @Test
    void isOccupied(){
        boolean result = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result = result || ocean.isOccupied(i,j);
            }
        }
        assertEquals(true,result);
    }
}