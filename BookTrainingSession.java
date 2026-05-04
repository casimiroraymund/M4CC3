import java.util.Scanner;

public class BookTrainingSession {
    private final DatabaseRepository repo;

    public BookTrainingSession(DatabaseRepository repo) {
        this.repo = repo;
    }

    public void bookTrainingSession(Scanner sc, String athleteEmail) {
        repo.viewAllTrainingSessions();
        System.out.print("\nEnter Session ID to book: ");
        int sessionID = Integer.parseInt(sc.nextLine().trim());

        double basePrice = repo.getSessionPrice(sessionID);
        double currentBalance = repo.getUserBalance(athleteEmail);

        // Handle empty discount input
        System.out.print("Discount % (Leave blank for 0%): ");
        String discIn = sc.nextLine().trim();
        double disc = discIn.isEmpty() ? 0.0 : Double.parseDouble(discIn);

        SessionPayment payment = new SessionPayment(basePrice, disc);
        double finalPrice = payment.getFinalPrice();

        // Wallet Balance Check
        if (currentBalance < finalPrice) {
            System.out.println("Insufficient funds! Balance: PHP " + currentBalance + " | Needed: PHP " + finalPrice);
            return;
        }

        // Refund Warning Confirmation
        System.out.println("\nWARNING: Cancellation yields only an 80% refund.");
        System.out.print("Confirm booking for PHP " + finalPrice + "? (yes/no): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Booking aborted.");
            return;
        }

        if (repo.bookTrainingSession(sessionID, athleteEmail)) {
            // Deduct from wallet
            repo.updateUserBalance(athleteEmail, currentBalance - finalPrice);
            repo.recordPayment(payment.getTransactionId(), athleteEmail, basePrice, finalPrice);
            System.out.println("Booked! New Balance: PHP " + (currentBalance - finalPrice));
        }
    }
}