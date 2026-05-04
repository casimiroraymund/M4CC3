import java.util.UUID;

public class SessionPayment extends PaymentFramework {
        private final String transactionId;

    public SessionPayment(double amount, double discount) {
        super(amount, discount);
        this.transactionId = UUID.randomUUID().toString();
    }

    @Override
    protected boolean validatePayment() {
        return this.amount > 0;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getFinalPrice() {
        double total = applyVAT(this.amount);
        return applyDiscount(total);
    }
}