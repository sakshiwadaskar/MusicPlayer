package edu.neu.player.command;
import edu.neu.player.playerstate.PlayerContext;

public class StopCommand implements MusicCommand {
    private PlayerContext playerContext;

    public StopCommand(PlayerContext playerContext) {
        this.playerContext = playerContext;
    }

    @Override
    public void execute() {
        playerContext.stop();
    }
}
