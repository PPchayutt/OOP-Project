import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class HotbarSlot {
    private int x, y, width, height;
    private WeaponType weaponType;
    private boolean isActive;
    private float lifespan;
    private final Image selectedFrameImage; // รูปภาพช่องที่ถูกเลือก
    private final double multiplier;
    
    public HotbarSlot(int x, int y, WeaponType weaponType, int width, int height, double multiplier) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.weaponType = weaponType;
        this.isActive = false;
        this.multiplier = multiplier;
        this.lifespan = 1.0f;
        this.selectedFrameImage = ImageManager.getImage("activeFrame");
    }
    
    public void render(Graphics g, int amount) {
        // วาดกรอบสำหรับอาวุธที่กำลังใช้อยู่ (ยกเว้นป้อมปืน)
        if (isActive && weaponType != WeaponType.TURRET) {
            if (selectedFrameImage != null) {
                g.drawImage(selectedFrameImage, x - 3, y - 3, width + 6, height + 6, null);
            } else {
            // หากไม่มีรูปให้วาดกรอบแทน
            g.setColor(Color.YELLOW);
            g.drawRect(x - 2, y - 2, width + 4, height + 4);
            g.drawRect(x - 1, y - 1, width + 2, height + 2); // วาดซ้อนเพื่อให้หนาขึ้น
            }
        }

        String weaponName = weaponType.getName();
        int maxChars = 8; // กำหนดจำนวนตัวอักษรสูงสุดที่จะแสดง
        if (weaponName.length() > maxChars) {
            weaponName = weaponName.substring(0, maxChars - 2) + ".."; // ตัดและเติม .. 
        }
        g.setColor(Color.WHITE);
        
        // ปรับขนาดฟอนต์ตามสัดส่วน
        int fontSize = Math.min((int)(7 * multiplier), height / 3);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));

        // คำนวณตำแหน่งเพื่อให้ข้อความอยู่ตรงกลาง
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(weaponName);
        int textHeight = metrics.getHeight();
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - textHeight) / 2 + metrics.getAscent();

        // วาดชื่ออาวุธตรงกลางช่อง
        g.drawString(weaponName, textX, textY);

        // วาดจำนวนอาวุธ
        if (amount > 1) {
            String amountText = "x" + amount;
            g.setColor(Color.YELLOW);  // ใช้สีเหลืองเพื่อให้เด่นชัด
            g.setFont(new Font("Arial", Font.BOLD, fontSize));

            // วาดจำนวนที่มุมขวาล่าง
            g.drawString(amountText, 
                        x + width - metrics.stringWidth(amountText) - 5, 
                        y + height - 7);
        }
        // วาดแถบเวลาที่เหลือใต้ชื่ออาวุธ (ยกเว้นป้อมปืน, อาวุธ tier 2)
//        if (weaponType != WeaponType.TURRET && weaponType != WeaponType.AK47) {
//            if (lifespan < 1.0f) {
//                // พื้นหลังแถบเวลา
//                g.setColor(Color.DARK_GRAY);
//                int barWidth = (int)(width * 0.8);
//                int barHeight = (int)(height * 0.15);
//                int barX = x + (width - barWidth) / 2;
//                int barY = y + height - barHeight - (int)(5 * multiplier);
//                g.fillRect(barX, barY, barWidth, barHeight);
//
//                // แถบเวลาที่เหลือ
//                g.setColor(Color.RED);
//                int remainingWidth = (int)(barWidth * lifespan);
//                g.fillRect(barX, barY, remainingWidth, barHeight);
//                
//                g.setColor(Color.WHITE);
//                g.drawRect(barX, barY, barWidth, barHeight);
//            }
//        }
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    // เพิ่มเมธอดสำหรับกำหนดเวลาที่เหลือของอาวุธ
    public void setLifespan(float lifespan) {
        this.lifespan = lifespan;
    }
    
    public WeaponType getWeaponType() {
        return weaponType;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}