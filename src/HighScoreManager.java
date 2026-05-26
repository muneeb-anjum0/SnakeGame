import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HighScoreManager {
    // This class manages persistent high score storage in a file on disk.
    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private int normalHighScore;
    private int endlessHighScore;

    public HighScoreManager() {
        loadHighScores();
    }

    private void loadHighScores() {
        // Load high scores from file for both modes, or default to 0.
        this.normalHighScore = 0;
        this.endlessHighScore = 0;
        
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                // Try to parse new format: "normal:950|endless:500"
                if (line.contains("|")) {
                    for (String entry : line.split("\\|")) {
                        if (entry.startsWith("normal:")) {
                            normalHighScore = Integer.parseInt(entry.substring(7));
                        } else if (entry.startsWith("endless:")) {
                            endlessHighScore = Integer.parseInt(entry.substring(8));
                        }
                    }
                } else {
                    // Legacy format: just a number (treat as normal mode)
                    normalHighScore = Integer.parseInt(line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            // If any error occurs, default to 0.
        }
    }

    public int getHighScore(GameMode mode) {
        // Get high score for the specified game mode.
        return mode == GameMode.NORMAL ? normalHighScore : endlessHighScore;
    }

    public void updateHighScore(int newScore, GameMode mode) {
        // Update the high score if the new score is better, then save to file.
        if (mode == GameMode.NORMAL) {
            if (newScore > normalHighScore) {
                normalHighScore = newScore;
                saveHighScores();
            }
        } else {
            if (newScore > endlessHighScore) {
                endlessHighScore = newScore;
                saveHighScores();
            }
        }
    }

    private void saveHighScores() {
        // Write both high scores to file for persistence across game sessions.
        try (FileWriter writer = new FileWriter(HIGH_SCORE_FILE)) {
            writer.write("normal:" + normalHighScore + "|endless:" + endlessHighScore);
        } catch (IOException e) {
            // Silent fail; the game continues even if file save fails.
        }
    }
}
