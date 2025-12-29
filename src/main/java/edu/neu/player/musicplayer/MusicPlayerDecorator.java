package edu.neu.player.musicplayer;

import edu.neu.player.apidata.MusicTrack;

public abstract class MusicPlayerDecorator implements MusicPlayerAPI {
    protected MusicPlayerAPI decoratedPlayer;

    public MusicPlayerDecorator(MusicPlayerAPI decoratedPlayer) {
        this.decoratedPlayer = decoratedPlayer;
    }

    @Override
    public void play(MusicTrack song) {
        decoratedPlayer.play(song);
    }

    @Override
    public void pause() {
        decoratedPlayer.pause(); 
    }

    @Override
    public void stop() {
        decoratedPlayer.stop(); 
    }
    
}
