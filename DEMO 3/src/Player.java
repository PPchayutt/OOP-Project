
import java.awt.*;
import java.util.ArrayList;
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
    private final long shootCooldown = 200;  // มิลลิวินาที
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

    public void move(int dx, int dy) {
        float targetX = 0;
        float targetY = 0;

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
        x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
        y = Math.max(0, Math.min(y, GamePanel.HEIGHT - height));

        // อัพเดทกระสุน
        updateBullets();

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

    @Override
    public void render(Graphics g) {
        // ถ้าอยู่ในช่วงอมตะให้กะพริบ
        if (invincibleTime <= 0 || invincibleTime % 10 < 5) {
            // วาดรูปภาพผู้เล่น
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
                        flashY = gunY + gunHeight / 2 - flashHeight / 2;

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
                        flashY = gunY + gunHeight / 2 - flashHeight / 2;

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
// แก้ไขเมธอด shoot ในคลาส Player
    public void shoot(int targetX, int targetY) {
        long currentTime = System.currentTimeMillis();

        // ยิงได้โดยไม่ต้องรอคูลดาวน์หมด (แต่ยังเก็บเวลาคูลดาวน์ไว้เหมือนเดิม)
        // คำนวณทิศทางการยิง
        double angle = Math.atan2(targetY - (y + height / 2), targetX - (x + width / 2));

        // กำหนดทิศทางของปืนให้ตรงกับเป้าหมาย
        if (targetX < x + width / 2) {
            gunDirection = -1; // เมื่อเป้าหมายอยู่ทางซ้าย ให้หันปืนไปทางซ้าย
        } else {
            gunDirection = 1;  // เมื่อเป้าหมายอยู่ทางขวา ให้หันปืนไปทางขวา
        }
        // สร้างกระสุนใหม่
        PlayerBullet bullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, angle);
        bullet.setDamage(bulletDamage);
        bullets.add(bullet);

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
            }
        } else {
            // ได้รับความเสียหายแต่ยังไม่ตาย ให้อมตะชั่วคราว
            invincibleTime = maxInvincibleTime / 2;
        }
    }

    /**
     * เพิ่มความเร็วให้กับผู้เล่น
     *
     * @param amount จำนวนที่เพิ่ม
     */
    public void increaseSpeed(int amount) {
        this.speed += amount;
    }

    /**
     * เพิ่มความเสียหายของกระสุน
     *
     * @param amount จำนวนที่เพิ่ม
     */
    public void increaseBulletDamage(int amount) {
        this.bulletDamage += amount;
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
     * ดึงค่าเวลาคูลดาวน์การยิง
     *
     * @return เวลาคูลดาวน์ (มิลลิวินาที)
     */
    public long getShootCooldown() {
        return shootCooldown;
    }

    /**
     * ดึงค่าเวลาคูลดาวน์ปัจจุบัน
     *
     * @return เวลาคูลดาวน์ปัจจุบัน (มิลลิวินาที)
     */
    public long getCurrentCooldown() {
        return currentCooldown;
    }
}
