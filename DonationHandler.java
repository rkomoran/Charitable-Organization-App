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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DonationHandler {
    private static final String FILE_NAME = "donations.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Donation> donations = loadDonations();
        
        System.out.println("\n=== Donation Handler - V1.0 ===");

        while (true) {
            System.out.print("\nEnter donor name (or 'quit' to exit): ");
            String name = scanner.nextLine();
            if (name.toLowerCase().equals("quit")) {
                System.out.print("\nClear donations.txt (Y/N): ");
                String response = scanner.nextLine();
                try {
                    if (response.toLowerCase().charAt(0) == 'y') {
                    clearDonationsFile();
                    System.out.println("donations.txt cleared.");
                }
                } catch (Exception e) {
                    System.out.println("donations.txt not cleared.");
                }
                
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

            Donation donation = new Donation(name, amount);
            donations.add(donation);
            saveDonation(donation);

            System.out.printf("\nDonation received from %s of $%.2f!\n", name, amount);
            printSummary(donations);
        }

        System.out.println("\nExiting simulation...");
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


    private static void saveDonation(Donation donation) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(donation.toString() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static List<Donation> loadDonations() {
        List<Donation> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(Donation.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return list;
    }

    public static void clearDonationsFile() {
        try (FileWriter writer = new FileWriter(FILE_NAME, false)) {
            writer.write("");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }
}