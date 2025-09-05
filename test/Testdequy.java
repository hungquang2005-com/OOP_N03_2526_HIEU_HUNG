public class Testdequy {
    public static void main(String[] args) {
        
        Food phoBo = new Food(1, "Phở Bò", 50000.0, "Phở bò tái nạm truyền thống");
        Food bunCha = new Food(2, "Bún Chả", 60000.0, "Bún chả Hà Nội");
        Food goiCuon = new Food(3, "Gỏi Cuốn", 30000.0, "Gỏi cuốn tôm thịt");

        Category monChinh = new Category(101, "Món Chính");
        monChinh.addFood(phoBo);
        monChinh.addFood(bunCha);
        monChinh.addFood(goiCuon);

        System.out.println("Category ID: " + monChinh.getCategoryID());
        System.out.println("Category Name: " + monChinh.getCategoryName());
        System.out.println("Số món ăn: " + monChinh.getFoods().size());

        System.out.println("\nDanh sách món ăn (in đệ quy):");
        monChinh.printFoodsRecursive(0);

        String searchName = "Bún Chả";
        Food foundFood = monChinh.findFoodByNameRecursive(searchName, 0);
        if (foundFood != null) {
            System.out.println("\nTìm thấy món: " + foundFood.getName() + " - Giá: " + foundFood.getPrice());
        } else {
            System.out.println("\nKhông tìm thấy món: " + searchName);
        }

        searchName = "Pizza";
        Food notFound = monChinh.findFoodByNameRecursive(searchName, 0);
        if (notFound != null) {
            System.out.println("\nTìm thấy món: " + notFound.getName());
        } else {
            System.out.println("\nKhông tìm thấy món: " + searchName);
        }
    }
}
