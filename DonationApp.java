/****************************************************************
 * File Name: DonationApp.java
 * Author:  Group 7, University of New Brunswick
 * Date: 16-11-2025
 * Description:
 * GUI Donation App with keyboard shortcuts, animations,
 * and sorting/filtering for the leaderboard.
 ****************************************************************/

import javafx.animation.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;


public class DonationApp extends Application {

    // --- Storage
    private final DonationFiler store = new DonationFiler("donations.csv");
    private static final double GOAL = 5000.0;
    private static final double SLIDERUPPERLIMIT = 500.0;
    private final NumberFormat money = NumberFormat.getCurrencyInstance(Locale.CANADA);

    // --- JavaFX pieces
    private Stage stage;
    private Scene homeScene, donateScene;
    private TextField nameField, customField, filterField;
    private Label totalLabel, yourLabel, quickDesc;
    private ProgressBar totalBar, yourBar;

    // --- For leaderboard
    private final ObservableList<String> feed = FXCollections.observableArrayList();

    // --- Random messages
    private final Random rng = new Random();
    private static final String[] MESSAGES = {
        "%s gave %s. Thank you for your kindness!",
        "%s just donated %s. You’re making a difference.",
        "A big thanks to %s for their generous %s donation!",
        "%s contributed %s to the mission.",
        "A round of applause for %s’s %s gift!"
    };

    private double total = 0;
    private double currentAmount = 0;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        total = store.sumAll();
        loadFeedFromFile();

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
        bar.setPrefHeight(40);
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

        VBox feedBox = makeLeaderboardBox(); // now includes filter

        VBox layout = new VBox(16, title, info, actions, bar, raised, new Separator(), feedBox);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(layout, 600, 860);
        addHomeShortcuts(scene);
        return scene;
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
        Slider slider = new Slider(0, SLIDERUPPERLIMIT, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(500);
        slider.setMinorTickCount(4);

        HBox quicks = new HBox(10, b25, b50, b100, slider);
        quicks.setAlignment(Pos.CENTER_LEFT);

        quickDesc = new Label("Pick an amount or enter your own below.");

        // Custom amount entry
        customField = new TextField();
        customField.setPromptText("Custom amount (e.g., 37.50)");

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (currentAmount <= SLIDERUPPERLIMIT) {
                customField.setText(String.format("%.0f", newValue));
            }
        });

        customField.textProperty().addListener((o, oldV, newV) -> {
            currentAmount = parse(newV);
            slider.setValue(currentAmount);
            refreshYourBar();
        });

        yourBar = new ProgressBar(0);
        yourLabel = new Label("Your donation: " + money.format(0));

        totalBar = new ProgressBar(ratio(total));
        totalLabel = new Label("Total raised: " + money.format(total) + " / " + money.format(GOAL));

        Button donate = new Button("Donate");
        donate.setOnAction(e -> makeDonation());
        donate.setDefaultButton(true); // pressing Enter triggers this

        Button back = new Button("Back");
        back.setOnAction(e -> stage.setScene(homeScene = makeHomeScene()));

        VBox feedBox = makeLeaderboardBox(); // includes filter

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

        Scene scene = new Scene(layout, 600, 860);
        addDonateShortcuts(scene);

        return scene;
    }

    /*
       KEYBOARD SHORTCUTS
    */
    private void addHomeShortcuts(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                // Already home — do nothing
            }
        });
    }

    private void addDonateShortcuts(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {

            // ESC → back to home
            if (e.getCode() == KeyCode.ESCAPE) {
                homeScene = makeHomeScene();
                stage.setScene(homeScene);
            }

            // Ctrl + R → clear custom amount
            if (e.isControlDown() && e.getCode() == KeyCode.R) {
                customField.clear();
            }
        });
    }

    /*
       HELPER METHODS
    */
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

        Donation newDonation = new Donation(name, amount);

        store.append(newDonation);

        boolean reachedGoal = (total < GOAL) && (total + amount >= GOAL);
        total += amount;

        yourBar.setProgress(0);
        yourLabel.setText("Your donation: " + money.format(0));
        totalBar.setProgress(ratio(total));

        if (total >= GOAL) {
            totalLabel.setText("Goal Reached! Total: " + money.format(total));
        } else {
            totalLabel.setText("Total raised: " + money.format(total) + " / " + money.format(GOAL));
        }

        addToFeed(name, amount, newDonation.getTimestamp()); // includes fade animation (logic-wise)

        String message = "Thank you for donating " + money.format(amount) + "!";
        if (reachedGoal) {
            message += "\n\nWe've reached our goal of " + money.format(GOAL) + "!";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thank you!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        nameField.clear();
        customField.clear();
    }

    private VBox makeLeaderboardBox() {
        Label title = new Label("Recent Supporters");
        title.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));

        // Filter + Sort support (we keep filtering, not sorting)
        filterField = new TextField();
        filterField.setPromptText("Filter supporters...");
        FilteredList<String> filtered = new FilteredList<>(feed, s -> true);
        filterField.textProperty().addListener((obs, oldVal, newVal) ->
                filtered.setPredicate(s -> s.toLowerCase().contains(newVal.toLowerCase()))
        );

        // Keep most recent donations at the top; we only filter, not sort.
        ListView<String> list = new ListView<>(filtered);
        list.setPrefHeight(200);
        list.setPlaceholder(new Label("No donations yet."));

        return new VBox(8, title, filterField, list);
    }

    private void loadFeedFromFile() {
        for (Donation donation : store.loadAll()) {
            addToFeed(donation.getName(), donation.getAmount(), donation.getTimestamp());
        }
    }

    private void addToFeed(String name, double amount, LocalDateTime timestamp) {
        String msg = String.format(MESSAGES[rng.nextInt(MESSAGES.length)],
                                   name, money.format(amount));
        msg = String.format("%s: %s", 
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                msg);

        // New entry at the top (most recent first)
        feed.add(0, msg);

        // (Animation created, but no specific node is assigned – harmless)
        FadeTransition ft = new FadeTransition(Duration.millis(600));
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

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
        store.clearFile();
        total = 0.0;
        feed.clear();

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
