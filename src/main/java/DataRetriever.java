import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                    SELECT id, name, dish_type, price
                    FROM dish
                    WHERE id = ?
                    """
            );
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("id"));
                dish.setName(resultSet.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));

                Object priceObj = resultSet.getObject("price");
                dish.setPrice(priceObj == null ? null : ((Number) priceObj).doubleValue());

                dish.setIngredients(findIngredientByDishId(id));
                return dish;
            }

            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                INSERT INTO dish (id, name, dish_type, price)
                VALUES (?, ?, ?::dish_type, ?)
                ON CONFLICT (id) DO UPDATE
                SET name = EXCLUDED.name,
                    dish_type = EXCLUDED.dish_type,
                    price = EXCLUDED.price
                RETURNING id
                """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            Integer dishId;

            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }

                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getDishType().name());

                if (toSave.getPrice() != null) {
                    ps.setDouble(4, toSave.getPrice());
                } else {
                    ps.setNull(4, Types.NUMERIC);
                }

                ResultSet rs = ps.executeQuery();
                rs.next();
                dishId = rs.getInt(1);
            }

            deleteDishIngredients(conn, dishId);
            saveDishIngredients(conn, dishId, toSave.getIngredients());

            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }

        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();

        try {
            conn.setAutoCommit(false);

            String insertSql = """
                    INSERT INTO ingredient (id, name, price, category)
                    VALUES (?, ?, ?, ?::ingredient_category)
                    RETURNING id
                    """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    if (ingredient.getId() != null) {
                        ps.setInt(1, ingredient.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }

                    ps.setString(2, ingredient.getName());
                    ps.setDouble(3, ingredient.getPrice());
                    ps.setString(4, ingredient.getCategory().name());

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    ingredient.setId(rs.getInt(1));
                    savedIngredients.add(ingredient);
                }
            }

            conn.commit();
            return savedIngredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }

    private List<Ingredient> findIngredientByDishId(Integer idDish) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<Ingredient> ingredients = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                    SELECT i.id, i.name, i.price, i.category,
                           di.quantity_required, di.unit
                    FROM ingredient i
                    JOIN dishingredient di ON di.id_ingredient = i.id
                    WHERE di.id_dish = ?
                    """
            );

            preparedStatement.setInt(1, idDish);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("id"));
                ingredient.setName(resultSet.getString("name"));
                ingredient.setPrice(resultSet.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));
                ingredient.setQuantity(resultSet.getDouble("quantity_required"));
                ingredient.setUnit(UnitTypeEnum.valueOf(resultSet.getString("unit")));
                ingredients.add(ingredient);
            }

            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    private void saveDishIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients)
            throws SQLException {

        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }

        String insertSql = """
                INSERT INTO dishingredient (id, id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?, ?::unit_type)
                """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (Ingredient ingredient : ingredients) {
                ps.setInt(1, getNextSerialValue(conn, "dishingredient", "id"));
                ps.setInt(2, dishId);
                ps.setInt(3, ingredient.getId());
                ps.setDouble(4, ingredient.getQuantity());
                ps.setString(5, ingredient.getUnit().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteDishIngredients(Connection conn, Integer dishId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dishingredient WHERE id_dish = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT pg_get_serial_sequence(?, ?)")) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString(1);
        }
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        try (PreparedStatement ps = conn.prepareStatement("SELECT nextval(?)")) {
            ps.setString(1, sequenceName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }
}
