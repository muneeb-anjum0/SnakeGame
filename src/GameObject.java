import java.awt.Graphics2D;

public abstract class GameObject {
    protected int x;
    protected int y;

    public GameObject(int x, int y) {
        // The base constructor stores the grid position shared by all game objects.
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Each concrete game object decides how it wants to draw itself.
    public abstract void draw(Graphics2D g);
}
