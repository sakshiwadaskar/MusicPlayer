package edu.neu.player.apidata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {

    /**
     * Reads a file and stores each line of the CSV string into a List.
     *
     * @param filePath The path to the CSV file.
     * @return A List containing each line from the file as a String.
     * @throws IOException If there is an error reading the file.
     */
    public List<String> readCsvFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}