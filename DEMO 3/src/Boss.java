
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Boss extends Enemy {

    private int phaseCounter = 0;
    private int phase = 0;
    private int moveDirection = 1;
    private int attackPattern = 0;
    private static final Random random = new Random();
    private final int level;

    public Boss(int x, int y, int level) {
        super(x, y, 80, 80, 200 * level, 1, 20, 1000 * level);
        this.level = level;
    }

    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        if (phaseCounter >= 300) {
            phaseCounter = 0;
            phase = (phase + 1) % 3;
            attackPattern = random.nextInt(3);
        }

        // รูปแบบการเคลื่อนที่ของบอส
        switch (phase) {
            case 0 -> {
                // เคลื่อนที่จากซ้ายไปขวา
                x += speed * moveDirection;
                if (x <= 0 || x >= GamePanel.WIDTH - width) {
                    moveDirection *= -1;
                }
            }
            case 1 -> {
                // เคลื่อนที่เป็นรูปคลื่น
                x += Math.cos(phaseCounter * 0.05) * speed;
                y += Math.sin(phaseCounter * 0.05) * speed;
                // จำกัดไม่ให้ออกนอกหน้าจอ
                x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                y = Math.max(0, Math.min(y, 300)); // บอสอยู่แค่ด้านบนของหน้าจอ
            }
            default -> {
            }
        }
        // หยุดนิ่ง (เตรียมโจมตีพิเศษ)
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image bossImage = ImageManager.getImage("boss");
        if (bossImage != null) {
            g.drawImage(bossImage, (int) x, (int) y, width, height, null);

            // วาดแถบพลังชีวิตเหนือบอส
            g.setColor(Color.RED);
            g.fillRect((int) x, (int) y - 10, width, 5);
            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((double) health / (200 * level) * width);
            g.fillRect((int) x, (int) y - 10, healthBarWidth, 5);
        } else {
            // ถ้าไม่มีรูปภาพให้วาดรูปทรงพื้นฐานแทน
            g.setColor(new Color(180, 0, 0));
            g.fillRect((int) x, (int) y, width, height);

            // ตาของบอส
            g.setColor(Color.YELLOW);
            g.fillOval((int) x + width / 4, (int) y + height / 4, width / 5, height / 5);
            g.fillOval((int) x + width * 3 / 5, (int) y + height / 4, width / 5, height / 5);

            // ปากของบอส
            g.setColor(Color.BLACK);
            g.fillRect((int) x + width / 4, (int) y + height * 2 / 3, width / 2, height / 8);

            // แถบพลังชีวิต
            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((double) health / (200 * level) * width);
            g.fillRect((int) x, (int) y - 10, healthBarWidth, 5);
        }

        // พิมพ์ข้อความแสดงสถานะใต้บอส (เพื่อการดีบัก)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("Boss Lv." + level + " HP:" + health, (int) x, (int) y + height + 15);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        switch (attackPattern) {
            case 0 -> {
                // ยิงตรงลงมา
                resetShootCooldown(30);
                return new EnemyBullet((int) x + width / 2 - 4, (int) y + height, 8, 8, Math.PI / 2, 3, damage);
            }
            case 1 -> {
                // ยิงทแยงมุม
                resetShootCooldown(60);

                double angle = Math.PI / 3 + random.nextDouble() * Math.PI / 3;
                return new EnemyBullet((int) x + width / 2 - 4, (int) y + height, 8, 8, angle, 2, damage);
            }
            default -> {
                // ยิงตรงไปที่ผู้เล่น (ต้องปรับให้เหมาะสมหลังจากเพิ่ม Player)
                resetShootCooldown(15);

                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;

                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double angle = Math.atan2(dy, dx);

                return new EnemyBullet((int) x + width / 2 - 4, (int) y + height / 2, 8, 8, angle, 3, damage);
            }
        }
    }

    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(120);

        List<EnemyBullet> bullets = new ArrayList<>();

        // ยิงรอบตัว
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            bullets.add(new EnemyBullet((int) x + width / 2 - 4, (int) y + height / 2, 8, 8, angle, 2, damage));
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // เมื่อพลังชีวิตเหลือครึ่งเดียว ให้บอสเร็วขึ้นและโจมตีแรงขึ้น
        if (health <= 100 * level && speed == 1) {
            speed = 2;
            this.damage = this.damage * 2;
        }
    }
}
