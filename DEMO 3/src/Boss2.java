
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Boss2 extends Boss {

    private static final Random random = new Random();

    public Boss2(int x, int y, int level) {
        super(x, y, level);
        // เพิ่มหรือแก้ไขคุณสมบัติพิเศษของ Boss2
        this.width = 100;
        this.height = 100;
        this.health = 300 * level;
        this.maxHealth = 300 * level;
        this.damage = 25;
    }

    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        if (phaseCounter >= 250) {
            phaseCounter = 0;
            phase = (phase + 1) % 3; // ลดเหลือ 3 เฟสเพื่อลดความซับซ้อน
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
                x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                y = Math.max(0, Math.min(y, 250)); // จำกัดให้อยู่ด้านบน
            }
            case 2 -> {
                // หยุดนิ่ง (เตรียมโจมตีพิเศษ)
                // ไม่มีการเคลื่อนที่
            }
        }

        // จำกัดตำแหน่งไม่ให้ออกนอกจอ
        x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
        y = Math.max(50, Math.min(y, 300));
    }

    /**
     *
     * @param g
     */
    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image boss2Image = ImageManager.getImage("boss2");
        if (boss2Image != null) {
            g.drawImage(boss2Image, (int) x, (int) y, width, height, null);
        } else {
            // บอสด่าน 2 เป็นสีม่วงเข้ม
            g.setColor(new Color(75, 0, 130));
            g.fillOval((int) x, (int) y, width, height);

            // ตาของบอส
            g.setColor(Color.GREEN);
            g.fillOval((int) x + width / 4, (int) y + height / 4, width / 5, height / 5);
            g.fillOval((int) x + width * 3 / 5, (int) y + height / 4, width / 5, height / 5);

            // ปากของบอส
            g.setColor(Color.WHITE);
            g.fillRect((int) x + width / 4, (int) y + height * 2 / 3, width / 2, height / 8);
        }

        // แถบพลังชีวิต
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / (300 * level) * width);
        g.fillRect((int) x, (int) y - 15, healthBarWidth, 10);
        g.setColor(Color.RED);
        g.drawRect((int) x, (int) y - 15, width, 10);

        // พิมพ์ข้อความแสดงสถานะใต้บอส
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Boss Lv." + level + " HP:" + health, (int) x, (int) y + height + 15);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        switch (attackPattern) {
            case 0 -> {
                // ยิงตรงลงมาเร็วขึ้น
                resetShootCooldown(25);
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height, 10, 10, Math.PI / 2, 4, damage);
            }
            case 1 -> {
                // ยิงทแยงมุมหลายทิศทาง
                resetShootCooldown(50);
                double angle = Math.PI / 4 + random.nextDouble() * Math.PI / 2;
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 3, damage);
            }
            case 2 -> {
                // ยิงตรงไปที่ผู้เล่นเร็วขึ้น
                resetShootCooldown(10);
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double angle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 4, damage);
            }
            default -> {
                // รูปแบบใหม่: ยิงกระสุนขนาดใหญ่
                resetShootCooldown(70);
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height / 2, 20, 20, Math.PI / 2, 2, damage * 2);
            }
        }
    }

    @Override
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(100);
        List<EnemyBullet> bullets = new ArrayList<>();

        // ยิงเป็นวงกลมรอบตัว 12 ทิศทาง
        for (int i = 0; i < 12; i++) {
            double angle = Math.PI * i / 6;
            bullets.add(new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 3, damage));
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // เมื่อพลังชีวิตเหลือน้อยกว่า 50% ให้บอสเร็วขึ้นและโจมตีแรงขึ้น 3 เท่า
        if (health <= 150 * level && speed == 1) {
            speed = 3;
            this.damage = this.damage * 3;
        }
    }
}
