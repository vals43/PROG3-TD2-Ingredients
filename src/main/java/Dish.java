import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<Ingredient> ingredients;

    public Dish() {}

    public Dish(Integer id, String name, DishTypeEnum dishType, Double price, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.ingredients = ingredients;
    }

    public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }

        double totalCost = 0.0;
        for (Ingredient ingredient : ingredients) {
            Double quantity = ingredient.getQuantity();
            Double unitPrice = ingredient.getPrice();

            if (quantity != null && unitPrice != null) {
                totalCost += unitPrice * quantity;
            }
        }
        return totalCost;
    }

    public Double getGrossMargin() {
        if (this.price == null) {
            throw new RuntimeException("Exception (prix NULL)");
        }
        return this.price - getDishCost();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}