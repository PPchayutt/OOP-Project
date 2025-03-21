
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
     */
    @Override
    public void render(Graphics g) {
        // วาดปุ่มขนาดใหญ่กว่าภาพต้นฉบับ (ขยาย 1.5 เท่า)
        g.drawImage(buttonImage, x, y, width * 2, height * 2, null);
    }

    @Override
    public void onClick() {
        JOptionPane.showMessageDialog(parent,
                "This game is a 2D bullet hell pixel game.\n"
                + "Control  with WASD and shoot with Left Click.\n"
                + "Collect buffs to strengthen your character.",
                "Skill Description", JOptionPane.INFORMATION_MESSAGE);
    }
}
