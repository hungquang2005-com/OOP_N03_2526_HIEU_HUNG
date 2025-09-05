import java.util.Date;

public class Test {
    public static void main(String[] args) {
        User user = new User("admin", "12345", "Quản lý");
        user.login();
        user.logout();

        Food phoBo = new Food(1, "Phở Bò", 50000.0, "Phở bò tái nạm truyền thống");
        Food phoGa = new Food(2, "Phở Gà", 45000.0, "Phở gà xé phay");
        Food bunCha = new Food(3, "Bún Chả", 60000.0, "Bún chả Hà Nội");
        Food goiCuon = new Food(4, "Gỏi Cuốn", 30000.0, "Gỏi cuốn tôm thịt");

        Category monChinh = new Category(101, "Món Chính");
        monChinh.addFood(phoBo);
        monChinh.addFood(phoGa);

        Category monAnNhe = new Category(102, "Món Ăn Nhẹ");
        monAnNhe.addFood(bunCha);
        monAnNhe.addFood(goiCuon);

        System.out.println("Category: " + monChinh.getCategoryName() + " có " + monChinh.getFoods().size() + " món.");
        System.out.println("Category: " + monAnNhe.getCategoryName() + " có " + monAnNhe.getFoods().size() + " món.");

        Table table1 = new Table(1, 4);
        table1.reserve();
        table1.reserve();

        Order order1 = new Order(1001, new Date(), "Chưa thanh toán");
        order1.addOrderDetail(new OrderDetail(phoBo, 2));
        order1.addOrderDetail(new OrderDetail(goiCuon, 3));
        System.out.println("Tổng tiền đơn hàng: " + order1.calculateTotal() + " VND");

        Payment payment = new Payment(2001, order1.calculateTotal(), "Tiền mặt");
        payment.processPayment();

        RestaurantManagement rm = new RestaurantManagement();
        rm.addFood(phoBo);
        rm.addFood(phoGa);
        rm.addFood(bunCha);
        rm.addFood(goiCuon);

        rm.createOrder(order1);
        rm.generateReport();
    }
}

