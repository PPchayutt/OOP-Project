import java.awt.*;
import java.util.Random;

/**
 * Monster คือศัตรูธรรมดาในเกม
 * จะเคลื่อนที่ตามรูปแบบต่างๆ และพยายามเข้าหาผู้เล่น
 */
public class Monster extends Enemy {
    
    private int movementPattern;
    private int patternCounter = 0;
    private Player target;
    private static final Random random = new Random();
    
    /**
     * สร้างมอนสเตอร์ใหม่
     * @param x ตำแหน่ง x เริ่มต้น
     * @param y ตำแหน่ง y เริ่มต้น
     * @param player ผู้เล่นที่จะตามล่า
     */
    public Monster(int x, int y, Player player) {
        super(x, y, 30, 30, 50, 2, 10, 100);
        this.target = player;
        this.movementPattern = random.nextInt(3); // 0=ตรงๆ, 1=ซิกแซก, 2=วงกลม
        this.dropsPowerup = random.nextDouble() < 0.2; // 20% โอกาสที่จะดรอปพาวเวอร์อัพ
    }
    
    @Override
    public void update() {
        updateCooldowns();
        
        // คำนวณทิศทางไปหาผู้เล่น
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;
        
        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        // ปรับทิศทางให้เป็นเวกเตอร์หนึ่งหน่วย
        if (distance > 0) {
            dx = dx / distance;
            dy = dy / distance;
        }
        
        // เคลื่อนที่ตามรูปแบบที่กำหนด
        switch (movementPattern) {
            case 0: // ตรงไปหาผู้เล่น
                x += dx * speed;
                y += dy * speed;
                break;
                
            case 1: // ซิกแซก
                patternCounter++;
                x += dx * speed + Math.sin(patternCounter * 0.1) * speed;
                y += dy * speed;
                break;
                
            case 2: // วนเป็นวงกลม
                patternCounter++;
                float circleRadius = 2.0f;
                x += dx * speed + Math.cos(patternCounter * 0.05) * circleRadius;
                y += dy * speed + Math.sin(patternCounter * 0.05) * circleRadius;
                break;
        }
    }
    
    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y, width, height);
        
        // วาดแถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 5, width, 3);
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((float) health / 50 * width);
        g.fillRect((int) x, (int) y - 5, healthBarWidth, 3);
    }
    
    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }
        
        // กำหนดเวลาคูลดาวน์
        resetShootCooldown(60);
        
        // คำนวณทิศทางการยิงไปหาผู้เล่น
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;
        
        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        double angle = Math.atan2(dy, dx);
        
        // สร้างกระสุน
        return new EnemyBullet((int) x + width / 2, (int) y + height / 2, 8, 8, angle, 5, damage);
    }
}