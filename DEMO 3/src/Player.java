
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private final long shootCooldown = 200;  // มิลลิวินาที (เปลี่ยนเป็น non-final เพื่อให้บัฟปรับได้)
    private long currentCooldown = 0;  // เวลาคูลดาวน์ปัจจุบัน

    // ช่วงเวลาที่ได้รับอมตะหลังโดนโจมตี
    private int invincibleTime = 0;
    private final int maxInvincibleTime = 60;  // เฟรม

    // ค่าความเสียหายของกระสุน
    private int bulletDamage = 25;

    private int score = 0;

    // ข้อมูลเกี่ยวกับการยิงปืน
    private boolean isShooting = false;
    private int shootAnimationTime = 0;
    private final int SHOOT_ANIMATION_DURATION = 10; // 10 เฟรม
    private int gunDirection = 1; // 1 = ขวา, -1 = ซ้าย

    // ข้อมูลเกี่ยวกับบัฟ
    private final List<Powerup> activeBuffs = new ArrayList<>();
    private boolean crazyShootingMode = false;
    private boolean knockbackEnabled = false;
    private int knockbackPower = 1;
    private int extraBullets = 0;
    private long shootCooldownReduction = 0; // ลดเวลาคูลดาวน์การยิง (ms)

    public Player(float x, float y, int width, int height, int health, int speed) {
        super(x, y, width, height, health, speed);
        this.maxHealth = 100;
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
                targetX *= 0.7071f; // ประมาณ 1/sqrt(2)
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
        // การเคลื่อนที่แบบนุ่มนวล
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

    /**
     * อัพเดทกระสุนทั้งหมดของผู้เล่น
     */
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

    /**
     * อัพเดทบัฟทั้งหมดที่ใช้งานอยู่
     */
    public void updateBuffs() {
        Iterator<Powerup> iterator = activeBuffs.iterator();
        while (iterator.hasNext()) {
            Powerup buff = iterator.next();
            buff.update();

            // ถ้าบัฟหมดเวลา ให้เอาออก
            if (buff.getDuration() == 0) {
                // สำคัญ: ใช้ removeBuffEffect แทน removeBuff
                // เพื่อลบเฉพาะเอฟเฟกต์ของบัฟนี้ โดยไม่กระทบบัฟอื่น
                removeBuffEffect(buff);
                iterator.remove();
            }
        }

        // อัพเดทสถานะบัฟทั้งหมดอีกรอบหลังจากลบบัฟที่หมดเวลา
        updateBuffStatus();
    }

    /**
     * เพิ่มบัฟให้กับผู้เล่น
     *
     * @param buff
     */
    public void addBuff(Powerup buff) {
        // เปลี่ยนสถานะของบัฟเป็นเก็บแล้ว
        buff.setActive(false);

        // ใช้บัฟตามประเภท
        applyBuff(buff);

        // เก็บบัฟไว้ในรายการถ้าไม่ใช่บัฟแบบใช้ครั้งเดียว
        if (buff.getDuration() != 0) {
            activeBuffs.add(buff);
        }

        // อัพเดทสถานะบัฟทั้งหมดหลังจากเพิ่มบัฟใหม่
        updateBuffStatus();

        // เล่นเสียงเก็บบัฟ
        SoundManager.playSound("get_skill");
    }

    /**
     * นำบัฟไปใช้กับผู้เล่น
     */
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

    /**
     * ลบบัฟที่หมดเวลา และอัพเดทสถานะของบัฟที่เหลือ
     */
    private void removeBuff(Powerup buff) {
        // แทนที่จะรีเซ็ตค่าทั้งหมดตรงนี้ ให้เรียกใช้ removeBuffEffect แทน
        // เพื่อลบเฉพาะเอฟเฟกต์ของบัฟนี้เท่านั้น
        removeBuffEffect(buff);
    }

    /**
     * ลบเฉพาะเอฟเฟกต์ของบัฟที่หมดอายุ
     * เมธอดใหม่ที่เพิ่มเข้ามาเพื่อแก้ปัญหาบัฟหายหมด
     */
    private void removeBuffEffect(Powerup buff) {
        // ลบเฉพาะเอฟเฟกต์ของบัฟนี้ออก
        switch (buff.getCategory()) {
            case Powerup.CATEGORY_CRAZY:
                // ไม่รีเซ็ตค่าสถานะที่นี่ เพราะอาจมีบัฟอื่นที่ยังทำงานอยู่
                // สถานะจะถูกอัพเดทในเมธอด updateBuffStatus
                break;

            case Powerup.CATEGORY_TEMPORARY:
                switch (buff.getType()) {
                    case Powerup.TYPE_INCREASE_DAMAGE:
                        bulletDamage -= buff.getValue();
                        break;
                    case Powerup.TYPE_INCREASE_SPEED:
                        speed -= buff.getValue();
                        break;
                    case Powerup.TYPE_INCREASE_SHOOTING_SPEED:
                        shootCooldownReduction -= buff.getValue();
                        break;
                    case Powerup.TYPE_KNOCKBACK:
                        // ไม่รีเซ็ตค่า knockbackEnabled ที่นี่
                        // เพราะอาจมีบัฟ knockback อื่นที่ยังทำงานอยู่
                        break;
                    case Powerup.TYPE_MULTIPLE_BULLETS:
                        extraBullets -= buff.getValue();
                        break;
                }
                break;
        }
    }

    /**
     * อัพเดทสถานะบัฟทั้งหมด หลังจากลบบัฟที่หมดอายุแล้ว
     * เมธอดใหม่ที่เพิ่มเข้ามาเพื่อแก้ปัญหาบัฟหายหมด
     */
    private void updateBuffStatus() {
        // รีเซ็ตค่าพื้นฐานของบัฟทั้งหมด
        boolean hasCrazyShooting = false;
        boolean hasStopTime = false;
        boolean hasKnockback = false;
        int maxKnockbackPower = 1;

        // ตรวจสอบบัฟที่ยังเหลืออยู่ทั้งหมด
        for (Powerup buff : activeBuffs) {
            // ตรวจสอบแต่ละประเภทของบัฟ
            if (buff.getCategory() == Powerup.CATEGORY_CRAZY) {
                if (buff.getType() == Powerup.TYPE_CRAZY_SHOOTING) {
                    hasCrazyShooting = true;
                } else if (buff.getType() == Powerup.TYPE_STOP_TIME) {
                    hasStopTime = true;
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

        // หมายเหตุ: ไม่ต้องอัพเดท Stop Time เพราะจะถูกตรวจสอบโดยเมธอด hasStopTimeBuff
    }

    /**
     *
     * @param g
     */
    @Override
    public void render(Graphics g) {
        // ถ้าอยู่ในช่วงอมตะให้กะพริบ
        if (invincibleTime <= 0 || invincibleTime % 10 < 5) {
            // วาดรูปภาพผู้เล่น - ใช้ขนาดปกติ (ไม่ต้องคูณด้วย 2)
            g.drawImage(ImageManager.getImage("player"), (int) x, (int) y, width, height, null);

            // แสดงแถบพลังชีวิต
            g.setColor(Color.RED);
            g.fillRect((int) x, (int) y - 10, width, 3);
            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((float) health / maxHealth * width);
            g.fillRect((int) x, (int) y - 10, healthBarWidth, 3);

            // วาดปืนและเอฟเฟคการยิง
            if (isShooting) {
                Image gunImage = ImageManager.getImage("gun");
                Image flashImage = ImageManager.getImage("muzzle_flash");

                if (gunImage != null && flashImage != null) {
                    // ลดขนาดปืนให้เล็กลงมากๆ (เหลือ 2% ของขนาดเดิม)
                    int gunWidth = Math.max(3, (int) (gunImage.getWidth(null) * 0.02));
                    int gunHeight = Math.max(2, (int) (gunImage.getHeight(null) * 0.02));

                    // ลดขนาดเอฟเฟคให้เล็กลงมากๆ (เหลือ 2% ของขนาดเดิม)
                    int flashWidth = Math.max(3, (int) (flashImage.getWidth(null) * 0.02));
                    int flashHeight = Math.max(3, (int) (flashImage.getHeight(null) * 0.02));

                    // คำนวณตำแหน่งปืนและเอฟเฟค
                    int gunX, gunY, flashX, flashY;

                    if (gunDirection > 0) { // ยิงไปทางขวา
                        // ตำแหน่งปืน - ด้านขวาของตัวละคร
                        gunX = (int) x + width;
                        gunY = (int) y + height / 2 - gunHeight / 2;

                        // วาดปืน
                        g.drawImage(gunImage, gunX, gunY, gunWidth, gunHeight, null);

                        // ตำแหน่งเอฟเฟค - ตรงปากกระบอกปืนด้านขวา
                        flashX = gunX + gunWidth - 1;
                        flashY = gunY + gunHeight / 2 - flashHeight / 2 - 9;

                        // วาดเอฟเฟค
                        if (shootAnimationTime > SHOOT_ANIMATION_DURATION / 2) {
                            g.drawImage(flashImage, flashX, flashY, flashWidth, flashHeight, null);
                        }
                    } else { // ยิงไปทางซ้าย
                        // ตำแหน่งปืน - ด้านซ้ายของตัวละคร
                        gunX = (int) x - gunWidth;
                        gunY = (int) y + height / 2 - gunHeight / 2;

                        // วาดปืนหันไปทางซ้าย
                        g.drawImage(gunImage, gunX + gunWidth, gunY, -gunWidth, gunHeight, null);

                        // ตำแหน่งเอฟเฟค - ตรงปากกระบอกปืนด้านซ้าย
                        flashX = gunX - flashWidth + 1;
                        flashY = gunY + (gunHeight - flashHeight) / 2 - 9;

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

    /**
     * ยิงกระสุน
     *
     * @param targetX ตำแหน่ง x เป้าหมาย
     * @param targetY ตำแหน่ง y เป้าหมาย
     */
    public void shoot(int targetX, int targetY) {
        long currentTime = System.currentTimeMillis();

        // คำนวณทิศทางการยิง
        double angle = Math.atan2(targetY - (y + height / 2), targetX - (x + width / 2));

        // กำหนดทิศทางของปืนให้ตรงกับเป้าหมาย
        if (targetX < x + width / 2) {
            gunDirection = -1; // เมื่อเป้าหมายอยู่ทางซ้าย ให้หันปืนไปทางซ้าย
        } else {
            gunDirection = 1;  // เมื่อเป้าหมายอยู่ทางขวา ให้หันปืนไปทางขวา
        }

        // บัฟยิงบ้าคลั่ง (Crazy Shooting)
        if (crazyShootingMode) {
            for (int i = 0; i < 5; i++) {
                double spread = (Math.random() - 0.5) * 0.5; // ความกระจายของกระสุน
                double shootAngle = angle + spread;

                PlayerBullet bullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, shootAngle);
                bullet.setDamage(bulletDamage * 2); // เพิ่มความเสียหายเป็น 2 เท่า
                bullet.setSpeed(15); // เพิ่มความเร็วกระสุน

                // เพิ่ม knockback ถ้ามีการเปิดใช้งาน
                if (knockbackEnabled) {
                    bullet.setKnockback(true);
                    bullet.setKnockbackPower(knockbackPower);
                }

                bullets.add(bullet);
            }
        } // บัฟยิงหลายนัด (Multiple Bullets)
        else if (extraBullets > 0) {
            // ยิงกระสุนหลัก (ตรงกลาง)
            PlayerBullet mainBullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, angle);
            mainBullet.setDamage(bulletDamage);

            // เพิ่ม knockback ถ้ามีการเปิดใช้งาน
            if (knockbackEnabled) {
                mainBullet.setKnockback(true);
                mainBullet.setKnockbackPower(knockbackPower);
            }

            bullets.add(mainBullet);

            // ยิงกระสุนเพิ่มเติม
            for (int i = 0; i < extraBullets; i++) {
                double spread = (i % 2 == 0 ? 1 : -1) * (i + 1) * 0.1; // กระจายสลับซ้ายขวา
                double shootAngle = angle + spread;

                PlayerBullet extraBullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, shootAngle);
                extraBullet.setDamage(bulletDamage);

                // เพิ่ม knockback ถ้ามีการเปิดใช้งาน
                if (knockbackEnabled) {
                    extraBullet.setKnockback(true);
                    extraBullet.setKnockbackPower(knockbackPower);
                }

                bullets.add(extraBullet);
            }
        } // ยิงปกติ
        else {
            PlayerBullet bullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, angle);
            bullet.setDamage(bulletDamage);

            // เพิ่ม knockback ถ้ามีการเปิดใช้งาน
            if (knockbackEnabled) {
                bullet.setKnockback(true);
                bullet.setKnockbackPower(knockbackPower);
            }

            bullets.add(bullet);
        }

        // เริ่มแอนิเมชันการยิง
        isShooting = true;
        shootAnimationTime = SHOOT_ANIMATION_DURATION;

        // เล่นเสียงยิงปืนทุกครั้ง
        SoundManager.playSound("gun_shot");

        // บันทึกเวลาที่ยิง
        lastShotTime = currentTime;
    }

    /**
     * ตั้งค่าความเร็วเป้าหมายของผู้เล่น
     *
     * @param targetVelX ความเร็วเป้าหมายในแกน X
     * @param targetVelY ความเร็วเป้าหมายในแกน Y
     */
    public void setTargetVelocity(float targetVelX, float targetVelY) {
        this.targetVelX = targetVelX;
        this.targetVelY = targetVelY;
    }

    @Override
    public void takeDamage(int damage) {
        // ถ้าอยู่ในช่วงอมตะให้ไม่รับความเสียหาย
        if (invincibleTime > 0) {
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

    /**
     * ดึงค่ารายการกระสุนทั้งหมด
     *
     * @return รายการกระสุน
     */
    public List<PlayerBullet> getBullets() {
        return bullets;
    }

    /**
     * ดึงค่ารายการบัฟที่ใช้งานอยู่
     *
     * @return รายการบัฟ
     */
    public List<Powerup> getActiveBuffs() {
        return activeBuffs;
    }

    /**
     * ดึงค่าความเร็วสูงสุด
     *
     * @return ความเร็วสูงสุด
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * ดึงค่าจำนวนชีวิต
     *
     * @return จำนวนชีวิต
     */
    public int getLives() {
        return lives;
    }

    /**
     * ดึงค่าเวลาคูลดาวน์การยิงที่แท้จริง (หลังจากลดด้วยบัฟ)
     *
     * @return เวลาคูลดาวน์การยิง (มิลลิวินาที)
     */
    public long getShootCooldown() {
        // คำนวณเวลาคูลดาวน์หลังหักลบด้วยบัฟต่างๆ (ต่ำสุด 50ms)
        return Math.max(50, shootCooldown - shootCooldownReduction);
    }

    /**
     * ดึงค่าเวลาคูลดาวน์ปัจจุบัน
     *
     * @return เวลาคูลดาวน์ปัจจุบัน (มิลลิวินาที)
     */
    public long getCurrentCooldown() {
        return currentCooldown;
    }

    /**
     * ตรวจสอบว่ามีบัฟ Stop Time ทำงานอยู่หรือไม่
     *
     * @return true ถ้ามีบัฟ Stop Time ทำงาน
     */
    public boolean hasStopTimeBuff() {
        // ตรวจสอบในรายการบัฟที่ใช้งานอยู่
        for (Powerup buff : activeBuffs) {
            if (buff.getCategory() == Powerup.CATEGORY_CRAZY
                    && buff.getType() == Powerup.TYPE_STOP_TIME
                    && buff.getDuration() > 0) {  // ต้องแน่ใจว่าเวลายังไม่หมด
                return true;
            }
        }
        return false;
    }
}
