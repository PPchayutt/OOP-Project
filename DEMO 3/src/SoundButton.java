
import java.awt.Image;
import javax.swing.ImageIcon;

public class SoundButton extends AbstractMenuButton {

    private boolean isMuted = false;
    private final Image unmutedImage;
    private final Image mutedImage;
    private final MenuPanel menuPanel;

    public SoundButton(int x, int y, MenuPanel menuPanel) {
        super(x, y, "resources/images/Unmute.png");
        this.menuPanel = menuPanel;
        this.unmutedImage = buttonImage;
        this.mutedImage = new ImageIcon("resources/images/mute.png").getImage();
    }

    @Override
    public void onClick() {
        isMuted = !isMuted;
        buttonImage = isMuted ? mutedImage : unmutedImage;
        if (isMuted) {
            menuPanel.stopMusic();
        } else {
            menuPanel.playMusic();
        }

        // เพิ่มบรรทัดนี้เพื่อบังคับให้วาดหน้าจอใหม่
        menuPanel.repaint();
    }
}
