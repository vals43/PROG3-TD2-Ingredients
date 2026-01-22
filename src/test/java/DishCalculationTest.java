import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DishCalculationTest {

    private final DataRetriever dr = new DataRetriever();

    @Test
    void testGetDishCost() {
        assertEquals(250.00, dr.findDishById(1).getDishCost());
        assertEquals(4500.00, dr.findDishById(2).getDishCost());
        assertEquals(0.00, dr.findDishById(3).getDishCost());
        assertEquals(1400.00, dr.findDishById(4).getDishCost());
        assertEquals(0.00, dr.findDishById(5).getDishCost());
    }

    @Test
    void testGetGrossMarginValidPrice() {
        assertEquals(3250.00, dr.findDishById(1).getGrossMargin());
        assertEquals(7500.00, dr.findDishById(2).getGrossMargin());
        assertEquals(6600.00, dr.findDishById(4).getGrossMargin());
    }

    @Test
    void Nullexception() {
        assertThrows(RuntimeException.class, () ->
                dr.findDishById(3).getGrossMargin()
        );

        assertThrows(RuntimeException.class, () ->
                dr.findDishById(5).getGrossMargin()
        );
    }
}
