
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Boss3 extends Boss {

    private static final Random random = new Random();

    public Boss3(int x, int y, int level) {
        super(x, y, level);
        this.width = 100;
        this.height = 100;
        this.health = 700 * level;
        this.maxHealth = 700 * level;
        this.damage = 25;
    }

    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        if (phaseCounter >= 180) {
            phaseCounter = 0;
            phase = (phase + 1) % 3;
            attackPattern = random.nextInt(3);
        }

        // รูปแบบการเคลื่อนที่ของบอส
        switch (phase) {
            case 0 -> {
                // เคลื่อนที่แบบซับซ้อน
                x += speed * 1.8f * moveDirection; // เพิ่มความเร็ว
                y += Math.sin(phaseCounter * 0.1) * 2; // เคลื่อนที่ขึ้นลง
                if (x <= 0 || x >= GamePanel.WIDTH - width) {
                    moveDirection *= -1;
                }
            }
            case 1 -> {
                // เคลื่อนที่เป็นรูปเลข 8
                x += Math.cos(phaseCounter * 0.07) * speed * 2;
                y += Math.sin(phaseCounter * 0.14) * speed * 1.5;
                x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                y = Math.max(0, Math.min(y, 300));
            }
            case 2 -> {
                // หยุดนิ่งแล้วเคลื่อนที่แบบกระตุก
                if (phaseCounter % 30 == 0) {
                    x += (random.nextDouble() - 0.5) * width;
                    y += (random.nextDouble() - 0.5) * height;
                    x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                    y = Math.max(50, Math.min(y, 300));
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image boss3Image = ImageManager.getImage("boss3");
        if (boss3Image != null) {
            g.drawImage(boss3Image, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(new Color(255, 213, 170));
            g.fillOval((int) x, (int) y, width, height);

            g.setColor(Color.BLACK);
            g.fillArc((int) x, (int) y, width, height / 2, 0, 180);

            g.setColor(new Color(139, 69, 19));
            g.fillRect((int) (x + width / 4), (int) (y + height / 3) - 5, width / 5, 3);
            g.fillRect((int) (x + width / 2), (int) (y + height / 3) - 5, width / 5, 3);

            g.setColor(Color.BLACK);
            g.drawLine((int) (x + width / 4), (int) (y + height / 3), (int) (x + width / 4 + width / 5), (int) (y + height / 3));
            g.drawLine((int) (x + width / 2), (int) (y + height / 3), (int) (x + width / 2 + width / 5), (int) (y + height / 3));

            g.setColor(new Color(220, 170, 140));
            int[] xPointsNose = {(int) (x + width / 2), (int) (x + width / 2 - 5), (int) (x + width / 2 + 5)};
            int[] yPointsNose = {(int) (y + height / 2), (int) (y + height / 2 + 10), (int) (y + height / 2 + 10)};
            g.fillPolygon(xPointsNose, yPointsNose, 3);

            g.setColor(new Color(200, 120, 120));
            g.drawArc((int) (x + width / 3), (int) (y + height * 2 / 3), width / 3, height / 8, 0, 180);
        }

        // แถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 25, width, 20);
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / (700 * level) * width);
        g.fillRect((int) x, (int) y - 25, healthBarWidth, 20);

        // กรอบแถบพลังชีวิต
        g.setColor(Color.WHITE);
        g.drawRect((int) x, (int) y - 25, width, 20);

        // พิมพ์ข้อความแสดงสถานะใต้บอส
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Boss Lv.3 HP:" + health, (int) x, (int) y + height + 15);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        switch (attackPattern) {
            case 0 -> {
                resetShootCooldown(15);
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height, 10, 10, Math.PI / 2, 5, damage);
            }
            case 1 -> {
                // ยิง 3 นัดติด
                if (phaseCounter % 5 == 0 && phaseCounter % 15 < 10) {
                    resetShootCooldown(5);
                    double angle = Math.PI / 4 + random.nextDouble() * Math.PI / 2;
                    return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 6, damage);
                }
                return null;
            }
            case 2 -> {
                // ล็อคเป้าผู้เล่น
                resetShootCooldown(7);
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 12, 12, targetAngle, 7, damage);
            }
            default -> {
                // กระสุนใหญ่
                resetShootCooldown(30);
                return new EnemyBullet((int) x + width / 2 - 15, (int) y + height / 2, 30, 30, Math.PI / 2, 3, damage * 3);
            }
        }
    }

    @Override
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(70);
        List<EnemyBullet> bullets = new ArrayList<>();

        // โจมตีแบบวงกลมซ้อน
        for (int i = 0; i < 20; i++) { // วงนอก
            double angle = Math.PI * i / 10;
            bullets.add(new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 4, damage));
        }

        for (int i = 0; i < 12; i++) { // วงใน
            double angle = Math.PI * i / 6 + Math.PI / 12;
            bullets.add(new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, angle, 3, damage * 2));
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // เลือดเหลือ 50%
        if (health <= 350 * level && speed == 1) {
            speed = 3;
            this.damage = this.damage * 3; // เพิ่มดาเมจ
        }

        // เลือดเหลือ 25%
        if (health <= 175 * level && speed == 3) {
            speed = 5; // เพิ่มความเร็วมากขึ้น
            this.damage = (int) (this.damage * 2); // เพิ่มดาเมจอีก
        }
    }
}
