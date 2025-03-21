
import java.awt.Graphics;

public interface MenuButton {

    void onClick();

    void render(Graphics g);

    boolean isClicked(int mouseX, int mouseY);
}
