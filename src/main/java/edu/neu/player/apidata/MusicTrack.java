package edu.neu.player.apidata;


public class MusicTrack {

    // extrinsic (per-track)
    private final int id;
    private final String title;
    private final String duration;
    private final String artist ; // extrinsic

    // intrinsic (shared)
    private final ITrackMetadata metadata;

    private MusicTrack(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.duration = builder.duration;
        this.artist = builder.artist;

        // Flyweight to store shared metadata (album, genre, releaseYear)
        this.metadata = TrackMetadataFlyweightFactory.get(
                builder.album,
                builder.genre,
                builder.releaseYear
        );
    }


    // getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDuration() { return duration; }
    public String getArtist() { return artist; } // extrinsic
    public String getAlbum() { return metadata.getAlbum(); }
    public String getGenre() { return metadata.getGenre(); }
    public int getReleaseYear() { return metadata.getReleaseYear(); }
    public int getMetadataId() { return System.identityHashCode(metadata); } //to display ID on GUI to show shared object

    @Override
    public String toString() {
        return  id + ") " +
                title + " [" + duration + "] - " + artist;
    }

    public static class Builder {
        private int id;
        private String title;
        private String duration;
        private String artist; // extrinsic
        private String album;
        private String genre;  // new field for Flyweight
        private int releaseYear;

        // helper to parse integer safely
        private int parseIntOrThrow(String value, String field) {
            try {
                int parsed = Integer.parseInt(value.trim());
                if (parsed < 0) {
                    throw new IllegalArgumentException(field + " must be non-negative: " + parsed);
                }
                return parsed;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid integer for " + field + ": " + value
                );
            }
        }

        // CSV static factory (now expects 7 fields: ID,Title,Artist,Album,Genre,Year,Duration)
        public static Builder fromCSV(String csv) {
            String[] parts = csv.split(",");
            if (parts.length != 7) {
                throw new IllegalArgumentException("Invalid CSV format: " + csv);
            }

            Builder b = new Builder();
            b.id = b.parseIntOrThrow(parts[0], "ID");
            b.title = parts[1].trim();
            b.artist = parts[2].trim();
            b.album = parts[3].trim();
            b.genre = parts[4].trim();
            b.releaseYear = b.parseIntOrThrow(parts[5], "Release Year");
            b.duration = parts[6].trim();

            return b;
        }

        // standard builder methods
        public Builder withId(int id) { this.id = id; return this; }
        public Builder withTitle(String title) { this.title = title; return this; }
        public Builder withDuration(String duration) { this.duration = duration; return this; }
        public Builder withArtist(String artist) { this.artist = artist; return this; }
        public Builder withAlbum(String album) { this.album = album; return this; }
        public Builder withGenre(String genre) { this.genre = genre; return this; }
        public Builder withReleaseYear(int year) { this.releaseYear = year; return this; }

        public MusicTrack build() {
            return new MusicTrack(this);
        }
    }
}