import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    /* ========================= DISH ========================= */

    public Dish findDishById(Integer id) {
        String sql = """
            select id, name, dish_type, price
            from dish
            where id = ?
        """;

        try (Connection conn = new DBConnection().getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Dish not found " + id);
            }

            Dish dish = new Dish();
            dish.setId(rs.getInt("id"));
            dish.setName(rs.getString("name"));
            dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
            dish.setPrice(rs.getObject("price") == null ? null : rs.getDouble("price"));

            dish.setDishIngredientList(findDishIngredients(conn, dish));

            return dish;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* ========================= DISH INGREDIENT ========================= */

    private List<DishIngredient> findDishIngredients(Connection conn, Dish dish) throws SQLException {
        String sql = """
            select d.id, d.id_ingredient, d.quantity_required, d.unit,
                   i.name, i.price, i.category
            from dishingredient d
            join ingredient i on i.id = d.id_ingredient
            where d.id_dish = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dish.getId());
            ResultSet rs = ps.executeQuery();

            List<DishIngredient> result = new ArrayList<>();

            while (rs.next()) {
                DishIngredient di = new DishIngredient();

                di.setId(rs.getInt("id"));
                di.setDish(dish);

                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id_ingredient"));
                ing.setName(rs.getString("name"));
                ing.setPrice(rs.getDouble("price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                di.setIngredient(ing);
                di.setQuantity_required(rs.getDouble("quantity_required"));
                di.setUnit(UnitType.valueOf(rs.getString("unit")));

                result.add(di);
            }

            return result;
        }
    }

    /* ========================= INGREDIENT ========================= */

    public Ingredient findIngredientById(Integer id) {
        String sql = """
            select id, name, price, category
            from ingredient
            where id = ?
        """;

        try (Connection conn = new DBConnection().getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Ingredient not found " + id);
            }

            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getInt("id"));
            ingredient.setName(rs.getString("name"));
            ingredient.setPrice(rs.getDouble("price"));
            ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

            ingredient.setStockMovementList(findStockMovements(conn, id));

            return ingredient;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* ========================= STOCK MOVEMENT ========================= */

    private List<StockMovement> findStockMovements(Connection conn, int ingredientId) throws SQLException {
        String sql = """
            select id, quantity, type, unit, creation_datetime
            from stock_movement
            where id_ingredient = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();

            List<StockMovement> list = new ArrayList<>();

            while (rs.next()) {
                StockMovement sm = new StockMovement();
                sm.setId(rs.getInt("id"));

                StockValue value = new StockValue();
                value.setQuantity(rs.getDouble("quantity"));
                value.setUnit(UnitType.valueOf(rs.getString("unit")));

                sm.setValue(value);
                sm.setType(MovementTypeEnum.valueOf(rs.getString("type")));
                sm.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

                list.add(sm);
            }

            return list;
        }
    }

    /* ========================= SAVE DISH ========================= */

    public Dish saveDish(Dish dish) {
        String sql = """
            insert into dish (id, name, dish_type, price)
            values (?, ?, ?::dish_type, ?)
            on conflict (id) do update
            set name = excluded.name,
                dish_type = excluded.dish_type,
                price = excluded.price
            returning id
        """;

        try (Connection conn = new DBConnection().getDBConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, dish.getId());
                ps.setString(2, dish.getName());
                ps.setString(3, dish.getDishType().name());

                if (dish.getPrice() == null) {
                    ps.setNull(4, Types.DOUBLE);
                } else {
                    ps.setDouble(4, dish.getPrice());
                }

                ResultSet rs = ps.executeQuery();
                rs.next();
                dish.setId(rs.getInt(1));
            }

            detachIngredients(conn, dish.getId());
            attachIngredients(conn, dish);

            conn.commit();
            return findDishById(dish.getId());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void detachIngredients(Connection conn, int dishId) throws SQLException {
        try (PreparedStatement ps =
                     conn.prepareStatement("delete from dishingredient where id_dish = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void attachIngredients(Connection conn, Dish dish) throws SQLException {
        if (dish.getDishIngredientList() == null) return;

        String sql = """
            insert into dishingredient (id_dish, id_ingredient, quantity_required, unit)
            values (?, ?, ?, ?::unit_type)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : dish.getDishIngredientList()) {
                ps.setInt(1, dish.getId());
                ps.setInt(2, di.getIngredient().getId());
                ps.setDouble(3, di.getQuantity_required());
                ps.setString(4, di.getUnit().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /* ========================= ORDER (LOGIC ONLY) ========================= */

    public void checkOrderStock(Order order) {
        for (DishOrder dishOrder : order.getDishOrders()) {
            for (DishIngredient di : dishOrder.getDish().getDishIngredientList()) {
                Ingredient ing = findIngredientById(di.getIngredient().getId());

                double required =
                        di.getQuantity_required() * dishOrder.getQuantity();

                double available =
                        ing.getStockValueAt(Instant.now()).getQuantity();

                if (available < required) {
                    throw new RuntimeException(
                            "Insufficient stock for ingredient: " + ing.getName()
                    );
                }
            }
        }
    }
}
