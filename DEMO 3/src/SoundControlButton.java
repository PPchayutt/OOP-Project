
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
// สร้างคลาสปุ่มปรับเสียงใหม่
public class SoundControlButton extends AbstractMenuButton {
    private final boolean isIncrease;
    private static final float STEP = 0.1f; // เพิ่ม/ลด 10%
    
    public SoundControlButton(int x, int y, boolean isIncrease) {
        super(x, y, null); // ไม่ใช้รูปภาพ
        this.isIncrease = isIncrease;
        
        // กำหนดขนาดปุ่มเอง
        this.width = 30;
        this.height = 30;
    }
    
    @Override
    public void render(Graphics g) {
        // วาดปุ่มเอง
        g.setColor(new Color(80, 80, 80));
        g.fillRect(x, y, width, height);
        
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
        
        // วาดสัญลักษณ์ + หรือ -
        g.setColor(Color.WHITE);
        g.drawLine(x + 10, y + 15, x + 20, y + 15); // เส้นนอน
        
        if (isIncrease) {
            g.drawLine(x + 15, y + 10, x + 15, y + 20); // เส้นตั้ง (เฉพาะปุ่ม +)
        }
        
        // ถ้าเป็นปุ่มลดเสียง ให้แสดงเปอร์เซ็นต์
        if (!isIncrease) {
            int volumePercent = (int) (SoundManager.getEffectVolume() * 100);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(volumePercent + "%", x - 35, y + 20);
        }
    }
    
    @Override
    public void onClick() {
        float currentVolume = SoundManager.getEffectVolume();
        
        if (isIncrease) {
            SoundManager.setEffectVolume(Math.min(1.0f, currentVolume + STEP));
        } else {
            SoundManager.setEffectVolume(Math.max(0.0f, currentVolume - STEP));
        }
        
        // เล่นเสียงตัวอย่าง
        if (SoundManager.getEffectVolume() > 0.0f) {
            SoundManager.playSound("gun_shot");
        }
    }
}
