package edu.neu.player;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.player.apidata.FileUtility;
import edu.neu.player.apidata.MusicTrack;
import edu.neu.player.command.MusicCommandInvoker;
import edu.neu.player.command.PauseCommand;
import edu.neu.player.command.PlayNextSongCommand;
import edu.neu.player.command.PlayPreviousSongCommand;
import edu.neu.player.command.PlaySpecificSongCommand;
import edu.neu.player.command.StopCommand;
import edu.neu.player.demo.observer.ObservableRateLimit;
import edu.neu.player.demo.observer.RateLimitNotifier;
import edu.neu.player.demo.strategy.RateLimitStrategyConfig;
import edu.neu.player.musicplayer.MusicPlayerAPI;
import edu.neu.player.musicplayer.MusicPlayerSingletonFactory;
import edu.neu.player.musicplayer.Decorators.BassBoostDecorator;
import edu.neu.player.musicplayer.Decorators.EqualizerDecorator;
import edu.neu.player.musicplayer.Decorators.TrebleBoostDecorator;
import edu.neu.player.playerstate.PlayerContext;
import edu.neu.player.Playlist.Playlist;
import edu.neu.player.Playlist.PlaylistSingletonFactory;

public class MusicPlayerGUI extends JFrame {
    // UI Components
    private DefaultListModel<String> songLibraryModel;
    private JList<String> songLibraryList;
    private DefaultListModel<String> playlistModel;
    private JList<String> playlistList;
    private DefaultListModel<String> playlistSongsModel;
    private JList<String> playlistSongsList;
    private JComboBox<String> playlistComboBox;
    private JLabel nowPlayingLabel;
    private JLabel statusLabel;
    private JLabel stateBox;
    private JTextArea consoleArea;
    private JTextArea albumInfoArea;
    // ===== Decorator Controls =====
    private JCheckBox equalizerCheckbox;
    private JComboBox<String> presetComboBox;

    private JCheckBox bassBoostCheckbox;
    private JSlider bassLevelSlider;

    private JCheckBox trebleBoostCheckbox;
    private JSlider trebleLevelSlider;

    private JLabel decoratorsLabel;


    // Data
    private HashMap<String, Playlist> playlists = new HashMap<>();
    private List<MusicTrack> allTracks = new ArrayList<>();
    private PlaylistSingletonFactory playlistFactory = PlaylistSingletonFactory.getInstance();

    // Rate Limiter
    private ObservableRateLimit rateLimiter;
    private String clientId = "gui-client-192.168.1.1";
    private boolean isPremium = false;

    // State Pattern + Command Pattern
    private MusicPlayerAPI mediaPlayer;
    private MusicPlayerAPI decoratedPlayer;
    private List<String> activeDecorators = new ArrayList<>();
    private PlayerContext playerContext;
    private MusicTrack currentTrack = null;
    private Playlist currentPlaylistContext = null;
     MusicCommandInvoker invoker = MusicCommandInvoker.getInstance();
    


    public MusicPlayerGUI() {
        setTitle("Music Player - Rate Limited");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize
        initializeRateLimiter();
        loadSongsFromCSV();
        mediaPlayer = MusicPlayerSingletonFactory.getMusicPlayer();   // singleton music player
        playerContext = new PlayerContext(mediaPlayer);   //saving to context
        decoratedPlayer = mediaPlayer;
        // Create UI
        createUI();
        
        setVisible(true);

        redirectConsoleToTextArea();
    }

