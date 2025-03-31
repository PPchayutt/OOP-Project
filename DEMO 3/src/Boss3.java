
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Boss3 คือบอสประจำด่าน 3 มีลักษณะเป็นใบหน้าคน
 */
public class Boss3 extends Boss {

    private static final Random random = new Random();

    /*
     * สร้างบอสด่าน 3 ใหม่
     * 
     * @param x ตำแหน่ง x
     * @param y ตำแหน่ง y
     * @param level ระดับความยาก
     */
    public Boss3(int x, int y, int level) {
        super(x, y, level);
        // เพิ่มหรือแก้ไขคุณสมบัติพิเศษของ Boss3
        this.width = 100;
        this.height = 100;
        this.health = 400 * level;
        this.maxHealth = 400 * level;
        this.damage = 25;
    }

    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        if (phaseCounter >= 250) {
            phaseCounter = 0;
            phase = (phase + 1) % 3; // ใช้ 3 เฟสเช่นเดียวกับ Boss2
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

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image boss3Image = ImageManager.getImage("boss3");
        if (boss3Image != null) {
            g.drawImage(boss3Image, (int) x, (int) y, width, height, null);
        } else {
        // วาดใบหน้าบอสด่าน 3 ตามรูปภาพที่ 1
        // วาดหน้าสีเนื้อ (พื้นหลัง)
            g.setColor(new Color(255, 213, 170)); // สีผิว
            g.fillOval((int) x, (int) y, width, height);

        // วาดผม
            g.setColor(Color.BLACK);
            g.fillArc((int) x, (int) y, width, height / 2, 0, 180);

        // วาดคิ้ว
            g.setColor(new Color(139, 69, 19)); // สีน้ำตาล
            g.fillRect((int) (x + width / 4), (int) (y + height / 3) - 5, width / 5, 3);
            g.fillRect((int) (x + width / 2), (int) (y + height / 3) - 5, width / 5, 3);

        // วาดตา (ปิด)
            g.setColor(Color.BLACK);
            g.drawLine((int) (x + width / 4), (int) (y + height / 3), (int) (x + width / 4 + width / 5), (int) (y + height / 3));
            g.drawLine((int) (x + width / 2), (int) (y + height / 3), (int) (x + width / 2 + width / 5), (int) (y + height / 3));

        // วาดจมูก
            g.setColor(new Color(220, 170, 140));
            int[] xPointsNose = {(int) (x + width / 2), (int) (x + width / 2 - 5), (int) (x + width / 2 + 5)};
            int[] yPointsNose = {(int) (y + height / 2), (int) (y + height / 2 + 10), (int) (y + height / 2 + 10)};
            g.fillPolygon(xPointsNose, yPointsNose, 3);

        // วาดปาก
            g.setColor(new Color(200, 120, 120));
            g.drawArc((int) (x + width / 3), (int) (y + height * 2 / 3), width / 3, height / 8, 0, 180);
        }

    // แถบพลังชีวิต
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / (300 * level) * width);
        g.fillRect((int) x, (int) y - 15, healthBarWidth, 10);
        g.setColor(Color.RED);
        g.drawRect((int) x, (int) y - 15, width, 10);

    // พิมพ์ข้อความแสดงสถานะใต้บอส - เปลี่ยนเป็นเลข 3 เลย
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

    /**
     *
     * @param damage
     */
    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // เมื่อพลังชีวิตเหลือน้อยกว่า 50% ให้บอสเร็วขึ้นและโจมตีแรงขึ้น
        if (health <= 200 * level && speed == 1) {
            speed = 3;
            this.damage = this.damage * 3;
        }

        // เพิ่มระยะที่ 2 เมื่อเลือดเหลือน้อยกว่า 25%
        if (health <= 100 * level && speed == 3) {
            speed = 4;
            this.damage = (int) (this.damage * 1.5);
        }
    }
}
