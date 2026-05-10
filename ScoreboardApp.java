// ============================================================
// FILE: ScoreboardApp.java
// Authors: Tucker Vanderwielen
// Date: 5/10/2026
// DESCRIPTION: JavaFX application for managing a climbing competition scoreboard.
// ============================================================



import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardApp extends Application {

    private Stage primaryStage;
    private Scoreboard scoreboard;

    // ---------------------------------------------------------------
    // Colors & styles
    private static final String BG_COLOR = "#09094d";
    private static final String CARD_COLOR = "#374d8a";
    private static final String ACCENT_COLOR = "#4879b4";

    // ---------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Climbing Scoreboard");
        showSetupScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ---------------------------------------------------------------
    // Screen 1 — Setup
    // ---------------------------------------------------------------

    private void showSetupScreen() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Label title = new Label("Climbing Scoreboard");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #e0e0e0;");

        // Competition name
        TextField compNameField = styledTextField("Competition name");

        // Number of problems
        TextField numProblemsField = styledTextField("Number of problems");

        // Number of climbers
        TextField numClimbersField = styledTextField("Number of climbers");

        // Dynamic climber name fields
        VBox nameFields = new VBox(8);
        nameFields.setAlignment(Pos.CENTER);

        numClimbersField.textProperty().addListener((obs, oldVal, newVal) -> {
            nameFields.getChildren().clear();
            try {
                int n = Integer.parseInt(newVal.trim());
                for (int i = 1; i <= n; i++) {
                    TextField tf = styledTextField("Climber " + i + " name");
                    nameFields.getChildren().add(tf);
                }
            } catch (NumberFormatException ignored) {}
        });

        Button startBtn = styledButton("Start Competition");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 13px;");

        startBtn.setOnAction(e -> {
            try {
                String compName = compNameField.getText().trim();
                int numProblems = Integer.parseInt(numProblemsField.getText().trim());
                int numClimbers = Integer.parseInt(numClimbersField.getText().trim());

                if (compName.isEmpty()) { errorLabel.setText("Enter a competition name."); return; }
                if (nameFields.getChildren().size() != numClimbers) { errorLabel.setText("Climber count mismatch."); return; }

                List<String> names = new ArrayList<>();
                for (var node : nameFields.getChildren()) {
                    String n = ((TextField) node).getText().trim();
                    if (n.isEmpty()) { errorLabel.setText("All climber names required."); return; }
                    names.add(n);
                }

                // Build model
                scoreboard = new Scoreboard(compName, numProblems);
                for (String name : names) {
                    Climber c = new Climber(name, numProblems);
                    scoreboard.addClimber(c);
                }

                showCalculatorScreen();

            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter valid numbers.");
            }
        });

        root.getChildren().addAll(title, compNameField, numProblemsField, numClimbersField, nameFields, errorLabel, startBtn);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_COLOR + "; -fx-background-color: " + BG_COLOR);

        primaryStage.setScene(new Scene(scroll, 500, 600));
    }

    // ---------------------------------------------------------------
    // Screen 2 — Calculator
    // ---------------------------------------------------------------

    private VBox sidePanel;
    private VBox leaderboardBox;

    private void showCalculatorScreen() {
        // Root: climber controls on left, leaderboard + side panel on right
        HBox root = new HBox(16);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- Left: climber problem controls ---
        VBox leftPane = new VBox(16);
        leftPane.setPrefWidth(420);

        Label leftTitle = new Label("Problem Entry");
        leftTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e0e0e0;");
        leftPane.getChildren().add(leftTitle);

        for (Climber c : scoreboard.getClimbers()) {
            leftPane.getChildren().add(buildClimberCard(c));
        }

        ScrollPane leftScroll = new ScrollPane(leftPane);
        leftScroll.setFitToWidth(true);
        leftScroll.setPrefWidth(450);
        leftScroll.setStyle("-fx-background: " + BG_COLOR + "; -fx-background-color: " + BG_COLOR);

        // --- Right: leaderboard + side panel ---
        VBox rightPane = new VBox(16);
        rightPane.setPrefWidth(340);

        Label rightTitle = new Label("Leaderboard");
        rightTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e0e0e0;");

        leaderboardBox = new VBox(6);
        refreshLeaderboard();

        sidePanel = new VBox(10);
        sidePanel.setPadding(new Insets(12));
        sidePanel.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");
        Label sidePlaceholder = new Label("Click a climber row to see\nwhat they need for rank 1.");
        sidePlaceholder.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");
        sidePanel.getChildren().add(sidePlaceholder);

        rightPane.getChildren().addAll(rightTitle, leaderboardBox, sidePanel);

        ScrollPane rightScroll = new ScrollPane(rightPane);
        rightScroll.setFitToWidth(true);
        rightScroll.setPrefWidth(360);
        rightScroll.setStyle("-fx-background: " + BG_COLOR + "; -fx-background-color: " + BG_COLOR);
        root.getChildren().addAll(leftScroll, rightScroll);

        primaryStage.setScene(new Scene(root, 860, 640));
        primaryStage.setTitle(scoreboard.getName());
    }

    // ---------------------------------------------------------------
    // Climber card — problem buttons
    // ---------------------------------------------------------------

    private VBox buildClimberCard(Climber climber) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8;");

        Label nameLabel = new Label(climber.getName());
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #e0e0e0;");

        Label scoreLabel = new Label("Score: 0.0");
        scoreLabel.setStyle("-fx-text-fill: #a0c4ff; -fx-font-size: 13px;");

        card.getChildren().addAll(nameLabel, scoreLabel);

        int numProblems = scoreboard.getNumProblems();
        for (int i = 0; i < numProblems; i++) {
            final int idx = i;
            Problem p = climber.getProblem(i);

            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);

            Label probLabel = new Label("P" + (i + 1));
            probLabel.setMinWidth(28);
            probLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 13px;");

            // Attempt counter
            Label attemptCount = new Label("0");
            attemptCount.setMinWidth(20);
            attemptCount.setAlignment(Pos.CENTER);
            attemptCount.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px;");

            Button minusBtn = smallButton("-");
            Button plusBtn  = smallButton("+");

            minusBtn.setOnAction(e -> {
                int current = p.getAttempts();
                if (current > 0) {
                    rebuildProblem(climber, idx, current - 1, p.isZoned(), p.isTopped());
                    attemptCount.setText(String.valueOf(climber.getProblem(idx).getAttempts()));
                    updateAttemptedState(climber, idx);
                    scoreLabel.setText("Score: " + String.format("%.1f", climber.getScore()));
                    refreshLeaderboard();
                }
            });

            plusBtn.setOnAction(e -> {
                p.addAttempt();
                p.setAttempted(true);
                attemptCount.setText(String.valueOf(p.getAttempts()));
                scoreLabel.setText("Score: " + String.format("%.1f", climber.getScore()));
                refreshLeaderboard();
            });

            // Zone toggle
            ToggleButton zoneBtn = styledToggle("Zone");
            zoneBtn.setOnAction(e -> {
                p.setZoned(zoneBtn.isSelected());
                if (!zoneBtn.isSelected()) p.setTopped(false);
                p.setAttempted(true);
                scoreLabel.setText("Score: " + String.format("%.1f", climber.getScore()));
                refreshLeaderboard();
            });

            // Top toggle
            ToggleButton topBtn = styledToggle("Top");
            topBtn.setOnAction(e -> {
                p.setTopped(topBtn.isSelected());
                if (topBtn.isSelected()) {
                    zoneBtn.setSelected(true);
                    p.setZoned(true);
                }
                p.setAttempted(true);
                scoreLabel.setText("Score: " + String.format("%.1f", climber.getScore()));
                refreshLeaderboard();
            });

            row.getChildren().addAll(probLabel, minusBtn, attemptCount, plusBtn, zoneBtn, topBtn);
            card.getChildren().add(row);
        }

        // Clicking the card updates side panel
        card.setOnMouseClicked(e -> updateSidePanel(climber));
        nameLabel.setOnMouseClicked(e -> updateSidePanel(climber));

        return card;
    }

    // ---------------------------------------------------------------
    // Leaderboard refresh
    // ---------------------------------------------------------------

    private void refreshLeaderboard() {
        leaderboardBox.getChildren().clear();

        HBox header = leaderboardRow("Rank", "Name", "Score", "Pts Left", "Pos Rank", true);
        leaderboardBox.getChildren().add(header);

        ArrayList<Climber> ranked = scoreboard.getRankedClimbers();

        ArrayList<Climber> possibleRanked = new ArrayList<>(ranked);
        possibleRanked.sort((a, b) -> Double.compare(b.getMaxPossibleScore(), a.getMaxPossibleScore()));

        for (int i = 0; i < ranked.size(); i++) {
            Climber c = ranked.get(i);
            int possibleRank = possibleRanked.indexOf(c) + 1;
            HBox row = leaderboardRow(
                String.valueOf(i + 1),
                c.getName(),
                String.format("%.1f", c.getScore()),
                String.format("+%.1f", c.getMaxPossibleScore() - c.getScore()),
                String.valueOf(possibleRank),
                false
            );
            row.setOnMouseClicked(e -> updateSidePanel(c));
            row.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-background-radius: 4; -fx-cursor: hand;");
            leaderboardBox.getChildren().add(row);
        }
    }

    private HBox leaderboardRow(String rank, String name, String score, String ptsLeft, String posRank, boolean isHeader) {
        HBox row = new HBox();
        row.setPadding(new Insets(6, 10, 6, 10));
        row.setAlignment(Pos.CENTER_LEFT);

        String style = isHeader
            ? "-fx-font-weight: bold; -fx-text-fill: #a0c4ff; -fx-font-size: 12px;"
            : "-fx-text-fill: #e0e0e0; -fx-font-size: 13px;";

        Label rankL    = styledCell(rank,    40,  style);
        Label nameL    = styledCell(name,    110, style);
        Label scoreL   = styledCell(score,   55,  style);
        Label ptsL     = styledCell(ptsLeft, 55,  style);
        Label posRankL = styledCell(posRank, 55,  style);

        row.getChildren().addAll(rankL, nameL, scoreL, ptsL, posRankL);
        return row;
    }

    // ---------------------------------------------------------------
    // Side panel
    // ---------------------------------------------------------------

    private void updateSidePanel(Climber climber) {
        sidePanel.getChildren().clear();

        Label nameLabel = new Label(climber.getName());
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #e0e0e0;");

        Label rankLabel = new Label("Current rank: " + scoreboard.getRank(climber));
        rankLabel.setStyle("-fx-text-fill: #a0c4ff; -fx-font-size: 13px;");

        String needed = scoreboard.getNeededString(climber);
        Label neededLabel = new Label(needed);
        neededLabel.setWrapText(true);
        neededLabel.setStyle("-fx-text-fill: #90ee90; -fx-font-size: 13px;");

        sidePanel.getChildren().addAll(nameLabel, rankLabel, new Separator(), neededLabel);
    }

    // ---------------------------------------------------------------
    // Problem rebuild helper (for decrementing attempts)
    // ---------------------------------------------------------------

    private void rebuildProblem(Climber climber, int idx, int attempts, boolean zoned, boolean topped) {
        climber.getProblem(idx).reset(attempts, zoned, topped);
    }

    private void updateAttemptedState(Climber climber, int idx) {
        Problem p = climber.getProblem(idx);
        if (p.getAttempts() == 0 && !p.isZoned() && !p.isTopped()) {
            p.setAttempted(false);
        }
    }

    // ---------------------------------------------------------------
    // Style helpers
    // ---------------------------------------------------------------

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(320);
        tf.setStyle("-fx-background-color: #16213e; -fx-text-fill: #e0e0e0; "
            + "-fx-prompt-text-fill: #666; -fx-border-color: #0f3460; "
            + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 14px; -fx-padding: 8;");
        return tf;
    }

    private Button styledButton(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: white; "
            + "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 6; -fx-cursor: hand;");
        return b;
    }

    private Button smallButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(28);
        b.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: #e0e0e0; "
            + "-fx-font-size: 13px; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 2 6;");
        return b;
    }

    private ToggleButton styledToggle(String text) {
        ToggleButton tb = new ToggleButton(text);
        tb.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: #e0e0e0; "
            + "-fx-font-size: 12px; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 3 10;");
        tb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                String color = text.equals("Top") ? "#217e73" : "#2a9d8f";
                tb.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                    + "-fx-font-size: 12px; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 3 10;");
            } else {
                tb.setStyle("-fx-background-color: #0f3460; -fx-text-fill: #e0e0e0; "
                    + "-fx-font-size: 12px; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 3 10;");
            }
        });
        return tb;
    }

    private Label styledCell(String text, double width, String style) {
        Label l = new Label(text);
        l.setMinWidth(width);
        l.setMaxWidth(width);
        l.setStyle(style);
        return l;
    }
}