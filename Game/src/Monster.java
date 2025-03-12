
import java.awt.*;

public class Monster extends Enemy {

    private int movementPattern;
    private int patternCounter = 0;
    private boolean dropsPowerup;
    private Player targetPlayer;
    
    public Monster(int x, int y,Player player) {
        super(x, y, 30, 30, 50, 2, 10, 100);
        this.movementPattern = (int) (Math.random() * 2);
        this.dropsPowerup = Math.random() < 0.2;
        this.targetPlayer = player;
    }

    @Override
    public void update() {
        updateCooldowns();

        // คำนวณทิศทางไปหาผู้เล่น
        int targetX = targetPlayer.getX();
        int targetY = targetPlayer.getY();

        // คำนวณเวกเตอร์ทิศทาง
        double dx = targetX - x;
        double dy = targetY - y;

        // ปรับให้เป็นเวกเตอร์หนึ่งหน่วย (normalized)
        double length = Math.sqrt(dx*dx + dy*dy);
        if (length > 0) {
            dx = dx / length;
            dy = dy / length;
        }

        // เคลื่อนที่ตามทิศทาง
        if (movementPattern == 0) {
            // เดินตรงไปหาผู้เล่น
            x += dx * speed;
            y += dy * speed;
        } else {
            // เดินแบบซิกแซกเข้าหาผู้เล่น
            x += dx * speed + Math.sin(patternCounter * 0.1) * speed;
            y += dy * speed;
            patternCounter++;
        }

        // เช็คว่าออกนอกจอหรือไม่
        if (x < -width || x > 800 || y < -height || y > 600) {
            alive = false;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);

        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / 50 * width);
        g.fillRect(x, y - 5, healthBarWidth, 3);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(60);
        return new EnemyBullet(x + width / 2 - 4, y + height, 8, 8, Math.PI / 2, 5, damage);
    }

    public boolean dropsPowerup() {
        return dropsPowerup;
    }
}
