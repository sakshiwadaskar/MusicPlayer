package edu.neu.player.playerstate;

import edu.neu.player.apidata.MusicTrack;
import edu.neu.player.musicplayer.MusicPlayerAPI;

public class PlayerContext {
    private PlayerState state;
    private MusicPlayerAPI player;
    private MusicTrack currentSong;

    public PlayerContext(MusicPlayerAPI player) {
        this.player = player;
        this.state = new StopState();
    }

    public void setCurrentSong(MusicTrack song) {
        this.currentSong = song;
    }

    public MusicTrack getCurrentSong() {
        return currentSong;
    }

    public MusicPlayerAPI getPlayer() {
        return player;
    }

    public void setPlayer(MusicPlayerAPI player) {
        this.player = player;
    }

    public void play() {
        PlayerState newState = new PlayingState();
        if (state.handleAction(newState) && currentSong != null) {
            player.play(currentSong);   // delegate to actual player
            setState(newState);
        }
    }

    public void pause() {
        PlayerState newState = new PausedState();
        if (state.handleAction(newState) && currentSong != null) {
            player.pause();
            System.out.println(currentSong);
            setState(newState);
        }
    }

    public void stop() {
        PlayerState newState = new StopState();
        if (state.handleAction(newState) && currentSong != null) {
            player.stop();
            System.out.println(currentSong);
            setState(newState);
        }
    }

    private void setState(PlayerState newState) {
        this.state = newState;
        System.out.println("Transitioned to: " + newState);
    }

    public PlayerState getState() {
        return state;
    }
}
