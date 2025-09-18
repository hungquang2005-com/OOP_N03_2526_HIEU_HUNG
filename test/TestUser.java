public class TestUser {
    public static void main(String[] args) {
        User user1 = new User("hungquang2005", "12345", "Quản lý");
        user1.setInfon("ID001", "Nguyen Van A", "01/01/2000");

        System.out.println(user1.getInfo("ID001"));

        user1.login();
        user1.logout();
    }
}

