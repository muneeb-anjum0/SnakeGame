import java.awt.Color;

public class Constants {
    // Retro typeface used across the entire game UI.
    public static final String RETRO_FONT_FAMILY = "Courier New";

    // Window dimensions
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;

    // Game board dimensions
    public static final int CELL_SIZE = 20;
    public static final int BOARD_COLS = 48;
    public static final int BOARD_ROWS = 31;
    public static final int BOARD_WIDTH = BOARD_COLS * CELL_SIZE;
    public static final int BOARD_HEIGHT = BOARD_ROWS * CELL_SIZE;
    public static final int BOARD_X = (WINDOW_WIDTH - BOARD_WIDTH) / 2;
    public static final int BOARD_Y = 75;

    // UI Layout
    public static final int HEADER_HEIGHT = 70;
    public static final int FOOTER_HEIGHT = 35;

    // Game mechanics
    public static final int INITIAL_DELAY = 150;
    public static final int MAX_LEVEL = 5;
    public static final int FOOD_PER_LEVEL = 8;  // Longer levels
    
    // Level-up animation
    public static final int LEVEL_UP_FLASH_DURATION = 167;  // ms per flash (3 flashes in 1 second)
    public static final int LEVEL_UP_FLASH_COUNT = 3;       // 3 flashes total

    // Colors
    public static final Color BACKGROUND_COLOR = new Color(5, 6, 12);
    public static final Color PANEL_COLOR = new Color(10, 12, 22);
    public static final Color TEXT_COLOR = new Color(248, 248, 248);
    public static final Color MUTED_TEXT_COLOR = new Color(181, 200, 222);
    public static final Color ACCENT_GREEN = new Color(54, 255, 166);
    public static final Color ACCENT_BLUE = new Color(69, 205, 255);
    public static final Color ACCENT_PINK = new Color(255, 58, 169);
    public static final Color ACCENT_GOLD = new Color(255, 210, 74);
    public static final Color DANGER_RED = new Color(255, 65, 91);
    public static final Color SNAKE_HEAD_COLOR = new Color(234, 255, 95);
    public static final Color SNAKE_BODY_COLOR = new Color(40, 247, 181);
    public static final Color FOOD_COLOR = new Color(255, 68, 147);
    public static final Color BUTTON_HOVER_COLOR = new Color(54, 255, 166);
    public static final Color BUTTON_NORMAL_COLOR = new Color(13, 16, 29);
    public static final Color BLOOD_RED = new Color(255, 45, 80);
}
