package edu.neu.player.musicplayer.Decorators;

import edu.neu.player.apidata.MusicTrack;
import edu.neu.player.musicplayer.MusicPlayerAPI;
import edu.neu.player.musicplayer.MusicPlayerDecorator;

public class TrebleBoostDecorator extends MusicPlayerDecorator {
    private int level;

    public TrebleBoostDecorator(MusicPlayerAPI player, int level) {
        super(player);
        this.level = level;
    }

    @Override
    public void play(MusicTrack song) {
        super.play(song);
        System.out.println("🎶 Treble Boost at level " + level);
    }
}