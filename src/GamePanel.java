import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class GamePanel extends BasePanel {
    private static BufferedImage gameBackground;
    static {
        try {
            gameBackground = ImageIO.read(new File("bg-blue.png"));
        } catch (IOException e) {
            gameBackground = null;
        }
    }

    private GameFrame frame;
    private Snake snake;
    private Food food;
    private ScoreManager scoreManager;
    private Timer gameTimer;
    private Timer renderTimer;
    private GameState gameState;
    private GameMode gameMode;
    private int timerDelay;
    private int animationTick;
    private double visualPhase;
    private int scorePulseFrames;
    private int screenShakeFrames;
    private long levelUpFlashStartTime;
    private boolean isLevelUpFlashing;
    private JButton pauseResumeButton;
    private JButton pauseMenuButton;

    public GamePanel(GameFrame frame) {
        super(frame);
        // This panel owns the actual gameplay loop and all on-screen game drawing.
        this.frame = frame;
        this.gameMode = GameMode.NORMAL;
        this.scoreManager = new ScoreManager(gameMode);
        this.gameState = GameState.MENU;
        this.timerDelay = Constants.INITIAL_DELAY;
        this.animationTick = 0;
        this.visualPhase = 0.0;
        this.scorePulseFrames = 0;
        this.screenShakeFrames = 0;
        this.levelUpFlashStartTime = 0;
        this.isLevelUpFlashing = false;

        setPreferredSize(new java.awt.Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setFocusable(true);

        setupKeyBindings();
        setupTimer();
        setupPauseMenu();
    }

    private void setupTimer() {
        // The Swing Timer acts like the heartbeat of the game.
        gameTimer = new Timer(timerDelay, e -> updateGame());
        renderTimer = new Timer(16, e -> {
            visualPhase += 0.045;
            if (scorePulseFrames > 0) {
                scorePulseFrames--;
            }
            if (screenShakeFrames > 0) {
                screenShakeFrames--;
            }
            if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
                repaint();
            }
        });
        renderTimer.start();
    }

    private void setupKeyBindings() {
        // Key bindings keep the controls responsive without using a KeyListener.
        bindKey(KeyEvent.VK_UP, "moveUp", Direction.UP);
        bindKey(KeyEvent.VK_DOWN, "moveDown", Direction.DOWN);
        bindKey(KeyEvent.VK_LEFT, "moveLeft", Direction.LEFT);
        bindKey(KeyEvent.VK_RIGHT, "moveRight", Direction.RIGHT);

        bindKey(KeyEvent.VK_W, "moveUp", Direction.UP);
        bindKey(KeyEvent.VK_A, "moveLeft", Direction.LEFT);
        bindKey(KeyEvent.VK_S, "moveDown", Direction.DOWN);
        bindKey(KeyEvent.VK_D, "moveRight", Direction.RIGHT);

        // P for pause
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pause");
        getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });

        // R for restart
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        // ESC toggles pause (shows pause overlay)
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pause");
        getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
    }

    private void bindKey(int keyCode, String actionName, Direction direction) {
        getInputMap().put(KeyStroke.getKeyStroke(keyCode, 0), actionName);
        getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (snake != null && gameState == GameState.RUNNING) {
                    snake.setDirection(direction);
                }
            }
        });
    }

    private void setupPauseMenu() {
        // Setup pause menu buttons with styled appearance like main menu
        setLayout(null);
        
        pauseResumeButton = createStyledButton("Resume Game");
        pauseMenuButton = createStyledButton("Main Menu");
        
        // Add action listeners
        pauseResumeButton.addActionListener(e -> togglePause());
        pauseMenuButton.addActionListener(e -> returnToMenu());
        
        // Add buttons to panel but keep them hidden
        add(pauseResumeButton);
        add(pauseMenuButton);
        
        updatePauseButtonVisibility();
    }

    public void startNewGame(GameMode mode) {
        // Start from the same clean beginning every time the player restarts.
        this.gameMode = mode;
        this.scoreManager = new ScoreManager(mode);
        snake = new Snake(Constants.BOARD_COLS / 2, Constants.BOARD_ROWS / 2);
        food = new Food();
        food.spawn(snake);
        gameState = GameState.RUNNING;
        timerDelay = getInitialDelayForMode(mode);
        animationTick = 0;
        isLevelUpFlashing = false;
        levelUpFlashStartTime = 0;
        updatePauseButtonVisibility();
        startTimer();
        requestFocusInWindow();
    }

    public void resetGame() {
        stopTimer();
        startNewGame(gameMode);
    }

    private void startTimer() {
        if (gameTimer == null) {
            gameTimer = new Timer(timerDelay, e -> updateGame());
        }
        gameTimer.setDelay(timerDelay);
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED;
            stopTimer();
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
            startTimer();
        }
        updatePauseButtonVisibility();
        repaint();
    }

    private void restartGame() {
        stopTimer();
        startNewGame(gameMode);
    }

    private void returnToMenu() {
        stopTimer();
        frame.showMenu();
    }

    private void updateGame() {
        if (gameState != GameState.RUNNING) {
            return;
        }

        animationTick++;
        
        // Use different movement based on game mode
        if (gameMode == GameMode.ENDLESS) {
            snake.moveWithWrapping();
            // Check for self-collision (endless mode has no walls)
            if (snake.hasSelfCollision()) {
                handleGameOver();
                return;
            }
            // Check if board is full (endless mode win condition)
            if (isBoardFull()) {
                handleEndlessWin();
                return;
            }
        } else {
            snake.move();
            // If the snake runs into a wall or itself, the round ends immediately.
            if (checkWallCollision() || snake.hasSelfCollision()) {
                handleGameOver();
                return;
            }
        }

        checkFoodCollision();

        repaint();
    }

    private boolean checkWallCollision() {
        Point head = snake.getHead();
        return head.x < 0 || head.x >= Constants.BOARD_COLS ||
               head.y < 0 || head.y >= Constants.BOARD_ROWS;
    }

    private boolean isBoardFull() {
        // Check if the entire board is filled with snake body
        int totalCells = Constants.BOARD_COLS * Constants.BOARD_ROWS;
        return snake.getBody().size() >= totalCells;
    }

    private void checkFoodCollision() {
        Point head = snake.getHead();
        if (head.x == food.getX() && head.y == food.getY()) {
            snake.grow();
            scoreManager.addFoodScore();
            scoreManager.updateHighScore();
            scorePulseFrames = 18;
            screenShakeFrames = 5;

            if (scoreManager.isWinConditionMet()) {
                handleWin();
                return;
            }

            if (gameMode == GameMode.NORMAL && scoreManager.shouldLevelUp()) {
                scoreManager.levelUp();
                startLevelUpFlash();  // Start the golden flash animation
                updateGameSpeed();
            } else if (gameMode == GameMode.ENDLESS) {
                // Gradually increase speed in endless mode
                updateEndlessSpeed();
            }

            food.spawn(snake);
        }
    }

    private void updateGameSpeed() {
        // Each level gets a faster timer so the game gradually becomes harder (NORMAL mode).
        timerDelay = getDelayForLevel(scoreManager.getLevel());
        stopTimer();
        startTimer();
    }

    private void updateEndlessSpeed() {
        // In endless mode, speed increases very gradually based on food count
        // Every 5 food collected, speed increases slightly
        int foodCount = scoreManager.getScore() / 10;  // Since endless gives 10 per food
        int newDelay = Math.max(35, 150 - (foodCount * 2));  // Gradual decrease, min 35ms
        
        if (newDelay != timerDelay) {
            timerDelay = newDelay;
            stopTimer();
            startTimer();
        }
    }

    private void startLevelUpFlash() {
        // Trigger the golden flash animation for this level-up
        isLevelUpFlashing = true;
        levelUpFlashStartTime = System.currentTimeMillis();
    }

    private boolean isCurrentlyFlashing() {
        if (!isLevelUpFlashing) {
            return false;
        }
        long elapsedTime = System.currentTimeMillis() - levelUpFlashStartTime;
        long totalFlashTime = Constants.LEVEL_UP_FLASH_DURATION * Constants.LEVEL_UP_FLASH_COUNT * 2;
        
        if (elapsedTime >= totalFlashTime) {
            isLevelUpFlashing = false;
            return false;
        }
        
        // Flash on/off every LEVEL_UP_FLASH_DURATION milliseconds
        long flashCycle = (elapsedTime / Constants.LEVEL_UP_FLASH_DURATION) % 2;
        return flashCycle == 0;
    }

    private int getInitialDelayForMode(GameMode mode) {
        return mode == GameMode.NORMAL ? 150 : 150;  // Both start at 150ms
    }

    private int getDelayForLevel(int level) {
        // More aggressive speed increases for higher difficulty
        switch (level) {
            case 1:
                return 150;
            case 2:
                return 115;    // Bigger jump
            case 3:
                return 85;     // Significant difficulty increase
            case 4:
                return 55;     // Very challenging
            case 5:
                return 35;     // Extreme difficulty
            default:
                return 150;
        }
    }

    private void handleGameOver() {
        gameState = GameState.GAME_OVER;
        stopTimer();
        scoreManager.updateHighScore();
        if (gameMode == GameMode.ENDLESS) {
            frame.showGameOver(scoreManager.getScore(), 0);  // Pass 0 for level in endless mode
        } else {
            frame.showGameOver(scoreManager.getScore(), scoreManager.getLevel());
        }
    }

    private void handleWin() {
        gameState = GameState.WIN;
        stopTimer();
        scoreManager.updateHighScore();
        frame.showWin(scoreManager.getScore(), scoreManager.getLevel());
    }

    private void handleEndlessWin() {
        gameState = GameState.WIN;
        stopTimer();
        scoreManager.updateHighScore();
        frame.showWin(scoreManager.getScore(), 0);  // Pass 0 for level in endless mode
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        enableAntialiasing(g2d);

        if (gameBackground != null && (gameState == GameState.RUNNING || gameState == GameState.PAUSED)) {
            g2d.drawImage(gameBackground, 0, 0, getWidth(), getHeight(), null);
        }
        drawAmbientOverlays(g2d, visualPhase);

        // Only draw gameplay during the active and paused states.
        if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
            int shakeX = 0;
            int shakeY = 0;
            if (screenShakeFrames > 0) {
                shakeX = (int) Math.round(Math.sin(visualPhase * 22) * screenShakeFrames);
                shakeY = (int) Math.round(Math.cos(visualPhase * 19) * screenShakeFrames);
                g2d.translate(shakeX, shakeY);
            }
            drawHeader(g2d);
            drawBoard(g2d);
            drawFooter(g2d);
            if (screenShakeFrames > 0) {
                g2d.translate(-shakeX, -shakeY);
            }

            if (gameState == GameState.PAUSED) {
                drawPauseOverlay(g2d);
            }
        }
    }

    private void drawHeader(Graphics2D g) {
        // Draw background with subtle gradient effect
        g.setPaint(new GradientPaint(0, 0, new Color(6, 9, 20, 235),
                Constants.WINDOW_WIDTH, Constants.HEADER_HEIGHT, new Color(17, 12, 32, 235)));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.HEADER_HEIGHT);

        // Draw accent line at bottom
        g.setPaint(new GradientPaint(0, Constants.HEADER_HEIGHT - 3, Constants.ACCENT_GREEN,
                Constants.WINDOW_WIDTH, Constants.HEADER_HEIGHT - 3, Constants.ACCENT_PINK));
        g.setStroke(new java.awt.BasicStroke(4));
        g.drawLine(0, Constants.HEADER_HEIGHT - 3, Constants.WINDOW_WIDTH, Constants.HEADER_HEIGHT - 3);

        Font labelFont = createRetroFont(Font.BOLD, 16);
        Font valueFont = createRetroFont(Font.BOLD, 18);
        
        int padding = 50;
        int y = 32;
        int x = padding;

        if (gameMode == GameMode.NORMAL) {
            // Display NORMAL mode stats
            // Score
            g.setFont(labelFont);
            g.setColor(Constants.TEXT_COLOR);
            g.drawString("Score:", x, y);
            g.setFont(valueFont);
            g.setColor(scorePulseFrames > 0 ? Constants.ACCENT_GOLD : Constants.ACCENT_GREEN);
            g.drawString(String.valueOf(scoreManager.getScore()), x + 90, y);
            
            // Level
            x += 220;
            g.setFont(labelFont);
            g.setColor(Constants.TEXT_COLOR);
            g.drawString("Level:", x, y);
            g.setFont(valueFont);
            g.setColor(Constants.ACCENT_BLUE);
            g.drawString(String.valueOf(scoreManager.getLevel()), x + 85, y);
            
            // High Score
            x += 200;
            g.setFont(labelFont);
            g.setColor(Constants.TEXT_COLOR);
            g.drawString("High Score:", x, y);
            g.setFont(valueFont);
            g.setColor(Constants.ACCENT_PINK);
            g.drawString(String.valueOf(scoreManager.getHighScore()), x + 130, y);
            
            // Food
            x += 240;
            g.setFont(labelFont);
            g.setColor(Constants.TEXT_COLOR);
            g.drawString("Food:", x, y);
            g.setFont(valueFont);
            g.setColor(Constants.ACCENT_GOLD);
            g.drawString(scoreManager.getFoodThisLevel() + "/" + Constants.FOOD_PER_LEVEL, x + 85, y);
        } else {
            // Display ENDLESS mode stats
            // Score
            g.setFont(labelFont);
            g.setColor(Constants.TEXT_COLOR);
            g.drawString("Score:", x, y);
            g.setFont(valueFont);
            g.setColor(scorePulseFrames > 0 ? Constants.ACCENT_GOLD : Constants.ACCENT_GREEN);
            g.drawString(String.valueOf(scoreManager.getScore()), x + 90, y);
            
            // High Score
            x += 220;
            g.setFont(labelFont);
            g.setColor(Constants.TEXT_COLOR);
            g.drawString("High Score:", x, y);
            g.setFont(valueFont);
            g.setColor(Constants.ACCENT_PINK);
            g.drawString(String.valueOf(scoreManager.getHighScore()), x + 130, y);
            
            // Cells Filled
            x += 280;
            int totalCells = Constants.BOARD_COLS * Constants.BOARD_ROWS;
            int cellsFilled = snake != null ? snake.getBody().size() : 0;
            g.setFont(labelFont);
            g.setColor(Constants.TEXT_COLOR);
            g.drawString("Cells:", x, y);
            g.setFont(valueFont);
            g.setColor(Constants.ACCENT_BLUE);
            g.drawString(cellsFilled + "/" + totalCells, x + 75, y);
        }
    }

    private void drawBoard(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRoundRect(Constants.BOARD_X - 8, Constants.BOARD_Y - 8,
                       Constants.BOARD_WIDTH + 16, Constants.BOARD_HEIGHT + 16, 26, 26);

        g.setPaint(new GradientPaint(Constants.BOARD_X, Constants.BOARD_Y, new Color(8, 12, 25, 205),
                Constants.BOARD_X + Constants.BOARD_WIDTH, Constants.BOARD_Y + Constants.BOARD_HEIGHT, new Color(17, 8, 28, 205)));
        g.fillRoundRect(Constants.BOARD_X - 4, Constants.BOARD_Y - 4,
                       Constants.BOARD_WIDTH + 8, Constants.BOARD_HEIGHT + 8, 24, 24);

        g.setColor(new Color(86, 219, 255, 38));
        g.setStroke(new java.awt.BasicStroke(0.5f));
        for (int i = 0; i <= Constants.BOARD_COLS; i++) {
            int x = Constants.BOARD_X + i * Constants.CELL_SIZE;
            g.drawLine(x, Constants.BOARD_Y, x, Constants.BOARD_Y + Constants.BOARD_HEIGHT);
        }
        for (int i = 0; i <= Constants.BOARD_ROWS; i++) {
            int y = Constants.BOARD_Y + i * Constants.CELL_SIZE;
            g.drawLine(Constants.BOARD_X, y, Constants.BOARD_X + Constants.BOARD_WIDTH, y);
        }

        int chase = (int) ((visualPhase * 55) % Constants.BOARD_WIDTH);
        g.setColor(new Color(Constants.ACCENT_GREEN.getRed(), Constants.ACCENT_GREEN.getGreen(), Constants.ACCENT_GREEN.getBlue(), 180));
        g.setStroke(new java.awt.BasicStroke(2.2f));
        g.drawRoundRect(Constants.BOARD_X - 4, Constants.BOARD_Y - 4,
                       Constants.BOARD_WIDTH + 8, Constants.BOARD_HEIGHT + 8, 24, 24);
        g.setColor(new Color(Constants.ACCENT_PINK.getRed(), Constants.ACCENT_PINK.getGreen(), Constants.ACCENT_PINK.getBlue(), 210));
        g.setStroke(new java.awt.BasicStroke(4.0f));
        g.drawLine(Constants.BOARD_X + chase, Constants.BOARD_Y - 6,
                Math.min(Constants.BOARD_X + Constants.BOARD_WIDTH, Constants.BOARD_X + chase + 90), Constants.BOARD_Y - 6);

        if (food != null) {
            food.draw(g);
        }
        if (snake != null) {
            if (isCurrentlyFlashing()) {
                // Draw snake with golden hue during level-up flash
                snake.drawWithGoldenHue(g);
            } else {
                snake.draw(g);
            }
        }
    }

    private void drawFooter(Graphics2D g) {
        int footerY = Constants.BOARD_Y + Constants.BOARD_HEIGHT + 20;

        Font footerFont = createRetroFont(Font.PLAIN, 12);
        g.setFont(footerFont);
        g.setColor(Constants.MUTED_TEXT_COLOR);

        String footerText = "Arrow Keys / WASD to move | P to pause | R to restart | ESC for menu";
        int textWidth = g.getFontMetrics().stringWidth(footerText);
        int x = (Constants.WINDOW_WIDTH - textWidth) / 2;
        g.drawString(footerText, x, footerY);
    }

    private void drawPauseOverlay(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(Constants.BOARD_X - 4, Constants.BOARD_Y - 4,
                  Constants.BOARD_WIDTH + 8, Constants.BOARD_HEIGHT + 8, 24, 24);

        Font pauseFont = createRetroFont(Font.BOLD, 36);
        String pauseText = "PAUSED";
        int textWidth = g.getFontMetrics(pauseFont).stringWidth(pauseText);
        int x = (Constants.WINDOW_WIDTH - textWidth) / 2;
        g.setFont(pauseFont);
        g.setColor(new Color(Constants.ACCENT_GREEN.getRed(), Constants.ACCENT_GREEN.getGreen(), Constants.ACCENT_GREEN.getBlue(), 65));
        g.drawString(pauseText, x - 3, Constants.BOARD_Y + Constants.BOARD_HEIGHT / 2 - 80);
        g.setColor(Constants.TEXT_COLOR);
        g.drawString(pauseText, x, Constants.BOARD_Y + Constants.BOARD_HEIGHT / 2 - 80);
        
        // Position pause menu buttons
        int buttonX = (Constants.WINDOW_WIDTH - 240) / 2;
        int buttonY = Constants.BOARD_Y + Constants.BOARD_HEIGHT / 2 + 20;
        pauseResumeButton.setBounds(buttonX, buttonY, 240, 54);
        pauseMenuButton.setBounds(buttonX, buttonY + 76, 240, 54);
    }

    private void updatePauseButtonVisibility() {
        // Show buttons when paused, hide otherwise
        boolean isPaused = gameState == GameState.PAUSED;
        pauseResumeButton.setVisible(isPaused);
        pauseMenuButton.setVisible(isPaused);
    }
}