    private void createUI() {
        // ===== MODELS & LISTS =====
        songLibraryModel = new DefaultListModel<>();
        for (MusicTrack track : allTracks) {
            songLibraryModel.addElement(track.toString());
        }
        songLibraryList = new JList<>(songLibraryModel);
        songLibraryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Album info area (below song list)
        albumInfoArea = new JTextArea(9, 20);
        albumInfoArea.setEditable(false);
        albumInfoArea.setLineWrap(true);
        albumInfoArea.setWrapStyleWord(true);

        // Update album info when a song is selected in the library
        songLibraryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = songLibraryList.getSelectedIndex();
                if (idx >= 0 && idx < allTracks.size()) {
                    MusicTrack t = allTracks.get(idx);
                    albumInfoArea.setText(
                            "🆔 Id: " + t.getId() + "\n" +
                            "🎵 Title: " + t.getTitle() + "\n" +
                            "🎤 Artist: " + t.getArtist() + "\n" +
                            "⏱️ Duration: " + t.getDuration() + " seconds"+"\n----------------------------\n"  +
                            "Metadata 🆔 (Flyweight): " + t.getMetadataId() + "\n----------------------------\n" +
                            "🎵 Album: " + t.getAlbum() + "\n" +
                            "🎶 Genre: " + t.getGenre() + "\n" +
                            "🗓️ Year: " + t.getReleaseYear() + "\n"
);
                } else {
                    albumInfoArea.setText("");
                }
            }
        });


        playlistModel = new DefaultListModel<>();
        playlistList = new JList<>(playlistModel);
        playlistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        playlistSongsModel = new DefaultListModel<>();
        playlistSongsList = new JList<>(playlistSongsModel);
        playlistSongsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // When a song inside a playlist is selected, show its album/info in the albumInfoArea
        playlistSongsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = playlistSongsList.getSelectedIndex();
                if (idx >= 0 && currentPlaylistContext != null && idx < currentPlaylistContext.getSize()) {
                    MusicTrack t = currentPlaylistContext.get(idx);
                    albumInfoArea.setText(
                            "🆔 Id: " + t.getId() + "\n" +
                            "🎵 Title: " + t.getTitle() + "\n" +
                            "🎤 Artist: " + t.getArtist() + "\n" +
                            "⏱️ Duration: " + t.getDuration() + " seconds" +"\n----------------------------\n"  +
                            "Metadata 🆔 (Flyweight): " + t.getMetadataId() + "\n----------------------------\n" +
                            "🎵 Album: " + t.getAlbum() + "\n" +
                            "🎶 Genre: " + t.getGenre() + "\n" +
                            "🗓️ Year: " + t.getReleaseYear() + "\n"
                    );
                }
            }
        });

        // ===== CONTROLS =====
        playlistComboBox = new JComboBox<>();
        playlistComboBox.setEditable(true);

        
        JButton createPlaylistBtn = new JButton("Create Playlist");
        JButton addSongBtn = new JButton("[+] to Playlist");
        JButton removeSongBtn = new JButton("[-] from Playlist");
        JButton playSongBtn = new JButton("▶ Play");
        JButton pauseSongBtn = new JButton("⏸ Pause");
        JButton stopSongBtn = new JButton("⏹ Stop");
        JButton previousSongBtn = new JButton("⏮ Previous");
        JButton nextSongBtn = new JButton("Next ⏭");
         equalizerCheckbox = new JCheckBox("Equalizer");
        presetComboBox = new JComboBox<>(new String[]{"Rock", "Pop", "Jazz"});

         bassBoostCheckbox = new JCheckBox("Bass Boost");
         bassLevelSlider = new JSlider(0, 10, 5);

         trebleBoostCheckbox = new JCheckBox("Treble Boost");
         trebleLevelSlider = new JSlider(0, 10, 5);

         decoratorsLabel = new JLabel("Active Effects: ");
        
        // ===== STATUS DISPLAYS =====
        nowPlayingLabel = new JLabel("Now Playing: None");
        nowPlayingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nowPlayingLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statusLabel = new JLabel("User Type: " + (isPremium ? "Premium" : "Standard"));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(isPremium ? Color.MAGENTA : Color.BLUE);

        stateBox = new JLabel("Stopped", SwingConstants.CENTER);
        stateBox.setOpaque(true);
        stateBox.setBackground(Color.BLACK);
        stateBox.setForeground(Color.WHITE);
        stateBox.setFont(new Font("Arial", Font.BOLD, 12));
        stateBox.setPreferredSize(new Dimension(100, 30));

        consoleArea = new JTextArea(6, 50);
        consoleArea.setEditable(false);
        consoleArea.setBackground(Color.BLACK);
        consoleArea.setForeground(Color.GREEN);
        consoleArea.setFont(new Font("Courier", Font.PLAIN, 11));
        //redirectConsoleToTextArea();
        JScrollPane consoleScroll = new JScrollPane(consoleArea);


  

        // ===== ACTION LISTENERS =====
        createPlaylistBtn.addActionListener(e -> handleCreatePlaylist());
        addSongBtn.addActionListener(e -> handleAddSongs());
        removeSongBtn.addActionListener(e -> handleRemoveSongs());
        playSongBtn.addActionListener(e -> handlePlaySong());
        pauseSongBtn.addActionListener(e -> handlePauseSong());
        stopSongBtn.addActionListener(e -> handleStopSong());
        nextSongBtn.addActionListener(e -> handleNextSong());
        previousSongBtn.addActionListener(e -> handlePreviousSong());
        equalizerCheckbox.addActionListener(e -> applyDecorators());
        presetComboBox.addActionListener(e -> applyDecorators());
        bassBoostCheckbox.addActionListener(e -> applyDecorators());
        bassLevelSlider.addChangeListener(e -> applyDecorators());
        trebleBoostCheckbox.addActionListener(e -> applyDecorators());
        trebleLevelSlider.addChangeListener(e -> applyDecorators());

        playlistList.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            String selectedName = playlistList.getSelectedValue();
            if (selectedName != null) {
                Playlist selectedPlaylist = playlists.get(selectedName);

                if (selectedPlaylist == null) {
                    System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + " Error: Playlist not found: " + selectedName);
                    return;
                }

                System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "➕ Selected Playlist: " + selectedName);

                updatePlaylistSongsDisplay(selectedPlaylist);

                // Set current context
                currentPlaylistContext = selectedPlaylist;
            }
        }
        });


        // ===== LAYOUT: LEFT PANEL (PLAYLISTS) =====
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Playlists"));
        JScrollPane playlistScroll = new JScrollPane(playlistList);
        JPanel playlistControlPanel = new JPanel(new BorderLayout(5, 5));
        playlistControlPanel.add(playlistComboBox, BorderLayout.CENTER);
        playlistControlPanel.add(createPlaylistBtn, BorderLayout.EAST);
        leftPanel.add(playlistScroll, BorderLayout.CENTER);
        leftPanel.add(playlistControlPanel, BorderLayout.SOUTH);

        // ===== LAYOUT: CENTER PANEL (PLAYLIST SONGS) =====
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Playlist Songs"));
        JScrollPane playlistSongsScroll = new JScrollPane(playlistSongsList);
        JPanel centerControlPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        centerControlPanel.add(addSongBtn);
        centerControlPanel.add(removeSongBtn);
        centerPanel.add(playlistSongsScroll, BorderLayout.CENTER);
        centerPanel.add(centerControlPanel, BorderLayout.SOUTH);

        // ===== LAYOUT: RIGHT PANEL (SONG LIBRARY) =====
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Song Library"));

        // Use a vertical split: top = song list, bottom = album info
        JScrollPane songLibraryScroll = new JScrollPane(songLibraryList);

        JPanel albumInfoPanel = new JPanel(new BorderLayout(5,5));
        albumInfoPanel.setBorder(BorderFactory.createTitledBorder("Song Info"));
        JScrollPane albumScroll = new JScrollPane(albumInfoArea);
        albumInfoPanel.add(albumScroll, BorderLayout.CENTER);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, songLibraryScroll, albumInfoPanel);
        rightSplit.setResizeWeight(0.75); // most space to the song list
        rightSplit.setOneTouchExpandable(true);
        rightSplit.setDividerSize(6);

        rightPanel.add(rightSplit, BorderLayout.CENTER);

        // ===== LAYOUT: TOP PANELS =====
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(nowPlayingLabel, BorderLayout.CENTER);
        topPanel.add(statusLabel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // ===== LAYOUT: BOTTOM PANEL (CONTROLS) =====
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(playSongBtn);
        buttonPanel.add(pauseSongBtn);
        buttonPanel.add(stopSongBtn);
        buttonPanel.add(previousSongBtn);
        buttonPanel.add(nextSongBtn);

        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        stateBox.setPreferredSize(new Dimension(100, 30)); // fixed size
        statusPanel.add(stateBox, BorderLayout.EAST);
        statusPanel.add(nowPlayingLabel, BorderLayout.CENTER); // fills remaining space
        JPanel decoratorPanel = new JPanel();
        decoratorPanel.setBorder(BorderFactory.createTitledBorder("Audio Effects"));
        decoratorPanel.setLayout(new BoxLayout(decoratorPanel, BoxLayout.Y_AXIS)); // vertical stack

        // Row 1: all checkboxes + sliders
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(equalizerCheckbox);
        row1.add(presetComboBox);
        row1.add(bassBoostCheckbox);
        row1.add(new JLabel("Level:"));
        row1.add(bassLevelSlider);
        row1.add(trebleBoostCheckbox);
        row1.add(new JLabel("Level:"));
        row1.add(trebleLevelSlider);

        // Row 2: active effects label
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(decoratorsLabel);

        // Add rows to decorator panel
        decoratorPanel.add(row1);
        decoratorPanel.add(row2);
                      
        JPanel topBottomPanel = new JPanel();
        topBottomPanel.setLayout(new BoxLayout(topBottomPanel, BoxLayout.Y_AXIS));
        topBottomPanel.add(statusPanel);
        topBottomPanel.add(decoratorPanel);

        bottomPanel.add(topBottomPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(consoleScroll, BorderLayout.SOUTH);


        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        // ===== MAIN CENTER PANEL =====
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 5, 5)); // 1 row, 3 columns, 5px gaps
        mainPanel.add(leftPanel);    // Playlists
        mainPanel.add(centerPanel);  // Playlist Songs
        mainPanel.add(rightPanel);   // Song Library
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    // ===== EVENT HANDLERS =====
    private void handleCreatePlaylist() {
        String selectedItem = (String) playlistComboBox.getSelectedItem();
    if (selectedItem == null || selectedItem.trim().isEmpty()) {
        System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "Playlist name cannot be empty");
        return;
    }

    String playlistName = selectedItem.trim();
        if (playlists.containsKey(playlistName)) {
            System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "Playlist already exists: " + playlistName);
            return;
        }
        // Use PlaylistFactory to create playlist (Factory Pattern)
        Playlist newPlaylist = playlistFactory.createPlaylist(playlistName);
        playlists.put(playlistName, newPlaylist);
        playlistModel.addElement(playlistName);
        playlistComboBox.addItem(playlistName);
        System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "📁 Created playlist: " + playlistName);
        playlistList.setSelectedValue(playlistName, true);
    }

    private void handleAddSongs() {
        if (!rateLimiter.isAllowed(clientId)) {
            return;
        }

        String selectedPlaylist = playlistList.getSelectedValue();
        if (selectedPlaylist == null) {
            System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "Please select a playlist first");
            return;
        }

        int[] selectedIndices = songLibraryList.getSelectedIndices();
        if (selectedIndices.length == 0) {
            System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "Please select songs to add");
            return;
        }

        Playlist playlist = playlists.get(selectedPlaylist);
        for (int idx : selectedIndices) {
            if (idx < allTracks.size()) {
                MusicTrack track = allTracks.get(idx);
                playlist.addTrack(track);
                System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "➕ Added: " + track.getTitle());
            }
        }
        updatePlaylistSongsDisplay(playlist);
    }

    private void handleRemoveSongs() {
        if (!rateLimiter.isAllowed(clientId)) {
            return;
        }

        String selectedPlaylist = playlistList.getSelectedValue();
        if (selectedPlaylist == null) {
            System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "Please select a playlist first");
            return;
        }

        int[] selectedIndices = playlistSongsList.getSelectedIndices();
        if (selectedIndices.length == 0) {
            System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "Please select songs to remove");
            return;
        }

        Playlist playlist = playlists.get(selectedPlaylist);
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            MusicTrack removedTrack = playlist.get(selectedIndices[i]);
            playlist.removeTrackAt(selectedIndices[i]);
            System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "➖ Removed: " + removedTrack.getTitle());
        }
        updatePlaylistSongsDisplay(playlist);
    }

    private void handlePlaySong() {
    if (!rateLimiter.isAllowed(clientId)) {
        return;
    }

    List<MusicTrack> trackList = null;
    int currentIndex = -1;
        if (currentPlaylistContext != null) {
        trackList = currentPlaylistContext.getTracks();
        currentIndex = playlistSongsList.getSelectedIndex();
    } 
    
    if (currentIndex<0 && !allTracks.isEmpty()) {
        trackList = allTracks;
        currentIndex = songLibraryList.getSelectedIndex();
    } 

    if (currentIndex < 0 || trackList == null || trackList.isEmpty()) {
        System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "⚠ No song selected to play.");
        return;
    }
    MusicTrack selectedTrack = trackList.get(currentIndex);
    invoker.setCommand(new PlaySpecificSongCommand(playerContext, selectedTrack ));
    invoker.executeCommand();
    stateBox.setText(playerContext.getState().toString());
    if(playerContext.getCurrentSong()!=null){
            nowPlayingLabel.setText(playerContext.getState() +" "+ playerContext.getCurrentSong().getTitle() + " - " + playerContext.getCurrentSong().getArtist());

    }else{
        nowPlayingLabel.setText(playerContext.getState().toString());
    }

   
}


    private void handlePauseSong() {
                if (!rateLimiter.isAllowed(clientId)) {
            return;
        }
    invoker.setCommand(new PauseCommand(playerContext));
    invoker.executeCommand();
    stateBox.setText(playerContext.getState().toString());
    if(playerContext.getCurrentSong()!=null){
            nowPlayingLabel.setText(playerContext.getState() +" "+ playerContext.getCurrentSong().getTitle() + " - " + playerContext.getCurrentSong().getArtist());

    }else{
        nowPlayingLabel.setText(playerContext.getState().toString());
    }
    }

    private void handleStopSong() {
                if (!rateLimiter.isAllowed(clientId)) {
            return;
        }
    invoker.setCommand(new StopCommand(playerContext));
    invoker.executeCommand();
    stateBox.setText(playerContext.getState().toString());
    if(playerContext.getCurrentSong()!=null){
            nowPlayingLabel.setText(playerContext.getState() +" "+ playerContext.getCurrentSong().getTitle() + " - " + playerContext.getCurrentSong().getArtist());

    }else{
        nowPlayingLabel.setText(playerContext.getState().toString());
    }    }

    private void handleNextSong() {

    if (!rateLimiter.isAllowed(clientId)) {
        return;
    }

    List<MusicTrack> trackList = null;
    int currentIndex = -1;

        if (currentPlaylistContext != null) {
        trackList = currentPlaylistContext.getTracks();
        currentIndex = playlistSongsList.getSelectedIndex();
    } 
    
    if (currentIndex<0 && !allTracks.isEmpty()) {
        trackList = allTracks;
        currentIndex = songLibraryList.getSelectedIndex();
    } 


    if (trackList != null && currentIndex != -1) {
        PlayNextSongCommand nextCommand = new PlayNextSongCommand(playerContext, trackList, currentIndex);
        invoker.setCommand(nextCommand);
        invoker.executeCommand();

        // Update GUI
        int newIndex = nextCommand.getCurrentIndex();
        if (currentPlaylistContext != null && currentPlaylistContext.getSize()>0) {
            playlistSongsList.setSelectedIndex(newIndex);
        } else {
            songLibraryList.setSelectedIndex(newIndex);
        }
        stateBox.setText(playerContext.getState().toString());
    if(playerContext.getCurrentSong()!=null){
            nowPlayingLabel.setText(playerContext.getState() +" "+ playerContext.getCurrentSong().getTitle() + " - " + playerContext.getCurrentSong().getArtist());

    }else{
        nowPlayingLabel.setText(playerContext.getState().toString());
    }
    } else {
        System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "⚠ No next song available.");
    }
}

