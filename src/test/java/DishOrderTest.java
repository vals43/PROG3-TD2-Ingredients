import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DishOrderTest {

    private final DataRetriever dr = new DataRetriever();

    @Test
    void TestFindOrderByReference() {
        assertEquals(1, (int) dr.findOrderByReference("ORD00001").getId());
        assertEquals(2, (int) dr.findOrderByReference("ORD00002").getId());
        assertEquals(3, (int) dr.findOrderByReference("ORD00003").getId());
    }
}
