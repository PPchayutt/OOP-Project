import java.awt.Graphics;

public interface GameState {
    void update();
    void render(Graphics g);
    void handleMouseClick(int x, int y);
}