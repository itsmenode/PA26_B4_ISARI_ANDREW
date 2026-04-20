package com.example.maze.ui;

import com.example.maze.model.Cell;
import com.example.maze.model.Maze;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Setter;

public class MazeCanvas extends Canvas {

    private Maze maze;

    @Setter private Color cellColor = Color.web("#E8F1FA");
    @Setter private Color wallColor = Color.web("#1F3B5B");
    @Setter private Color backgroundColor = Color.web("#FAFAFA");
    @Setter private double wallThickness = 2.0;
    @Setter private double padding = 12.0;

    public MazeCanvas(double width, double height) {
        super(width, height);
        widthProperty().addListener(obs -> redraw());
        heightProperty().addListener(obs -> redraw());
        redraw();
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        redraw();
    }

    /** Canvas needs to opt-in to resizing when inside a layout pane. */
    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();

        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, w, h);

        if (maze == null || w <= 0 || h <= 0) {
            return;
        }

        double availW = Math.max(1, w - 2 * padding);
        double availH = Math.max(1, h - 2 * padding);
        double cellSize = Math.floor(Math.min(availW / maze.getCols(), availH / maze.getRows()));
        if (cellSize < 2) cellSize = 2;

        double gridW = cellSize * maze.getCols();
        double gridH = cellSize * maze.getRows();
        double offsetX = (w - gridW) / 2.0;
        double offsetY = (h - gridH) / 2.0;

        gc.setFill(cellColor);
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                double x = offsetX + c * cellSize;
                double y = offsetY + r * cellSize;
                gc.fillRect(x, y, cellSize, cellSize);
            }
        }

        gc.setStroke(wallColor);
        gc.setLineWidth(wallThickness);
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);
                double x = offsetX + c * cellSize;
                double y = offsetY + r * cellSize;
                if (cell.isTopWall())    gc.strokeLine(x, y, x + cellSize, y);
                if (cell.isRightWall())  gc.strokeLine(x + cellSize, y, x + cellSize, y + cellSize);
                if (cell.isBottomWall()) gc.strokeLine(x, y + cellSize, x + cellSize, y + cellSize);
                if (cell.isLeftWall())   gc.strokeLine(x, y, x, y + cellSize);
            }
        }
    }
}
