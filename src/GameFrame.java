import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private MenuPanel menuPanel;
    private InstructionsPanel instructionsPanel;
    private GamePanel gamePanel;
    private GameOverPanel gameOverPanel;
    private WinPanel winPanel;
    private GameMode currentGameMode;

    public static final String MENU = "MENU";
    public static final String INSTRUCTIONS = "INSTRUCTIONS";
    public static final String GAME = "GAME";
    public static final String GAME_OVER = "GAME_OVER";
    public static final String WIN = "WIN";

    public GameFrame() {
        // The frame is the central controller that switches between all game screens.
        setTitle("Java Snake Game - OOP Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        this.currentGameMode = GameMode.NORMAL;

        // Setup CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create all panels
        menuPanel = new MenuPanel(this);
        instructionsPanel = new InstructionsPanel(this);
        gamePanel = new GamePanel(this);
        gameOverPanel = new GameOverPanel(this);
        winPanel = new WinPanel(this);

        // Add panels to card panel
        cardPanel.add(menuPanel, MENU);
        cardPanel.add(instructionsPanel, INSTRUCTIONS);
        cardPanel.add(gamePanel, GAME);
        cardPanel.add(gameOverPanel, GAME_OVER);
        cardPanel.add(winPanel, WIN);

        add(cardPanel);

        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        pack();
        setLocationRelativeTo(null);

        // Show menu first
        showMenu();
    }

    public void showMenu() {
        cardLayout.show(cardPanel, MENU);
    }

    public void showInstructions() {
        cardLayout.show(cardPanel, INSTRUCTIONS);
        instructionsPanel.requestFocusInWindow();
    }

    public void startGame(GameMode mode) {
        this.currentGameMode = mode;
        gamePanel.startNewGame(mode);
        cardLayout.show(cardPanel, GAME);
        javax.swing.SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    public void restartGame() {
        startGame(currentGameMode);
    }

    public void showGameOver(int finalScore, int levelReached) {
        gameOverPanel.setResults(finalScore, levelReached);
        cardLayout.show(cardPanel, GAME_OVER);
    }

    public void showWin(int finalScore, int levelReached) {
        winPanel.setResults(finalScore, levelReached);
        cardLayout.show(cardPanel, WIN);
    }

    public void exitGame() {
        System.exit(0);
    }
}
