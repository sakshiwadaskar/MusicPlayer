package edu.neu.player.playerstate;

public interface PlayerState {
    boolean handleAction(PlayerState next);
}
