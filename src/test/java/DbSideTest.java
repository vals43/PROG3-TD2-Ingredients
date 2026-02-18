import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbSideTest {
    @Test
    void testGetMovementAtT() {
        DataRetrieverDbSide dbSide = new DataRetrieverDbSide();
        DataRetriever dr = new DataRetriever();

        double value1 = dr.findIngredientById(1).getStockValueAt(Instant.parse("2024-01-06T12:00:00Z")).getQuantity();
        double value2 = dbSide.getStockValueAtDate(1, Instant.parse("2024-01-06T12:00:00Z"));

        assertEquals(value1 , value2);
    }
    @Test
    void testDishCost() {
        DataRetrieverDbSide dbSide = new DataRetrieverDbSide();
        DataRetriever dr = new DataRetriever();

        double costDb = dbSide.getDishCost(1);
        double cost = dr.findDishById(1).getDishCost();

        assertEquals(cost , costDb);
    }
    @Test
    void TestMargin() {
        DataRetrieverDbSide dbSide = new DataRetrieverDbSide();
        DataRetriever dr = new DataRetriever();

        double marginDb = dbSide.getGrossMargin(1);
        double margin = dr.findDishById(1).getGrossMargin();

        assertEquals(marginDb , margin);
    }


}
