package edu.neu.player.command;

import java.util.List;
import edu.neu.player.apidata.MusicTrack;
import edu.neu.player.playerstate.PlayerContext;

public class PlayNextSongCommand implements MusicCommand {
    private PlayerContext playerContext;
    private List<MusicTrack> songList;
    private int currentIndex;

    public PlayNextSongCommand(PlayerContext playerContext, List<MusicTrack> songList, int currentIndex) {
        this.playerContext = playerContext;
        this.songList = songList;
        this.currentIndex = currentIndex;
    }

    @Override
    public void execute() {
        if (currentIndex < songList.size() - 1) {
        currentIndex++;
        } else {
        // wrap around to the first song
        System.out.println("No Next Song available. Wrapping around to the first song.");
        currentIndex = 0;
        }
        MusicTrack songDetails = songList.get(currentIndex);
        playerContext.setCurrentSong(songDetails);
        playerContext.play();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
