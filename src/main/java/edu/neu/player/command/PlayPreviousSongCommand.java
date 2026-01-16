package edu.neu.player.command;

import java.util.List;

import edu.neu.player.apidata.MusicTrack;
import edu.neu.player.playerstate.PlayerContext;

public class PlayPreviousSongCommand implements MusicCommand {
    private PlayerContext playerContext;
    private List<MusicTrack> songList;
    private int currentIndex;

    public PlayPreviousSongCommand(PlayerContext playerContext, List<MusicTrack> songList, int currentIndex) {
        this.playerContext = playerContext;
        this.songList = songList;
        this.currentIndex = currentIndex;
    }

    @Override
    public void execute() {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            // wrap around to the last song
            System.out.println("No Previous Song available. Wrapping around to the last song.");
            currentIndex = songList.size() - 1;
        }
        MusicTrack songDetails = songList.get(currentIndex);
        playerContext.setCurrentSong(songDetails);
        playerContext.play();
    }


    public int getCurrentIndex() {
        return currentIndex;
    }

}