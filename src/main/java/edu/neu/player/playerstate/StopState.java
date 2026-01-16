package edu.neu.player.playerstate;

public class StopState implements PlayerState {
    public boolean handleAction(PlayerState state) {
        if( state instanceof PlayingState) {
            System.out.println("Transitioning from Stop State to " + state.toString());
            return true;
        } else {
            System.out.println("Invalid transition from Stop State to " + state.toString());
            return false;
        }
    }
    @Override
    public String toString() {
        return "Stopped";
    }
}
