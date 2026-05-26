import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;

public class Snake extends GameObject {
    private LinkedList<Point> body;
    private Direction currentDirection;
    private Direction nextDirection;
    private boolean growOnNextMove;

    public Snake(int startX, int startY) {
        super(startX, startY);
        // The constructor builds a starting snake of length three.
        this.body = new LinkedList<>();
        this.currentDirection = Direction.RIGHT;
        this.nextDirection = Direction.RIGHT;
        this.growOnNextMove = false;

        // Initialize snake with 3 segments
        body.addLast(new Point(startX, startY));
        body.addLast(new Point(startX - 1, startY));
        body.addLast(new Point(startX - 2, startY));
    }

    public void reset(int startX, int startY) {
        // Reset gives the player a fresh snake without creating a new object.
        body.clear();
        body.addLast(new Point(startX, startY));
        body.addLast(new Point(startX - 1, startY));
        body.addLast(new Point(startX - 2, startY));
        currentDirection = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        growOnNextMove = false;
    }

    public void move() {
        // First we apply the requested direction, but only when it is legal.
        if (!currentDirection.isOpposite(nextDirection)) {
            currentDirection = nextDirection;
        }

        Point head = body.getFirst();
        int newX = head.x;
        int newY = head.y;

        // The new head is calculated from the current direction.
        switch (currentDirection) {
            case UP:
                newY--;
                break;
            case DOWN:
                newY++;
                break;
            case LEFT:
                newX--;
                break;
            case RIGHT:
                newX++;
                break;
        }

        // Add the new head to the front of the linked list.
        body.addFirst(new Point(newX, newY));

        // If the snake just ate food, we keep the tail once to make it grow.
        if (!growOnNextMove) {
            body.removeLast();
        } else {
            growOnNextMove = false;
        }
    }

    public void moveWithWrapping() {
        // Move for endless mode with wrapping (toroidal effect)
        if (!currentDirection.isOpposite(nextDirection)) {
            currentDirection = nextDirection;
        }

        Point head = body.getFirst();
        int newX = head.x;
        int newY = head.y;

        // The new head is calculated from the current direction.
        switch (currentDirection) {
            case UP:
                newY--;
                break;
            case DOWN:
                newY++;
                break;
            case LEFT:
                newX--;
                break;
            case RIGHT:
                newX++;
                break;
        }

        // Wrap around board edges (toroidal effect)
        newX = (newX + Constants.BOARD_COLS) % Constants.BOARD_COLS;
        newY = (newY + Constants.BOARD_ROWS) % Constants.BOARD_ROWS;

        // Add the new head to the front of the linked list.
        body.addFirst(new Point(newX, newY));

        // If the snake just ate food, we keep the tail once to make it grow.
        if (!growOnNextMove) {
            body.removeLast();
        } else {
            growOnNextMove = false;
        }
    }

    public void grow() {
        // Growth is delayed until the next movement so the animation looks natural.
        growOnNextMove = true;
    }

    public void setDirection(Direction direction) {
        nextDirection = direction;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public LinkedList<Point> getBody() {
        return body;
    }

    public boolean hasSelfCollision() {
        Point head = body.getFirst();
        // The head should never overlap any of the body segments behind it.
        for (int i = 1; i < body.size(); i++) {
            if (body.get(i).equals(head)) {
                return true;
            }
        }
        return false;
    }

    public boolean occupiesPosition(int x, int y) {
        for (Point segment : body) {
            if (segment.x == x && segment.y == y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        drawSnake(g, Constants.SNAKE_BODY_COLOR, Constants.SNAKE_HEAD_COLOR, Constants.ACCENT_BLUE);
    }

    public void drawWithGoldenHue(Graphics2D g) {
        drawSnake(g, new Color(255, 190, 58), new Color(255, 244, 111), Constants.ACCENT_GOLD);
    }

    private void drawSnake(Graphics2D g, Color bodyColor, Color headColor, Color glowColor) {
        // Body segments are drawn first so the head can sit on top cleanly.
        for (int i = 1; i < body.size(); i++) {
            Point segment = body.get(i);
            int pixelX = Constants.BOARD_X + segment.x * Constants.CELL_SIZE;
            int pixelY = Constants.BOARD_Y + segment.y * Constants.CELL_SIZE;
            int size = Constants.CELL_SIZE - 3;
            int offset = 2;
            float fade = 1f - (i / (float) Math.max(body.size(), 1));
            int alpha = 150 + (int) (fade * 95);

            g.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 45));
            g.fillRoundRect(pixelX - 2, pixelY - 2, Constants.CELL_SIZE + 4, Constants.CELL_SIZE + 4, 11, 11);

            g.setPaint(new GradientPaint(pixelX, pixelY,
                    new Color(bodyColor.getRed(), bodyColor.getGreen(), bodyColor.getBlue(), alpha),
                    pixelX + size, pixelY + size,
                    new Color(Constants.ACCENT_BLUE.getRed(), Constants.ACCENT_BLUE.getGreen(), Constants.ACCENT_BLUE.getBlue(), Math.max(75, alpha - 60))));
            g.fillRoundRect(pixelX + offset, pixelY + offset, size, size, 8, 8);

            g.setColor(new Color(255, 255, 255, 55));
            g.drawRoundRect(pixelX + offset, pixelY + offset, size, size, 8, 8);
        }

        // The head gets a brighter color and a simple eye detail to give it personality.
        if (body.size() > 0) {
            Point head = body.getFirst();
            int pixelX = Constants.BOARD_X + head.x * Constants.CELL_SIZE;
            int pixelY = Constants.BOARD_Y + head.y * Constants.CELL_SIZE;
            int size = Constants.CELL_SIZE - 2;
            int offset = 1;

            g.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 90));
            g.fillOval(pixelX - 8, pixelY - 8, Constants.CELL_SIZE + 16, Constants.CELL_SIZE + 16);

            g.setPaint(new GradientPaint(pixelX, pixelY, headColor,
                    pixelX + Constants.CELL_SIZE, pixelY + Constants.CELL_SIZE, Constants.ACCENT_GREEN));
            g.fillRoundRect(pixelX + offset, pixelY + offset, size, size, 9, 9);
            g.setColor(new Color(255, 255, 255, 175));
            g.drawRoundRect(pixelX + offset, pixelY + offset, size, size, 9, 9);

            // Draw simple eyes based on direction
            g.setColor(Color.BLACK);
            int eyeSize = 4;
            if (currentDirection == Direction.RIGHT) {
                g.fillOval(pixelX + 10, pixelY + 4, eyeSize, eyeSize);
                g.fillOval(pixelX + 10, pixelY + 11, eyeSize, eyeSize);
            } else if (currentDirection == Direction.LEFT) {
                g.fillOval(pixelX + 5, pixelY + 4, eyeSize, eyeSize);
                g.fillOval(pixelX + 5, pixelY + 11, eyeSize, eyeSize);
            } else if (currentDirection == Direction.UP) {
                g.fillOval(pixelX + 4, pixelY + 5, eyeSize, eyeSize);
                g.fillOval(pixelX + 11, pixelY + 5, eyeSize, eyeSize);
            } else if (currentDirection == Direction.DOWN) {
                g.fillOval(pixelX + 4, pixelY + 10, eyeSize, eyeSize);
                g.fillOval(pixelX + 11, pixelY + 10, eyeSize, eyeSize);
            }
        }
    }
}
