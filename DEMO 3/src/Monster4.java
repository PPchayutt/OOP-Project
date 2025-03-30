
import java.awt.*;
import java.util.Random;

/*
 * Monster4 คือมอนสเตอร์ประจำด่าน 4 มีลักษณะเป็นหุ่นยนต์
 */
public class Monster4 extends Monster {

    private int patternCounter = 0;
    private static final Random random = new Random();
    private float speedMultiplier = 0.5f;
    private int spawnTime = 0;

    /*
     * สร้างมอนสเตอร์ด่าน 4 ใหม่
     *
     * @param x      ตำแหน่ง x
     * @param y      ตำแหน่ง y
     * @param player ผู้เล่นที่เป็นเป้าหมาย
     */
    public Monster4(int x, int y, Player player) {
        super(x, y, player); // เรียก constructor ของ Monster

        // ปรับค่าพารามิเตอร์ให้แข็งแกร่งขึ้นกว่าด่าน 3
        this.width = 40;
        this.height = 40;
        this.health = 85;
        this.maxHealth = 85;
        this.speed = 2;
        this.damage = 16;
        this.points = 200;
        this.dropsPowerup = random.nextDouble() < 0.35;
        this.speedMultiplier = 0.25f;
    }

    @Override
    public void update() {
        updateCooldowns();

        spawnTime++;

        // ลดความซับซ้อนการคำนวณความเร็ว
        if (spawnTime < 90) {
            speedMultiplier = 0.4f + (spawnTime / 500f);
        } else {
            speedMultiplier = 0.9f;
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

        // รูปแบบการเคลื่อนที่ที่ซับซ้อนขึ้น
        patternCounter++;
        int pattern = (patternCounter / 120) % 3;

        switch (pattern) {
            case 0:
                // ตรงไปหาผู้เล่น
                x += dx * speed * speedMultiplier;
                y += dy * speed * speedMultiplier;
                break;
            case 1:
                // วนเป็นวงกลมด้วยรัศมีใหญ่ขึ้น
                x += dx * speed * speedMultiplier + Math.cos(patternCounter * 0.03) * 2.5f;
                y += dy * speed * speedMultiplier + Math.sin(patternCounter * 0.03) * 2.5f;
                break;
            case 2:
                // เคลื่อนที่แบบซิกแซกเร็วขึ้น
                x += dx * speed * speedMultiplier;
                y += dy * speed * speedMultiplier + Math.sin(patternCounter * 0.1) * 3.0f;
                break;
        }

        // ดูแลไม่ให้ออกนอกจอ
        x = Math.max(10, Math.min(x, GamePanel.WIDTH - width - 10));
        y = Math.max(10, Math.min(y, GamePanel.HEIGHT - height - 10));
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image monster4Image = ImageManager.getImage("monster4");
        if (monster4Image != null) {
            g.drawImage(monster4Image, (int) x, (int) y, width, height, null);
        } else {
            // ถ้าไม่มีรูปภาพให้วาดรูปทรงพื้นฐานแทน
            g.setColor(new Color(150, 150, 200)); // สีฟ้าเทา
            g.fillRect((int) x, (int) y, width, height);

            // วาดรายละเอียดของหุ่นยนต์
            g.setColor(Color.RED);
            g.fillOval((int) (x + width / 4), (int) (y + height / 4), width / 6, height / 6);
            g.fillOval((int) (x + width * 3 / 5), (int) (y + height / 4), width / 6, height / 6);

            g.setColor(Color.DARK_GRAY);
            g.fillRect((int) (x + width / 4), (int) (y + height * 2 / 3), width / 2, height / 8);
        }

        // วาดแถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 5, width, 3);
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((float) health / 100 * width);
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

        // คูลดาวน์สั้นลงและยิงเร็วขึ้น
        resetShootCooldown(35);

        // คำนวณทิศทางการยิงไปหาผู้เล่น
        float targetX = target.getX() + target.getWidth() / 2;
        float targetY = target.getY() + target.getHeight() / 2;

        float dx = targetX - (x + width / 2);
        float dy = targetY - (y + height / 2);
        double angle = Math.atan2(dy, dx);

        // สร้างกระสุนที่เร็วและแรงขึ้น
        return new EnemyBullet((int) x + width / 2, (int) y + height / 2, 12, 12, angle, 7, damage);
    }
}
