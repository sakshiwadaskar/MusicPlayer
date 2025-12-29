package edu.neu.player;

import javax.swing.*;


public class Driver {
	public static void main(String[] args) {
		System.out.println("============Main Execution Start===================\n\n");

		SwingUtilities.invokeLater(MusicPlayerGUI::new);
    System.out.println("\n\n============Main Execution End===================");
	}

}
