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
 * [3-11-2025] - Reworked GUI Implementation Added.
 * [4-11-2025] - Improved Donation handling and Processing Logic.
 *             - Made donation button default for enter key.
 * 
 ****************************************************************
 * Questions/Comments: Please email Said Obaid at sobaid@unb.ca
 * Copyright 2025. Group 7: Said Obaid, Hannah Sarty, Rinor Komorani, and
 * Mahir Daiyan Mahi; University of New Brunswick
 ****************************************************************/

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.text.NumberFormat;
import java.util.*;

public class DonationApp extends Application {

    // --- Storage
    private final DonationFiler store = new DonationFiler("donations.csv");
    private static final double GOAL = 5000.0; // goal for progress bar
    private final NumberFormat money = NumberFormat.getCurrencyInstance(Locale.CANADA);

    // --- JavaFX pieces
    private Stage stage;
    private Scene homeScene, donateScene;
    private TextField nameField, customField;
    private Label totalLabel, yourLabel, quickDesc;
    private ProgressBar totalBar, yourBar;

    // --- For leaderboard
    private final ObservableList<String> feed = FXCollections.observableArrayList();
    private final Random rng = new Random();
    private static final String[] MESSAGES = {
        "%s donated %s to support local families.",
        "%s just gave %s — thank you!",
        "%s contributed %s to the mission.",
        "A round of applause for %s’s %s gift!"
    };

    private double total = 0;
    private double currentAmount = 0;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        total = store.sumAll();       // start with total from CSV
        loadFeedFromFile();           // fill the leaderboard

        homeScene = makeHomeScene();
        donateScene = makeDonateScene();

        stage.setTitle("Helping Hands Charity");
        stage.setScene(homeScene);
        stage.show();
    }

    /* =========================
       HOME SCREEN
       ========================= */
    private Scene makeHomeScene() {
        Text title = new Text("Helping Hands Charity");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));

        Label info = new Label(
            "We help Fredericton families with food, shelter, and support.\n" +
            "Your donations make real impact in our community."
        );
        info.setWrapText(true);

        ProgressBar bar = new ProgressBar(ratio(total));
        Label raised;
        if (total >= GOAL) {
            raised = new Label("🎉🎉🎉 Goal Reached! Total: " + money.format(total) + " 🎉🎉🎉");
        } else {
            raised = new Label("Total raised: " + money.format(total) + " / " + money.format(GOAL));
        }

        Button donate = new Button("Donate Now");
        donate.setOnAction(e -> stage.setScene(donateScene));

        Button clear = new Button("Clear");
        clear.setOnAction(e -> clearAll());

        HBox actions = new HBox(10, donate, clear);

        VBox feedBox = makeLeaderboardBox();

        VBox layout = new VBox(16, title, info, actions, bar, raised, new Separator(), feedBox);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        return new Scene(layout, 700, 500);
    }

    /* =========================
       DONATE SCREEN
       ========================= */
    private Scene makeDonateScene() {
        Text title = new Text("Make a Donation");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        nameField = new TextField();
        nameField.setPromptText("Your name (optional)");

        // Quick amount buttons
        Button b25 = quickButton(25, "Buys a week of groceries.");
        Button b50 = quickButton(50, "Funds hygiene kits.");
        Button b100 = quickButton(100, "Supports emergency shelter.");
        HBox quicks = new HBox(10, b25, b50, b100);
        quickDesc = new Label("Pick an amount or enter your own below.");

        // Custom amount
        customField = new TextField();
        customField.setPromptText("Custom amount (e.g., 37.50)");
        customField.textProperty().addListener((o, oldV, newV) -> {
            currentAmount = parse(newV);
            refreshYourBar();
        });

        yourBar = new ProgressBar(0);
        yourLabel = new Label("Your donation: " + money.format(0));

        totalBar = new ProgressBar(ratio(total));
        totalLabel = new Label("Total raised: " + money.format(total) + " / " + money.format(GOAL));

        Button donate = new Button("Donate");
        donate.setOnAction(e -> makeDonation());
        donate.setDefaultButton(true);

        Button back = new Button("Back");
        back.setOnAction(e -> stage.setScene(homeScene = makeHomeScene()));

        VBox feedBox = makeLeaderboardBox();

        VBox layout = new VBox(15, title,
                new Label("Name:"), nameField,
                new Label("Quick amounts:"), quicks,
                quickDesc,
                new Label("Custom amount:"), customField,
                yourLabel, yourBar,
                totalLabel, totalBar,
                new Separator(), feedBox,
                new Separator(), new HBox(10, donate, back)
        );
        layout.setPadding(new Insets(20));

        return new Scene(layout, 700, 600);
    }

    /* =========================
       HELPER METHODS
       ========================= */
    private Button quickButton(int amount, String desc) {
        Button b = new Button("$" + amount);
        b.setOnAction(e -> {
            customField.setText(String.valueOf(amount));
            quickDesc.setText(desc);
        });
        return b;
    }

    private void makeDonation() {
        double amount = parse(customField.getText());
        if (amount <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid amount.").showAndWait();
            return;
        }

        String name = nameField.getText().trim();
        if (name.isBlank()) name = "Anonymous";

        store.append(new Donation(name, amount));

        // Check if goal was just reached
        boolean reachedGoal = (total < GOAL) && (total + amount >= GOAL);
        total += amount;

        // Update UI
        yourBar.setProgress(0);
        yourLabel.setText("Your donation: " + money.format(0));
        totalBar.setProgress(ratio(total));
        // Update the label text based on whether goal is reached
        if (total >= GOAL) {
            totalLabel.setText("🎉 Goal Reached! Total: " + money.format(total) + " 🎉");
        } else {
            totalLabel.setText("Total raised: " + money.format(total) + " / " + money.format(GOAL));
        }

        addToFeed(name, amount);

        // Thank the donor
        String message = "Thank you for donating " + money.format(amount) + "!";

        // Add goal reached message if applicable
        if (reachedGoal) {
            message += "\n\nWe've reached our goal of " + money.format(GOAL) + "!";
        }

        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();

        // Reset fields
        nameField.clear();
        customField.clear();
    }

    private VBox makeLeaderboardBox() {
        Label title = new Label("Recent Supporters");
        title.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));

        ListView<String> list = new ListView<>(feed);
        list.setPrefHeight(200);
        list.setPlaceholder(new Label("No donations yet."));

        return new VBox(8, title, list);
    }

    private void loadFeedFromFile() {
        for (Donation d : store.loadAll()) {
            addToFeed(d.getName(), d.getAmount());
        }
    }

    private void addToFeed(String name, double amount) {
        String msg = String.format(MESSAGES[rng.nextInt(MESSAGES.length)],
                                   name, money.format(amount));
        feed.add(0, msg);
        if (feed.size() > 8) feed.remove(feed.size() - 1); // keep short
    }

    private void refreshYourBar() {
        yourBar.setProgress(ratio(currentAmount));
        yourLabel.setText("Your donation: " + money.format(currentAmount));
    }

    private double ratio(double x) {
        return Math.min(1.0, Math.max(0.0, x / GOAL));
    }

    private void clearAll() {
        // wipe CSV
        store.clearFile();

        // reset in-memory state
        total = 0.0;
        feed.clear();

        // rebuild the Home scene so progress/labels refresh
        homeScene = makeHomeScene();
        stage.setScene(homeScene);
    }

    private double parse(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return 0.0; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}