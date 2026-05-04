package com.example.maze.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import lombok.Getter;

@Getter
public class ControlPanel extends HBox {

    private final Button createButton;
    private final Button skipButton;
    private final Button resetButton;
    private final Button validateButton;
    private final Button exportPngButton;
    private final Button saveButton;
    private final Button loadButton;
    private final Button exitButton;

    public ControlPanel() {
        super(8);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        createButton = new Button("Create");
        skipButton = new Button("Skip");
        skipButton.setDisable(true);
        resetButton = new Button("Reset");
        validateButton = new Button("Validate");
        exportPngButton = new Button("Export PNG");
        saveButton = new Button("Save");
        loadButton = new Button("Load");
        exitButton = new Button("Exit");

        for (Button b : new Button[]{
                createButton, skipButton, resetButton, validateButton,
                exportPngButton, saveButton, loadButton, exitButton}) {
            b.setMinWidth(90);
        }

        getChildren().addAll(
                createButton, skipButton, resetButton,
                new Separator(Orientation.VERTICAL),
                validateButton, exportPngButton,
                new Separator(Orientation.VERTICAL),
                saveButton, loadButton,
                new Separator(Orientation.VERTICAL),
                exitButton);
    }
}
