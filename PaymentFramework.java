public abstract class PaymentFramework {

    protected double amount;
    protected double discount;
    protected final double VAT_RATE = 0.12;

    public PaymentFramework(double amount, double discount) {
        this.amount = amount;
        this.discount = discount;
    }

    protected abstract boolean validatePayment();

    protected double applyVAT(double amount) {
        return amount + (amount * VAT_RATE);
    }

    protected double applyDiscount(double amount) {
        return amount - (amount * (discount / 100));
    }

    protected void finalizeTransaction(double finalAmount) {
        System.out.println("Transaction completed successfully.");
        System.out.println("Final Amount Paid: " + finalAmount);
    }

    public void processInvoice() {
        if (!validatePayment()) {
            System.out.println("Payment validation failed.");
            return;
        }

        double total = amount;

        total = applyVAT(total);
        total = applyDiscount(total);

        finalizeTransaction(total);
    }
}