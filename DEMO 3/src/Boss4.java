
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
        // เพิ่มพลังชีวิตจาก 500*level เป็น 850*level
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

        // เพิ่มการอัพเดทตัวนับโจมตี
        attackCounter++;

        // รูปแบบการเคลื่อนที่ของบอส
        switch (phase) {
            case 0:
                // เคลื่อนที่จากซ้ายไปขวาเร็วขึ้นมาก
                x += speed * 2.0f * moveDirection; // เพิ่มจาก 1.2f เป็น 2.0f
                if (x <= 0 || x >= GamePanel.WIDTH - width) {
                    moveDirection *= -1;
                }
                break;
            case 1:
                // เคลื่อนที่แบบซับซ้อนมากขึ้น - วงกลม/วงรี
                x += Math.cos(phaseCounter * 0.08) * speed * 2.0f; // เพิ่ม
                y += Math.sin(phaseCounter * 0.08) * speed * 1.8f; // เพิ่ม
                x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                y = Math.max(50, Math.min(y, 300));
                break;
            case 2:
                // อยู่นิ่งแต่เคลื่อนที่ทุก 30 เฟรมแบบวูบวาบ
                if (attackCounter % 30 == 0) {
                    attackMode = (attackMode + 1) % 3;

                    // วาร์ปไปตำแหน่งใหม่ทุกครั้งที่เปลี่ยนโหมด
                    if (random.nextBoolean()) {
                        x = random.nextInt(GamePanel.WIDTH - width);
                        y = random.nextInt(200) + 50;
                    }
                }
                break;
        }
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
                // ลด cooldown และเพิ่มความเร็วกระสุน
                resetShootCooldown(15); // ลดจาก 20
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height, 20, 20, Math.PI / 2, 6, damage);
            case 1:
                // ลด cooldown และเพิ่มความเร็วกระสุน
                resetShootCooldown(25); // ลดจาก 40
                double angle = Math.PI / 4 + random.nextDouble() * Math.PI / 2;
                return new EnemyBullet((int) x + width / 2 - 5, (int) y + height / 2, 10, 10, angle, 7, damage);
            case 2:
                // ลด cooldown และล็อคเป้าแม่นยำขึ้น
                resetShootCooldown(10); // ลดจาก 15
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle, 8, damage);
            case 3:
                // รูปแบบการโจมตีใหม่: ยิงกระสุนขนาดใหญ่ที่แยกย่อยเป็นกระสุนเล็ก
                resetShootCooldown(50);
                return new EnemyBullet((int) x + width / 2 - 20, (int) y + height / 2, 40, 40, Math.PI / 2, 3, damage * 3);
            default:
                resetShootCooldown(30);
                return new EnemyBullet((int) x + width / 2 - 8, (int) y + height, 16, 16, Math.PI / 2, 5, damage);
        }
    }

    @Override
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(60); // ลดจาก 80
        List<EnemyBullet> bullets = new ArrayList<>();

        switch (attackMode) {
            case 0:
                // ยิงเป็นวงกลมรอบตัว 16 ทิศทาง
                for (int i = 0; i < 16; i++) {
                    double angle = Math.PI * i / 8;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 6, (int) y + height / 2, 12, 12, angle, 5, damage));
                }
                break;
            case 1:
                // ยิงแบบเป็นรูปตัว X (กากบาท)
                for (int i = 0; i < 12; i++) {
                    double angle = Math.PI * i / 6 + Math.PI / 8;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, angle, 6, damage * 2));
                }
                break;
            case 2:
                // ยิงพร้อมกัน 5 นัดตรงไปที่ผู้เล่น
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
                // กระสุนด้านนอกสุด
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle - 0.4, 7, damage));
                bullets.add(new EnemyBullet((int) x + width / 2 - 7, (int) y + height / 2, 15, 15, targetAngle + 0.4, 7, damage));
                break;
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // เปลี่ยนเงื่อนไขและเพิ่มดาเมจมากขึ้น (เลือดเหลือ 50%)
        if (health <= 425 * level && speed == 1) {
            speed = 3;
            this.damage = this.damage * 3;
        }

        // เปลี่ยนเฟสที่ 2 เมื่อเลือดเหลือ 25% ให้โจมตีแรงขึ้นอีก
        if (health <= 212 * level && speed == 3) {
            this.damage = this.damage * 2; // เพิ่มดาเมจอีก 2 เท่า
        }
    }
}
