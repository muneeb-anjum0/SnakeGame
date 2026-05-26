public class ScoreManager {
    private int score;
    private int level;
    private int foodThisLevel;
    private HighScoreManager highScoreManager;
    private GameMode gameMode;

    public ScoreManager(GameMode gameMode) {
        // The constructor starts a fresh session with a clean score board.
        this.highScoreManager = new HighScoreManager();
        this.gameMode = gameMode;
        reset();
    }

    public void reset() {
        // Reset only the current run; the high score stays for this app session.
        this.score = 0;
        this.level = 1;
        this.foodThisLevel = 0;
    }

    public void addFoodScore() {
        // In normal mode, each food is worth more points as the player climbs levels.
        // In endless mode, food is worth a consistent 10 points.
        int points = (gameMode == GameMode.NORMAL) ? (10 * level) : 10;
        score += points;
        foodThisLevel++;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScoreManager.getHighScore(gameMode);
    }

    public int getLevel() {
        return level;
    }

    public int getFoodThisLevel() {
        return foodThisLevel;
    }

    public void updateHighScore() {
        highScoreManager.updateHighScore(score, gameMode);
    }

    public boolean shouldLevelUp() {
        if (gameMode == GameMode.ENDLESS) {
            return false;  // No level-up in endless mode
        }
        return foodThisLevel >= Constants.FOOD_PER_LEVEL;
    }

    public void levelUp() {
        if (level < Constants.MAX_LEVEL) {
            level++;
            foodThisLevel = 0;
        }
    }

    public boolean isWinConditionMet() {
        if (gameMode == GameMode.ENDLESS) {
            return false;  // Win condition for endless is handled differently
        }
        // The player wins by finishing the fifth batch of food on Level 5.
        return level == Constants.MAX_LEVEL && foodThisLevel >= Constants.FOOD_PER_LEVEL;
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}
