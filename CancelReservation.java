import java.util.Scanner;

public class CancelReservation {
    private final DatabaseRepository repo;

    public CancelReservation(DatabaseRepository repo) {
        this.repo = repo;
    }

    public void cancelReservation(Scanner sc, String email) {
        repo.viewMyReservations(email);
        System.out.print("\nEnter Reservation ID to cancel: ");
        String resID = sc.nextLine().trim();

        // Prompt for 80% refund confirmation
        System.out.print("Confirm cancellation (Only 80% refund will be issued)? (yes/no): ");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            repo.cancelReservation(resID, email);
        } else {
            System.out.println("Cancellation aborted.");
        }
    }
}