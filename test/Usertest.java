public class Usertest {
    public static void main(String[] args) {
    
        User user = new User("admin", "12345", "Quản lý");

        System.out.println("Tên đăng nhập: " + user.getUserName());
        System.out.println("Mật khẩu: " + user.getPassword());
        System.out.println("Vai trò: " + user.getRole());

        user.setUserName("Hung");
        user.setPassword("10022005");
        user.setRole("Director");

        System.out.println("\nSau khi thay đổi:");
        System.out.println("Tên đăng nhập: " + user.getUserName());
        System.out.println("Mật khẩu: " + user.getPassword());
        System.out.println("Vai trò: " + user.getRole());

        user.login();
        user.logout();
    }
}
