import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    String[] pitches = {"C", "C\u266F/D\u266D", "D", "D\u266F/E\u266D", "E", "F",
            "F\u266F/G\u266D", "G", "G\u266F/A\u266D", "A", "A\u266F/B\u266D", "B"};
    ArrayList<Button> buttons = new ArrayList<>(12);
    int selButton;
    ComboBox<String> combo;

    static ArrayList<Double> currFreq;
    static int mSec;
    static int volume;
    static Thread sound;

    @Override
    public void start(Stage stage) {
        for (int i = 0; i < 12; i++)
            buttons.add(i, new Button(pitches[i]));
        for (int selButton = 0; selButton < buttons.size(); selButton++) {
            buttons.get(selButton).setText(pitches[selButton]);
            buttons.get(selButton).setOnAction(e -> btnClick());
        }

        currFreq = new ArrayList<>(12);
        combo = new ComboBox<>();

        HBox freqBox = new HBox();
        freqBox.getChildren().add(combo);
        freqBox.setAlignment(Pos.TOP_CENTER);

        TilePane tile = new TilePane();
        for (Button button : buttons)
            tile.getChildren().add(button);
        tile.setPrefColumns(6);
        tile.setPrefRows(2);
        tile.setAlignment(Pos.CENTER);
        for (Button button : buttons)
            button.setMinSize(64, 16);

        VBox box = new VBox();
        box.getChildren().add(freqBox);
        box.getChildren().add(tile);
        box.setAlignment(Pos.CENTER);

        Scene scene = new Scene(box, 420, 128);
        stage.setScene(scene);
        stage.setMinHeight(160);
        stage.setMinWidth(420);
        stage.setTitle("DiapasonFX");
        stage.show();
    }

    public void btnClick() {
        sound = new Thread(new Tone(), "Sound");
        Tone.freq = currFreq.get(selButton);
        Tone.ms = mSec;
        Tone.volume = volume;
        sound.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
