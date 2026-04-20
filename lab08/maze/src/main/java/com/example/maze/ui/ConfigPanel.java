package com.example.maze.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import lombok.Getter;


@Getter
public class ConfigPanel extends HBox {

    private final TextField rowsField;
    private final TextField colsField;
    private final Button drawButton;

    private final ToggleGroup generationMode;
    private final RadioButton randomModeRadio;
    private final RadioButton dfsModeRadio;
    private final Slider densitySlider;

    public ConfigPanel() {
        super(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);

        Label rowsLabel = new Label("Rows:");
        rowsField = new TextField("15");
        rowsField.setPrefColumnCount(4);

        Label colsLabel = new Label("Cols:");
        colsField = new TextField("25");
        colsField.setPrefColumnCount(4);

        drawButton = new Button("Draw Grid");

        Separator sep = new Separator(Orientation.VERTICAL);

        Label modeLabel = new Label("Generator:");
        generationMode = new ToggleGroup();
        randomModeRadio = new RadioButton("Random removal");
        randomModeRadio.setToggleGroup(generationMode);
        dfsModeRadio = new RadioButton("DFS (perfect maze)");
        dfsModeRadio.setToggleGroup(generationMode);
        dfsModeRadio.setSelected(true);

        Label densityLabel = new Label("Removal p:");
        densitySlider = new Slider(0.0, 1.0, 0.35);
        densitySlider.setShowTickLabels(true);
        densitySlider.setShowTickMarks(true);
        densitySlider.setMajorTickUnit(0.25);
        densitySlider.setMinorTickCount(4);
        densitySlider.setPrefWidth(180);
        densitySlider.disableProperty().bind(randomModeRadio.selectedProperty().not());
        densityLabel.disableProperty().bind(randomModeRadio.selectedProperty().not());

        getChildren().addAll(
                rowsLabel, rowsField,
                colsLabel, colsField,
                drawButton,
                sep,
                modeLabel, randomModeRadio, dfsModeRadio,
                densityLabel, densitySlider
        );
    }

    public boolean isRandomModeSelected() {
        return randomModeRadio.isSelected();
    }

    public double getRemovalProbability() {
        return densitySlider.getValue();
    }

    public int parseRows() {
        return Integer.parseInt(rowsField.getText().trim());
    }

    public int parseCols() {
        return Integer.parseInt(colsField.getText().trim());
    }
}
