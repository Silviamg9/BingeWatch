package com.example.bingewatch.vista;

import android.media.AudioManager;
import android.media.ToneGenerator;
public class Sonido {

    public static void reproducirClic() {
        try {
            ToneGenerator generadorTonos = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
            generadorTonos.startTone(ToneGenerator.TONE_PROP_BEEP, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
