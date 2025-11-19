/****************************************************************
 * File Name: DonationApp.java
 * Author:  Group 7, University of New Brunswick
 * Date: 18-11-2025
 * Description:
 * GUI Donation App for the Helping Hands Charity.
 *
 * This class now focuses mainly on:
 *  - wiring together JavaFX screens,
 *  - delegating donation logic to DonationManager,
 *  - delegating leaderboard UI to LeaderboardPane.
 *
 * The business rules (goal, total, file storage) live in
 * DonationManager.java, and the leaderboard/filter UI lives in
 * LeaderboardPane.java.
 ****************************************************************/

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class DonationApp extends Application {

    // --- Constants -----------------------------------------------------------
    private static final double GOAL = 5000.0;
    private static final int MAX_FEED_SIZE = 8;

    // Random messages for the leaderboard feed.
    private static final String[] MESSAGES = {
            "%s gave %s. Thank you for your kindness!",
            "%s just donated %s. You’re making a difference.",
            "A big thanks to %s for their generous %s donation!",
            "%s contributed %s to the mission.",
            "A round of applause for %s’s %s gift!"
    };

    // --- Non-UI state -------------------------------------------------------
    // Encapsulates file I/O and total / goal logic.
    private final DonationManager donationManager =
            new DonationManager("donations.csv", GOAL);

    private final NumberFormat money =
            NumberFormat.getCurrencyInstance(Locale.CANADA);

    // Observable list backing the leaderboard text messages.
    private final ObservableList<String> feed =
            FXCollections.observableArrayList();

    private final Random rng = new Random();

    private double currentAmount = 0.0; // For the "your donation" preview bar

    // --- JavaFX fields that need to be reused / updated ---------------------
    private Stage stage;
    private Scene homeScene;
    private Scene donateScene;

    // Donate scene controls
    private TextField nameField;
    private TextField customField;
    private Label yourLabel;
    private Label totalLabel;      // donate scene total label
    private ProgressBar yourBar;
    private ProgressBar totalBar;  // donate scene total bar

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Load existing donations into the feed (latest-first messages).
        loadFeedFromManager();

        homeScene = buildHomeScene();
        donateScene = buildDonateScene();

        stage.setTitle("Helping Hands Charity");
        stage.setScene(homeScene);
        stage.show();
    }

    // ========================================================================
    //  HOME SCENE
    // ========================================================================

    private Scene buildHomeScene() {
        Text title = new Text("Helping Hands Charity");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));

        Label info = new Label(
                "We help Fredericton families with food, shelter, and support.\n" +
                "Your donations make real impact in our community."
        );
        info.setWrapText(true);

        double total = donationManager.getTotal();
        ProgressBar homeBar = new ProgressBar(ratio(total));
        Label homeRaisedLabel;

        if (total >= GOAL) {
            homeRaisedLabel = new Label(
                    "🎉🎉🎉 Goal Reached! Total: " + money.format(total) + " 🎉🎉🎉"
            );
        } else {
            homeRaisedLabel = new Label(
                    "Total raised: " + money.format(total) + " / " + money.format(GOAL)
            );
        }

        Button donateButton = new Button("Donate Now");
        donateButton.setOnAction(e -> stage.setScene(donateScene));

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> handleClearAll());

        HBox actions = new HBox(10, donateButton, clearButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        // Reuse the same feed list, but UI is encapsulated in LeaderboardPane.
        LeaderboardPane leaderboard = new LeaderboardPane(feed);

        VBox layout = new VBox(16,
                title,
                info,
                actions,
                homeBar,
                homeRaisedLabel,
                new Separator(),
                leaderboard
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(layout, 700, 500);
        addHomeShortcuts(scene);
        return scene;
    }

    // ========================================================================
    //  DONATE SCENE
    // ========================================================================

    private Scene buildDonateScene() {
        Text title = new Text("Make a Donation");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Name
        Label nameLabel = new Label("Name:");
        nameField = new TextField();
        nameField.setPromptText("Your name (optional)");

        // Quick amount buttons + description label
        Label quickLabel = new Label("Quick amounts:");
        Label quickDesc = new Label("Pick an amount or enter your own below.");

        Button b25 = quickButton(25, "Buys a week of groceries.", quickDesc);
        Button b50 = quickButton(50, "Funds hygiene kits.", quickDesc);
        Button b100 = quickButton(100, "Supports emergency shelter.", quickDesc);

        HBox quickRow = new HBox(10, b25, b50, b100);
        quickRow.setAlignment(Pos.CENTER_LEFT);

        // Custom amount
        Label customLabel = new Label("Custom amount:");
        customField = new TextField();
        customField.setPromptText("Custom amount (e.g., 37.50)");
        customField.textProperty().addListener((o, oldV, newV) -> {
            currentAmount = parse(newV);
            refreshYourBar();
        });

        // "Your donation" preview bar
        yourBar = new ProgressBar(0);
        yourLabel = new Label("Your donation: " + money.format(0));

        // Total bar for this scene
        totalBar = new ProgressBar(ratio(donationManager.getTotal()));
        totalLabel = new Label(buildTotalLabelText());

        // Buttons row
        Button donateButton = new Button("Donate");
        donateButton.setOnAction(e -> handleMakeDonation());
        donateButton.setDefaultButton(true); // Enter key triggers this

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            homeScene = buildHomeScene();
            stage.setScene(homeScene);
        });

        HBox donateButtons = new HBox(10, donateButton, backButton);

        // Leaderboard UI (same feed list)
        LeaderboardPane leaderboard = new LeaderboardPane(feed);

        VBox layout = new VBox(15,
                title,
                nameLabel,
                nameField,
                quickLabel,
                quickRow,
                quickDesc,
                customLabel,
                customField,
                yourLabel,
                yourBar,
                totalLabel,
                totalBar,
                new Separator(),
                leaderboard,
                new Separator(),
                donateButtons
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(layout, 700, 600);
        addDonateShortcuts(scene);
        return scene;
    }

    // ========================================================================
    //  EVENT HANDLERS / LOGIC
    // ========================================================================

    /**
     * Handle a user clicking the Donate button (or pressing Enter).
     */
    private void handleMakeDonation() {
        double amount = parse(customField.getText());
        if (amount <= 0) {
            new Alert(Alert.AlertType.WARNING,
                    "Please enter a valid amount.").showAndWait();
            return;
        }

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isBlank()) {
            name = "Anonymous";
        }

        boolean goalReachedWithThisDonation =
                donationManager.willReachGoalWith(amount);

        donationManager.addDonation(name, amount);

        // Reset "your donation" preview
        currentAmount = 0.0;
        yourBar.setProgress(0);
        yourLabel.setText("Your donation: " + money.format(0));

        // Update total bar + label on this screen
        totalBar.setProgress(ratio(donationManager.getTotal()));
        totalLabel.setText(buildTotalLabelText());

        // Add to leaderboard feed (most recent at the top)
        addToFeed(name, amount);

        // Thank-you message, and optionally goal reached message.
        String message = "Thank you for donating " + money.format(amount) + "!";
        if (goalReachedWithThisDonation) {
            message += "\n\nWe've reached our goal of " + money.format(GOAL) + "!";
        }

        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();

        // Clear inputs for next use.
        nameField.clear();
        customField.clear();
    }

    /**
     * User clicked Clear on the home screen.
     * This clears file data, resets totals, feed, and rebuilds scenes.
     */
    private void handleClearAll() {
        donationManager.clearAll();
        feed.clear();

        // Rebuild scenes so progress bars / labels reset everywhere.
        homeScene = buildHomeScene();
        donateScene = buildDonateScene();
        stage.setScene(homeScene);
    }

    // ========================================================================
    //  UI HELPERS
    // ========================================================================

    private Button quickButton(int amount, String description, Label descriptionLabel) {
        Button b = new Button("$" + amount);
        b.setOnAction(e -> {
            customField.setText(String.valueOf(amount));
            descriptionLabel.setText(description);
        });
        return b;
    }

    private void addHomeShortcuts(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            // Currently, ESC on home does nothing, but we keep the hook here
            // in case you want to add behavior later.
            if (e.getCode() == KeyCode.ESCAPE) {
                // no-op
            }
        });
    }

    private void addDonateShortcuts(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                // ESC behaves like Back: rebuild home and show it.
                homeScene = buildHomeScene();
                stage.setScene(homeScene);
            }

            if (e.isControlDown() && e.getCode() == KeyCode.R) {
                // Ctrl+R clears the custom amount field.
                customField.clear();
            }
        });
    }

    /**
     * Build the text that shows the current total and whether the goal
     * was reached. Used in the donate scene total label.
     */
    private String buildTotalLabelText() {
        double total = donationManager.getTotal();
        if (total >= GOAL) {
            return "Goal Reached! Total: " + money.format(total);
        }
        return "Total raised: " + money.format(total) + " / " + money.format(GOAL);
    }

    private void loadFeedFromManager() {
        for (Donation d : donationManager.getAllDonations()) {
            addToFeed(d.getName(), d.getAmount());
        }
    }

    private void addToFeed(String name, double amount) {
        String msg = String.format(
                MESSAGES[rng.nextInt(MESSAGES.length)],
                name,
                money.format(amount)
        );

        // New entry at the top (most recent first)
        feed.add(0, msg);

        // Keep list size bounded so UI stays compact.
        if (feed.size() > MAX_FEED_SIZE) {
            feed.remove(feed.size() - 1);
        }
    }

    private void refreshYourBar() {
        yourBar.setProgress(ratio(currentAmount));
        yourLabel.setText("Your donation: " + money.format(currentAmount));
    }

    private double ratio(double x) {
        if (GOAL <= 0) return 0.0;
        double r = x / GOAL;
        if (r < 0) return 0.0;
        if (r > 1) return 1.0;
        return r;
    }

    /**
     * Parse a string as a double. Returns 0.0 if parsing fails.
     */
    private double parse(String s) {
        if (s == null) return 0.0;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
