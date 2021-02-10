import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone implements Runnable {
    public static double freq;
    public static int ms;
    public static int volume;

    @Override
    public void run() {
        Thread t = Thread.currentThread();
        byte[] buf = new byte[1];
        int sampleRate = 44100;
        AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
        SourceDataLine sdl = null;
        try {
            sdl = AudioSystem.getSourceDataLine(af);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        try {
            assert sdl != null;
            sdl.open(af);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        sdl.start();
        for (double i = 0; i < ms * sampleRate / 1000.0; i++) {
            double angle = i / (sampleRate / freq) * 2 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * volume);
            sdl.write(buf, 0, 1);
            if (t.isInterrupted()) {
                System.out.println("Interrupted");
                t.interrupt();
                return;
            }
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    }
}