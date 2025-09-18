import java.util.*;

public class Test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ListOfUser listOfUser = new ListOfUser();

        System.out.println("=== HỆ THỐNG QUẢN LÝ NHÀ HÀNG ===");

        // ===== Đăng ký =====
        System.out.print("Nhập username để đăng ký: ");
        String regUser = sc.nextLine();
        System.out.print("Nhập password: ");
        String regPass = sc.nextLine();
        System.out.print("Nhập lại password: ");
        String regPass2 = sc.nextLine();
        System.out.print("Nhập vai trò (Quản lý/Khách): ");
        String role = sc.nextLine();

        boolean registered = listOfUser.register(regUser, regPass, regPass2, role);
        if (!registered) {
            System.out.println("❌ Đăng ký thất bại, thoát chương trình.");
            return;
        }

        // ===== Đăng nhập =====
        User currentUser = null;
        while (currentUser == null) {
            System.out.print("\nĐăng nhập\nUsername: ");
            String loginUser = sc.nextLine();
            System.out.print("Password: ");
            String loginPass = sc.nextLine();

            currentUser = listOfUser.login(loginUser, loginPass);
            if (currentUser == null) {
                System.out.println("❌ Sai thông tin, thử lại!");
            }
        }
        currentUser.login();

        // ===== Kiểm tra vai trò =====
        RestaurantManagement rm = new RestaurantManagement();
        if (currentUser.getRole().equalsIgnoreCase("Quản lý")) {
            System.out.println("\n=== CHỨC NĂNG QUẢN LÝ ===");
            rm.generateReport(); // Quản lý chỉ xem báo cáo
            System.out.println("Bạn đã thoát chương trình.");
            currentUser.logout();
            sc.close();
            return;
        }

        // ===== Vòng lặp chính cho Khách =====
        boolean running = true;
        while (running) {

            // ===== Đặt bàn =====
            System.out.print("\nNhập số lượng bàn muốn đặt: ");
            int soBan = sc.nextInt();
            sc.nextLine();

            List<Table> reservedTables = new ArrayList<>();
            for (int i = 1; i <= soBan; i++) {
                System.out.println("\n--- Nhập thông tin bàn " + i + " ---");
                System.out.print("Nhập ID bàn: ");
                int tableId = sc.nextInt();
                System.out.print("Nhập sức chứa bàn: ");
                int capacity = sc.nextInt();
                System.out.print("Nhập số lượng khách đi cùng: ");
                int numPeople = sc.nextInt();
                sc.nextLine(); // clear buffer

                Table table = new Table(tableId, capacity);
                if (numPeople <= capacity) {
                    table.reserve();
                    reservedTables.add(table);
                    System.out.println("✅ Đã đặt bàn " + tableId + " cho " + numPeople + " người.");
                } else {
                    System.out.println("⚠️ Bàn " + tableId + " không đủ chỗ. Không thể đặt.");
                }
            }

            if (reservedTables.isEmpty()) {
                System.out.println("❌ Không có bàn nào được đặt. Quay lại menu chính.");
                continue;
            }

            // ===== Menu món ăn =====
            Food phoBo = new Food(1, "Phở Bò", 50000.0, "Phở bò tái nạm truyền thống");
            Food phoGa = new Food(2, "Phở Gà", 45000.0, "Phở gà xé phay");
            Food bunCha = new Food(3, "Bún Chả", 60000.0, "Bún chả Hà Nội");
            Food goiCuon = new Food(4, "Gỏi Cuốn", 30000.0, "Gỏi cuốn tôm thịt");

            List<Food> menu = Arrays.asList(phoBo, phoGa, bunCha, goiCuon);

            // ===== Gọi món =====
            Order order = new Order(1001, new Date(), "Chưa thanh toán");
            int menuChoice;
            do {
                System.out.println("\n=== MENU ===");
                for (Food f : menu) {
                    System.out.println(f.getFoodID() + ". " + f.getName() + " - " + f.getPrice() + " VND");
                }
                System.out.print("Nhập ID món (0 để xong): ");
                menuChoice = sc.nextInt();

                if (menuChoice != 0) {
                    System.out.print("Số lượng: ");
                    int qty = sc.nextInt();
                    sc.nextLine();

                    Food selected = null;
                    for (Food f : menu) {
                        if (f.getFoodID() == menuChoice) {
                            selected = f;
                            break;
                        }
                    }

                    if (selected != null) {
                        order.addOrderDetail(new OrderDetail(selected, qty));
                        System.out.println("✅ Đã thêm " + qty + " x " + selected.getName());
                    } else {
                        System.out.println("❌ Món không tồn tại!");
                    }
                }
            } while (menuChoice != 0);

            // ===== Thanh toán =====
            double total = order.calculateTotal();

            // In hóa đơn
            System.out.println("\n=== HÓA ĐƠN ===");
            for (OrderDetail d : order.getOrderDetails()) {
                System.out.println(d.getQuantity() + " x " + d.getFood().getName() + " = " + d.subTotal() + " VND");
            }
            System.out.println("Tổng cộng: " + total + " VND");

            // ===== Nhập mã giảm giá =====
            boolean discountApplied = false;
            while (!discountApplied) {
                System.out.print("\nNhập mã giảm giá (nếu không có thì để trống): ");
                String discountCode = sc.nextLine().trim();

                if (discountCode.isEmpty()) {
                    System.out.println("Bạn không nhập mã giảm giá.");
                    System.out.println("1. Nhập lại mã");
                    System.out.println("2. Bỏ qua và tiếp tục thanh toán");
                    System.out.print("Lựa chọn: ");
                    int discountChoice = Integer.parseInt(sc.nextLine().trim());
                    if (discountChoice == 2) break;
                    else if (discountChoice == 1) continue;
                    else System.out.println("❌ Lựa chọn không hợp lệ, vui lòng nhập lại.");
                } else {
                    if (discountCode.equalsIgnoreCase("DISCOUNT10")) {
                        double newTotal = order.calculateTotal() * 0.9;
                        order.setTotal(newTotal); // cập nhật tổng tiền vào order
                        System.out.println("🎉 Áp dụng mã giảm giá thành công! Tổng sau giảm: " + newTotal + " VND");
                        discountApplied = true;
                    } else {
                        System.out.println("❌ Mã giảm giá không hợp lệ! Vui lòng nhập lại.");
                    }
                }
            }

            // ===== Chọn phương thức thanh toán =====
            System.out.println("\nChọn phương thức thanh toán:");
            System.out.println("1. Tiền mặt");
            System.out.println("2. Thẻ tín dụng");
            System.out.println("3. Ví điện tử");
            System.out.print("Lựa chọn: ");
            int payChoice = sc.nextInt();
            sc.nextLine();

            String method = "Tiền mặt";
            if (payChoice == 2) method = "Thẻ tín dụng";
            else if (payChoice == 3) method = "Ví điện tử";

            Payment payment = new Payment(2001, order.getTotal(), method);
            payment.processPayment();

            // ===== Báo cáo quản lý =====
            rm.createOrder(order);
            rm.generateReport();

            // ===== Hỏi tiếp tục hay thoát =====
            System.out.println("\nBạn muốn làm gì tiếp theo?");
            System.out.println("1. Tiếp tục đặt bàn và gọi món");
            System.out.println("2. Đăng xuất và thoát chương trình");
            System.out.print("Lựa chọn: ");
            int next = sc.nextInt();
            sc.nextLine();

            if (next == 2) {
                currentUser.logout();
                running = false;
            }
        }

        sc.close();
    }
}
