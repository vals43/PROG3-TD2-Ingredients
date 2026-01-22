import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        DataRetriever dr = new DataRetriever();

        Dish dish1 = dr.findDishById(1);
        System.out.println("Dish: " + dish1.getGrossMargin());

        Dish dish3 = dr.findDishById(3);
        System.out.println("Dish: " + dish3.getGrossMargin());

        dish1.setPrice(2500.0);
        dr.saveDish(dish1);

        Dish saladeUpdated = dr.findDishById(dish1.getId());
        System.out.println("Nouveau prix : " + saladeUpdated.getPrice());
        System.out.println("Gross Margin : " + saladeUpdated.getGrossMargin());

    }
}