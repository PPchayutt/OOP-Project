
import java.awt.*;

public abstract class Weapon implements GameObject {

    protected int x, y;
    protected int width, height;
    protected int damage;
    protected int cooldown;
    protected int currentCooldown;
    protected int lifespan;
    protected int maxLifespan;
    protected boolean active = true;
    protected boolean deployable;
    protected boolean deployed = false;
    protected boolean using = false;
    protected WeaponType type;
    protected boolean isPermanent = false;

    public Weapon(int x, int y, int width, int height, int damage, int cooldown, int lifespan, WeaponType type, boolean deployable, boolean isPermanent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
        this.lifespan = lifespan;
        this.maxLifespan = lifespan;
        this.type = type;
        this.deployable = deployable;
        this.isPermanent = isPermanent;
    }

    @Override
    public void update() {
        // ลดเวลาลงทุกเฟรม
        if (!isPermanent && (deployed || using)) {
            lifespan--;
            if (lifespan <= 0) {
                active = false;
            }

            // ลดเวลาคูลดาวน์
            if (currentCooldown > 0) {
                currentCooldown--;
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public boolean isUsing() {
        return using;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public boolean isDeployable() {
        return deployable;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    public boolean canAttack() {
        return currentCooldown <= 0;
    }

    public void resetCooldown() {
        currentCooldown = cooldown;
    }

    public int getDamage() {
        return damage;
    }

    public WeaponType getType() {
        return type;
    }

    public float getLifespanPercentage() {
        if (maxLifespan <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(1.0f, (float) lifespan / maxLifespan));
    }

    public int getRemainingLifespan() {
        return lifespan;
    }

    public void resetLifespan() {
        lifespan = maxLifespan;
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
