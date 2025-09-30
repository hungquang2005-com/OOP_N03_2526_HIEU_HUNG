import java.util.*;

public class Test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ListOfUser listOfUser = new ListOfUser();
        RestaurantManagement rm = new RestaurantManagement();

        Set<Integer> usedOrderIds = new HashSet<>();

        System.out.println("=== HỆ THỐNG QUẢN LÝ NHÀ HÀNG ===");

        // ===== Menu chính: Đăng ký / Đăng nhập / Thoát =====
        User currentUser = null;
        while (true) {
            System.out.println("\n1. Đăng ký");
            System.out.println("2. Đăng nhập");
            System.out.println("3. Thoát");
            System.out.print("Lựa chọn: ");
            String mainChoice = sc.nextLine().trim();

            if (mainChoice.equals("1")) {
                System.out.print("Nhập username để đăng ký: ");
                String regUser = sc.nextLine();
                System.out.print("Nhập password: ");
                String regPass = sc.nextLine();
                System.out.print("Nhập lại password: ");
                String regPass2 = sc.nextLine();
                System.out.print("Nhập vai trò (Quản lý/Khách): ");
                String role = sc.nextLine();

                boolean registered = listOfUser.register(regUser, regPass, regPass2, role);
                if (registered) {
                    System.out.println("✅ Đăng ký thành công. Bạn có thể đăng nhập ngay.");
                } else {
                    System.out.println("❌ Đăng ký thất bại.");
                }
            } else if (mainChoice.equals("2")) {
                System.out.print("Username: ");
                String loginUser = sc.nextLine();
                System.out.print("Password: ");
                String loginPass = sc.nextLine();

                currentUser = listOfUser.login(loginUser, loginPass);
                if (currentUser == null) {
                    System.out.println("❌ Sai thông tin, thử lại!");
                    // quay lại menu
                } else {
                    System.out.println("✅ Đăng nhập thành công. Chào " + currentUser.getUserName());
                    currentUser.login();
                    break; 
                }
            } else if (mainChoice.equals("3")) {
                System.out.println("Thoát chương trình. Tạm biệt!");
                sc.close();
                return;
            } else {
                System.out.println("Lựa chọn không hợp lệ, thử lại.");
            }
        }

        // Nếu user là Quản lý => chỉ xem báo cáo
        if (currentUser.getRole().equalsIgnoreCase("Quản lý")) {
            System.out.println("\n=== CHỨC NĂNG QUẢN LÝ ===");
            rm.generateReport();
            System.out.println("Bạn đã thoát chương trình.");
            currentUser.logout();
            sc.close();
            return;
        }

        // ===== Luồng cho Khách =====
        boolean running = true;
        Random rand = new Random();

        while (running) {
            // ===== Đặt bàn =====
            System.out.print("\nNhập số lượng bàn muốn đặt: ");
            int soBan = 0;
            try {
                soBan = Integer.parseInt(sc.nextLine().trim());
                if (soBan <= 0) {
                    System.out.println("Số lượng bàn phải > 0. Quay lại.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Nhập không hợp lệ. Quay lại.");
                continue;
            }

            List<Table> reservedTables = new ArrayList<>();
            Set<Integer> reservedIds = new HashSet<>(); 

            for (int i = 1; i <= soBan; i++) {
                System.out.println("\n--- Nhập thông tin bàn " + i + " ---");
                int tableId;
                while (true) {
                    try {
                        System.out.print("Nhập ID bàn: ");
                        tableId = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("ID không hợp lệ, nhập số nguyên.");
                        continue;
                    }
                    if (reservedIds.contains(tableId)) {
                        System.out.println("❌ ID bàn đã được đặt trong lần này, hãy chọn ID khác.");
                        continue;
                    }
                    break;
                }

                int capacity;
                while (true) {
                    try {
                        System.out.print("Nhập sức chứa bàn (tối đa 6): ");
                        capacity = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Sức chứa không hợp lệ, nhập số nguyên.");
                        continue;
                    }
                    if (capacity <= 0) {
                        System.out.println("Sức chứa phải > 0.");
                        continue;
                    }
                    if (capacity > 6) {
                        System.out.println("❌ Sức chứa tối đa là 6, nhập lại!");
                        continue;
                    }
                    break;
                }

                int numPeople;
                while (true) {
                    try {
                        System.out.print("Nhập số lượng khách đi cùng: ");
                        numPeople = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Số khách không hợp lệ, nhập số nguyên.");
                        continue;
                    }
                    if (numPeople <= 0) {
                        System.out.println("Số khách phải > 0.");
                        continue;
                    }
                    if (numPeople > capacity) {
                        System.out.println("❌ Quá sức chứa của bàn, nhập lại số khách hoặc thay đổi bàn.");
                        continue;
                    }
                    break;
                }

                Table table = new Table(tableId, capacity);
                table.reserve();
                reservedIds.add(tableId);
                reservedTables.add(table);
                System.out.println("✅ Đã đặt bàn " + tableId + " cho " + numPeople + " người.");
            }

            if (reservedTables.isEmpty()) {
                System.out.println("❌ Không có bàn nào được đặt. Quay lại menu chính.");
                continue;
            }

            // ===== Menu món ăn (mặc định) =====
            Food phoBo = new Food(1, "Phở Bò", 50000.0, "Phở bò tái nạm truyền thống");
            Food phoGa = new Food(2, "Phở Gà", 45000.0, "Phở gà xé phay");
            Food bunCha = new Food(3, "Bún Chả", 60000.0, "Bún chả Hà Nội");
            Food goiCuon = new Food(4, "Gỏi Cuốn", 30000.0, "Gỏi cuốn tôm thịt");

            List<Food> menu = Arrays.asList(phoBo, phoGa, bunCha, goiCuon);

            // ===== Tạo order với id ngẫu nhiên không trùng =====
            int orderId;
            do {
                orderId = rand.nextInt(9000) + 1000; // 1000 - 9999
            } while (usedOrderIds.contains(orderId));
            usedOrderIds.add(orderId);

            Order order = new Order(orderId, new Date(), "Chưa thanh toán");
            System.out.println("\nTạo hóa đơn với ID: " + orderId);

            // ===== Gọi món =====
            int menuChoice;
            do {
                System.out.println("\n=== MENU ===");
                for (Food f : menu) {
                    System.out.println(f.getFoodID() + ". " + f.getName() + " - " + f.getPrice() + " VND");
                }
                System.out.print("Nhập ID món (0 để xong): ");
                try {
                    menuChoice = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Nhập không hợp lệ, thử lại.");
                    menuChoice = -1;
                    continue;
                }

                if (menuChoice != 0) {
                    System.out.print("Số lượng: ");
                    int qty;
                    try {
                        qty = Integer.parseInt(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Số lượng không hợp lệ, bỏ qua lựa chọn.");
                        continue;
                    }

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

            // ===== Tính tổng trước giảm =====
            double total = order.calculateTotal();
            order.setTotal(total); 

            // In hóa đơn tạm thời (chưa thanh toán)
            System.out.println("\n=== HÓA ĐƠN (Tạm) ===");
            for (OrderDetail d : order.getOrderDetails()) {
                System.out.println(d.getQuantity() + " x " + d.getFood().getName() + " = " + d.subTotal() + " VND");
            }
            System.out.println("Tổng tạm: " + total + " VND");

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
                    String discountChoice = sc.nextLine().trim();
                    if (discountChoice.equals("2")) break;
                    else if (discountChoice.equals("1")) continue;
                    else System.out.println("❌ Lựa chọn không hợp lệ, tiếp tục.");
                } else {
                    if (discountCode.equalsIgnoreCase("DISCOUNT10")) {
                        double newTotal = order.calculateTotal() * 0.9;
                        order.setTotal(newTotal); 
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
            System.out.println("2. QR");
            System.out.println("3. Thẻ tín dụng");
            System.out.print("Lựa chọn: ");
            int payChoice = 1;
            try {
                payChoice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Lựa chọn không hợp lệ, mặc định tiền mặt.");
                payChoice = 1;
            }

            boolean paymentSuccess = false;
            if (payChoice == 1) {
                // Tiền mặt
                double paymentAmount = order.getTotal();
                Payment payment = new Payment(rand.nextInt(9000) + 1000, paymentAmount, "Tiền mặt");
                payment.processPayment();
                System.out.println("✅ Thanh toán tiền mặt " + paymentAmount + " VND thành công.");
                printInvoice(order);
                paymentSuccess = true;
            } else if (payChoice == 2) {
                // QR
                System.out.println("\n📱 Mã QR (giả lập): [*** QR_CODE_FOR_" + orderId + " ***]");
                System.out.print("Xác nhận quét mã thành công (y/n): ");
                String confirm = sc.nextLine().trim();
                if (confirm.equalsIgnoreCase("y")) {
                    double paymentAmount = order.getTotal();
                    Payment payment = new Payment(rand.nextInt(9000) + 1000, paymentAmount, "QR");
                    payment.processPayment();
                    System.out.println("✅ Thanh toán QR thành công: " + paymentAmount + " VND");
                    printInvoice(order);
                    paymentSuccess = true;
                } else {
                    System.out.println("❌ Quét mã thất bại. Thanh toán không hoàn tất.");
                }
            } else if (payChoice == 3) {
                double paymentAmount = order.getTotal();
                System.out.print("Nhập 4 số cuối thẻ (giả lập): ");
                String last4 = sc.nextLine().trim();
                Payment payment = new Payment(rand.nextInt(9000) + 1000, paymentAmount, "Thẻ tín dụng");
                payment.processPayment();
                System.out.println("✅ Thanh toán thẻ (" + last4 + ") thành công: " + paymentAmount + " VND");
                printInvoice(order);
                paymentSuccess = true;
            } else {
                System.out.println("Lựa chọn thanh toán không hợp lệ. Quay lại menu.");
            }

            if (paymentSuccess) {
                rm.createOrder(order);
                rm.generateReport();
            }

            System.out.println("\nBạn muốn làm gì tiếp theo?");
            System.out.println("1. Tiếp tục đặt bàn và gọi món");
            System.out.println("2. Đăng xuất và thoát chương trình");
            System.out.print("Lựa chọn: ");
            String next = sc.nextLine().trim();

            if (next.equals("2")) {
                currentUser.logout();
                running = false;
                System.out.println("👋 Đã đăng xuất. Thoát chương trình.");
            }
        } 
        sc.close();
    }


    private static void printInvoice(Order order) {
        System.out.println("\n=== HÓA ĐƠN CHÍNH THỨC ===");
        System.out.println("Mã hóa đơn: " + order.getOrderID());
        System.out.println("-----------------------------");
        for (OrderDetail d : order.getOrderDetails()) {
            System.out.println(d.getQuantity() + " x " + d.getFood().getName() + " = " + d.subTotal() + " VND");
        }
        System.out.println("-----------------------------");
        System.out.println("Tổng thanh toán: " + order.getTotal() + " VND");
        System.out.println("Trạng thái: Đã thanh toán");
        System.out.println("-----------------------------");
    }
}
