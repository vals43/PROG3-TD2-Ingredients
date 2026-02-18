

import java.sql.*;
import java.time.Instant;

public class DataRetrieverDbSide {

    public double getStockValueAtDate(int ingredientId, Instant date) {

        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        String sql = """
                SELECT
                    id_ingredient,
                    unit,
                    SUM(
                            CASE
                                WHEN type = 'OUT' THEN quantity * -1
                                ELSE quantity
                                END
                    ) AS actual_quantity
                FROM stockmovement
                WHERE id_ingredient = ?
                  AND creation_datetime <= ?
                GROUP BY id_ingredient, unit;
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ingredientId);
            stmt.setTimestamp(2, Timestamp.from(date));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("actual_quantity");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public double getDishCost(int dishId) {

        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        String sql = """
                    SELECT
                        SUM(i.price * di.quantity_required) AS dish_cost
                    FROM dishingredient as di
                             JOIN ingredient i ON di.id_ingredient = i.id
                    WHERE di.id_dish = ? ;
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("dish_cost");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public double getGrossMargin(int dishId) {

        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        String sql = """
                SELECT d.price - SUM(i.price * di.quantity) AS gross_margin
                FROM dish d
                JOIN dish_ingredient di ON d.id = di.id_dish
                JOIN ingredient i ON di.id_ingredient = i.id
                WHERE d.id = ?
                GROUP BY d.price
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("gross_margin");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

}
