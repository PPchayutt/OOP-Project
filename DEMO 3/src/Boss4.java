
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Boss4 extends Boss {

    private static final Random random = new Random();
    private int attackMode = 0; // โหมดการโจมตี
    private int attackCounter = 0; // ตัวนับการโจมตี

    public Boss4(int x, int y, int level) {
        super(x, y, level);
        this.width = 120;
        this.height = 120;
        this.health = 850 * level;
        this.maxHealth = 850 * level;
        this.damage = 35;
    }

    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        // ลดเวลาเปลี่ยนเฟสให้เร็วขึ้น
        if (phaseCounter >= 180) {
            phaseCounter = 0;
            phase = (phase + 1) % 3;
            attackPattern = random.nextInt(4); // เพิ่มรูปแบบการโจมตีเป็น 4 แบบ
        }

        // อัพเดทตัวนับการโจมตี
        attackCounter++;

        // รูปแบบการเคลื่อนที่ของบอส
        switch (phase) {
            case 0 -> {
                // เคลื่อนที่จากซ้ายไปขวา
                x += speed * 2.0f * moveDirection;
                if (x <= 0 || x >= GamePanel.WIDTH - width) {
                    moveDirection *= -1;
                }
            }
            case 1 -> {
                // เคลื่อนที่แบบวงกลม
                x += Math.cos(phaseCounter * 0.08) * speed * 2.0f;
                y += Math.sin(phaseCounter * 0.08) * speed * 1.8f;
                x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                y = Math.max(50, Math.min(y, 300));
            }
            case 2 -> {
                //เคลื่อนที่แบบวูบวาบ
                if (attackCounter % 30 == 0) {
                    attackMode = (attackMode + 1) % 3;

                    // วาร์ปไปตำแหน่งใหม่ทุกครั้งที่เปลี่ยนโหมด
                    if (random.nextBoolean()) {
                        x = random.nextInt(GamePanel.WIDTH - width);
                        y = random.nextInt(200) + 50;
                    }
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image boss4Image = ImageManager.getImage("boss4");
        if (boss4Image != null) {
            g.drawImage(boss4Image, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(new Color(120, 120, 180));
            g.fillRect((int) x, (int) y, width, height);

            g.setColor(new Color(80, 80, 100));
            g.fillRect((int) (x + width / 4), (int) (y + height / 4), width / 2, height / 2);

            g.setColor(Color.RED);
            g.fillOval((int) (x + width / 4), (int) (y + height / 4), width / 6, height / 6);
            g.fillOval((int) (x + width * 3 / 5), (int) (y + height / 4), width / 6, height / 6);

            g.setColor(new Color(100, 100, 140));
            g.fillRect((int) (x - width / 4), (int) (y + height / 3), width / 4, height / 8);
            g.fillRect((int) (x + width), (int) (y + height / 3), width / 4, height / 8);
        }

        // แถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 25, width, 20);
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / (850 * level) * width);
        g.fillRect((int) x, (int) y - 25, healthBarWidth, 20);

        // กรอบแถบพลังชีวิต
        g.setColor(Color.WHITE);
        g.drawRect((int) x, (int) y - 25, width, 20);

        // ข้อความแสดงสถานะใต้บอส
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Boss Lv.4 HP:" + health, (int) x, (int) y + height + 15);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        switch (attackPattern) {
            case 0 -> {
                // ลด cooldown และเพิ่มความเร็วกระสุน
                resetShootCooldown(15); // ลดจาก 20
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height, 20, 20, Math.PI / 2, 6, damage);
            }
            case 1 -> {
                // ลด cooldown และเพิ่มความเร็วกระสุน
                resetShootCooldown(25);
                double angle = Math.PI / 4 + random.nextDouble() * Math.PI / 2;
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 7, damage);
            }
            case 2 -> {
                // ลด cooldown และล็อคเป้าแม่นยำขึ้น
                resetShootCooldown(10);
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle, 8, damage);
            }
            case 3 -> {
                // ยิงกระสุนขนาดใหญ่ที่แยกเป็นกระสุนเล็ก
                resetShootCooldown(50);
                return new EnemyBullet((int) x + width / 2 - 20, (int) y + height / 2, 40, 40, Math.PI / 2, 3, damage * 3);
            }
            default -> {
                resetShootCooldown(30);
                return new EnemyBullet((int) x + width / 2 - 8, (int) y + height, 16, 16, Math.PI / 2, 5, damage);
            }
        }
    }

    @Override
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(60);
        List<EnemyBullet> bullets = new ArrayList<>();

        switch (attackMode) {
            case 0 -> {
                // ยิงเป็นวงกลมรอบตัว
                for (int i = 0; i < 16; i++) {
                    double angle = Math.PI * i / 8;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 6, (int) y + height / 2, 12, 12, angle, 5, damage));
                }
            }
            case 1 -> {
                // ยิงแบบเป็นรูปตัว X
                for (int i = 0; i < 12; i++) {
                    double angle = Math.PI * i / 6 + Math.PI / 8;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, angle, 6, damage * 2));
                }
            }
            case 2 -> {
                // ยิงพร้อมกัน 5 นัด
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);

                // กระสุนหลัก (ตรงกลาง)
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle, 7, damage));
                // กระสุนด้านข้าง
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle - 0.2, 7, damage));
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle + 0.2, 7, damage));
                // กระสุนด้านนอก
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle - 0.4, 7, damage));
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle + 0.4, 7, damage));
            }
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        if (health <= 425 * level && speed == 1) {
            speed = 3;
            this.damage = this.damage * 3;
        }

        if (health <= 212 * level && speed == 3) {
            this.damage = this.damage * 2;
        }
    }
}
