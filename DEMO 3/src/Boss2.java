
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Boss2 extends Boss {

    private static final Random random = new Random();

    public Boss2(int x, int y, int level) {
        super(x, y, level);
        // เพิ่มพลังชีวิตจาก 400*level เป็น 550*level
        this.width = 100;
        this.height = 100;
        this.health = 550 * level;
        this.maxHealth = 550 * level;
        this.damage = 25;
    }

    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        // ลดระยะเวลาในการเปลี่ยนเฟสจาก 250 เป็น 200
        if (phaseCounter >= 200) {
            phaseCounter = 0;
            phase = (phase + 1) % 3;
            attackPattern = random.nextInt(3);
        }

        // รูปแบบการเคลื่อนที่ของบอส
        switch (phase) {
            case 0:
                // เพิ่มความเร็วการเคลื่อนที่
                x += speed * 1.5f * moveDirection; // เพิ่มตัวคูณจาก 1 เป็น 1.5
                if (x <= 0 || x >= GamePanel.WIDTH - width) {
                    moveDirection *= -1;
                }
                break;
            case 1:
                // เคลื่อนที่เป็นรูปคลื่นเร็วขึ้น
                x += Math.cos(phaseCounter * 0.08) * speed; // เพิ่มจาก 0.05 เป็น 0.08
                y += Math.sin(phaseCounter * 0.08) * speed;
                x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                y = Math.max(0, Math.min(y, 250));
                break;
            case 2:
                // ยังคงหยุดนิ่งเตรียมโจมตีพิเศษ
                break;
        }
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

        // พิมพ์ข้อความแสดงสถานะใต้บอส - เปลี่ยนจาก level เป็นตัวเลข 2 เลย
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Boss Lv.2 HP:" + health, (int) x, (int) y + height + 15);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        switch (attackPattern) {
            case 0:
                // ลด cooldown จาก 25 เป็น 18
                resetShootCooldown(18);
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height, 10, 10, Math.PI / 2, 4, damage);
            case 1:
                // ลด cooldown จาก 50 เป็น 35
                resetShootCooldown(35);
                double angle = Math.PI / 4 + random.nextDouble() * Math.PI / 2;
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 3, damage);
            case 2:
                // ลด cooldown จาก 10 เป็น 8
                resetShootCooldown(8);
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, targetAngle, 4, damage);
            default:
                resetShootCooldown(40);
                // เพิ่มขนาดกระสุนใหญ่ขึ้น
                return new EnemyBullet((int) x + width / 2 - 15, (int) y + height / 2, 25, 25, Math.PI / 2, 2, damage * 2);
        }
    }

    @Override
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        // ลด cooldown จาก 100 เป็น 80
        resetShootCooldown(80);
        List<EnemyBullet> bullets = new ArrayList<>();

        // เพิ่มจำนวนกระสุนจาก 12 เป็น 16
        for (int i = 0; i < 16; i++) {
            double angle = Math.PI * i / 8;
            bullets.add(new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 3, damage));
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // เปลี่ยนเงื่อนไขจาก 200*level เป็น 275*level (50% ของเลือด)
        if (health <= 275 * level && speed == 1) {
            speed = 3;
            this.damage = this.damage * 3; // เพิ่มจาก 2 เป็น 3 เท่า
        }

        // เปลี่ยนเงื่อนไขจาก 100*level เป็น 137*level (25% ของเลือด)
        if (health <= 137 * level && speed == 3) {
            speed = 4;
            this.damage = (int) (this.damage * 1.5);
        }
    }
}
