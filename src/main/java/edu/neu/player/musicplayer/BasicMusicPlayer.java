package edu.neu.player.musicplayer;
import edu.neu.player.apidata.MusicTrack;

public class BasicMusicPlayer implements MusicPlayerAPI {

    @Override
    public void play(MusicTrack song) {
        System.out.println("▶️ Playing: " + song);
    }

    @Override
    public void pause() {
        System.out.print("⏸ Paused: ");
    }

    @Override
    public void stop() {
        System.out.print("⏹ Stopped: ");
    }
}