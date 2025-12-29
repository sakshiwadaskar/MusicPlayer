package edu.neu.player.musicplayer;

import edu.neu.player.apidata.MusicTrack;

public interface MusicPlayerAPI {
    void play(MusicTrack song);
    void pause();
    void stop();
}
