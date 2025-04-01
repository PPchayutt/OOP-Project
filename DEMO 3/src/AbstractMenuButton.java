
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
        // คำนวณพื้นที่คลิกให้ตรงกับการแสดงผลที่ขยาย 2 เท่า
        int displayWidth = width * 2;  // ขยายความกว้าง 2 เท่า
        int displayHeight = height * 2; // ขยายความสูง 2 เท่า

        // คำนวณพื้นที่คลิกเพิ่มขึ้นอีกนิดเพื่อความสะดวกในการคลิก
        int extraWidth = (int) (displayWidth * 0.1);
        int extraHeight = (int) (displayHeight * 0.1);

        return mouseX >= x && mouseX <= (x + displayWidth)
                && mouseY >= y && mouseY <= (y + displayHeight);
    }
}
