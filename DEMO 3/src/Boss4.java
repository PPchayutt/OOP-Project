
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Boss4 คือบอสประจำด่าน 4 มีลักษณะเป็นหุ่นยนต์ขนาดใหญ่
 */
public class Boss4 extends Boss {

    private static final Random random = new Random();
    private int attackMode = 0; // โหมดการโจมตี
    private int attackCounter = 0; // ตัวนับการโจมตี

    /*
     * สร้างบอสด่าน 4 ใหม่
     * 
     * @param x ตำแหน่ง x
     * @param y ตำแหน่ง y
     * @param level ระดับความยาก
     */
    public Boss4(int x, int y, int level) {
        super(x, y, level);

        // ปรับแต่งค่าพารามิเตอร์ให้แข็งแกร่งขึ้น
        this.width = 120;
        this.height = 120;
        this.health = 500 * level;
        this.maxHealth = 500 * level;
        this.damage = 35;
    }

    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        if (phaseCounter >= 220) {
            phaseCounter = 0;
            phase = (phase + 1) % 3;
            attackPattern = random.nextInt(4); // เพิ่มรูปแบบการโจมตีเป็น 4 แบบ
        }

        // รูปแบบการเคลื่อนที่ของบอส
        switch (phase) {
            case 0:
                // เคลื่อนที่จากซ้ายไปขวาเร็วขึ้น
                x += speed * 1.2f * moveDirection;
                if (x <= 0 || x >= GamePanel.WIDTH - width) {
                    moveDirection *= -1;
                }
                break;
            case 1:
                // เคลื่อนที่เป็นรูปคลื่นซับซ้อน
                x += Math.cos(phaseCounter * 0.06) * speed * 1.5f;
                y += Math.sin(phaseCounter * 0.06) * speed * 1.2f;
                x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                y = Math.max(50, Math.min(y, 300));
                break;
            case 2:
                // หยุดนิ่ง (เตรียมโจมตีพิเศษ)
                attackCounter++;
                if (attackCounter >= 60) { // ทุก 1 วินาที
                    attackCounter = 0;
                    attackMode = (attackMode + 1) % 3;
                }
                break;
        }

        // จำกัดตำแหน่งไม่ให้ออกนอกจอ
        x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
        y = Math.max(50, Math.min(y, 300));
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image boss4Image = ImageManager.getImage("boss4");
        if (boss4Image != null) {
            g.drawImage(boss4Image, (int) x, (int) y, width, height, null);
        } else {
        // ถ้าไม่มีรูปภาพให้วาดรูปทรงพื้นฐานแทน
            g.setColor(new Color(120, 120, 180)); // สีเทาฟ้า
            g.fillRect((int) x, (int) y, width, height);

        // เพิ่มรายละเอียดให้ดูเป็นหุ่นยนต์
            g.setColor(new Color(80, 80, 100));
            g.fillRect((int) (x + width / 4), (int) (y + height / 4), width / 2, height / 2);

        // ตาเรืองแสง
            g.setColor(Color.RED);
            g.fillOval((int) (x + width / 4), (int) (y + height / 4), width / 6, height / 6);
            g.fillOval((int) (x + width * 3 / 5), (int) (y + height / 4), width / 6, height / 6);

        // แขนกล
            g.setColor(new Color(100, 100, 140));
            g.fillRect((int) (x - width / 4), (int) (y + height / 3), width / 4, height / 8);
            g.fillRect((int) (x + width), (int) (y + height / 3), width / 4, height / 8);
        }

    // แถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 20, width, 15);
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / (400 * level) * width);
        g.fillRect((int) x, (int) y - 20, healthBarWidth, 15);

    // กรอบแถบพลังชีวิต
        g.setColor(Color.WHITE);
        g.drawRect((int) x, (int) y - 20, width, 15);

    // พิมพ์ข้อความแสดงสถานะใต้บอส - เปลี่ยนเป็นเลข 4 เลย
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Boss Lv.4 HP:" + health, (int) x, (int) y + height + 15);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        switch (attackPattern) {
            case 0:
                // ยิงตรงลงมาด้วยกระสุนใหญ่และเร็ว
                resetShootCooldown(20);
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height, 20, 20, Math.PI / 2, 5, damage);
            case 1:
                // ยิงทแยงมุมหลายทิศทางด้วยกระสุนเล็กแต่เร็ว
                resetShootCooldown(40);
                double angle = Math.PI / 4 + random.nextDouble() * Math.PI / 2;
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 6, damage);
            case 2:
                // ยิงตรงไปที่ผู้เล่นอย่างแม่นยำ
                resetShootCooldown(15);
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle, 5, damage);
            case 3:
                // รูปแบบใหม่: ยิงกระสุนแยกตัว (จะแยกตัวใน GamePanel)
                resetShootCooldown(60);
                return new EnemyBullet((int) x + width / 2 - 15, (int) y + height / 2, 30, 30, Math.PI / 2, 3, damage * 2);
            default:
                resetShootCooldown(30);
                return new EnemyBullet((int) x + width / 2 - 8, (int) y + height, 16, 16, Math.PI / 2, 4, damage);
        }
    }

    @Override
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(80);
        List<EnemyBullet> bullets = new ArrayList<>();

        switch (attackMode) {
            case 0:
                // ยิงเป็นวงกลมรอบตัว 16 ทิศทาง
                for (int i = 0; i < 16; i++) {
                    double angle = Math.PI * i / 8;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 6, (int) y + height / 2, 12, 12, angle, 4, damage));
                }
                break;
            case 1:
                // ยิงแบบเป็นรูปตัว X
                for (int i = 0; i < 8; i++) {
                    double angle = Math.PI * i / 4 + Math.PI / 8;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, angle, 5, damage * 2));
                }
                break;
            case 2:
                // ยิงพร้อมกัน 3 นัดตรงไปที่ผู้เล่น
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);

                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle, 6, damage));
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle - 0.2, 6, damage));
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle + 0.2, 6, damage));
                break;
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // เมื่อพลังชีวิตเหลือน้อยกว่า 50% ให้บอสเร็วขึ้นและโจมตีแรงขึ้น 3 เท่า
        if (health <= 200 * level && speed == 1) {
            speed = 3;
            this.damage = this.damage * 3;
        }

        // เมื่อพลังชีวิตเหลือน้อยกว่า 25% ให้โจมตีแรงขึ้นอีก
        if (health <= 100 * level && speed == 3) {
            this.damage = this.damage * 2;
        }
    }
}
