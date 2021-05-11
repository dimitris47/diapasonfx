package com.dimitris47.diapasonfx;

import javafx.application.Platform;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone implements Runnable {
    public static double freq;
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
        for (double i = 0; i < 10.0 * sampleRate; i++) {
            double angle = i / (sampleRate / freq) * 2 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * volume);
            sdl.write(buf, 0, 1);
            double finalI = i;
            Platform.runLater(() -> Diapason.bar.setProgress(finalI / 441000.0));
            if (t.isInterrupted()) {
                t.interrupt();
                return;
            }
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
        for (var button : Diapason.buttons)
            button.setSelected(false);
    }
}
