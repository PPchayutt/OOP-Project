import java.awt.*;

public class EnemyBullet extends Projectile {
    
    private int damage;

    public EnemyBullet(int x, int y, int width, int height, double angle, int speed, int damage) {
        super(x, y, width, height, angle, speed);
        this.damage = damage;
    }

    @Override
    public void update() {
        // เคลื่อนที่ตามทิศทางและความเร็ว
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;
        
        // ตรวจสอบว่าออกนอกหน้าจอหรือไม่
        if (x < -width || x > GamePanel.WIDTH || y < -height || y > GamePanel.HEIGHT) {
            active = false;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) x, (int) y, width, height);
    }
    
    public int getDamage() {
        return damage;
    }
}