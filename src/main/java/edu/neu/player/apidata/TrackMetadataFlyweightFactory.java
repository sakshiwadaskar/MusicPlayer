package edu.neu.player.apidata;

import java.util.HashMap;
import java.util.Map;

// FlyweightFactory
public class TrackMetadataFlyweightFactory {

    private static final Map<String, ITrackMetadata> pool = new HashMap<>();

    public static ITrackMetadata get(String album, String genre, int releaseYear) {

        String k = releaseYear+"|"+album.toLowerCase().trim();

        if (!pool.containsKey(k)) {
            pool.put(k, new TrackMetadata(album, genre, releaseYear));
        }

        return pool.get(k);
    }
}
