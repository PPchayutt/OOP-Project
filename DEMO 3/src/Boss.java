
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Boss extends Enemy {

    int phaseCounter = 0;
    int phase = 0;
    int moveDirection = 1;
    int attackPattern = 0;
    private static final Random random = new Random();
    final int level;

    public Boss(int x, int y, int level) {
        super(x, y, 80, 80, 400 * level, 1, 20, 1000 * level);
        this.level = level;
    }

    /**
     *
     */
    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        if (phaseCounter >= 300) {
            phaseCounter = 0;
            phase = (phase + 1) % 3;
            attackPattern = random.nextInt(3);
        }

        // รูปแบบการเคลื่อนที่ของบอส
        switch (phase) {
            case 0 -> {
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
                y = Math.max(0, Math.min(y, 300));
            }
            default -> {
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image bossImage = ImageManager.getImage("boss");
        if (bossImage != null) {
            g.drawImage(bossImage, (int) x, (int) y, width, height, null);

            // วาดแถบพลังชีวิตเหนือบอส
            g.setColor(Color.RED);
            g.fillRect((int) x, (int) y - 25, width, 20);
            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((double) health / (400 * level) * width);
            g.fillRect((int) x, (int) y - 25, healthBarWidth, 20);

            // กรอบแถบพลังชีวิต
            g.setColor(Color.WHITE);
            g.drawRect((int) x, (int) y - 25, width, 20);
        } else {
            g.setColor(new Color(180, 0, 0));
            g.fillRect((int) x, (int) y, width, height);

            g.setColor(Color.YELLOW);
            g.fillOval((int) x + width / 4, (int) y + height / 4, width / 5, height / 5);
            g.fillOval((int) x + width * 3 / 5, (int) y + height / 4, width / 5, height / 5);

            g.setColor(Color.BLACK);
            g.fillRect((int) x + width / 4, (int) y + height * 2 / 3, width / 2, height / 8);

            // แถบพลังชีวิต
            g.setColor(Color.RED);
            g.fillRect((int) x, (int) y - 25, width, 20);
            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((double) health / (400 * level) * width);
            g.fillRect((int) x, (int) y - 25, healthBarWidth, 20);

            // กรอบแถบพลังชีวิต
            g.setColor(Color.WHITE);
            g.drawRect((int) x, (int) y - 25, width, 20);
        }

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
                resetShootCooldown(20);
                return new EnemyBullet((int) x + width / 2 - 4, (int) y + height, 8, 8, Math.PI / 2, 3, damage);
            }
            case 1 -> {
                resetShootCooldown(45);
                double angle = Math.PI / 3 + random.nextDouble() * Math.PI / 3;
                return new EnemyBullet((int) x + width / 2 - 4, (int) y + height, 8, 8, angle, 2, damage);
            }
            default -> {
                resetShootCooldown(10);
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 4, (int) y + height / 2, 8, 8, targetAngle, 3, damage);
            }
        }
    }

    /**
     *
     * @return
     */
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(100);
        List<EnemyBullet> bullets = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            double angle = Math.PI * i / 6;
            bullets.add(new EnemyBullet((int) x + width / 2 - 4, (int) y + height / 2, 8, 8, angle, 2, damage));
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        if (health <= 200 * level && speed == 1) {
            speed = 3; // เพิ่มจาก 2 เป็น 3
            this.damage = this.damage * 2;
        }
    }

    public int getLevel() {
        return level;
    }

    /**
     *
     * @return
     */
    @Override
    public Rectangle getBounds() {
        // ลดขนาด hitbox ให้เล็กลง
        int hitboxWidth = (int) (width * 0.8);
        int hitboxHeight = (int) (height * 0.8);

        // ปรับตำแหน่งให้ hitbox อยู่ตรงกลาง
        int hitboxX = (int) (x + (width - hitboxWidth) / 2);
        int hitboxY = (int) (y + (height - hitboxHeight) / 2);

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

}
