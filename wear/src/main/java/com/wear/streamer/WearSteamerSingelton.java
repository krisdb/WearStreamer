package com.wear.streamer;

import android.media.MediaPlayer;

public class WearSteamerSingelton {

    private static WearSteamerSingelton wearitems = null;
    public MediaPlayer MediaPlayer;

    public static synchronized WearSteamerSingelton getInstance() {

        if (wearitems == null) {
            wearitems = new WearSteamerSingelton();
        }

        return wearitems;
    }
}
