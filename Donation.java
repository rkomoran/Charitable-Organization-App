/****************************************************************
 * File Name: Donation.java
 * Author:  Group 7, University of New Brunswick
 * Date: 29-10-2025
 * Version: 2.0
 * Description: A simple donation handler backend simulation in Java.
 *
 ****************************************************************
 * Modification History:
 * [29-10-2025] - Original File Developed by Group 7.
 * [29-10-2025] - Version 2.0: Added fromString method for file loading.
 * [3-11-2025] - Added getter methods for name and amount.
 * 
 ****************************************************************
 * Questions/Comments: Please email Said Obaid at sobaid@unb.ca
 * Copyright 2025. Group 7: Said Obaid, Hannah Sarty, Rinor Komorani, and
 * Mahir Daiyan Mahi; University of New Brunswick
 ****************************************************************/

class Donation {

    private final String name;
    private final double amount;

    // Make a new donation with a name and an amount.
    Donation(String name, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Donation amount cannot be negative. Received: " + amount
            );
        }
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    // Turn this donation into one line for the CSV file.
    // Example: "John,50.0"
    @Override
    public String toString() {
        return name + "," + amount;
    }

    // Turn a CSV line like "John,50.0" back into a Donation object.
    public static Donation fromString(String line) {
        String[] parts = line.split(",", 2);  // Split at the first comma
        String name = parts.length > 0 ? parts[0] : "";
        double amount = 0.0;
        if (parts.length > 1 && !parts[1].isBlank()) {
            amount = Double.parseDouble(parts[1]);
        }
        return new Donation(name, amount);
    }
}