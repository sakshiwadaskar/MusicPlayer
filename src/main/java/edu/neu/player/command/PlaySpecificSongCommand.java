package edu.neu.player.command;
import edu.neu.player.apidata.MusicTrack;
import edu.neu.player.playerstate.PlayerContext;

public class PlaySpecificSongCommand implements MusicCommand {
    private PlayerContext playerContext;

    public PlaySpecificSongCommand(PlayerContext playerContext, MusicTrack song) {
        this.playerContext = playerContext;
        if(song!=null) playerContext.setCurrentSong(song);
    }

    @Override
    public void execute() {
        if (playerContext.getCurrentSong()!=null) {
            System.out.println("Playing track #: " + playerContext.getCurrentSong().getId());
            playerContext.play();
        } else {
            System.out.println("Please select a song.");
        }
    }
}