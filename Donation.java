
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Donation {

    private final String name;
    private final double amount;
    private final LocalDateTime timestamp;

    // Make a new donation with a name and an amount.
    Donation(String name, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Donation amount cannot be negative. Received: " + amount
            );
        }
        this.name = name;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public Donation(String name, double amount, LocalDateTime timestamp) {
        this.name = name;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Turn this donation into one line for the CSV file.
    // Example: "John,50.0"
    @Override
    public String toString() {
        return name + "," + amount 
        + "," + timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Turn a CSV line like "John,50.0" back into a Donation object.
    public static Donation fromString(String line) {
        String[] parts = line.split(",", 3);  // Split at the first comma
        String name = parts.length > 0 ? parts[0] : "";
        double amount = 0.0;
        if (parts.length > 1 && !parts[1].isBlank()) {
            amount = Double.parseDouble(parts[1]);
        }
        if (parts.length > 2 && !parts[2].isBlank()) {
            LocalDateTime timestamp = LocalDateTime.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return new Donation(name, amount, timestamp);
        }
        
        return new Donation(name, amount);
    }
}