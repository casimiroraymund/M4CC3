public class User {
    private String name, email, role, sportType;
    public User(String n, String e, String r, String s) { name = n; email = e; role = r; sportType = s; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getSportType() { return sportType; }
}