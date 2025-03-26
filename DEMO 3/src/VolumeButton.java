
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Asus
 */
public class VolumeButton extends AbstractMenuButton {
    private boolean isIncreaseButton; // true = เพิ่ม, false = ลด
    private float volumeStep = 0.1f; // เพิ่ม/ลดครั้งละ 10%
    
    public VolumeButton(int x, int y, boolean isIncreaseButton) {
        super(x, y, isIncreaseButton ? 
                "resources/images/volume_up.png" : 
                "resources/images/volume_down.png");
        this.isIncreaseButton = isIncreaseButton;
    }
    
    public boolean isIncreaseButton() {
    return isIncreaseButton;
}
    
    @Override
    public void render(Graphics g) {
        super.render(g);
        
        // เพิ่มการแสดงระดับเสียงปัจจุบัน (ถ้าเป็นปุ่มลดเสียง)
        if (!isIncreaseButton) {
            int percentage = (int)(SoundManager.getEffectVolume() * 100);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(percentage + "%", x - 40, y + height / 2 + 5);
        }
    }
    
    @Override
    public void onClick() {
        if (isIncreaseButton) {
            SoundManager.increaseEffectVolume(volumeStep);
            // เล่นเสียงตัวอย่างเมื่อเพิ่มเสียง
            SoundManager.playSound("gun_shot");
        } else {
            SoundManager.decreaseEffectVolume(volumeStep);
            // เล่นเสียงตัวอย่างเมื่อลดเสียง
            SoundManager.playSound("gun_shot");
        }
    }
}
