public class Main {

    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();
        DataRetrieverDbSide dbSide = new DataRetrieverDbSide();

        double costDb = dbSide.getDishCost(1);
        double cost = dr.findDishById(1).getDishCost();

        System.out.println("Cost = " + cost);
        System.out.println("Cost = " + costDb);

    }
}