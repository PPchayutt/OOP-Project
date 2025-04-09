
import java.awt.*;

public class Weapon implements GameObject {

    protected int damage;
    protected int bulletSpeed;
    protected int bulletCount;
    protected boolean spreadShot;
    protected double spreadAmount;
    protected int cooldown;
    protected int lifespan;
    protected int maxLifespan;
    protected boolean active = true;
    protected boolean deployable;
    protected boolean deployed = false;
    protected boolean using = false;
    protected WeaponType type;
    protected boolean isPermanent;

    public Weapon(int damage, int bulletSpeed, int bulletCount, boolean spreadShot, double spreadAmount, int cooldown, int lifespan, WeaponType type, boolean deployable, boolean isPermanent) { 
        this.damage = damage;
        this.bulletSpeed = bulletSpeed;
        this.bulletCount = bulletCount;
        this.spreadShot = spreadShot;
        this.spreadAmount = spreadAmount;
        this.cooldown = cooldown;
        this.lifespan = lifespan;
        this.maxLifespan = lifespan;
        this.type = type;
        this.deployable = deployable;
        this.isPermanent = isPermanent;
    }
    
    @Override
    public void render(Graphics g) { }

    @Override
    public void update() {
        // ลดเวลาลงทุกเฟรม
        if (!isPermanent() && (isDeployed() || isUsing())) {
            lifespan--;
            if (lifespan <= 0) {
                active = false;
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return null;
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

    public int getDamage() {
        return damage;
    }
    
   public int getBulletSpeed() {
       return bulletSpeed;
   }
    
    public int getBulletCount() {
        return bulletCount;
    }
    
    public boolean getSpreadShot() {
        return spreadShot;
    }
    
    public double getSpreadAmount() {
        return spreadAmount;
    }
    
    public int getCooldown() {
        return cooldown;
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
        
    public WeaponType getType() {
        return type;
    }
}
