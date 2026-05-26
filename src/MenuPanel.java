import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import javax.swing.Timer;

public class MenuPanel extends BasePanel {
    private javax.swing.JButton normalButton;
    private javax.swing.JButton endlessButton;
    private javax.swing.JButton instructionsButton;
    private javax.swing.JButton exitButton;
    private Timer animationTimer;
    private double animationPhase;

    public MenuPanel(GameFrame frame) {
        // This constructor builds the menu screen and starts a soft background animation.
        super(frame);
        this.animationPhase = 0.0;
        setupUI();
        setupAnimation();
    }

    private void setupUI() {
        setLayout(null);

        normalButton = createStyledButton("Normal");
        endlessButton = createStyledButton("Endless");
        instructionsButton = createStyledButton("Instructions");
        exitButton = createStyledButton("Exit");

        int buttonY = 382;
        int buttonX = (Constants.WINDOW_WIDTH - 240) / 2;

        normalButton.setBounds(buttonX, buttonY, 240, 54);
        endlessButton.setBounds(buttonX, buttonY + 76, 240, 54);
        instructionsButton.setBounds(buttonX, buttonY + 152, 240, 54);
        exitButton.setBounds(buttonX, buttonY + 228, 240, 54);

        // Add action listeners
        normalButton.addActionListener(e -> frame.startGame(GameMode.NORMAL));
        endlessButton.addActionListener(e -> frame.startGame(GameMode.ENDLESS));
        instructionsButton.addActionListener(e -> frame.showInstructions());
        exitButton.addActionListener(e -> frame.exitGame());

        add(normalButton);
        add(endlessButton);
        add(instructionsButton);
        add(exitButton);
    }

    private void setupAnimation() {
        // A lightweight timer keeps the background glow slowly moving.
        animationTimer = new Timer(16, e -> {
            animationPhase += 0.04;
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        enableAntialiasing(g2d);
        drawAmbientOverlays(g2d, animationPhase);

        drawOrbit(g2d, 0.0, Constants.ACCENT_GREEN);
        drawOrbit(g2d, 2.1, Constants.ACCENT_BLUE);
        drawOrbit(g2d, 4.2, Constants.ACCENT_PINK);

        Font titleFont = createRetroFont(Font.BOLD, 58);
        int titleY = 124 + (int) (Math.sin(animationPhase * 1.4) * 4);
        drawGlowText(g2d, "SNAKE GAME", titleY, titleFont, Constants.TEXT_COLOR, Constants.ACCENT_GREEN);

        Font subtitleFont = createRetroFont(Font.PLAIN, 20);
        drawCenteredText(g2d, "NEON ARCADE EDITION", 170, subtitleFont, Constants.ACCENT_BLUE);

        Font creditFont = createRetroFont(Font.BOLD, 18);
        drawCenteredText(g2d, "Made by Muneeb and Mustafa", 204, creditFont, Constants.TEXT_COLOR);

        g2d.setPaint(new GradientPaint(332, 240, Constants.ACCENT_GREEN, 692, 240, Constants.ACCENT_PINK));
        g2d.fillRoundRect(332, 238, 360, 4, 4, 4);
    }

    private void drawOrbit(Graphics2D g, double offset, Color color) {
        int centerX = Constants.WINDOW_WIDTH / 2;
        int centerY = 278;
        int radiusX = 250;
        int radiusY = 44;
        double angle = animationPhase + offset;
        int x = centerX + (int) (Math.cos(angle) * radiusX);
        int y = centerY + (int) (Math.sin(angle) * radiusY);

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 70));
        g.fillOval(x - 18, y - 18, 36, 36);
        g.setColor(color);
        g.fillOval(x - 5, y - 5, 10, 10);
    }
}
