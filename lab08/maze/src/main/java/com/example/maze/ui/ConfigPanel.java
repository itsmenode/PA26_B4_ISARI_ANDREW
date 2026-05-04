package com.example.maze.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

    private static final double MIN_SPEED_LOG = 0.0;
    private static final double MAX_SPEED_LOG = 4.0;

    private final TextField rowsField;
    private final TextField colsField;
    private final Button drawButton;

    private final ToggleGroup generationMode;
    private final RadioButton randomModeRadio;
    private final RadioButton dfsModeRadio;
    private final Slider densitySlider;

    private final Slider speedSlider;
    private final Label speedValueLabel;
    private final CheckBox instantCheckBox;

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

        Separator sep1 = new Separator(Orientation.VERTICAL);

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
        densitySlider.setPrefWidth(160);
        densitySlider.disableProperty().bind(randomModeRadio.selectedProperty().not());
        densityLabel.disableProperty().bind(randomModeRadio.selectedProperty().not());

        Separator sep2 = new Separator(Orientation.VERTICAL);

        Label speedLabel = new Label("Speed:");
        speedSlider = new Slider(MIN_SPEED_LOG, MAX_SPEED_LOG, 1.5);
        speedSlider.setPrefWidth(180);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1.0);

        speedValueLabel = new Label();
        speedValueLabel.setMinWidth(70);
        updateSpeedLabel();
        speedSlider.valueProperty().addListener((obs, oldV, newV) -> updateSpeedLabel());

        instantCheckBox = new CheckBox("Instant");
        speedSlider.disableProperty().bind(instantCheckBox.selectedProperty());
        speedLabel.disableProperty().bind(instantCheckBox.selectedProperty());
        speedValueLabel.disableProperty().bind(instantCheckBox.selectedProperty());

        getChildren().addAll(
                rowsLabel, rowsField,
                colsLabel, colsField,
                drawButton,
                sep1,
                modeLabel, randomModeRadio, dfsModeRadio,
                densityLabel, densitySlider,
                sep2,
                speedLabel, speedSlider, speedValueLabel,
                instantCheckBox
        );
    }

    public boolean isRandomModeSelected() {
        return randomModeRadio.isSelected();
    }

    public double getRemovalProbability() {
        return densitySlider.getValue();
    }

    public boolean isInstantSelected() {
        return instantCheckBox.isSelected();
    }

    public double getStepsPerSecond() {
        return Math.pow(10.0, speedSlider.getValue());
    }

    public int parseRows() {
        return Integer.parseInt(rowsField.getText().trim());
    }

    public int parseCols() {
        return Integer.parseInt(colsField.getText().trim());
    }

    private void updateSpeedLabel() {
        double sps = Math.pow(10.0, speedSlider.getValue());
        if (sps >= 1000) {
            speedValueLabel.setText(String.format("%.1fk st/s", sps / 1000.0));
        } else if (sps >= 100) {
            speedValueLabel.setText(String.format("%.0f st/s", sps));
        } else {
            speedValueLabel.setText(String.format("%.1f st/s", sps));
        }
    }
}
