import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;

public class WinPanel extends BasePanel {
    private static BufferedImage winBackground;
    static {
        try {
            winBackground = ImageIO.read(new File("bg-gold.png"));
        } catch (IOException e) {
            winBackground = null;
        }
    }

    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JButton playAgainButton;
    private JButton menuButton;
    private JButton exitButton;
    private int finalScore;
    private int finalLevel;
    private double animationPhase;

    public WinPanel(GameFrame frame) {
        // This constructor prepares the victory screen and its navigation buttons.
        super(frame);
        this.animationPhase = 0.0;
        setupUI();
        new Timer(16, e -> {
            animationPhase += 0.05;
            repaint();
        }).start();
    }

    private void setupUI() {
        setLayout(null);

        // Create labels
        scoreLabel = new JLabel();
        levelLabel = new JLabel();

        // Create buttons
        playAgainButton = createStyledButton("Play Again");
        menuButton = createStyledButton("Main Menu");
        exitButton = createStyledButton("Exit");

        // Position buttons
        int buttonX = (Constants.WINDOW_WIDTH - 240) / 2;
        playAgainButton.setBounds(buttonX, 354, 240, 54);
        menuButton.setBounds(buttonX, 430, 240, 54);
        exitButton.setBounds(buttonX, 506, 240, 54);

        // Add action listeners
        playAgainButton.addActionListener(e -> frame.restartGame());
        menuButton.addActionListener(e -> frame.showMenu());
        exitButton.addActionListener(e -> frame.exitGame());

        // Add components
        add(scoreLabel);
        add(levelLabel);
        add(playAgainButton);
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
        Graphics2D g2d = (Graphics2D) g;
        enableAntialiasing(g2d);

        if (winBackground != null) {
            g2d.drawImage(winBackground, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(Constants.BACKGROUND_COLOR);
            g2d.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        }
        drawAmbientOverlays(g2d, animationPhase);
        drawConfetti(g2d);

        Font titleFont = createRetroFont(Font.BOLD, 42);
        drawGlowText(g2d, "YOU WIN!", 86 + (int) (Math.sin(animationPhase * 1.8) * 4), titleFont, Constants.TEXT_COLOR, Constants.ACCENT_GOLD);

        Font subtitleFont = createRetroFont(Font.BOLD, 20);
        drawCenteredText(g2d, "Perfect neon domination", 146, subtitleFont, Constants.ACCENT_GREEN);

        Font resultFont = createRetroFont(Font.BOLD, 24);
        drawCenteredText(g2d, "Final Score: " + finalScore,
            218, resultFont, Constants.ACCENT_GOLD);
        drawCenteredText(g2d, "Level Completed: " + finalLevel,
            260, resultFont, Constants.TEXT_COLOR);

    }

    private void drawConfetti(Graphics2D g) {
        Color[] colors = {Constants.ACCENT_GREEN, Constants.ACCENT_BLUE, Constants.ACCENT_PINK, Constants.ACCENT_GOLD};
        for (int i = 0; i < 36; i++) {
            int x = (i * 73 + (int) (animationPhase * 42)) % Constants.WINDOW_WIDTH;
            int y = (int) ((i * 41 + animationPhase * 90) % Constants.WINDOW_HEIGHT);
            Color c = colors[i % colors.length];
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 160));
            g.fillRoundRect(x, y, 10, 4, 3, 3);
        }
    }
}
