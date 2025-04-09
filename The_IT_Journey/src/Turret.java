
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Turret extends Weapon implements GameObject {
    
    private int x;
    private int y;
    private final int width = 45;
    private final int height = 45;
    private int currentCooldown;
    private int turretDirection;
    private final Image turretHead, turretBase, flashImage;
    private boolean isShooting = false;
    private int flashWidth;
    private int flashHeight;

    public Turret() {
        super(25, 10, 1, false, 0, 15, 600, WeaponType.TURRET, true, false);
        this.currentCooldown = 0;
        this.turretHead = ImageManager.getImage("turretHead");
        this.turretBase = ImageManager.getImage("turretBase");
        this.flashImage = ImageManager.getImage("muzzle_flash");
    }
    
    @Override
    public void update() {
        if (!isPermanent() && (isDeployed() || isUsing())) {
            lifespan--;
            if (lifespan <= 0) {
                active = false;
            }
            if (currentCooldown > 0) {
                currentCooldown--;
                isShooting = false;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // วาดป้อมปืน
        g.drawImage(turretBase, x, y, width, height, null);
                
        flashWidth = Math.max(3, (int) (flashImage.getWidth(null) * 0.03));
        flashHeight = Math.max(3, (int) (flashImage.getHeight(null) * 0.03));
        
        if (turretDirection > 0) {
            // เมื่อเป้าหมายอยู่ทางขวา ให้หันปืนไปทางขวา
            g.drawImage(turretHead, x, y - 10, width, height, null);
        } else {
            // เมื่อเป้าหมายอยู่ทางซ้าย ให้หันปืนไปทางซ้าย
            g.drawImage(turretHead, x + width, y - 10, -width, height, null);
        }
        
        // วาดเอฟเฟกตอนยิง
        if (isShooting) {
            if (turretDirection > 0) {
                g.drawImage(flashImage, x + width - 4, y, flashWidth, flashHeight, null);
            } else {
                g.drawImage(flashImage, x + 4, y, -flashWidth, flashHeight, null);
            }
        }

        // วาดแถบเวลาที่เหลือ
        g.setColor(Color.YELLOW);
        g.drawRect((int) x, (int) y + height + 5, width, 3);
        int timeBarWidth = (int) ((float) lifespan / 600 * width);
        g.fillRect((int) x, (int) y + height + 5, timeBarWidth, 3);
    }

    // หาศัตรูที่อยู่ใกล้ที่สุด
    public GameObject findNearestEnemy(List<Enemy> monsters, List<Boss> bosses) {
        GameObject target = null;
        float minDistance = 1000;

        // กรณีมอนทั่วไป
        for (Enemy monster : monsters) {
            if (monster.isAlive()) {
                float dx = monster.getX() + monster.getWidth() / 2 - (x + width / 2);
                float dy = monster.getY() + monster.getHeight() / 2 - (y + height / 2);
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < minDistance) {
                    minDistance = distance;
                    target = monster;
                }
            }
        }
        // กรณีบอส
        for (Boss boss : bosses) {
            if (boss.isAlive()) {
                float dx = boss.getX() + boss.getWidth() / 2 - (x + width / 2);
                float dy = boss.getY() + boss.getHeight() / 2 - (y + height / 2);
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < minDistance) {
                    minDistance = distance;
                    target = boss;
                }
            }
        }
        return target;
    }

    // ยิงเป้าหมาย
    public List<PlayerBullet> shoot(List<Enemy> monsters, List<Boss> bosses) {
        if (!deployed || !canAttack()) {
            return null;
        }
        
        isShooting = true;

        List<PlayerBullet> bullets = new ArrayList<>();

        // หาศัตรูที่อยู่ใกล้ที่สุด
        GameObject target = findNearestEnemy(monsters, bosses);

        if (target != null) {
            // คำนวณทิศทางการยิง
            float targetX = 0, targetY = 0;

            if (target instanceof Entity entity) {
                targetX = entity.getX() + entity.getWidth() / 2;
                targetY = entity.getY() + entity.getHeight() / 2;
            }

            float dx = targetX - (x + width / 2);
            float dy = targetY - (y + height / 2);
            double angle = Math.atan2(dy, dx); // มุมระหว่างป้อมปืนกับศัตรู

            // สร้างกระสุนใหม่
            PlayerBullet bullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, angle);
            bullet.setDamage(damage);
            bullets.add(bullet);

            // รีเซ็ตคูลดาวน์การยิง
            resetCooldown();

            if (targetX < x + width / 2) {
                turretDirection = -1; // เมื่อเป้าหมายอยู่ทางซ้าย ให้หันปืนไปทางซ้าย
                
            } else {
                turretDirection = 1; // เมื่อเป้าหมายอยู่ทางขวา ให้หันปืนไปทางขวา

            }
        }
        return bullets;
    }

    public boolean canAttack() {
        return currentCooldown <= 0;
    }

    public void resetCooldown() {
        currentCooldown = cooldown;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
    
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setLocation(int x, int y) {
        setX(x);
        setY(y);
    }

}