private void handlePreviousSong() {
    
    if (!rateLimiter.isAllowed(clientId)) {
        return;
    }

    List<MusicTrack> trackList = null;
    int currentIndex = -1;

    // Determine current playlist or library
    if (currentPlaylistContext != null) {
        trackList = currentPlaylistContext.getTracks();
        currentIndex = playlistSongsList.getSelectedIndex();
    }

    if (currentIndex < 0 && !allTracks.isEmpty()) {
        trackList = allTracks;
        currentIndex = songLibraryList.getSelectedIndex();
    }

    if (trackList != null && currentIndex != -1) {
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            prevIndex = trackList.size() - 1; // wrap around to last song
        }

        invoker.setCommand(new PlayPreviousSongCommand(playerContext, trackList, currentIndex));
        invoker.executeCommand();

        // Update GUI selection
        if (currentPlaylistContext != null && currentPlaylistContext.getSize() > 0) {
            playlistSongsList.setSelectedIndex(prevIndex);
        } else {
            songLibraryList.setSelectedIndex(prevIndex);
        }

        // Update status labels
        stateBox.setText(playerContext.getState().toString());
           if(playerContext.getCurrentSong()!=null){
            nowPlayingLabel.setText(playerContext.getState() +" "+ playerContext.getCurrentSong().getTitle() + " - " + playerContext.getCurrentSong().getArtist());

    }else{
        nowPlayingLabel.setText(playerContext.getState().toString());
    }

    } else {
        System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + "⚠ No previous song available.");
    }
}


