
import java.awt.Graphics;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SkillButton extends AbstractMenuButton {

    private final JPanel parent;

    public SkillButton(int x, int y, JPanel parent) {
        super(x, y, "resources/images/skill des2.png");
        this.parent = parent;
    }

    /**
     *
     * @param g
     * @param scaleX
     * @param scaleY
     */
    @Override
    public void render(Graphics g, float scaleX, float scaleY) {
        int scaledX = (int) (x * scaleX);
        int scaledY = (int) (y * scaleY);
        int scaledWidth = (int) (width * scaleX);
        int scaledHeight = (int) (height * scaleY);

        // วาดปุ่มขนาดใหญ่กว่าภาพต้นฉบับ (ขยาย 2 เท่า)
        g.drawImage(buttonImage, scaledX, scaledY, scaledWidth * 2, scaledHeight * 2, null);
    }

    @Override
    public void onClick() {
        JOptionPane.showMessageDialog(parent, """
                                              This game is a 2D bullet hell pixel game.
                                              Control  with WASD and shoot with Left Click.
                                              Collect buffs to strengthen your character.""",
                "Skill Description", JOptionPane.INFORMATION_MESSAGE);
    }
}
