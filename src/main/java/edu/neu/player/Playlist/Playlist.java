package edu.neu.player.Playlist;

import java.util.ArrayList;
import java.util.List;
import edu.neu.player.apidata.MusicTrack;

public class Playlist {
    private String name;
    private List<MusicTrack> tracks = new ArrayList<>();

    public Playlist(String name) {
        this.name = name;
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<MusicTrack> getTracks() {
        return tracks;
    }

    public int getSize() {
        return tracks.size();
    }

    public MusicTrack get(int index) {
        return tracks.get(index);
    }

    // Add a track (only reference, no copy)
    public void addTrack(MusicTrack track) {
        tracks.add(track);
    }

    // Add multiple tracks
    public void addTracks(List<MusicTrack> tracksToAdd) {
        tracks.addAll(tracksToAdd);
    }

    // Remove a track by object
    public void removeTrack(MusicTrack track) {
        tracks.remove(track);
    }

    // Remove a track by index
    public MusicTrack removeTrackAt(int index) {
        if (tracks.isEmpty()) {
            System.out.println("Playlist is empty. No tracks to remove.");
            return null;
        }

        if (index >= 0 && index < tracks.size()) {
            return tracks.remove(index);
        } else {
            System.out.println("Invalid track number.");
            return null;
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Playlist: ").append(name).append("\n");
        sb.append("Songs:\n");

        for (int i = 0; i < tracks.size(); i++) {
            MusicTrack track = tracks.get(i);
            sb.append((i + 1)) // track number starting from 1
                    .append(". ")
                    .append(track.toString())
                    .append("\n");
        }

        return sb.toString();
    }

}