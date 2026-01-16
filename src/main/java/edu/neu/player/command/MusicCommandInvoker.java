package edu.neu.player.command;

public class MusicCommandInvoker {

    private static MusicCommandInvoker instance; // singleton instance
    private MusicCommand command;

    private MusicCommandInvoker() {}

    public static MusicCommandInvoker getInstance() {
        if (instance == null) {
            instance = new MusicCommandInvoker();
        }
        return instance;
    }

    public void setCommand(MusicCommand command) {
        this.command = command;
    }

    public void executeCommand() {
        if (command != null) {
            command.execute();
        }
    }
}
