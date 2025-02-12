
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Boss extends Entity {

    private int health;
    private final int maxHealth;
    private final float speed;
    private final Player target;
    private final List<Bullet> bosseBullets;
    private long lastShotTime = 0;
    private final long shootCooldown;
    private final int difficulty;

    public Boss(float x, float y, Player target, int difficulty) {
        super(x, y);
        this.width = 50;
        this.height = 50;
        this.target = target;
        this.difficulty = difficulty;

        // Scale boss stats based on difficulty
        this.maxHealth = 500 + (difficulty * 100);
        this.health = maxHealth;
        this.speed = 0.5f + (difficulty * 0.1f);
        this.shootCooldown = Math.max(500, 1000 - (difficulty * 100));

        this.bosseBullets = new ArrayList<>();
    }

    @Override
    public void update() {
        float dx = target.getX() - x;
        float dy = target.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            velX = (dx / distance) * speed;
            velY = (dy / distance) * speed;
        }

        x += velX;
        y += velY;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= shootCooldown) {
            shoot();
            lastShotTime = currentTime;
        }

        bosseBullets.forEach(Bullet::update);
        bosseBullets.removeIf(bullet -> !bullet.isActive());
    }

    private void shoot() {
        float centerX = x + width / 2;
        float centerY = y + height / 2;

        int bulletCount = 3 + (difficulty);
        float spreadAngle = 45 + (difficulty * 10);

        for (int i = 0; i < bulletCount; i++) {
            float angleOffset = (i - (bulletCount - 1) / 2.0f) * (spreadAngle / (bulletCount - 1));

            float targetX = target.getX() + angleOffset;
            float targetY = target.getY() + angleOffset;

            Bullet bossBullet = new Bullet(centerX, centerY, targetX, targetY,
                    Bullet.BulletType.STRONG) {
                @Override
                public int getDamage() {
                    return 50 + (difficulty * 10);
                }
            };

            bosseBullets.add(bossBullet);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(difficulty == 1 ? Color.MAGENTA
                : difficulty == 2 ? Color.RED
                        : Color.DARK_GRAY);
        g.fillRect((int) x, (int) y, width, height);

        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 5, width, 3);
        g.setColor(Color.GREEN);
        int currentHPWidth = (int) ((health / (float) maxHealth) * width);
        g.fillRect((int) x, (int) y - 5, currentHPWidth, 3);

        ArrayList<Bullet> bulletsCopy = new ArrayList<>(bosseBullets);
        for (Bullet bullet : bulletsCopy) {
            bullet.render(g);
        }
    }

    public void damage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public List<Bullet> getBossBullets() {
        return bosseBullets;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
