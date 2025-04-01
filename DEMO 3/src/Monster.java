
import java.awt.*;
import java.util.List;
import java.util.Random;

public class Monster extends Enemy {

    private final int movementPattern;
    private int patternCounter = 0;
    final Player target;
    private static final Random random = new Random();
    private float speedMultiplier = 0.15f;
    private int spawnTime = 0;

    public Monster(int x, int y, Player player) {
        super(x, y, 30, 30, 45, 1, 8, 100);
        this.target = player;
        this.movementPattern = random.nextInt(3);
        this.dropsPowerup = random.nextDouble() < 0.35;
        this.speedMultiplier = 0.2f;
    }

    protected void avoidOtherMonsters(List<Enemy> monsters) {
        for (Enemy other : monsters) {
            if (this != other && collidesWith(other)) {
                // คำนวณทิศทางหลบ
                float dx = x - other.getX();
                float dy = y - other.getY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance > 0) {
                    // ผลักออกจากกัน
                    float pushForce = 1.0f;
                    x += (dx / distance) * pushForce;
                    y += (dy / distance) * pushForce;
                } else {
                    // ถ้าซ้อนทับพอดี ผลักในทิศทางสุ่ม
                    x += (random.nextDouble() * 2 - 1) * 2;
                    y += (random.nextDouble() * 2 - 1) * 2;
                }
            }
        }
    }

    @Override
    public void update() {
        updateCooldowns();

        // เพิ่มเวลาที่มอนสเตอร์อยู่ในเกม
        spawnTime++;

        // ค่อยๆ เพิ่มความเร็วจนถึงความเร็วปกติ แต่ใช้เวลานานขึ้น
        if (spawnTime < 300) {
            speedMultiplier = Math.min(0.2f + (spawnTime / 1000f), 1.0f);
        } else {
            speedMultiplier = 1.0f;
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

        // เคลื่อนที่ตามรูปแบบที่กำหนด
        switch (movementPattern) {
            case 0 -> {
                // ตรงไปหาผู้เล่น
                x += dx * speed * speedMultiplier;
                y += dy * speed * speedMultiplier;
            }
            case 1 -> {
                // ซิกแซก
                patternCounter++;
                x += dx * speed * speedMultiplier + Math.sin(patternCounter * 0.05) * speed * speedMultiplier;
                y += dy * speed * speedMultiplier;
            }
            case 2 -> {
                // วนเป็นวงกลม
                patternCounter++;
                float circleRadius = 1.5f;
                x += dx * speed * speedMultiplier + Math.cos(patternCounter * 0.03) * circleRadius;
                y += dy * speed * speedMultiplier + Math.sin(patternCounter * 0.03) * circleRadius;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // รูปภาพมอนสเตอร์
        g.drawImage(ImageManager.getImage("monster"), (int) x, (int) y, width, height, null);

        // แถบพลังชีวิต
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

        // ตรวจสอบว่าอยู่ในจอหรือไม่
        if (x < 0 || x > GamePanel.WIDTH || y < 0 || y > GamePanel.HEIGHT) {
            return null; // ไม่ยิงถ้าอยู่นอกจอ
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