private void applyDecorators() {
    // Start from base player
    decoratedPlayer = mediaPlayer;
    activeDecorators.clear(); // rebuild from scratch

    // BassBoost
    if (bassBoostCheckbox.isSelected()) {
        int level = bassLevelSlider.getValue();
        decoratedPlayer = new BassBoostDecorator(decoratedPlayer, level);
        activeDecorators.add("BassBoost - " + level);
    }

    // TrebleBoost
    if (trebleBoostCheckbox.isSelected()) {
        int level = trebleLevelSlider.getValue();
        decoratedPlayer = new TrebleBoostDecorator(decoratedPlayer, level);
        activeDecorators.add("TrebleBoost - " + level);
    }

    // Equalizer
    if (equalizerCheckbox.isSelected()) {
        String preset = (String) presetComboBox.getSelectedItem();
        decoratedPlayer = new EqualizerDecorator(decoratedPlayer, preset);
        activeDecorators.add("Equalizer - " + preset);
    }

    // Update player context and label
    playerContext.setPlayer(decoratedPlayer);
    updateDecoratorsLabel();
}



private void updateDecoratorsLabel() {
    String label = "Active Effects: " + (activeDecorators.isEmpty() ? "" : String.join(", ", activeDecorators));
    decoratorsLabel.setText(label);
}


    // ===== UTILITY METHODS =====
    private void updatePlaylistSongsDisplay(Playlist playlist) {
        playlistSongsModel.clear();
        if (playlist != null) {
            for (int i = 0; i < playlist.getSize(); i++) {
                MusicTrack track = playlist.get(i);
                playlistSongsModel.addElement(track.toString());
            }
        }
    }

