package edu.neu.player.command;
import edu.neu.player.playerstate.PlayerContext;

public class PauseCommand implements MusicCommand {
    private PlayerContext playerContext;

    public PauseCommand(PlayerContext playerContext) {
        this.playerContext = playerContext;
    }

    @Override
    public void execute() {
        playerContext.pause();
    }
}
