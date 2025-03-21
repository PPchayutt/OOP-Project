
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

        // ถ้า width หรือ height เป็น -1 ให้กำหนดค่าเริ่มต้น
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

        // สำหรับดีบัก แสดงพื้นที่ hitbox (ถ้าต้องการ)
        // g.drawRect(x, y, width, height);
    }

    @Override
    public boolean isClicked(int mouseX, int mouseY) {
        // เพิ่มพื้นที่ hitbox ให้ใหญ่ขึ้น (ขยาย 20% ทั้งความกว้างและความสูง)
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
