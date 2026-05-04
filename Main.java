import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseRepository repo = new DatabaseRepository();
        Scanner sc = new Scanner(System.in);

        repo.createUserTable();
        repo.createSessionTables();

        while (true) {
            System.out.println("\n=== Sports Academy System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) login(sc, repo);
            else if (choice.equals("2")) register(sc, repo);
            else if (choice.equals("3")) break;
        }
        sc.close();
    }

    private static void login(Scanner sc, DatabaseRepository repo) {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        User user = repo.loginUser(email, password);
        if (user != null) {
            System.out.println("\nWelcome, " + user.getName());
            String role = user.getRole().toLowerCase();
            if (role.equals("athlete")) {
                athleteMenu(sc, user.getEmail(), repo);
            } else if (role.equals("coach") || role.equals("trainer")) {
                coachMenu(sc, user.getEmail(), repo);
            }
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private static void register(Scanner sc, DatabaseRepository repo) {
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        if (repo.emailExists(email)) {
            System.out.println("Email taken.");
            return;
        }
        System.out.print("Password: ");
        String password = sc.nextLine().trim();
        System.out.print("Role (Athlete/Trainer): ");
        String role = sc.nextLine().trim();
        System.out.print("Sport Type: ");
        String sport = sc.nextLine().trim();

        repo.registerUser(name, email, password, role, sport);
        System.out.println("Registered successfully!");
    }

    private static void displayUserProfile(String email, DatabaseRepository repo) {
        User u = repo.getUserProfile(email);
        if (u != null) {
            System.out.println("\n--- My Profile ---");
            System.out.println("Name:  " + u.getName());
            System.out.println("Email: " + u.getEmail());
            System.out.println("Role:  " + u.getRole());
            System.out.println("Sport: " + u.getSportType());
            if (u.getRole().equalsIgnoreCase("Athlete")) {
                System.out.println("Wallet Balance: PHP " + repo.getUserBalance(email));
            }
            System.out.println("------------------");
        }
    }

    private static void athleteMenu(Scanner sc, String email, DatabaseRepository repo) {
        BookTrainingSession booker = new BookTrainingSession(repo);
        CancelReservation canceler = new CancelReservation(repo);
        while (true) {
            System.out.println("\n--- Athlete Menu ---");
            System.out.println("1. View Profile\n2. Book Session\n3. Top-up\n4. My Reservations\n5. Cancel\n6. Logout");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            if (c.equals("1")) displayUserProfile(email, repo);
            else if (c.equals("2")) booker.bookTrainingSession(sc, email);
            else if (c.equals("3")) {
                System.out.print("Amount: ");
                double amt = Double.parseDouble(sc.nextLine());
                repo.updateUserBalance(email, repo.getUserBalance(email) + amt);
                System.out.println("Topped up!");
            }
            else if (c.equals("4")) repo.viewMyReservations(email);
            else if (c.equals("5")) canceler.cancelReservation(sc, email);
            else if (c.equals("6")) break;
        }
    }

    private static void coachMenu(Scanner sc, String email, DatabaseRepository repo) {
        while (true) {
            System.out.println("\n--- Trainer Menu ---");
            System.out.println("1. View Profile\n2. Create Session\n3. View Sessions\n4. Income Statement\n5. Logout");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            if (c.equals("1")) displayUserProfile(email, repo);
            else if (c.equals("2")) {
                System.out.print("Label: "); String l = sc.nextLine();
                System.out.print("Date: "); String d = sc.nextLine();
                System.out.print("Price: "); double p = Double.parseDouble(sc.nextLine());
                repo.createTrainingSession(l, d, "Coach", 10, "", p);
                System.out.println("Created!");
            }
            else if (c.equals("3")) repo.viewAllTrainingSessions();
            else if (c.equals("4")) repo.showIncomeStatement();
            else if (c.equals("5")) break;
        }
    }
}