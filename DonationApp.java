/****************************************************************
 * File Name: DonationApp.java
 * Author:  Group 7, University of New Brunswick
 * Date: 3-10-2025
 * Version: 1.0
 * Description: 
 * The successor to DonationHandler.java with a GUI using JavaFX.
 * Handles donation entries and displays total donations.
 *
 ****************************************************************
 * Modification History:
 * [29-10-2025] - Original File Developed by Group 7.
 * [3-11-2025] - Reworked GUI Implementation Added by Group 7.
 * 
 ****************************************************************
 * Questions/Comments: Please email Said Obaid at sobaid@unb.ca
 * Copyright 2025. Group 7: Said Obaid, Hannah Sarty, Rinor Komorani, and
 * Mahir Daiyan Mahi; University of New Brunswick
 ****************************************************************/

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.NumberFormat;

import java.io.*;
import java.util.*;

public class DonationApp extends Application {
  private final ListView<String> list = new ListView<>();
  private final TextField nameField = new TextField();
  private final TextField amountField = new TextField();
  private final Label totalLabel = new Label("Total: $0.00");
  private double total = 0.0;
  private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CANADA);

  private static final String FILE_NAME = "donations.csv";
  List<Donation> donations = DonationFiler.loadDonations(FILE_NAME);

  private void initialize() {
    if (!donations.isEmpty()) {
      for (Donation donation : donations) {
        list.getItems().add(donation.getDonorName() + " — " + currency.format(donation.getAmount()));
        total += donation.getAmount();
      }
      totalLabel.setText("Total: " + currency.format(total));
    }
  }
  
  public void start(Stage stage) {
    stage.setTitle("Donations (Simple)");

    nameField.setPromptText("Name");
    amountField.setPromptText("Amount (e.g., 25 or 25.00)");

    Button addButton = new Button("Add");
    
    addButton.setDefaultButton(true); // Connecting Enter key to add donation button; essentially another way to trigger add()
                                      // -> is a lambda expression, first time using it -SO
    addButton.setOnAction(e -> add()); // e for event

    Button clearButton = new Button("Clear");
    
    clearButton.setOnAction(e -> clear());
  
    HBox row = new HBox(8, new Label("Name:"), nameField, new Label("Amount:"), amountField, addButton, clearButton);
    row.setPadding(new Insets(10));

    // Bold the total label, just for emphasis
    totalLabel.setStyle("-fx-font-weight: bold");

    VBox root = new VBox(8, row, list, new Separator(), totalLabel);
    root.setPadding(new Insets(12));

    initialize();

    stage.setScene(new Scene(root, 580, 360));
    stage.show();
  }

  private void add() {
    String name = nameField.getText().trim();
    String amtText = amountField.getText().trim();

    if (name.isEmpty()) { 
      alert("Please enter a name."); 
      return; 
    }
    double amount;
    try {
      amount = Double.parseDouble(amtText);
      if (amount <= 0) { alert("Amount must be positive."); return; }
    } catch (NumberFormatException ex) {
      alert("Amount must be a number (e.g., 25 or 25.00).");
      return;
    }

    Donation donation = new Donation(name, amount);
    donations.add(donation);
    DonationFiler.saveDonation(donation, FILE_NAME);

    list.getItems().add(name + " — " + currency.format(amount));
    total += amount;
    totalLabel.setText("Total: " + currency.format(total));

    nameField.clear();
    amountField.clear();
    nameField.requestFocus();
  }

  private void clear() {
    DonationFiler.clearDonationsFile(FILE_NAME);

    list.getItems().clear();
    donations.clear();
    total = 0.0;
    totalLabel.setText("Total: " + currency.format(total));

    nameField.clear();
    amountField.clear();
    nameField.requestFocus();
  }

  private void alert(String msg) {
    new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
  }

  public static void main(String[] args) { launch(args); }
}