private void appendToConsole(String text) {
    SwingUtilities.invokeLater(() -> {
        consoleArea.append(text);
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    });
}

   private void redirectConsoleToTextArea() {
    OutputStream os = new OutputStream() {
        @Override
        public void write(int b) {
            appendToConsole(String.valueOf((char) b));
        }

        @Override
        public void write(byte[] b, int off, int len) {
            appendToConsole(new String(b, off, len, StandardCharsets.UTF_8));
        }
    };

    PrintStream ps = new PrintStream(os, true, StandardCharsets.UTF_8);
    System.setOut(ps);
    System.setErr(ps);
}


    private void initializeRateLimiter() {
        String[] options = {"Standard", "Premium"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select your user type:",
                "Rate Limiter Configuration",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        isPremium = (choice == 1);
        String userType = isPremium ? "premium" : "standard";
        rateLimiter = new ObservableRateLimit(RateLimitStrategyConfig.getStrategy(userType));
        rateLimiter.addObserver(new RateLimitNotifier());
    }

    private void loadSongsFromCSV() {
        String filePath = "src/main/resources/apidata.csv";
        FileUtility fileUtility = new FileUtility();
        try {
            List<String> csvLines = fileUtility.readCsvFile(filePath);
            for (String line : csvLines) {
                MusicTrack track = MusicTrack.Builder.fromCSV(line).build(); //use MusicTrack builder + flyweight
                allTracks.add(track);
            }
            JOptionPane.showMessageDialog(this, "Loaded " + allTracks.size() + " songs from CSV");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
