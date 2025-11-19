/****************************************************************
 * File Name: DonationManager.java
 * Author:  Group 7, University of New Brunswick
 * Date: 18-11-2025
 * Description:
 * Business-logic class that coordinates Donation and DonationFiler.
 * It knows the goal, keeps track of the running total, and provides
 * simple methods for the GUI to call.
 ****************************************************************/

import java.util.List;

public class DonationManager {

    private final DonationFiler store;
    private final double goal;
    private double total;

    /**
     * Create a DonationManager with the given backing file and goal.
     */
    public DonationManager(String fileName, double goal) {
        this.store = new DonationFiler(fileName);
        this.goal = goal;
        this.total = store.sumAll();
    }

    public double getGoal() {
        return goal;
    }

    public double getTotal() {
        return total;
    }

    /**
     * Return all donations currently stored on disk.
     */
    public List<Donation> getAllDonations() {
        return store.loadAll();
    }

    /**
     * Add a new donation (both to the file and to the running total).
     */
    public void addDonation(String name, double amount) {
        store.append(new Donation(name, amount));
        total += amount;
    }

    /**
     * Remove all donations and reset the running total back to zero.
     */
    public void clearAll() {
        store.clearFile();
        total = 0.0;
    }

    /**
     * Has the overall goal been reached or exceeded?
     */
    public boolean hasReachedGoal() {
        return total >= goal;
    }

    /**
     * If we add the given amount, will that cause the goal to be reached
     * for the very first time?
     */
    public boolean willReachGoalWith(double amount) {
        return (total < goal) && (total + amount >= goal);
    }
}
