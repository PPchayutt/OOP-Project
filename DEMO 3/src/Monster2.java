
import java.awt.*;
import java.util.Random;

public class Monster2 extends Monster {

    private int patternCounter = 0;
    private static final Random random = new Random();
    private float speedMultiplier = 0.4f;
    private int spawnTime = 0;

    public Monster2(int x, int y, Player player) {
        super(x, y, player); // เรียก constructor ของ Monster

        // แก้ไขค่าพารามิเตอร์ต่างๆ
        this.width = 35;
        this.height = 35;
        this.health = 80;
        this.maxHealth = 80;
        this.speed = 2;
        this.damage = 15;
        this.points = 150;
        this.dropsPowerup = random.nextDouble() < 0.25; // โอกาสดรอปบัฟมากขึ้น (25%)
        this.speedMultiplier = 0.3f;
    }

    @Override
    public void update() {
        updateCooldowns();

        spawnTime++;

        // เพิ่มความเร็วเร็วกว่าด่าน 1
        if (spawnTime < 200) { // ใช้เวลาปรับความเร็วน้อยกว่า
            speedMultiplier = Math.min(0.3f + (spawnTime / 500f), 1.0f);
        } else {
            speedMultiplier = 1.0f;
        }

        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;

        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx = dx / distance;
            dy = dy / distance;
        }

        // รูปแบบการเคลื่อนที่ใหม่ของมอนสเตอร์ด่าน 2
        int movementPattern = patternCounter / 60 % 4; // เปลี่ยนรูปแบบทุก 1 วินาที

        switch (movementPattern) {
            case 0 -> {
                // ตรงไปหาผู้เล่นเร็วกว่า
                x += dx * speed * speedMultiplier * 1.2f;
                y += dy * speed * speedMultiplier * 1.2f;
            }
            case 1 -> {
                // ซิกแซกถี่มากขึ้น
                patternCounter++;
                x += dx * speed * speedMultiplier + Math.sin(patternCounter * 0.1) * speed * speedMultiplier;
                y += dy * speed * speedMultiplier;
            }
            case 2 -> {
                // วนเป็นวงกลมใหญ่ขึ้น
                patternCounter++;
                float circleRadius = 2.5f; // รัศมีการวนใหญ่ขึ้น
                x += dx * speed * speedMultiplier + Math.cos(patternCounter * 0.04) * circleRadius;
                y += dy * speed * speedMultiplier + Math.sin(patternCounter * 0.04) * circleRadius;
            }
            case 3 -> {
                // รูปแบบใหม่: เคลื่อนที่สุ่ม
                patternCounter++;
                if (patternCounter % 30 == 0) { // เปลี่ยนทิศทางทุก 30 เฟรม
                    dx = random.nextFloat() * 2 - 1;
                    dy = random.nextFloat() * 2 - 1;
                }
                x += dx * speed * speedMultiplier;
                y += dy * speed * speedMultiplier;

                // หากออกนอกจอให้กลับเข้ามา
                if (x < 0) {
                    x = 0;
                }
                if (x > GamePanel.WIDTH - width) {
                    x = GamePanel.WIDTH - width;
                }
                if (y < 0) {
                    y = 0;
                }
                if (y > GamePanel.HEIGHT - height) {
                    y = GamePanel.HEIGHT - height;
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager หากมี
        Image monster2Image = ImageManager.getImage("monster2");
        if (monster2Image != null) {
            g.drawImage(monster2Image, (int) x, (int) y, width, height, null);
        } else {
            // ถ้าไม่มีรูปภาพให้วาดรูปทรงพื้นฐานแทน
            g.setColor(new Color(50, 150, 50)); // สีเขียว
            g.fillRect((int) x, (int) y, width, height);

            // ตาของมอนสเตอร์
            g.setColor(Color.RED);
            g.fillOval((int) x + 5, (int) y + 5, 10, 10);
            g.fillOval((int) x + width - 15, (int) y + 5, 10, 10);
        }

        // วาดแถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 5, width, 3);
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((float) health / 80 * width);
        g.fillRect((int) x, (int) y - 5, healthBarWidth, 3);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        // คูลดาวน์สั้นกว่า (ยิงถี่กว่า)
        resetShootCooldown(45);

        // คำนวณทิศทางการยิงไปหาผู้เล่น
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;

        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        double angle = Math.atan2(dy, dx);

        // สร้างกระสุนที่เร็วขึ้น
        return new EnemyBullet((int) x + width / 2, (int) y + height / 2, 10, 10, angle, 6, damage);
    }
}
