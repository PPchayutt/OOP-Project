
import java.awt.*;

public class Block implements GameObject {

    private final float x;
    private final float y;
    private final int width;
    private final int height;
    private boolean visible;

    public Block(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = false;
    }

    @Override
    public void update() { }

    @Override
    public void render(Graphics g) {
        if (visible) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect((int) x, (int) y, width, height);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
