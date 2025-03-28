import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.ImageIcon;

public class SoundButton extends AbstractMenuButton {

    private final Image unmutedImage;
    private final Image mutedImage;
    private final MenuPanel menuPanel;

    public SoundButton(int x, int y, MenuPanel menuPanel) {
        super(x, y, "resources/images/Unmute.png");
        this.menuPanel = menuPanel;
        this.unmutedImage = buttonImage;
        this.mutedImage = new ImageIcon("resources/images/mute.png").getImage();
        
         // อัพเดทรูปภาพปุ่มตามสถานะปัจจุบันของ SoundManager
        updateButtonImage();
    }
    
     // เพิ่มเมธอดสำหรับอัพเดทรูปภาพปุ่มตามสถานะการปิดเสียง
    private void updateButtonImage() {
        buttonImage = SoundManager.isMusicMuted() ? mutedImage : unmutedImage;
    }
    
    @Override
    public void render(Graphics g) {
        // อัพเดทรูปภาพก่อนวาด
        updateButtonImage();
        super.render(g);
    }
    
    @Override
    public void render(Graphics g, float scaleX, float scaleY) {
        // อัพเดทรูปภาพก่อนวาด
        updateButtonImage();
        super.render(g, scaleX, scaleY);
    }
    
@Override
    public void onClick() {
        // สลับสถานะการปิดเสียงเพลงใน SoundManager
        boolean isMuted = SoundManager.isMusicMuted();
        SoundManager.setMusicMuted(!isMuted);
        
        // อัพเดทรูปภาพปุ่ม
        updateButtonImage();
        
        // จัดการเสียงในเมนู
        if (SoundManager.isMusicMuted()) {
            menuPanel.stopMusic();
        } else {
            menuPanel.playMusic();
        }

        // เพิ่มบรรทัดนี้เพื่อบังคับให้วาดหน้าจอใหม่
        menuPanel.repaint();
    }
}