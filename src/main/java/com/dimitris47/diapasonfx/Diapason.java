package com.dimitris47.diapasonfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Diapason extends Application {
    Preferences prefs;
    double minWidth;
    double minHeight;

    String[] notes;
    ArrayList<Button> buttons;
    ComboBox<String> freqCombo;
    ComboBox<String> durCombo;
    Slider vol;
    Button stop;
    Button about;

    static ArrayList<Double> currFreq;
    static int mSec;
    static int volume;
    static Thread sound;

    @Override
    public void start(Stage stage) {
        prefs = Preferences.userNodeForPackage(Diapason.class);
        minWidth = 496;
        minHeight = 192;

        notes = new String[] {"C", "C\u266F/D\u266D", "D", "D\u266F/E\u266D", "E", "F",
                "F\u266F/G\u266D", "G", "G\u266F/A\u266D", "A", "A\u266F/B\u266D", "B"};

        buttons = new ArrayList<>(12);
        for (int i = 0; i < 12; i++)
            buttons.add(i, new Button(notes[i]));
        for (int note = 0; note < buttons.size(); note++) {
            buttons.get(note).setText(notes[note]);
            int finalNote = note;
            buttons.get(note).setOnAction(e -> btnClick(buttons.indexOf(buttons.get(finalNote))));
        }

        currFreq = new ArrayList<>(12);
        ArrayList<String> aFreq = new ArrayList<>(97);
        for (int i = 392; i < 490; i++)
            aFreq.add("a = " + i + "Hz");
        freqCombo = new ComboBox<>();
        freqCombo.getItems().addAll(aFreq);
        freqCombo.setValue("a = 440Hz");
        freqCombo.setOnAction(e -> freqClick());

        ArrayList<String> dur = new ArrayList<>();
        for (int i = 1; i < 11; i++)
            dur.add(i + " sec");
        durCombo = new ComboBox<>();
        durCombo.getItems().addAll(dur);
        durCombo.setValue("1 sec");
        freqCombo.setOnAction(e -> durClick());

        vol = new Slider();
        vol.setValue(50);
        vol.setTooltip(new Tooltip("Volume"));
        vol.setMinHeight(26);
        vol.setOnDragDetected(e -> volChanged());

        stop = new Button("Stop");
        stop.setOnAction(e -> stopPressed());

        HBox freqBox = new HBox();
        freqBox.setSpacing(8);
        freqBox.getChildren().addAll(freqCombo, vol, durCombo, stop);
        freqBox.setAlignment(Pos.TOP_CENTER);

        TilePane tile = new TilePane();
        for (Button button : buttons)
            tile.getChildren().add(button);
        tile.setPrefColumns(6);
        tile.setPrefRows(2);
        tile.setAlignment(Pos.CENTER);

        HBox infoBox = new HBox();
        about = new Button("About");
        about.setOnAction(e -> aboutClicked(stage));
        infoBox.getChildren().add(about);
        infoBox.setAlignment(Pos.BOTTOM_CENTER);

        VBox box = new VBox();
        box.getChildren().addAll(freqBox, tile, infoBox);
        box.setPadding(new Insets(8));
        box.setSpacing(8);
        box.setAlignment(Pos.CENTER);

        for (Button button : buttons) {
            button.setMinSize(64, 16);
            button.minWidthProperty().bind(stage.widthProperty().divide(7));
            button.minHeightProperty().bind(stage.heightProperty().divide(7));
        }
        freqCombo.minWidthProperty().bind(stage.widthProperty().divide(4));
        freqCombo.minHeightProperty().bind(stage.heightProperty().divide(7));
        vol.minWidthProperty().bind(stage.widthProperty().divide(3));
        vol.minHeightProperty().bind(stage.heightProperty().divide(7));
        durCombo.minWidthProperty().bind(stage.widthProperty().divide(5));
        durCombo.minHeightProperty().bind(stage.heightProperty().divide(7));
        stop.minWidthProperty().bind(stage.widthProperty().divide(8));
        stop.minHeightProperty().bind(stage.heightProperty().divide(7));
        about.minWidthProperty().bind(stage.widthProperty().divide(6));
        about.minHeightProperty().bind(stage.heightProperty().divide(7));

        Scene scene = new Scene(box, minWidth, minHeight);
        stage.setScene(scene);
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.setTitle("Diapason");

        restorePrefs(stage);
        stage.setOnCloseRequest(e -> savePrefs(stage));
        stage.show();
    }

    private void savePrefs(Stage stage) {
        final String selectedPitch = "freqIndex";
        String pitch = freqCombo.getValue();
        prefs.put(selectedPitch, pitch);
        final String selectedVolume = "volume";
        String volume = String.valueOf(vol.getValue());
        prefs.put(selectedVolume, volume);
        final String selectedDuration = "durIndex";
        String duration = durCombo.getValue();
        prefs.put(selectedDuration, duration);
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
        vol.setValue(Double.parseDouble(savedVolume));
        final String savedDuration = prefs.get("durIndex", "1 sec");
        durCombo.setValue(savedDuration);
        final double savedX = Double.parseDouble(prefs.get("locationX", "128.0"));
        final double savedY = Double.parseDouble(prefs.get("locationY", "64.0"));
        stage.setX(savedX);
        stage.setY(savedY);
        final double savedWidth = Double.parseDouble(prefs.get("width", String.valueOf(minWidth)));
        final double savedHeight = Double.parseDouble(prefs.get("height", String.valueOf(minHeight)));
        stage.setWidth(savedWidth);
        stage.setHeight(savedHeight);
    }

    public void btnClick(int currButton) {
        freqClick();
        durClick();
        volChanged();
        sound = new Thread(new Tone(), "Sound");
        Tone.freq = currFreq.get(currButton);
        Tone.ms = mSec;
        Tone.volume = volume;
        sound.start();
    }

    public void freqClick() {
        String[] item = freqCombo.getValue().split("a = ");
        String[] stripped = item[1].split("Hz");
        int selection = Integer.parseInt(stripped[0]);
        Frequency f = new Frequency(selection);
        currFreq = f.currFreq;
    }

    public void durClick() {
        String[] item = durCombo.getValue().split(" sec");
        int selection = Integer.parseInt(item[0]);
        mSec = selection * 1000;
    }

    public void volChanged() {
        volume = (int) vol.getValue();
    }

    public void stopPressed() {
        while (!sound.isInterrupted()) {
            for (int i = 0; i < mSec * 1000; i++)
                sound.interrupt();
            if (sound.isInterrupted())
                return;
        }
    }

    public void aboutClicked(Stage stage) {
        String info = """
                Program created by Dimitris Psathas

                Written in Java, utilizing the JavaFX toolkit

                Published under the GPLv3 License

                \u00A9 2021 Dimitris Psathas""";
        Dialog<String> infoDialog = new Dialog<>();
        infoDialog.setTitle("Program info");
        ButtonType type = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        infoDialog.setContentText(info);
        infoDialog.getDialogPane().getButtonTypes().add(type);
        infoDialog.initOwner(stage);
        infoDialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
