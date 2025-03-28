
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
        this.visible = false; // ถ้า false = จะไม่แสดงภาพแต่ยังมีการชน, true = แสดงภาพ
    }

    /**
     *
     */
    @Override
    public void update() {
        // ไม่ต้องทำอะไร Block ไม่เคลื่อนที่
    }

    @Override
    public void render(Graphics g) {
        if (visible) {
            g.setColor(new Color(255, 0, 0, 100)); // สีแดงโปร่งใส
            g.fillRect((int) x, (int) y, width, height);
        }
    }

    /**
     *
     * @return
     */
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
