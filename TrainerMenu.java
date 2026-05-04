import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TrainerMenu {
    private final DatabaseRepository repo;
    private final String trainerEmail;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public TrainerMenu(DatabaseRepository repo, String trainerEmail) {
        this.repo = repo;
        this.trainerEmail = trainerEmail;
    }

    public void showMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Trainer Menu ---");
            System.out.println("1. Create Session");
            System.out.println("2. View All Sessions");
            System.out.println("3. View Income Statement");
            System.out.println("4. Logout");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();
            if (choice.equals("1")) createSession(sc);
            else if (choice.equals("2")) repo.viewAllTrainingSessions();
            else if (choice.equals("3")) repo.showIncomeStatement();
            else if (choice.equals("4")) return;
        }
    }

    private void createSession(Scanner sc) {
        System.out.println("\n[Session Creator Guide]");
        System.out.print("Title (e.g., Muay Thai Basics): ");
        String label = sc.nextLine().trim();

        String dateStr;
        while (true) {
            System.out.print("Date (MM-DD-YYYY, e.g., 12-31-2027): ");
            dateStr = sc.nextLine().trim();
            try {
                LocalDate inputDate = LocalDate.parse(dateStr, formatter);
                if (inputDate.isBefore(LocalDate.now())) {
                    System.out.println("Error: Date cannot be in the past!");
                } else { break; }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format! Use MM-DD-YYYY.");
            }
        }

        System.out.print("Capacity: ");
        int slots = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Description: ");
        String desc = sc.nextLine().trim();

        System.out.print("Price (Leave blank for 500.0): ");
        String priceIn = sc.nextLine().trim();
        double price = priceIn.isEmpty() ? 500.0 : Double.parseDouble(priceIn);

        repo.createTrainingSession(label, dateStr, trainerEmail, slots, desc, price);
    }
}