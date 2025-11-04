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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DonationFiler {
    public static void saveDonation(Donation donation, String FILE_NAME) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(donation.toString() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static List<Donation> loadDonations(String FILE_NAME) {
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

    public static void clearDonationsFile(String FILE_NAME) {
        try (FileWriter writer = new FileWriter(FILE_NAME, false)) {
            writer.write("");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }
  
}
