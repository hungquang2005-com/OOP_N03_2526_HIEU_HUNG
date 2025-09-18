public class User extends Person implements PeopleInterface {
    private String userName;
    private String password;
    private String role;

    // Constructor chính: chỉ cần username, password, role
    public User(String userName, String password, String role) {
        super("", "", "");
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    // Constructor đầy đủ: bao gồm thông tin cá nhân
    public User(String userName, String password, String role, String identity, String fullName, String dateOfBirth) {
        super(identity, fullName, dateOfBirth);
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    // Implement từ PeopleInterface
    @Override
    public void setInfon(String identity, String fullName, String dateOfBirth) {
        super.setInfon(identity, fullName, dateOfBirth); 
    }

    @Override
    public String getInfo(String identity) {
        if (super.getIdentity() != null && super.getIdentity().equals(identity)) {
            return "Username: " + userName +
                   ", FullName: " + super.getFullName() +
                   ", DOB: " + super.getDateOfBirth() +
                   ", Identity: " + super.getIdentity() +
                   ", Role: " + role;
        }
        return "Không tìm thấy thông tin user với Identity: " + identity;
    }

    // Getter
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Login/Logout
    public void login() {
        System.out.println(userName + " logged in.");
    }

    public void logout() {
        System.out.println(userName + " logged out.");
    }
}
