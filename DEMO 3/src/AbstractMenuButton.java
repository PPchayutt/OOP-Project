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
    }
    
    @Override
    public void render(Graphics g) {
        g.drawImage(buttonImage, x, y, null);
    }
    
    @Override
    public boolean isClicked(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && 
               mouseY >= y && mouseY <= y + height;
    }
}