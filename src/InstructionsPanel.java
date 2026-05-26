import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class InstructionsPanel extends BasePanel {
    private JButton backButton;
    private double animationPhase;

    public InstructionsPanel(GameFrame frame) {
        // This constructor sets up the help screen and the ESC shortcut.
        super(frame);
        this.animationPhase = 0.0;
        setupUI();
        setupKeyBindings();
        new Timer(16, e -> {
            animationPhase += 0.035;
            repaint();
        }).start();
    }

    private void setupUI() {
        setLayout(null);

        backButton = createStyledButton("Back to Menu");
        int buttonX = (Constants.WINDOW_WIDTH - 240) / 2;
        backButton.setBounds(buttonX, 640, 240, 54);
        backButton.addActionListener(e -> frame.showMenu());

        add(backButton);
    }

    private void setupKeyBindings() {
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "returnToMenu");
        getActionMap().put("returnToMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.showMenu();
            }
        });
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        enableAntialiasing(g2d);
        drawAmbientOverlays(g2d, animationPhase);

        Font titleFont = createRetroFont(Font.BOLD, 36);
        drawGlowText(g2d, "HOW TO PLAY", 78, titleFont, Constants.TEXT_COLOR, Constants.ACCENT_BLUE);

        Font sectionFont = createRetroFont(Font.BOLD, 18);
        Font textFont = createRetroFont(Font.PLAIN, 15);

        drawCard(g2d, 210, 112, 604, 474, new java.awt.Color(8, 11, 23, 180), new java.awt.Color(69, 205, 255, 140));

        int y = 140;
        int lineHeight = 24;
        int sectionSpacing = 40;

        drawCenteredText(g2d, "CONTROLS", 130, sectionFont, Constants.ACCENT_GREEN);
        y = 160;
        drawCenteredText(g2d, "Arrow Keys or WASD: Move snake", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "P: Pause or Resume game", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "R: Restart game", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "ESC: Return to main menu", y, textFont, Constants.TEXT_COLOR);

        // RULES section
        y += sectionSpacing;
        drawCenteredText(g2d, "RULES", y, sectionFont, Constants.ACCENT_GREEN);
        y += 30;
        drawCenteredText(g2d, "Eat food to grow and gain points", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "Every 5 food items increases level", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "Higher levels make the snake faster", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "Avoid hitting walls and your own body", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "Reach Level 5 to win!", y, textFont, Constants.TEXT_COLOR);

        y += sectionSpacing;
        drawCenteredText(g2d, "OOP CONCEPTS", y, sectionFont, Constants.ACCENT_BLUE);
        y += 30;
        drawCenteredText(g2d, "GameObject is an abstract parent class", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "Snake and Food inherit from GameObject", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "Snake and Food both override draw()", y, textFont, Constants.TEXT_COLOR);
        y += lineHeight;
        drawCenteredText(g2d, "Private fields in the model classes show encapsulation", y, textFont, Constants.TEXT_COLOR);

        // The screen stays plain so the text reads cleanly without a decorative card.
    }
}
