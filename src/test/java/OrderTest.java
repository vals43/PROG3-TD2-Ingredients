import model.*;
import org.junit.jupiter.api.Test;
import service.DataRetriever;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testSaveAndFindOrder() {

        DataRetriever dr = new DataRetriever();

        Order order = new Order();
        order.setReference("ORD99999");
        order.setType(OrderTypeEnum.TAKE_AWAY);
        order.setStatus(OrderStatusEnum.CREATED);

        Dish dish = dr.findDishById(1);
        DishOrder dishOrder = new DishOrder();
        dishOrder.setDish(dish);
        order.setDishOrders(List.of(dishOrder));

        dr.saveOrder(order);

        Order loaded = dr.findOrderByReference("ORD99999");

        assertNotNull(loaded);
        assertEquals("ORD99999", loaded.getReference());
        assertEquals(OrderTypeEnum.TAKE_AWAY, loaded.getType());
        assertEquals(OrderStatusEnum.CREATED, loaded.getStatus());
    }

    @Test
    public void testOrderWithNoDish() {

        DataRetriever dr = new DataRetriever();

        Order order = new Order();
        order.setReference("ORD99998");
        order.setType(OrderTypeEnum.EAT_IN);
        order.setStatus(OrderStatusEnum.DELIVERED);

        assertThrows(RuntimeException.class, () -> {
            dr.saveOrder(order);
        });


    }
    @Test
    public void testDeliveredCannotBeModified() {

        DataRetriever dr = new DataRetriever();

        Order order = new Order();
        order.setReference("BLOCKED-2");
        order.setType(OrderTypeEnum.EAT_IN);
        order.setStatus(OrderStatusEnum.DELIVERED);

        Dish dish = dr.findDishById(1);
        DishOrder dishOrder = new DishOrder();
        dishOrder.setDish(dish);
        order.setDishOrders(List.of(dishOrder));

        dr.saveOrder(order);

        order.setStatus(OrderStatusEnum.CREATED);

        assertThrows(RuntimeException.class, () -> {
            dr.saveOrder(order);
        });
    }


}
