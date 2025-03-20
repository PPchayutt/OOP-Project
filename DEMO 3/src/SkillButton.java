import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SkillButton extends AbstractMenuButton {
    private JPanel parent;
    
    public SkillButton(int x, int y, JPanel parent) {
        super(x, y, "resources/images/skill des2.png");
        this.parent = parent;
    }
    
    @Override
    public void onClick() {
        JOptionPane.showMessageDialog(parent, 
            "เกมนี้เป็นเกม pixel 2D แนว bullet hell\n" +
            "ควบคุมด้วย WASD และยิงด้วย Left Click\n" +
            "สะสมบัฟเพื่อเพิ่มความแข็งแกร่งให้ตัวละคร!", 
            "ความสามารถในเกม", JOptionPane.INFORMATION_MESSAGE);
    }
}