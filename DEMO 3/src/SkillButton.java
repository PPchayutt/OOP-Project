
import java.awt.Graphics;
import javax.swing.JPanel;

public class SkillButton extends AbstractMenuButton {

    private final JPanel parent;

    public SkillButton(int x, int y, JPanel parent) {
        super(x, y, "resources/images/skill des2.png");
        this.parent = parent;
    }

    @Override
    public void render(Graphics g, float scaleX, float scaleY) {
        int scaledX = (int) (x * scaleX);
        int scaledY = (int) (y * scaleY);
        int scaledWidth = (int) (width * scaleX);
        int scaledHeight = (int) (height * scaleY);

        // วาดปุ่ม
        g.drawImage(buttonImage, scaledX, scaledY, scaledWidth * 2, scaledHeight * 2, null);
    }

    @Override
    public void onClick() {
        // แสดงหน้าต่างคำอธิบายสกิลใหม่
        SkillDescriptionDialog dialog = new SkillDescriptionDialog(parent);
        dialog.setVisible(true);
    }
}
