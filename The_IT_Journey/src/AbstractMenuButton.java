
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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(buttonImage, x, y, null);
    }

    public void render(Graphics g, float scaleX, float scaleY) {
        int scaledX = (int) (x * scaleX);
        int scaledY = (int) (y * scaleY);
        int scaledWidth = (int) (width * scaleX);
        int scaledHeight = (int) (height * scaleY);

        g.drawImage(buttonImage, scaledX, scaledY, scaledWidth, scaledHeight, null);
    }

    @Override
    public boolean isClicked(int mouseX, int mouseY) {
        int displayWidth = width * 2;
        int displayHeight = height * 2;

        return mouseX >= x && mouseX <= (x + displayWidth)
                && mouseY >= y && mouseY <= (y + displayHeight);
    }
}
