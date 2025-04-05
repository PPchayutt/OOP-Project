
import java.awt.Graphics2D;

public interface GameScreen {
    
    void render(Graphics2D g2d, float scaleX, float scaleY);
    
    boolean handleMouseClick(int x, int y);
}
