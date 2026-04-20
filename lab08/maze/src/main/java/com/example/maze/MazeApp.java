package com.example.maze;

import com.example.maze.generator.DfsGenerator;
import com.example.maze.generator.MazeGenerator;
import com.example.maze.generator.RandomRemovalGenerator;
import com.example.maze.model.Maze;
import com.example.maze.ui.ConfigPanel;
import com.example.maze.ui.ControlPanel;
import com.example.maze.ui.MazeCanvas;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MazeApp extends Application {

    private static final int MIN_DIM = 1;
    private static final int MAX_DIM = 100;

    private Maze maze;
    private MazeCanvas canvas;
    private ConfigPanel configPanel;
    private ControlPanel controlPanel;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        configPanel = new ConfigPanel();
        controlPanel = new ControlPanel();

        Pane canvasHolder = new Pane();
        canvasHolder.setPadding(new Insets(4));
        canvas = new MazeCanvas(800, 560);
        canvasHolder.getChildren().add(canvas);

        canvas.widthProperty().bind(canvasHolder.widthProperty());
        canvas.heightProperty().bind(canvasHolder.heightProperty());

        root.setTop(configPanel);
        root.setCenter(canvasHolder);
        root.setBottom(controlPanel);

        wireHandlers();

        Scene scene = new Scene(root, 960, 720);
        stage.setTitle("Maze Builder");
        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(540);
        stage.show();
    }

    private void wireHandlers() {
        configPanel.getDrawButton().setOnAction(e -> onDrawGrid());
        controlPanel.getCreateButton().setOnAction(e -> onCreate());
        controlPanel.getResetButton().setOnAction(e -> onReset());
        controlPanel.getExitButton().setOnAction(e -> Platform.exit());
    }

    private void onDrawGrid() {
        int rows, cols;
        try {
            rows = configPanel.parseRows();
            cols = configPanel.parseCols();
        } catch (NumberFormatException ex) {
            showError("Rows and columns must be integers.");
            return;
        }
        if (rows < MIN_DIM || rows > MAX_DIM || cols < MIN_DIM || cols > MAX_DIM) {
            showError("Rows and columns must be between " + MIN_DIM + " and " + MAX_DIM + ".");
            return;
        }
        maze = new Maze(rows, cols);
        canvas.setMaze(maze);
    }

    private void onCreate() {
        if (maze == null) {
            showError("Draw the grid first.");
            return;
        }
        MazeGenerator generator = configPanel.isRandomModeSelected()
                ? new RandomRemovalGenerator(configPanel.getRemovalProbability())
                : new DfsGenerator();
        generator.generate(maze);
        canvas.redraw();
    }

    private void onReset() {
        if (maze == null) {
            return;
        }
        maze.resetAllWalls();
        canvas.redraw();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText(null);
        alert.setTitle("Maze Builder");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
