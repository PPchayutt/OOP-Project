
import java.awt.*;
import java.util.Random;

public class Monster5 extends Monster {

    private int patternCounter = 0;
    private static final Random random = new Random();
    private float speedMultiplier = 0.6f;
    private int spawnTime = 0;

    public Monster5(int x, int y, Player player) {
        super(x, y, player);
        this.width = 45;
        this.height = 45;
        this.health = 100;
        this.maxHealth = 100;
        this.speed = 3;
        this.damage = 20;
        this.points = 250;
        this.dropsPowerup = random.nextDouble() < 0.35;
        this.speedMultiplier = 0.3f;
    }

    @Override
    public void update() {
        updateCooldowns();

        spawnTime++;

        // ความเร็วจะเพิ่มขึ้นเร็วกว่าด่านอื่นๆ
        if (spawnTime < 60) {
            speedMultiplier = 0.5f + (spawnTime / 400f);
        } else {
            speedMultiplier = 1.0f; // ความเร็วเต็มที่ตั้งแต่เริ่มเร็ว
        }

        // คำนวณทิศทางไปหาผู้เล่น
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;

        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx = dx / distance;
            dy = dy / distance;
        }

        // รูปแบบการเคลื่อนที่
        patternCounter++;
        int pattern = (patternCounter / 90) % 4; // เปลี่ยนรูปแบบเร็วขึ้น มี 4 รูปแบบ

        switch (pattern) {
            case 0 -> {
                // ตรงไปหาผู้เล่นด้วยความเร็วสูง
                x += dx * speed * speedMultiplier * 1.2f;
                y += dy * speed * speedMultiplier * 1.2f;
            }
            case 1 -> {
                // วนเป็นวงกลม
                x += dx * speed * speedMultiplier + Math.cos(patternCounter * 0.04) * 3.5f;
                y += dy * speed * speedMultiplier + Math.sin(patternCounter * 0.04) * 3.5f;
            }
            case 2 -> {
                // เคลื่อนที่แบบซิกแซก
                x += dx * speed * speedMultiplier + Math.sin(patternCounter * 0.2) * 2.5f;
                y += dy * speed * speedMultiplier + Math.cos(patternCounter * 0.2) * 2.5f;
            }
            case 3 -> {
                // บางครั้งจะพุ่งตรงเข้าใส่ผู้เล่นอย่างเร็ว
                if (patternCounter % 30 < 15) {
                    x += dx * speed * speedMultiplier * 1.5f;
                    y += dy * speed * speedMultiplier * 1.5f;
                } else {
                    // หยุดชั่วขณะ
                    x += dx * speed * speedMultiplier * 0.3f;
                    y += dy * speed * speedMultiplier * 0.3f;
                }
            }
        }

        // ดูแลไม่ให้ออกนอกจอ
        x = Math.max(10, Math.min(x, GamePanel.WIDTH - width - 10));
        y = Math.max(10, Math.min(y, GamePanel.HEIGHT - height - 10));
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image monster5Image = ImageManager.getImage("monster5");
        if (monster5Image != null) {
            g.drawImage(monster5Image, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(new Color(30, 0, 50));
            g.fillOval((int) x, (int) y, width, height);

            g.setColor(new Color(120, 0, 200, 100));
            g.fillOval((int) (x - 5), (int) (y - 5), width + 10, height + 10);

            g.setColor(Color.RED);
            g.fillOval((int) (x + width / 4), (int) (y + height / 3), width / 5, height / 5);
            g.fillOval((int) (x + width * 3 / 5), (int) (y + height / 3), width / 5, height / 5);
        }

        // วาดแถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 5, width, 3);
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((float) health / 120 * width);
        g.fillRect((int) x, (int) y - 5, healthBarWidth, 3);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        // เพิ่มเงื่อนไขตรวจสอบว่าอยู่ในจอหรือไม่
        if (x < 0 || x > GamePanel.WIDTH || y < 0 || y > GamePanel.HEIGHT) {
            return null; // ไม่ยิงถ้าอยู่นอกจอ
        }

        // คูลดาวน์สั้นมาก
        resetShootCooldown(25);

        // คำนวณทิศทางการยิงไปหาผู้เล่น
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;

        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        double angle = Math.atan2(dy, dx);

        // ใส่ความไม่แน่นอนในการยิงบ้าง
        double randomAngle = angle + (random.nextDouble() - 0.5) * 0.2;

        // สร้างกระสุนที่เร็วและแรงที่สุด
        return new EnemyBullet((int) x + width / 2, (int) y + height / 2, 15, 15, randomAngle, 8, damage);
    }
}
