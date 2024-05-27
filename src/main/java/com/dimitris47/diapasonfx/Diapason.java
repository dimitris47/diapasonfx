/**
 * Copyright 2021 Dimitris Psathas <dimitrisinbox@gmail.com>
 * <p>
 * This file is part of DiapasonFX.
 * <p>
 * DiapasonFX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License  as  published by  the  Free Software
 * Foundation,  either version 3 of the License,  or (at your option)  any later
 * version.
 * <p>
 * DiapasonFX is distributed in the hope that it will be useful,  but  WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the  GNU General Public License  for more details.
 * <p>
 * You should have received a copy of the  GNU General Public License along with
 * DiapasonFX. If not, see <http://www.gnu.org/licenses/>.
 */


package com.dimitris47.diapasonfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Diapason extends Application {
    Preferences prefs;
    double minWidth;
    double minHeight;

    String[] notes;
    static ArrayList<ToggleButton> buttons;
    ComboBox<String> freqCombo;
    Label volLbl, instructLbl;
    Slider volSlider;
    static ProgressBar bar;
    Button helpBtn, aboutBtn;

    static ArrayList<Double> currFreq;
    static int volume;
    static Thread soundThread;

    @Override
    public void start(Stage stage) {
        prefs = Preferences.userNodeForPackage(Diapason.class);
        minWidth = 504;
        minHeight = 352;

        notes = new String[] {"C", "C♯/D♭", "D", "D♯/E♭", "E", "F",
                "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B"};

        instructLbl = new Label("Click to start/stop sound or right-click to make a short sound");

        buttons = new ArrayList<>(12);
        for (int i=0; i<12; i++) {
            buttons.add(i, new ToggleButton(notes[i]));
        }
        for (int note=0; note<buttons.size(); note++) {
            buttons.get(note).setText(notes[note]);
            int finalNote = note;
            buttons.get(note).setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    buttons.get(finalNote).setSelected(true);
                    btnClick(buttons.indexOf(buttons.get(finalNote)), 0.8);
                }
                else {
                    btnClick(buttons.indexOf(buttons.get(finalNote)), 10.0);
                }
            });
        }

        currFreq = new ArrayList<>(12);
        ArrayList<String> aFreq = new ArrayList<>(97);
        for (int i=392; i<490; i++) {
            aFreq.add("a = " + i + "Hz");
        }
        freqCombo = new ComboBox<>();
        freqCombo.getItems().addAll(aFreq);
        Tooltip tip = new Tooltip("Click or scroll to select your preferred A4 frequency");
        tip.setShowDelay(new Duration(200));
        freqCombo.setTooltip(tip);
        freqCombo.setValue("a = 440Hz");
        freqCombo.setOnAction(e -> freqClick());
        freqCombo.setOnScroll(e -> {
            int deltaY = (int) e.getDeltaY();
            if (deltaY > 0) {
                try {
                    Robot r = new Robot();
                    r.keyPress(KeyEvent.VK_UP);
                    r.keyRelease(KeyEvent.VK_UP);
                } catch (AWTException exc) { exc.printStackTrace(); }
            }
            else if (deltaY < 0) {
                try {
                    Robot r = new Robot();
                    r.keyPress(KeyEvent.VK_DOWN);
                    r.keyRelease(KeyEvent.VK_DOWN);
                } catch (AWTException exc) { exc.printStackTrace(); }
            }
        });

        volLbl = new Label("Volume");
        volLbl.setPadding(new Insets(0, 4, 0, 12));

        volSlider = new Slider();
        volSlider.setValue(50);
        volSlider.setMinHeight(26);
        volSlider.setOnDragDetected(e -> volChanged());

        HBox freqBox = new HBox();
        freqBox.setSpacing(8);
        freqBox.getChildren().addAll(freqCombo, volLbl, volSlider);
        freqBox.setAlignment(Pos.TOP_CENTER);

        TilePane tile = new TilePane();
        tile.setHgap(4);
        tile.setVgap(4);
        for (ToggleButton button : buttons) {
            tile.getChildren().add(button);
        }
        tile.setPrefColumns(6);
        tile.setPrefRows(2);
        tile.setAlignment(Pos.CENTER);

        bar = new ProgressBar();
        bar.setPadding(new Insets(8));
        bar.setProgress(0.0);

        HBox infoBox = new HBox();
        helpBtn = new Button("Help");
        helpBtn.setOnAction(e -> helpClicked(stage));
        aboutBtn = new Button("About");
        aboutBtn.setOnAction(e -> aboutClicked(stage));
        infoBox.getChildren().addAll(helpBtn, aboutBtn);
        infoBox.setSpacing(4);
        infoBox.setAlignment(Pos.BOTTOM_CENTER);

        VBox box = new VBox();
        box.getChildren().addAll(freqBox, instructLbl, tile, bar, infoBox);
        box.setSpacing(12);
        box.setAlignment(Pos.CENTER);

        for (ToggleButton button : buttons) {
            button.setMinSize(64, 16);
            button.minWidthProperty().bind(stage.widthProperty().divide(7));
            button.minHeightProperty().bind(stage.heightProperty().divide(7));
            button.setFont(new Font(16));
        }
        freqCombo.minWidthProperty().bind(stage.widthProperty().divide(6));
        freqCombo.minHeightProperty().bind(stage.heightProperty().divide(9));
        volLbl.minHeightProperty().bind(stage.heightProperty().divide(8));
        volSlider.minWidthProperty().bind(stage.widthProperty().divide(3));
        volSlider.minHeightProperty().bind(stage.heightProperty().divide(8));
        bar.minWidthProperty().bind(stage.widthProperty().divide(2));
        helpBtn.minWidthProperty().bind(stage.widthProperty().divide(7));
        helpBtn.minHeightProperty().bind(stage.heightProperty().divide(9));
        aboutBtn.minWidthProperty().bind(stage.widthProperty().divide(7));
        aboutBtn.minHeightProperty().bind(stage.heightProperty().divide(9));

        Scene scene = new Scene(box, minWidth, minHeight);
        stage.setScene(scene);
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setTitle("Diapason");
        stage.getIcons().add(new Image("diapason.png"));

        restorePrefs(stage);
        stage.setOnCloseRequest(e -> {
            if (soundThread != null) {
                stopSound();
            }
            savePrefs(stage);
        });
        stage.show();
    }

    private void savePrefs(Stage stage) {
        final String selectedPitch = "freqIndex";
        String pitch = freqCombo.getValue();
        prefs.put(selectedPitch, pitch);
        final String selectedVolume = "volume";
        String volume = String.valueOf(volSlider.getValue());
        prefs.put(selectedVolume, volume);
        final String locX = "locationX";
        prefs.put(locX, String.valueOf(stage.getX()));
        final String locY = "locationY";
        prefs.put(locY, String.valueOf(stage.getY()));
        final String stWidth = "width";
        String currWidth = String.valueOf(stage.getWidth());
        prefs.put(stWidth, currWidth);
        final String stHeight = "height";
        String currHeight = String.valueOf(stage.getHeight());
        prefs.put(stHeight, currHeight);
    }

    private void restorePrefs(Stage stage) {
        final String savedFrequency = prefs.get("freqIndex", "a = 440Hz");
        freqCombo.setValue(savedFrequency);
        final String savedVolume = prefs.get("volume", "50.0");
        volSlider.setValue(Double.parseDouble(savedVolume));
        final double savedX = Double.parseDouble(prefs.get("locationX", "128.0"));
        final double savedY = Double.parseDouble(prefs.get("locationY", "64.0"));
        stage.setX(savedX);
        stage.setY(savedY);
        final double savedWidth = Double.parseDouble(prefs.get("width", String.valueOf(minWidth)));
        final double savedHeight = Double.parseDouble(prefs.get("height", String.valueOf(minHeight)));
        stage.setWidth(savedWidth);
        stage.setHeight(savedHeight);
    }

    public void btnClick(int currButton, double sec) {
        if (soundThread != null) {
            stopSound();
        }
        for (int i=0; i<12; i++) {
            if (i != currButton) {
                buttons.get(i).setSelected(false);
            }
        }
        if (buttons.get(currButton).isSelected()) {
            freqClick();
            volChanged();
            soundThread = new Thread(new Tone(), "Sound");
            Tone.sec = sec;
            Tone.freq = currFreq.get(currButton);
            Tone.volume = volume;
            soundThread.start();
        }
    }

    public void stopSound() {
        bar.setProgress(0.0);
        while (!soundThread.isInterrupted()) {
            for (int i=0; i<10_000_000; i++) {
                soundThread.interrupt();
            }
            if (soundThread.isInterrupted()) {
                return;
            }
        }
    }

    public void freqClick() {
        String[] item = freqCombo.getValue().split("a = ");
        String[] stripped = item[1].split("Hz");
        int selection = Integer.parseInt(stripped[0]);
        Frequency f = new Frequency(selection);
        currFreq = f.currFreq;
    }

    public void volChanged() {
        volume = (int) volSlider.getValue();
    }

    private void helpClicked(Stage stage) {
        String info = """
                Choose the frequency you want for A.
                
                Each note button will produce a sound of appropriate frequency, corresponding to the selected A frequency.
                
                Pushing the button again or pushing another note button will stop the sound.
                
                The sound will automatically stop after 10 seconds.
                
                """;
        Alert infoDialog = new Alert(Alert.AlertType.INFORMATION);
        infoDialog.setResizable(true);
        infoDialog.setTitle("How to use");
        infoDialog.setHeaderText("Instructions");
        infoDialog.setContentText(info);
        infoDialog.initOwner(stage);
        infoDialog.showAndWait();
    }

    public void aboutClicked(Stage stage) {
        String info = """
                Program created by Dimitris Psathas

                Written in Java, utilizing the JavaFX toolkit

                Published under the GPLv3 License

                © 2021-2024 Dimitris Psathas
                
                """;
        Alert infoDialog = new Alert(Alert.AlertType.INFORMATION);
        infoDialog.setResizable(true);
        infoDialog.setTitle("Program Info");
        infoDialog.setHeaderText("DiapasonFX");
        infoDialog.setContentText(info);
        infoDialog.initOwner(stage);
        infoDialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
