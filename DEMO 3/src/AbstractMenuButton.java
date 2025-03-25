
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public abstract class AbstractMenuButton implements MenuButton {

    protected int x, y, width, height;
    protected Image buttonImage;

    public AbstractMenuButton(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.buttonImage = new ImageIcon(imagePath).getImage();
        this.width = buttonImage.getWidth(null);
        this.height = buttonImage.getHeight(null);

        if (this.width <= 0) {
            this.width = 150;
        }
        if (this.height <= 0) {
            this.height = 40;
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(buttonImage, x, y, null);

    }

    @Override
    public boolean isClicked(int mouseX, int mouseY) {
        int extraWidth = (int) (width * 0.2);
        int extraHeight = (int) (height * 0.2);

        return mouseX >= (x - extraWidth / 2) && mouseX <= (x + width + extraWidth / 2)
                && mouseY >= (y - extraHeight / 2) && mouseY <= (y + height + extraHeight / 2);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
