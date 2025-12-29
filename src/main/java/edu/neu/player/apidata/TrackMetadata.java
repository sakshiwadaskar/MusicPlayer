package edu.neu.player.apidata;

// ConcreteFlyweight
public class TrackMetadata implements ITrackMetadata {

    // intrinsic state (shared)
    private final String genre;
    private final String album;
    private final int releaseYear;

    public TrackMetadata(String album, String genre, int releaseYear) {
        this.genre = genre;
        this.album = album;
        this.releaseYear = releaseYear;
    }

    @Override
    public String getGenre() { return genre; }
    @Override
    public String getAlbum() { return album; }
    @Override
    public int getReleaseYear() { return releaseYear; }

    @Override
    public String toString() {
        return "🎶 " + genre + " / 🎵 " + album + " (🗓️ " + releaseYear + ") [🆔" + System.identityHashCode(this) + "] ";
    }

}
