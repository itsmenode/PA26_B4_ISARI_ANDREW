package com.example.maze;

import com.example.maze.generator.DfsGenerator;
import com.example.maze.generator.MazeGenerator;
import com.example.maze.generator.RandomRemovalGenerator;
import com.example.maze.model.Cell;
import com.example.maze.model.Maze;
import com.example.maze.solver.MazeSolver;
import com.example.maze.ui.ConfigPanel;
import com.example.maze.ui.ControlPanel;
import com.example.maze.ui.MazeCanvas;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.List;

public class MazeApp extends Application {

    private static final int MIN_DIM = 1;
    private static final int MAX_DIM = 100;

    private Maze maze;
    private MazeCanvas canvas;
    private ConfigPanel configPanel;
    private ControlPanel controlPanel;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
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
        controlPanel.getValidateButton().setOnAction(e -> onValidate());
        controlPanel.getExportPngButton().setOnAction(e -> onExportPng());
        controlPanel.getSaveButton().setOnAction(e -> onSave());
        controlPanel.getLoadButton().setOnAction(e -> onLoad());
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

    private void onValidate() {
        if (maze == null) {
            showError("Draw the grid first.");
            return;
        }
        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();
        if (start == null || end == null) {
            showError("Pick a start cell (Shift-click) and an end cell (Ctrl-click) first.");
            return;
        }
        List<Cell> path = MazeSolver.shortestPath(maze, start, end);
        if (path.isEmpty()) {
            canvas.clearPath();
            showInfo("Maze is NOT traversable from start to end.");
        } else {
            canvas.setPath(path);
            showInfo("Maze is traversable. Path length: " + path.size() + " cells.");
        }
    }

    private void onExportPng() {
        if (maze == null) {
            showError("Nothing to export — draw the grid first.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export maze as PNG");
        chooser.setInitialFileName("maze.png");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG image", "*.png"));
        File file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }
        try {
            WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), null);
            BufferedImage img = SwingFXUtils.fromFXImage(snapshot, null);
            ImageIO.write(img, "png", file);
            showInfo("Saved PNG to " + file.getAbsolutePath());
        } catch (IOException ex) {
            showError("Could not write PNG: " + ex.getMessage());
        }
    }

    private void onSave() {
        if (maze == null) {
            showError("Nothing to save — draw the grid first.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save maze");
        chooser.setInitialFileName("maze.ser");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized maze", "*.ser"));
        File file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file.toPath()))) {
            out.writeObject(maze);
            showInfo("Maze saved to " + file.getAbsolutePath());
        } catch (IOException ex) {
            showError("Could not save maze: " + ex.getMessage());
        }
    }

    private void onLoad() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load maze");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized maze", "*.ser"));
        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
            Object loaded = in.readObject();
            if (!(loaded instanceof Maze loadedMaze)) {
                showError("File does not contain a maze.");
                return;
            }
            maze = loadedMaze;
            canvas.setMaze(maze);
            configPanel.getRowsField().setText(Integer.toString(maze.getRows()));
            configPanel.getColsField().setText(Integer.toString(maze.getCols()));
        } catch (IOException | ClassNotFoundException ex) {
            showError("Could not load maze: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText(null);
        alert.setTitle("Maze Builder");
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setHeaderText(null);
        alert.setTitle("Maze Builder");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
