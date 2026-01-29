package model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private OrderTypeEnum type;
    private OrderStatusEnum status;
    private List<DishOrder> dishOrders;

    public Order() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }

    public OrderTypeEnum getType() { return type; }
    public void setType(OrderTypeEnum type) { this.type = type; }

    public OrderStatusEnum getStatus() { return status; }
    public void setStatus(OrderStatusEnum status) { this.status = status; }

    public List<DishOrder> getDishOrders() { return dishOrders; }
    public void setDishOrders(List<DishOrder> dishOrders) { this.dishOrders = dishOrders; }

    public Double getTotalAmountWithoutVAT(){
        double total = 0;
        for (DishOrder d : dishOrders) {
            total += d.getDish().getPrice() * d.getQuantity();
        }
        return total;
    }

    public Double getTotalAmountWithVAT(){
        return getTotalAmountWithoutVAT() + (getTotalAmountWithoutVAT() * 0.2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
