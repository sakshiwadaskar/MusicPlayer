package edu.neu.player.playerstate;

public class PlayingState implements PlayerState {
    @Override
    public boolean handleAction(PlayerState state) {
        if( state instanceof PlayerState) {
            System.out.println("Transitioning from Playing State to " + state.toString());
            return true;
        } else {
            System.out.println("Invalid transition from Playing State to " + state.toString());
            return false;
        }
    }
    @Override
    public String toString() {
        return "Playing";
    }
}
