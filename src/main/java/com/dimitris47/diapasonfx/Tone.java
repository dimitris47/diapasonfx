/**
 * Copyright 2021-2024 Dimitris Psathas <dimitrisinbox@gmail.com>
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

import javafx.application.Platform;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone implements Runnable {
    public static double freq;
    public static int volume;
    public static double sec;

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
        for (double i=0; i<sec*sampleRate; i++) {
            double angle = i / (sampleRate / freq) * 2 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * volume);
            sdl.write(buf, 0, 1);
            double finalI = i;
            Platform.runLater(() -> Diapason.bar.setProgress(finalI / 44100.0 / sec));
            if (t.isInterrupted()) {
                t.interrupt();
                return;
            }
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
        for (var button : Diapason.buttons) {
            button.setSelected(false);
        }
    }
}
