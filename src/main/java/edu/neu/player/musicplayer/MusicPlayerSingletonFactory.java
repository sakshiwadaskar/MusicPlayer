package edu.neu.player.musicplayer;

public class MusicPlayerSingletonFactory {

    //Lazy Singleton factory
    private static MusicPlayerAPI musicPlayerInstance;

    private MusicPlayerSingletonFactory() {}

    public static MusicPlayerAPI getMusicPlayer() {
        if (musicPlayerInstance == null) {
            musicPlayerInstance = new BasicMusicPlayer();
        }
        return musicPlayerInstance;
    }
}
