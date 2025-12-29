package edu.neu.player.musicplayer.Decorators;

import edu.neu.player.apidata.MusicTrack;
import edu.neu.player.musicplayer.MusicPlayerAPI;
import edu.neu.player.musicplayer.MusicPlayerDecorator;

public class EqualizerDecorator extends MusicPlayerDecorator {
    private String preset;

    public EqualizerDecorator(MusicPlayerAPI player, String preset) {
        super(player);
        this.preset = preset;
    }

    @Override
    public void play(MusicTrack song) {
        super.play(song);
        System.out.println("🎛️ Equalizer preset: " + preset);
    }
}