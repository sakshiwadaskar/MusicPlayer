package edu.neu.player.Playlist;

public class PlaylistSingletonFactory implements IPlaylistFactory {
    // Singleton instance
    private static PlaylistSingletonFactory instance;

    private PlaylistSingletonFactory() {}

    // Public method to get singleton instance
    public static PlaylistSingletonFactory getInstance() {
        if (instance == null) {
            instance = new PlaylistSingletonFactory();
        }
        return instance;
    }
     @Override
    public Playlist createPlaylist(String name) {
        return new Playlist(name);
    }
}
