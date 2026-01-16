package edu.neu.player.playerstate;

public class PausedState implements PlayerState {
    @Override
    public boolean handleAction(PlayerState state) {
        if( state instanceof PlayingState || state instanceof StopState) {
            System.out.println("Transitioning from Paused State to " + state.toString());
            return true;
        } else {
            System.out.println("Invalid transition from Paused State to " + state.toString());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Paused";
    }
}
