import java.util.ArrayList;

public class Frequency {
    static double[] defFreq = {261.626, 277.183, 293.665, 311.127, 329.626, 349.228,
            369.994, 391.995, 415.305, 440.000, 466.164, 493.883};
    ArrayList<Double> currFreq = new ArrayList<>(12);

    public Frequency(int pitch) {
        for (var item : defFreq) {
            assert false;
            currFreq.add(item * pitch / 440);
        }
    }
}