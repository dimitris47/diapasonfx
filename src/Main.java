import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Main extends Application {
    Preferences prefs;

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
        prefs = Preferences.userNodeForPackage(Main.class);

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
        vol.setOnDragDetected(e -> volChanged());

        stop = new Button("Stop");
        stop.setOnAction(e -> stopPressed());

        HBox freqBox = new HBox();
        freqBox.getChildren().add(freqCombo);
        freqBox.getChildren().add(vol);
        freqBox.getChildren().add(durCombo);
        freqBox.getChildren().add(stop);
        freqBox.setAlignment(Pos.TOP_CENTER);

        TilePane tile = new TilePane();
        for (Button button : buttons)
            tile.getChildren().add(button);
        tile.setPrefColumns(6);
        tile.setPrefRows(2);
        tile.setAlignment(Pos.CENTER);
        for (Button button : buttons)
            button.setMinSize(64, 16);

        HBox infoBox = new HBox();
        about = new Button("About");
        about.setOnAction(e -> aboutClicked());
        infoBox.getChildren().add(about);
        infoBox.setAlignment(Pos.BOTTOM_CENTER);

        VBox box = new VBox();
        box.getChildren().add(freqBox);
        box.getChildren().add(tile);
        box.getChildren().add(infoBox);
        box.setAlignment(Pos.CENTER);

        Scene scene = new Scene(box, 420, 128);
        stage.setScene(scene);
        stage.setMinHeight(160);
        stage.setMinWidth(420);
        stage.setTitle("DiapasonFX");
        Image applicationIcon = new Image(getClass().getResourceAsStream("diapason.png"));
        stage.getIcons().add(applicationIcon);

        // RESTORE PREFERENCES
        final String savedFrequency = prefs.get("freqIndex", "a = 440Hz");
        freqCombo.setValue(savedFrequency);

        final String savedDuration = prefs.get("durIndex", "1 sec");
        durCombo.setValue(savedDuration);

        final double savedWidth = Double.parseDouble(prefs.get("width", "480.0"));
        final double savedHeight = Double.parseDouble(prefs.get("height", "192.0"));
        stage.setWidth(savedWidth);
        stage.setHeight(savedHeight);

        // SAVE PREFERENCES
        stage.setOnCloseRequest(e -> {
            final String selectedPitch = "freqIndex";
            String pitch = freqCombo.getValue();
            prefs.put(selectedPitch, pitch);

            final String selectedDuration = "durIndex";
            String duration = durCombo.getValue();
            prefs.put(selectedDuration, duration);

            final String stWidth = "width";
            String currWidth = String.valueOf(stage.getWidth());
            prefs.put(stWidth, currWidth);
            final String stHeight = "height";
            String currHeight = String.valueOf(stage.getHeight());
            prefs.put(stHeight, currHeight);
        });

        stage.show();
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
        int signal = 0;
        while (!sound.isInterrupted()) {
            sound.interrupt();
            System.out.println("sent signal " + signal);
            if (sound.isInterrupted())
                return;
        }
    }

    public void aboutClicked() {
        String info = """
                Program created by Dimitris Psathas

                Written in Java, utilizing the JavaFX toolkit

                Published under the GPLv3 License

                \u00A9 2021 Dimitris Psathas""";
        Dialog<String> infoDialog = new Dialog<>();
        infoDialog.setTitle("Program info");
        ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        infoDialog.setContentText(info);
        infoDialog.getDialogPane().getButtonTypes().add(type);
        infoDialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
