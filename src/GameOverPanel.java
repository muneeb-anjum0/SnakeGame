import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;

public class GameOverPanel extends BasePanel {
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JButton restartButton;
    private JButton menuButton;
    private JButton exitButton;
    private int finalScore;
    private int finalLevel;
    private double animationPhase;

    public GameOverPanel(GameFrame frame) {
        // This screen appears when the snake crashes into a wall or itself.
        super(frame);
        this.animationPhase = 0.0;
        setupUI();
        new Timer(16, e -> {
            animationPhase += 0.055;
            repaint();
        }).start();
    }

    private static BufferedImage gameOverBg;
    static {
        try {
            gameOverBg = ImageIO.read(new File("bg-red.png"));
        } catch (IOException e) {
            gameOverBg = null;
        }
    }

    private void setupUI() {
        setLayout(null);

        // Create labels
        scoreLabel = new JLabel();
        levelLabel = new JLabel();

        // Create buttons
        restartButton = createStyledButton("Play Again");
        menuButton = createStyledButton("Main Menu");
        exitButton = createStyledButton("Exit");

        // Position buttons
        int buttonX = (Constants.WINDOW_WIDTH - 240) / 2;
        restartButton.setBounds(buttonX, 354, 240, 54);
        menuButton.setBounds(buttonX, 430, 240, 54);
        exitButton.setBounds(buttonX, 506, 240, 54);

        // Add action listeners
        restartButton.addActionListener(e -> frame.restartGame());
        menuButton.addActionListener(e -> frame.showMenu());
        exitButton.addActionListener(e -> frame.exitGame());

        // Add components
        add(scoreLabel);
        add(levelLabel);
        add(restartButton);
        add(menuButton);
        add(exitButton);
    }

    public void setResults(int score, int level) {
        this.finalScore = score;
        this.finalLevel = level;
        scoreLabel.setText("Score: " + score);
        levelLabel.setText("Level: " + level);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Draw a special red background for the Game Over screen if available.
        if (gameOverBg != null) {
            Graphics2D gbg = (Graphics2D) g.create();
            enableAntialiasing(gbg);
            gbg.drawImage(gameOverBg, 0, 0, getWidth(), getHeight(), null);
            gbg.dispose();
        } else {
            // Fallback to default background behavior
            super.paintComponent(g);
        }
        Graphics2D g2d = (Graphics2D) g;
        enableAntialiasing(g2d);
        drawAmbientOverlays(g2d, animationPhase);

        Font titleFont = createRetroFont(Font.BOLD, 42);
        drawGlowText(g2d, "GAME OVER", 124 + (int) (Math.sin(animationPhase * 2) * 3), titleFont, Constants.TEXT_COLOR, Constants.BLOOD_RED);

        Font subtitleFont = createRetroFont(Font.BOLD, 20);
        drawCenteredText(g2d, "The run burned out", 184, subtitleFont, Constants.DANGER_RED);

        Font resultFont = createRetroFont(Font.BOLD, 24);
        drawCenteredText(g2d, "Final Score: " + finalScore,
                        250, resultFont, Constants.ACCENT_GOLD);
        drawCenteredText(g2d, "Level Reached: " + finalLevel,
                        292, resultFont, Constants.TEXT_COLOR);
    }
}
