import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class StockCalculationTest {

    private final DataRetriever dr = new DataRetriever();

    @Test
    void getStockAtT_Test(){
        assertEquals(4.8, dr.findIngredientById(1).getStockValueAt(Instant.parse("2024-01-06T12:00:00Z")).getQuantity());
        assertEquals(3.85, dr.findIngredientById(2).getStockValueAt(Instant.parse("2024-01-06T12:00:00Z")).getQuantity());
        assertEquals(9, dr.findIngredientById(3).getStockValueAt(Instant.parse("2024-01-06T12:00:00Z")).getQuantity());
        assertEquals(2.7, dr.findIngredientById(4).getStockValueAt(Instant.parse("2024-01-06T12:00:00Z")).getQuantity());
        assertEquals(2.3, dr.findIngredientById(5).getStockValueAt(Instant.parse("2024-01-06T12:00:00Z")).getQuantity());

    }
}
