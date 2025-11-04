/****************************************************************
 * File Name: DonationFiler.java
 * Author:  Group 7, University of New Brunswick
 * Date: 3-11-2025
 * Version: 1.0
 * Description: Donation file handling utility class for saving,
 * loading, and clearing donations from a specified file. 
 * Built from methods originally in DonationHandler.java.
 ****************************************************************
 * Modification History:
 * [29-10-2025] - Original File Developed by Group 7.
 * [3-11-2025] - Refactored from DonationHandler.java by Group 7.
 * 
 ****************************************************************
 * Questions/Comments: Please email Said Obaid at sobaid@unb.ca
 * Copyright 2025. Group 7: Said Obaid, Hannah Sarty, Rinor Komorani, and
 * Mahir Daiyan Mahi; University of New Brunswick
 ****************************************************************/

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class DonationFiler {

    private final String fileName;

    DonationFiler(String fileName) {
        this.fileName = fileName;
        makeFileIfMissing();  // Ensure file exists
    }

    // Create the CSV file if it doesn't already exist.
    private void makeFileIfMissing() {
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                new FileWriter(f, false).close();
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    // Add a single donation to the file.
    public void append(Donation d) {
        try (FileWriter w = new FileWriter(fileName, true)) { // true = append mode
            w.write(d.toString());
            w.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing donation: " + e.getMessage());
        }
    }

    // Read all donations from the file and return them as a list.
    public List<Donation> loadAll() {
        List<Donation> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    list.add(Donation.fromString(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading donations: " + e.getMessage());
        }
        return list;
    }

    // Add up all donations in the file.
    public double sumAll() {
        double total = 0.0;
        for (Donation d : loadAll()) {
            total += d.getAmount();
        }
        return total;
    }
}