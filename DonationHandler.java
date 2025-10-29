/****************************************************************
 * File Name: DonationHandler.java
 * Author:  Group 7, University of New Brunswick
 * Date: 29-10-2025
 * Version: 1.0
 * Description: A simple donation handler backend simulation in Java.
 *
 ****************************************************************
 * Modification History:
 * [29-10-2025] - Original File Developed by Group 7.
 * 
 ****************************************************************
 * Questions/Comments: Please email Said Obaid at sobaid@unb.ca
 * Copyright 2025. Group 7: Said Obaid, Hannah Sarty, Rinor Komorani, and
 * Mahir Daiyan Mahi; University of New Brunswick
 ****************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DonationHandler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Donation> donations = new ArrayList<>();

        System.out.println("\n=== Donation Handler - V1.0 ===");

        while (true) {
            System.out.print("\nEnter donor name (or 'quit' to exit): ");
            String name = scanner.nextLine();
            if (name.toLowerCase().equals("quit")) {
                break;
            }

            System.out.print("Enter donation amount: ");
            double amount = 0.0;
            try {
                amount = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Try again.");
                continue;
            }

            if (amount <= 0) {
                System.out.println("Amount must be greater than 0. Try again.");
                continue;
            }

            donations.add(new Donation(name, amount));
            System.out.printf("\nDonation received from %s of $%.2f!\n", name, amount);
            printSummary(donations);
        }

        System.out.println("Exiting simulation...");
    }

    public static void printSummary(List<Donation> donations) {
        double total = 0;
        System.out.println("\n--- Donation Summary ---");
        for (Donation donation : donations) {
            System.out.printf("%s donated $%.2f\n", donation.name, donation.amount);
            total += donation.amount;
        }
        System.out.printf("------------------------\nTotal Raised: $%.2f\n", total);
    }
}