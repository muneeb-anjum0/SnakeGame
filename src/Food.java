import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.util.Random;

public class Food extends GameObject {
    private Random random;

    public Food() {
        super(0, 0);
        // Food only needs random placement, so the constructor prepares one random source.
        this.random = new Random();
    }

    public void spawn(Snake snake) {
        // Keep rolling random grid positions until the food lands in an empty cell.
        int x, y;
        do {
            x = random.nextInt(Constants.BOARD_COLS);
            y = random.nextInt(Constants.BOARD_ROWS);
        } while (snake.occupiesPosition(x, y));

        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Graphics2D g) {
        int pixelX = Constants.BOARD_X + x * Constants.CELL_SIZE;
        int pixelY = Constants.BOARD_Y + y * Constants.CELL_SIZE;
        double pulse = (Math.sin(System.currentTimeMillis() / 120.0) + 1.0) / 2.0;
        int aura = 8 + (int) (pulse * 8);
        int size = Constants.CELL_SIZE - 5 + (int) (pulse * 2);
        int offset = (Constants.CELL_SIZE - size) / 2;

        g.setColor(new Color(Constants.ACCENT_PINK.getRed(), Constants.ACCENT_PINK.getGreen(), Constants.ACCENT_PINK.getBlue(), 70));
        g.fillOval(pixelX + Constants.CELL_SIZE / 2 - aura, pixelY + Constants.CELL_SIZE / 2 - aura, aura * 2, aura * 2);

        g.setPaint(new GradientPaint(pixelX, pixelY, Constants.FOOD_COLOR,
                pixelX + Constants.CELL_SIZE, pixelY + Constants.CELL_SIZE, Constants.ACCENT_GOLD));
        g.fillOval(pixelX + offset, pixelY + offset, size, size);

        // A tiny highlight keeps the food looking a little less flat.
        g.setColor(new Color(255, 255, 255, 195));
        g.fillOval(pixelX + offset + 4, pixelY + offset + 3, 5, 5);
    }
}
