/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Theep
 */
import java.awt.*;

public class PowerUp extends Entity {
    private PowerUpType type;
    private boolean active = true;
    private static final int SIZE = 30;
    
    public enum PowerUpType {
        RAPID_FIRE(Color.YELLOW, "RF"),
        STRONG_BULLET(Color.RED, "SB"),
        HEALTH(Color.GREEN, "HP");
        
        private final Color color;
        private final String text;
        
        PowerUpType(Color color, String text) {
            this.color = color;
            this.text = text;
        }
    }
    
    public PowerUp(float x, float y, PowerUpType type) {
        super(x, y);
        this.type = type;
        this.width = SIZE;
        this.height = SIZE;
    }
    
    @Override
    public void update() {
        // อาจจะเพิ่มการเคลื่อนที่หรือหมุน
    }
    
    @Override
    public void render(Graphics g) {
        if (!active) return;
        
        g.setColor(type.color);
        g.fillRect((int)x, (int)y, width, height);
        
        // วาดตัวอักษรกำกับ
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g.getFontMetrics();
        int textX = (int)x + (width - fm.stringWidth(type.text)) / 2;
        int textY = (int)y + (height + fm.getAscent()) / 2;
        g.drawString(type.text, textX, textY);
    }
    
    public PowerUpType getType() {
        return type;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void deactivate() {
        active = false;
    }
}
