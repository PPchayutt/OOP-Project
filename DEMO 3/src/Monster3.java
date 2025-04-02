
import java.awt.*;
import java.util.Random;

public class Monster3 extends Monster {

    private int patternCounter = 0;
    private static final Random random = new Random();
    private float speedMultiplier = 0.4f;
    private int spawnTime = 0;

    public Monster3(int x, int y, Player player) {
        super(x, y, player);
        this.width = 35;
        this.height = 35;
        this.health = 70;
        this.maxHealth = 70;
        this.speed = 2;
        this.damage = 12;
        this.points = 150;
        this.dropsPowerup = random.nextDouble() < 0.35;
        this.speedMultiplier = 0.2f;
    }

    @Override
    public void update() {
        updateCooldowns();

        spawnTime++;

        // ลดความซับซ้อนการคำนวณความเร็ว
        if (spawnTime < 120) {
            speedMultiplier = 0.3f + (spawnTime / 600f);
        } else {
            speedMultiplier = 0.8f;
        }

        // คำนวณทิศทางไปหาผู้เล่น
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;

        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx = dx / distance;
            dy = dy / distance;
        }

        // ลดความซับซ้อนการเคลื่อนไหว เหลือเพียง 2 รูปแบบ
        patternCounter++;
        boolean simpleMovement = (patternCounter % 120 < 60);

        if (simpleMovement) {
            // รูปแบบง่าย: ตรงไปหาผู้เล่น
            x += dx * speed * speedMultiplier;
            y += dy * speed * speedMultiplier;
        } else {
            // รูปแบบซับซ้อน: วนเป็นวงกลม
            x += dx * speed * speedMultiplier + Math.cos(patternCounter * 0.02) * 1.5f;
            y += dy * speed * speedMultiplier + Math.sin(patternCounter * 0.02) * 1.5f;
        }

        // ดูแลไม่ให้ออกนอกจอ
        x = Math.max(10, Math.min(x, GamePanel.WIDTH - width - 10));
        y = Math.max(10, Math.min(y, GamePanel.HEIGHT - height - 10));
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image monster3Image = ImageManager.getImage("monster3");
        if (monster3Image != null) {
            g.drawImage(monster3Image, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(new Color(60, 170, 210));
            g.fillOval((int) x, (int) y, width, height);

            g.setColor(Color.BLACK);
            g.fillOval((int) (x + width / 4), (int) (y + height / 3), width / 5, height / 5);
            g.fillOval((int) (x + width * 3 / 5), (int) (y + height / 3), width / 5, height / 5);

            g.setColor(new Color(0, 0, 150));
            g.drawOval((int) x, (int) y, width, height);
        }

        // วาดแถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 5, width, 3);
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((float) health / 80 * width);
        g.fillRect((int) x, (int) y - 5, healthBarWidth, 3);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        // เพิ่มเงื่อนไขตรวจสอบว่าอยู่ในจอหรือไม่
        if (x < 0 || x > GamePanel.WIDTH || y < 0 || y > GamePanel.HEIGHT) {
            return null; // ไม่ยิงถ้าอยู่นอกจอ
        }

        // คูลดาวน์สั้นกว่า
        resetShootCooldown(45);

        // คำนวณทิศทางการยิงไปหาผู้เล่น
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;

        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        double angle = Math.atan2(dy, dx);

        // สร้างกระสุนที่เร็วขึ้น
        return new EnemyBullet((int) x + width / 2, (int) y + height / 2, 10, 10, angle, 6, damage);
    }
}
