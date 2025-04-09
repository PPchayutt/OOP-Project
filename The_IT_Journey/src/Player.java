
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Player extends Entity {

    private int lives = 3;    // จำนวนชีวิตเริ่มต้น

    // ค่าความเร็ว
    private float velX = 0;
    private float velY = 0;
    private float targetVelX = 0;
    private float targetVelY = 0;
    private final float acceleration = 0.5f;
    private final float deceleration = 0.3f;
    private final float maxSpeed = 5.0f;

    // รายการกระสุน
    private final List<PlayerBullet> bullets = new ArrayList<>();

    // ช่วงเวลาระหว่างการยิง
    private long lastShotTime = 0;
    private long shootCooldown = 200;  // มิลลิวินาที
    private long currentCooldown = 0;  // เวลาคูลดาวน์ปัจจุบัน

    // ช่วงเวลาที่ได้รับอมตะหลังโดนโจมตี
    private int invincibleTime = 0;
    private final int maxInvincibleTime = 60;  // เฟรม

    // ค่าความเสียหายของกระสุนเริ่มต้น
    private static final int baseBulletDamage = 25;
    private static int bulletDamage = baseBulletDamage;

    private int score = 0;

    // ข้อมูลเกี่ยวกับการยิงปืน
    private boolean isShooting = false;
    private int shootAnimationTime = 0;
    private final int SHOOT_ANIMATION_DURATION = 10; // 10 เฟรม
    private int gunDirection = 1; // 1 = ขวา, -1 = ซ้าย

    // ข้อมูลเกี่ยวกับบัฟ
    private final List<Powerup> activeBuffs = new ArrayList<>();

    // เพิ่มแฮชแมพเพื่อเก็บจำนวนบัฟถาวรที่ซ้ำกัน
    private final Map<String, Integer> permanentBuffCounts = new HashMap<>();

    private boolean crazyShootingMode = false;
    private boolean knockbackEnabled = false;
    private int knockbackPower = 1;
    private int extraBullets = 0;
    private long shootCooldownReduction = 0; // ลดเวลาคูลดาวน์การยิง (ms)

    private WeaponManager weaponManager;

    private boolean immortalMode = false;

    public Player(float x, float y, int width, int height, int health, int speed) {
        super(x, y, width, height, health, speed);
        this.maxHealth = 100;
        weaponManager = GamePanel.getWeaponManager();
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public long getLastShotTime() {
        return lastShotTime;
    }

    // สร้างคีย์สำหรับแฮชแมพจากบัฟถาวร
    private String getPermanentBuffKey(Powerup buff) {
        return buff.getCategory() + "_" + buff.getType();
    }

    public void move(int dx, int dy) {
        float targetX = 0;
        float targetY = 0;
        float newX = x + velX;
        float newY = y + velY;

        if (dx != 0 || dy != 0) {
            // แปลงค่า dx, dy เป็นความเร็วเป้าหมาย
            targetX = dx * speed;
            targetY = dy * speed;

            // ปรับความเร็วเมื่อเคลื่อนที่แนวทแยง
            if (dx != 0 && dy != 0) {
                targetX *= 0.7071f;
                targetY *= 0.7071f;
            }
        }

        //ถ้าออกนอกขอบจอให้หยุดเคลื่อนที่
        if (newX < 10 || newX > GamePanel.WIDTH - width - 20 || newY < 10 || newY > GamePanel.HEIGHT - height - 50) {
            velX = 0;
            velY = 0;
        }

        setTargetVelocity(targetX, targetY);
    }

    // เพิ่ม method takeDamage ที่รับ boolean
    public void takeDamage(int damage, boolean isBossDamage) {
        if (isBossDamage) {
            takeDamage(damage * 2); // ความเสียหายจากบอสให้คูณ 2
        } else {
            takeDamage(damage);
        }
    }

    @Override
    public void update() {
        // เพิ่มความเร็วเข้าหาเป้าหมาย
        if (targetVelX > velX) {
            velX = Math.min(targetVelX, velX + acceleration);
        } else if (targetVelX < velX) {
            velX = Math.max(targetVelX, velX - deceleration);
        }

        if (targetVelY > velY) {
            velY = Math.min(targetVelY, velY + acceleration);
        } else if (targetVelY < velY) {
            velY = Math.max(targetVelY, velY - deceleration);
        }

        // อัพเดทตำแหน่ง
        x += velX;
        y += velY;

        // จำกัดให้อยู่ในหน้าจอ
        x = Math.max(0, Math.min(x, GamePanel.WIDTH - width - 20));
        y = Math.max(0, Math.min(y, GamePanel.HEIGHT - height - 50));
        // อัพเดทกระสุน
        updateBullets();

        // อัพเดทบัฟ
        updateBuffs();

        // อัพเดทแอนิเมชันการยิง
        if (shootAnimationTime > 0) {
            shootAnimationTime--;
            if (shootAnimationTime <= 0) {
                isShooting = false;
            }
        }

        // ลดเวลาอมตะ
        if (invincibleTime > 0) {
            invincibleTime--;
        }

        // ลดเวลาคูลดาวน์การยิง
        long currentTime = System.currentTimeMillis();
        currentCooldown = currentTime - lastShotTime;
    }

    private void updateBullets() {
        // อัพเดตแต่ละกระสุน
        for (int i = bullets.size() - 1; i >= 0; i--) {
            PlayerBullet bullet = bullets.get(i);
            bullet.update();

            // ลบกระสุนที่ไม่ใช้งานแล้ว
            if (!bullet.isActive()) {
                bullets.remove(i);
            }
        }
    }

    public void updateBuffs() {
        Iterator<Powerup> iterator = activeBuffs.iterator();
        while (iterator.hasNext()) {
            Powerup buff = iterator.next();
            buff.update();

            // ถ้าบัฟหมดเวลา ให้เอาออก
            if (buff.getDuration() == 0) {
                removeBuffEffect(buff);
                iterator.remove();
            }
        }

        // อัพเดทสถานะบัฟทั้งหมดอีกรอบหลังจากลบบัฟที่หมดเวลา
        updateBuffStatus();
    }

    public void addBuff(Powerup buff) {
        // เปลี่ยนสถานะของบัฟเป็นเก็บแล้ว
        buff.setActive(false);

        boolean hasExisting = false;
        Powerup existingBuff = null;

        // ตรวจสอบว่ามีบัฟประเภทเดียวกันอยู่แล้วหรือไม่
        for (Powerup activeBuff : activeBuffs) {
            if (activeBuff.getCategory() == buff.getCategory()
                    && activeBuff.getType() == buff.getType()) {
                hasExisting = true;
                existingBuff = activeBuff;
                break;
            }
        }

        // ตรวจสอบว่าเป็นบัฟประเภทไหน และจัดการตามประเภท
        if (buff.getCategory() == Powerup.CATEGORY_PERMANENT) {
            // สำหรับบัฟถาวร
            String buffKey = getPermanentBuffKey(buff);
            int count = permanentBuffCounts.getOrDefault(buffKey, 0) + 1;
            permanentBuffCounts.put(buffKey, count);

            // เพิ่มเข้ารายการเฉพาะถ้ายังไม่มี
            if (!hasExisting) {
                activeBuffs.add(buff);
            }
        } else if (hasExisting) {
            if (existingBuff != null) {
                // รีเซ็ตเวลานับถอยหลังของบัฟที่มีอยู่แล้ว
                if (buff.getCategory() == Powerup.CATEGORY_TEMPORARY) {
                    // บัฟชั่วคราวมีเวลา 10 วินาที 
                    existingBuff.setDuration(600);
                } else if (buff.getCategory() == Powerup.CATEGORY_CRAZY) {
                    // บัฟสุดโหดมีเวลา 5 วินาที
                    existingBuff.setDuration(300);
                }
            }
        } else {
            // ถ้ายังไม่มีบัฟนี้ ให้เพิ่มเข้ารายการ
            activeBuffs.add(buff);
        }

        // ใช้บัฟตามประเภท
        applyBuff(buff);

        // อัพเดทสถานะบัฟทั้งหมดหลังจากเพิ่มบัฟใหม่
        updateBuffStatus();

        // เล่นเสียงเก็บบัฟ
        SoundManager.playSound("get_skill");
    }

    private void applyBuff(Powerup buff) {
        switch (buff.getCategory()) {
            case Powerup.CATEGORY_CRAZY -> {
                switch (buff.getType()) {
                    case Powerup.TYPE_CRAZY_SHOOTING -> {
                        crazyShootingMode = true;
                        SoundManager.playSound("crazy_shooting");
                    }
                    case Powerup.TYPE_STOP_TIME -> {
                        // จัดการโดย GamePanel
                        SoundManager.playSound("time_stop");
                    }
                }
            }
            case Powerup.CATEGORY_PERMANENT -> {
                switch (buff.getType()) {
                    case Powerup.TYPE_INCREASE_DAMAGE -> {
                        bulletDamage += buff.getValue();
                    }
                    case Powerup.TYPE_INCREASE_SPEED -> {
                        speed += buff.getValue();
                    }
                    case Powerup.TYPE_INCREASE_SHOOTING_SPEED -> {
                        shootCooldownReduction += buff.getValue();
                    }
                    case Powerup.TYPE_KNOCKBACK -> {
                        knockbackEnabled = true;
                        knockbackPower = Math.max(knockbackPower, buff.getValue());
                    }
                    case Powerup.TYPE_MORE_HEART -> {
                        lives++;
                    }
                    case Powerup.TYPE_MULTIPLE_BULLETS -> {
                        extraBullets += buff.getValue();
                    }
                }
            }
            case Powerup.CATEGORY_TEMPORARY -> {
                switch (buff.getType()) {
                    case Powerup.TYPE_INCREASE_DAMAGE -> {
                        bulletDamage += buff.getValue();
                    }
                    case Powerup.TYPE_INCREASE_SPEED -> {
                        speed += buff.getValue();
                    }
                    case Powerup.TYPE_INCREASE_SHOOTING_SPEED -> {
                        shootCooldownReduction += buff.getValue();
                    }
                    case Powerup.TYPE_KNOCKBACK -> {
                        knockbackEnabled = true;
                        knockbackPower = Math.max(knockbackPower, buff.getValue());
                    }
                    case Powerup.TYPE_MULTIPLE_BULLETS -> {
                        extraBullets += buff.getValue();
                    }
                    case Powerup.TYPE_HEALING -> {
                        int newHealth = health + buff.getValue();
                        setHealth(Math.min(newHealth, maxHealth));
                        buff.setDuration(0); // ใช้แล้วหมดทันที
                    }
                }
            }
        }
    }

    private void removeBuffEffect(Powerup buff) {
        // ลบเฉพาะเอฟเฟกต์ของบัฟนี้ออก
        switch (buff.getCategory()) {
            case Powerup.CATEGORY_CRAZY -> {
            }

            case Powerup.CATEGORY_TEMPORARY -> {
                switch (buff.getType()) {
                    case Powerup.TYPE_INCREASE_DAMAGE ->
                        bulletDamage -= buff.getValue();
                    case Powerup.TYPE_INCREASE_SPEED ->
                        speed -= buff.getValue();
                    case Powerup.TYPE_INCREASE_SHOOTING_SPEED ->
                        shootCooldownReduction -= buff.getValue();
                    case Powerup.TYPE_KNOCKBACK -> {
                    }
                    case Powerup.TYPE_MULTIPLE_BULLETS ->
                        extraBullets -= buff.getValue();
                }
            }
        }
    }

    private void updateBuffStatus() {
        // รีเซ็ตค่าพื้นฐานของบัฟทั้งหมด
        boolean hasCrazyShooting = false;
        boolean hasKnockback = false;
        int maxKnockbackPower = 1;

        // ตรวจสอบบัฟที่ยังเหลืออยู่ทั้งหมด
        for (Powerup buff : activeBuffs) {
            // ตรวจสอบแต่ละประเภทของบัฟ
            if (buff.getCategory() == Powerup.CATEGORY_CRAZY) {
                if (buff.getType() == Powerup.TYPE_CRAZY_SHOOTING) {
                    hasCrazyShooting = true;
                } else if (buff.getType() == Powerup.TYPE_STOP_TIME) {
                }
            } // ตรวจสอบบัฟ knockback ทั้งแบบถาวรและชั่วคราว
            else if ((buff.getCategory() == Powerup.CATEGORY_PERMANENT
                    || buff.getCategory() == Powerup.CATEGORY_TEMPORARY)
                    && buff.getType() == Powerup.TYPE_KNOCKBACK) {
                hasKnockback = true;
                maxKnockbackPower = Math.max(maxKnockbackPower, buff.getValue());
            }
        }

        // อัพเดทสถานะของเอฟเฟกต์บัฟที่ทำงานอยู่
        crazyShootingMode = hasCrazyShooting;

        // อัพเดทค่า knockback
        knockbackEnabled = hasKnockback;
        if (hasKnockback) {
            knockbackPower = maxKnockbackPower;
        } else {
            knockbackPower = 1;
        }

    }

    @Override
    public void render(Graphics g) {
        // ถ้าอยู่ในช่วงอมตะให้กะพริบ
        if (invincibleTime <= 0 || invincibleTime % 10 < 5) {
            int displayWidth = width * 2;
            int displayHeight = height * 2;

            // ปรับตำแหน่งเพื่อให้ตัวละครอยู่ตรงกลางและไม่เลื่อนไปมา
            int displayX = (int) x - (displayWidth - width) / 2;
            int displayY = (int) y - (displayHeight - height) / 2;

            g.drawImage(ImageManager.getImage("player"), displayX, displayY, displayWidth, displayHeight, null);

            // แสดงแถบพลังชีวิต
            g.setColor(Color.RED);
            g.fillRect((int) x, (int) y - 10, width, 3);
            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((float) health / maxHealth * width);
            g.fillRect((int) x, (int) y - 10, healthBarWidth, 3);

            // วาดปืนและเอฟเฟคการยิง
            if (isShooting) {
                WeaponType type = weaponManager.getActiveWeaponType();
                Image gunImage = getGunImage(type);
                Image flashImage = ImageManager.getImage("muzzle_flash");

                if (gunImage != null && flashImage != null) {
                    int gunWidth = Math.max(3, (int) (gunImage.getWidth(null) * 0.02));
                    int gunHeight = Math.max(2, (int) (gunImage.getHeight(null) * 0.02));

                    int flashWidth = Math.max(3, (int) (flashImage.getWidth(null) * 0.02));
                    int flashHeight = Math.max(3, (int) (flashImage.getHeight(null) * 0.02));

                    // คำนวณตำแหน่งปืนและเอฟเฟค
                    int gunX, gunY, flashX, flashY;

                    if (gunDirection > 0) { // ยิงไปทางขวา
                        // ตำแหน่งปืน - ด้านขวาของตัวละคร
                        gunX = (int) x + width;
                        gunY = (int) y + height / 2 - gunHeight / 2;

                        // ตำแหน่งเอฟเฟค - ตรงปากกระบอกปืนด้านขวา
                        flashX = gunX + gunWidth - 1;
                        flashY = gunY + gunHeight / 2 - flashHeight / 2 - 9;

                        // วาดปืน
                        if (type != null && type.equals(WeaponType.AK47)) {
                            // กรณี Ak47 ไฟล์รูปกลับด้านกับปืนอื่น
                            g.drawImage(gunImage, gunX + gunWidth, gunY, -gunWidth, gunHeight, null);
                            flashY = gunY + (gunHeight - flashHeight) / 2 - 3;
                        } else {
                            g.drawImage(gunImage, gunX, gunY, gunWidth, gunHeight, null);
                        }

                        if (type != null && type.equals(WeaponType.GATLING_GUN)) {
                            flashY = gunY + (gunHeight - flashHeight) / 2 + 1;
                        }

                        // วาดเอฟเฟค
                        if (shootAnimationTime > SHOOT_ANIMATION_DURATION / 2) {
                            g.drawImage(flashImage, flashX, flashY, flashWidth, flashHeight, null);
                        }
                    } else { // ยิงไปทางซ้าย
                        // ตำแหน่งปืน - ด้านซ้ายของตัวละคร
                        gunX = (int) x - gunWidth;
                        gunY = (int) y + height / 2 - gunHeight / 2;

                        // ตำแหน่งเอฟเฟค - ตรงปากกระบอกปืนด้านซ้าย
                        flashX = gunX - flashWidth + 1;
                        flashY = gunY + (gunHeight - flashHeight) / 2 - 9;

                        // วาดปืนหันไปทางซ้าย
                        if (type != null && type.equals(WeaponType.AK47)) {
                            // กรณี Ak47 ไฟล์รูปกลับด้านกับปืนอื่น
                            g.drawImage(gunImage, gunX, gunY, gunWidth, gunHeight, null);
                            flashY = gunY + (gunHeight - flashHeight) / 2 - 3;
                        } else {
                            g.drawImage(gunImage, gunX + gunWidth, gunY, -gunWidth, gunHeight, null);
                        }

                        if (type != null && type.equals(WeaponType.GATLING_GUN)) {
                            flashY = gunY + (gunHeight - flashHeight) / 2 + 1;
                        }

                        // วาดเอฟเฟค
                        if (shootAnimationTime > SHOOT_ANIMATION_DURATION / 2) {
                            g.drawImage(flashImage, flashX + flashWidth, flashY, -flashWidth, flashHeight, null);
                        }
                    }
                }
            }
        }

        // วาดกระสุน
        for (PlayerBullet bullet : bullets) {
            bullet.render(g);
        }
    }

    public void addBullets(List<PlayerBullet> newBullets) {
        if (newBullets != null && !newBullets.isEmpty()) {
            bullets.addAll(newBullets);
        }
    }

    public void shoot(int targetX, int targetY) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastShotTime < getShootCooldown()) {
            return; // ยังไม่ถึงเวลายิง
        }

        // คำนวณทิศทางการยิง
        double angle = Math.atan2(targetY - (y + height / 2), targetX - (x + width / 2));

        // กำหนดทิศทางของปืนให้ตรงกับเป้าหมาย
        if (targetX < x + width / 2) {
            gunDirection = -1; // เมื่อเป้าหมายอยู่ทางซ้าย ให้หันปืนไปทางซ้าย
        } else {
            gunDirection = 1; // เมื่อเป้าหมายอยู่ทางขวา ให้หันปืนไปทางขวา
        }

        int effectiveDamage = bulletDamage;
        int bulletCount = 1;
        int bulletSpeed = 10;
        boolean spread = false;
        double spreadAmount = 0;
        shootCooldown = 200;

        WeaponType activeWeapon = weaponManager.getActiveWeaponType();
        System.out.println("equiping : " + activeWeapon);
        
        if (activeWeapon != null) {
            Weapon weapon = weaponManager.createWeapon(activeWeapon);
            bulletSpeed = weapon.getBulletSpeed();
            bulletCount = weapon.getBulletCount();
            spread = weapon.getSpreadShot();
            spreadAmount = weapon.getSpreadAmount();
            shootCooldown = weapon.getCooldown();
        }
        
        // บัฟยิงบ้าคลั่ง (Crazy Shooting)
        if (crazyShootingMode) {
            effectiveDamage = bulletDamage * 2;
            bulletCount = 5;
            bulletSpeed = (int) (bulletSpeed * 1.5);
            spreadAmount += 0.05;
            shootCooldown = (int) (shootCooldown / 1.5);
        }
        // บัฟยิงหลายนัด (Multiple Bullets)
        if (extraBullets > 0) {
            System.out.println("Extra bullet : " + extraBullets);
            bulletCount += extraBullets;
        }
        System.out.println("Damage : " + effectiveDamage);
        System.out.println("Gun Damage : " + bulletDamage);
        System.out.println("bulletCount : " + bulletCount);
        if (crazyShootingMode || spread) {
            // ยิงกระจายเป็นแฉกๆ
            for (int i = 0; i < bulletCount; i++) {
                createSingleBullet(angle, effectiveDamage, bulletSpeed, spreadAmount);
            }
        } else {
            // ยิงแบบ spray
            createBullets(angle, effectiveDamage, bulletCount, bulletSpeed, true);
        }
        // เริ่มแอนิเมชันการยิง
        isShooting = true;
        shootAnimationTime = SHOOT_ANIMATION_DURATION;

        // เล่นเสียงยิงปืนทุกครั้ง
        SoundManager.playSound("gun_shot");

        // บันทึกเวลาที่ยิง
        lastShotTime = currentTime;
    }

    private void createSingleBullet(double angle, int damage, int speed, double spreadAmount) {
        double randomSpread = (Math.random() * 2 - 1) * spreadAmount;
        double finalAngle = angle + randomSpread;

        PlayerBullet bullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, finalAngle);
        bullet.setDamage(damage);
        bullet.setSpeed(speed);

        if (knockbackEnabled) {
            bullet.setKnockback(true);
            bullet.setKnockbackPower(knockbackPower);
        }

        bullets.add(bullet);
    }

    private void createBullets(double angle, int damage, int count, int speed, boolean spread) {
        // สร้างกระสุนหลัก (ตรงกลาง)
        PlayerBullet firstBullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, angle);
        firstBullet.setDamage(damage);
        firstBullet.setSpeed(speed);

        if (knockbackEnabled) {
            firstBullet.setKnockback(true);
            firstBullet.setKnockbackPower(knockbackPower);
        }

        bullets.add(firstBullet);

        if (spread && count > 1) {
            for (int i = 1; i < count; i++) {
                double spreadAngle;
                double amount = (i % 2 == 0 ? 1 : -1) * (i + 1) * 0.1;
                spreadAngle = angle + amount;

                PlayerBullet bullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, spreadAngle);
                bullet.setDamage(damage);
                bullet.setSpeed(speed);

                if (knockbackEnabled) {
                    bullet.setKnockback(true);
                    bullet.setKnockbackPower(knockbackPower);
                }

                bullets.add(bullet);
            }
        }
    }

    public void setTargetVelocity(float targetVelX, float targetVelY) {
        this.targetVelX = targetVelX;
        this.targetVelY = targetVelY;
    }

    @Override
    public void takeDamage(int damage) {
        // ถ้าอยู่ในโหมดอมตะหรือช่วงเวลาอมตะหลังโดนโจมตี ให้ไม่รับความเสียหาย
        if (immortalMode || invincibleTime > 0) {
            return;
        }

        health -= damage;
        if (health <= 0) {
            health = 0;
            lives--;

            if (lives > 0) {
                // ยังมีชีวิตเหลือ ฟื้นฟูพลังชีวิต
                health = maxHealth;
                invincibleTime = maxInvincibleTime;
            } else {
                // หมดชีวิต
                alive = false;

                // เคลียร์บัฟทั้งหมดเมื่อตาย
                activeBuffs.clear();
                permanentBuffCounts.clear(); // เคลียร์จำนวนบัฟถาวร
                crazyShootingMode = false;
                knockbackEnabled = false;
                knockbackPower = 1;
                extraBullets = 0;
                shootCooldownReduction = 0;
                bulletDamage = 25; // รีเซ็ตค่าพื้นฐาน
                speed = 5; // รีเซ็ตค่าพื้นฐาน
            }
        } else {
            // ได้รับความเสียหายแต่ยังไม่ตาย ให้อมตะชั่วคราว
            invincibleTime = maxInvincibleTime / 2;
        }
    }
    
    public static void setBulletDamage(int damage) {
        bulletDamage = damage;
    }
    
    public static void resetBulletDamage() {
        bulletDamage = baseBulletDamage;
    }

    public List<PlayerBullet> getBullets() {
        return bullets;
    }

    public List<Powerup> getActiveBuffs() {
        return activeBuffs;
    }

    public Map<String, Integer> getPermanentBuffCounts() {
        return permanentBuffCounts;
    }

    public int getPermanentBuffCount(Powerup buff) {
        String key = getPermanentBuffKey(buff);
        return permanentBuffCounts.getOrDefault(key, 0);
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public int getLives() {
        return lives;
    }

    public long getShootCooldown() {
        // คำนวณเวลาคูลดาวน์หลังหักลบด้วยบัฟต่างๆ
        return Math.max(50, shootCooldown - shootCooldownReduction);
    }

    public long getCurrentCooldown() {
        return currentCooldown;
    }

    public boolean isImmortalMode() {
        return immortalMode;
    }

    public void toggleImmortalMode() {
        immortalMode = !immortalMode;
    }

    public boolean hasStopTimeBuff() {
        // ตรวจสอบในรายการบัฟที่ใช้งานอยู่
        for (Powerup buff : activeBuffs) {
            if (buff.getCategory() == Powerup.CATEGORY_CRAZY
                    && buff.getType() == Powerup.TYPE_STOP_TIME
                    && buff.getDuration() > 0) {
                return true;
            }
        }
        return false;
    }

    public void setWeaponManager(WeaponManager weaponManager) {
        this.weaponManager = weaponManager;
    }

    public WeaponType getSelectedWeaponType() {
        return weaponManager.getSelectedWeaponType();
    }
    
    public Image getGunImage(WeaponType type) {
        String gunType = "pistol";
        if (type != null) {
            switch (type) {
                case AK47:
                    gunType = "ak47";
                    break;
                case GATLING_GUN:
                    gunType = "gatlingGun";
                    break;
                case SHOTGUN:
                    gunType = "shotgun";
                    break;
                case SHURIKEN:
                    gunType = "shuriken";
                    break;
            }
        }
        return ImageManager.getImage(gunType);
    }
}
