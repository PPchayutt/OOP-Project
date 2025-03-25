
import java.awt.*;
import java.util.Random;

/**
 * Monster คือศัตรูธรรมดาในเกม จะเคลื่อนที่ตามรูปแบบต่างๆ และพยายามเข้าหาผู้เล่น
 */
public class Monster extends Enemy {

    private final int movementPattern;
    private int patternCounter = 0;
    private final Player target;
    private static final Random random = new Random();
    private float speedMultiplier = 0.3f; // เพิ่มตัวแปรใหม่เพื่อทำให้เคลื่อนที่ช้าลงตอนเริ่มต้น
    private int spawnTime = 0; // นับเวลาตั้งแต่เกิด

    /**
     * สร้างมอนสเตอร์ใหม่
     */
    public Monster(int x, int y, Player player) {
        super(x, y, 30, 30, 50, 1, 10, 100); // เปลี่ยนความเร็วจาก 2 เป็น 1
        this.target = player;
        this.movementPattern = random.nextInt(3);
        this.dropsPowerup = random.nextDouble() < 0.2;
    }

    @Override
    public void update() {
        updateCooldowns();

        // เพิ่มเวลาที่มอนสเตอร์อยู่ในเกม
        spawnTime++;

        // ค่อยๆ เพิ่มความเร็วจนถึงความเร็วปกติ
        if (spawnTime < 180) { // 3 วินาที (60 FPS × 3)
            speedMultiplier = Math.min(0.3f + (spawnTime / 600f), 1.0f);
        } else {
            speedMultiplier = 1.0f; // ความเร็วปกติหลังจาก 3 วินาที
        }

        // คำนวณทิศทางไปหาผู้เล่น (โค้ดเดิม)
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;

        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx = dx / distance;
            dy = dy / distance;
        }

        // เคลื่อนที่ตามรูปแบบที่กำหนด แต่คูณด้วย speedMultiplier ให้ช้าลง
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
                float circleRadius = 1.5f; // ลดรัศมีการวนลงจาก 2.0f
                x += dx * speed * speedMultiplier + Math.cos(patternCounter * 0.03) * circleRadius;
                y += dy * speed * speedMultiplier + Math.sin(patternCounter * 0.03) * circleRadius;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // วาดรูปภาพมอนสเตอร์
        g.drawImage(ImageManager.getImage("monster"), (int) x, (int) y, width, height, null);

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
