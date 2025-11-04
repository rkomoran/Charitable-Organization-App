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
    private String name;
    private double amount;

    Donation(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getDonorName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }
    
    @Override
    public String toString() {
        return name + "," + amount;
    }

    public static Donation fromString(String line) {
        String[] parts = line.split(",");
        return new Donation(parts[0], Double.parseDouble(parts[1]));
    }
}
