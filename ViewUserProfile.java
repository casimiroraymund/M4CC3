public class ViewUserProfile {
    private final DatabaseRepository repo;
    public ViewUserProfile(DatabaseRepository repo) { this.repo = repo; }
    public void viewUserProfile(String email) {
        User u = repo.getUserProfile(email);
        if (u != null) {
            System.out.println("\n=== User Profile ===");
            System.out.println("Full Name : " + u.getName());
            System.out.println("Email     : " + u.getEmail());
            System.out.println("Role      : " + u.getRole());
            System.out.println("Sport     : " + u.getSportType());
            System.out.println("====================\n");
        }
    }
}