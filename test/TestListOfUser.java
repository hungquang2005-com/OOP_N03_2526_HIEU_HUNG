public class TestListOfUser {
    public static void main(String[] args) {
        ListOfUser listOfUser = new ListOfUser();

        System.out.println("== TEST ĐĂNG KÝ ==");

        // Đăng ký với 4 tham số (có confirm password)
        listOfUser.register("admin", "12345", "12345", "Quản lý");
        listOfUser.register("hungquang2005", "10022005", "10022005", "Khách");

        // Trường hợp mật khẩu không khớp
        listOfUser.register("testUser", "1111", "2222", "Khách");

        // Trường hợp username bị trùng
        listOfUser.register("admin", "9999", "9999", "Khách");

        // Đăng ký nhanh (3 tham số)
        listOfUser.register("guest", "guest123", "Khách");

        // In danh sách user
        System.out.println("\n== DANH SÁCH USER ==");
        for (User u : listOfUser.getUsers()) {
            System.out.println("- " + u.getUserName() + " | Role: " + u.getRole());
        }

        // Thử đăng nhập
        System.out.println("\n== TEST ĐĂNG NHẬP ==");
        listOfUser.login("admin", "12345");      // đúng
        listOfUser.login("admin", "sai_mk");     // sai password
        listOfUser.login("not_exist", "12345");  // sai username
    }
}
