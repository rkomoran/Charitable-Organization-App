/****************************************************************
 * File Name: DonationApp.java
 * Author:  Group 7, University of New Brunswick
 * Date: 16-11-2025
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
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.*;

public class DonationApp extends Application {

    // --- Storage
    private final DonationFiler store = new DonationFiler("donations.csv");
    private static final double GOAL = 5000.0;
    private final NumberFormat money = NumberFormat.getCurrencyInstance(Locale.CANADA);

    // --- JavaFX pieces
    private Stage stage;
    private Scene homeScene, donateScene;
    private TextField nameField, customField;
    private Label totalLabel, yourLabel, quickDesc;
    private ProgressBar totalBar, yourBar;

    // --- For leaderboard
    private final ObservableList<String> feed = FXCollections.observableArrayList();
    private ListView<String> feedView;
    private final Random rng = new Random();
    private static final String[] MESSAGES = {
            "%s donated %s to support local families.",
            "%s just gave %s — thank you!",
            "%s contributed %s to the mission.",
            "A round of applause for %s’s %s gift!"
    };

    // --- Filtering and sorting
    private FilteredList<String> filteredFeed;
    private boolean newestFirst = true;
    private TextField filterField;

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

        VBox feedBox = makeLeaderboardBox(); // now includes filter + sort

        VBox layout = new VBox(16, title, info, new HBox(10, donate, clear),
                bar, raised, new Separator(), feedBox);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(layout, 700, 500);
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

        HBox quicks = new HBox(10, b25, b50, b100);
        quickDesc = new Label("Pick an amount or enter your own below.");

        // Custom amount entry
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
        back.setOnAction(e -> stage.setScene(homeScene));

        VBox feedBox = makeLeaderboardBox(); // includes filter + sort

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

        layout.setPadding(new Insets(20)

