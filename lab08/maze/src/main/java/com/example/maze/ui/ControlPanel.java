package com.example.maze.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import lombok.Getter;

@Getter
public class ControlPanel extends HBox {

    private final Button createButton;
    private final Button resetButton;
    private final Button exitButton;

    public ControlPanel() {
        super(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        createButton = new Button("Create");
        resetButton = new Button("Reset");
        exitButton = new Button("Exit");

        createButton.setMinWidth(90);
        resetButton.setMinWidth(90);
        exitButton.setMinWidth(90);

        getChildren().addAll(createButton, resetButton, exitButton);
    }
}
