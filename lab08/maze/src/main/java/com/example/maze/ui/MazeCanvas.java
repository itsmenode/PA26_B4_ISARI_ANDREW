package com.example.maze.ui;

import com.example.maze.model.Cell;
import com.example.maze.model.Direction;
import com.example.maze.model.Maze;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.util.List;

public class MazeCanvas extends Canvas {

    private Maze maze;
    private List<Cell> path = List.of();
    private Cell activeCell;

    @Setter private Color cellColor = Color.web("#E8F1FA");
    @Setter private Color wallColor = Color.web("#1F3B5B");
    @Setter private Color backgroundColor = Color.web("#FAFAFA");
    @Setter private Color startColor = Color.web("#2E8B57");
    @Setter private Color endColor = Color.web("#C0392B");
    @Setter private Color pathColor = Color.web("#F1C40F");
    @Setter private Color activeColor = Color.web("#E67E22");
    @Setter private double wallThickness = 2.0;
    @Setter private double padding = 12.0;

    private double cellSize;
    private double offsetX;
    private double offsetY;

    public MazeCanvas(double width, double height) {
        super(width, height);
        widthProperty().addListener(obs -> redraw());
        heightProperty().addListener(obs -> redraw());
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
        redraw();
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        this.path = List.of();
        this.activeCell = null;
        redraw();
    }

    public Maze getMaze() {
        return maze;
    }

    public void setPath(List<Cell> path) {
        this.path = (path == null) ? List.of() : path;
        redraw();
    }

    public void clearPath() {
        setPath(List.of());
    }

    public void setActiveCell(Cell cell) {
        this.activeCell = cell;
    }

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

    private void onMouseClicked(MouseEvent e) {
        if (maze == null || cellSize <= 0 || e.getButton() != MouseButton.PRIMARY) {
            return;
        }
        double localX = e.getX() - offsetX;
        double localY = e.getY() - offsetY;
        if (localX < 0 || localY < 0) return;

        int col = (int) (localX / cellSize);
        int row = (int) (localY / cellSize);
        if (row < 0 || row >= maze.getRows() || col < 0 || col >= maze.getCols()) {
            return;
        }
        Cell cell = maze.getCell(row, col);

        if (e.isShiftDown()) {
            maze.setStartCell(cell);
            path = List.of();
            redraw();
            return;
        }
        if (e.isControlDown()) {
            maze.setEndCell(cell);
            path = List.of();
            redraw();
            return;
        }

        double xInCell = localX - col * cellSize;
        double yInCell = localY - row * cellSize;
        Direction side = nearestSide(xInCell, yInCell, cellSize);
        maze.toggleWall(cell, side);
        path = List.of();
        redraw();
    }

    private static Direction nearestSide(double x, double y, double size) {
        double dTop = y;
        double dBottom = size - y;
        double dLeft = x;
        double dRight = size - x;
        double min = Math.min(Math.min(dTop, dBottom), Math.min(dLeft, dRight));
        if (min == dTop) return Direction.TOP;
        if (min == dBottom) return Direction.BOTTOM;
        if (min == dLeft) return Direction.LEFT;
        return Direction.RIGHT;
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
        cellSize = Math.floor(Math.min(availW / maze.getCols(), availH / maze.getRows()));
        if (cellSize < 2) cellSize = 2;

        double gridW = cellSize * maze.getCols();
        double gridH = cellSize * maze.getRows();
        offsetX = (w - gridW) / 2.0;
        offsetY = (h - gridH) / 2.0;

        gc.setFill(cellColor);
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                double x = offsetX + c * cellSize;
                double y = offsetY + r * cellSize;
                gc.fillRect(x, y, cellSize, cellSize);
            }
        }

        if (!path.isEmpty()) {
            gc.setFill(pathColor);
            for (Cell cell : path) {
                double x = offsetX + cell.getCol() * cellSize;
                double y = offsetY + cell.getRow() * cellSize;
                gc.fillRect(x, y, cellSize, cellSize);
            }
        }

        if (activeCell != null) {
            double x = offsetX + activeCell.getCol() * cellSize;
            double y = offsetY + activeCell.getRow() * cellSize;
            gc.setFill(activeColor);
            gc.fillRect(x, y, cellSize, cellSize);
        }

        drawMarker(gc, maze.getStartCell(), startColor);
        drawMarker(gc, maze.getEndCell(), endColor);

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

    private void drawMarker(GraphicsContext gc, Cell cell, Color color) {
        if (cell == null) return;
        double x = offsetX + cell.getCol() * cellSize;
        double y = offsetY + cell.getRow() * cellSize;
        double inset = Math.max(2, cellSize * 0.18);
        gc.setFill(color);
        gc.fillOval(x + inset, y + inset, cellSize - 2 * inset, cellSize - 2 * inset);
    }
}
