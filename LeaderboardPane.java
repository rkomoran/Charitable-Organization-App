/****************************************************************
 * File Name: LeaderboardPane.java
 * Author:  Group 7, University of New Brunswick
 * Date: 18-11-2025
 * Description:
 * Reusable JavaFX component that shows the recent supporters list
 * with a filter text field.
 ****************************************************************/

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LeaderboardPane extends VBox {

    private final TextField filterField;
    private final ListView<String> listView;

    public LeaderboardPane(ObservableList<String> feed) {
        super(8);                     // spacing between children
        setPadding(new Insets(0));    // layout can add its own padding

        Label title = new Label("Recent Supporters");
        title.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));

        filterField = new TextField();
        filterField.setPromptText("Filter supporters...");

        // Wrap the shared feed list in a FilteredList for text search.
        FilteredList<String> filtered = new FilteredList<>(feed, s -> true);
        filterField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal == null ? "" : newVal.trim().toLowerCase();
            if (query.isEmpty()) {
                filtered.setPredicate(s -> true); // Show all
            } else {
                filtered.setPredicate(s -> s.toLowerCase().contains(query));
            }
        });

        listView = new ListView<>(filtered);
        listView.setPrefHeight(200);
        listView.setPlaceholder(new Label("No donations yet."));

        getChildren().addAll(title, filterField, listView);
    }

    public TextField getFilterField() {
        return filterField;
    }

    public ListView<String> getListView() {
        return listView;
    }
}
