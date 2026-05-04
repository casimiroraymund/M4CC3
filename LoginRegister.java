import java.util.Scanner;
import java.util.regex.Pattern;

public class LoginRegister {
    private final DatabaseRepository repo;

    public LoginRegister(DatabaseRepository repo) {
        this.repo = repo;
    }

    public void userRegister(Scanner sc) {
        System.out.println("\n=== Registration ===");
        System.out.print("Email: "); String email = sc.nextLine().trim();

        // Added Email Format Check
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format! Please use example@domain.com.");
            return;
        }

        if (repo.emailExists(email)) {
            System.out.println("Email already registered!");
            return;
        }

        System.out.print("Full Name: "); String name = sc.nextLine().trim();
        System.out.print("Sport: "); String sportType = sc.nextLine().trim();

        String role = "";
        while (true) {
            System.out.println("Select Role: 1. Athlete  2. Trainer");
            String input = sc.nextLine().trim();
            if (input.equals("1")) { role = "athlete"; break; }
            else if (input.equals("2")) { role = "trainer"; break; }
            else System.out.println("Invalid choice.");
        }

        System.out.print("Password: "); String password = sc.nextLine();
        repo.registerUser(name, email, password, role, sportType);
    }

    public String userLogin(Scanner sc) {
        System.out.print("Enter email: "); String email = sc.nextLine().trim();
        System.out.print("Enter password: "); String password = sc.nextLine();
        User user = repo.loginUser(email, password);
        if (user != null) {
            return email;
        }
        System.out.println("Invalid credentials!");
        return null;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(regex).matcher(email).matches();
    }
}