
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Boss5 คือบอสสุดท้ายของเกม มีลักษณะเป็นปีศาจโบราณและมี 2 เฟส เฟสที่ 2
 * จะเปลี่ยนรูปร่างและมีการโจมตีที่รุนแรงมากขึ้น
 */
public class Boss5 extends Boss {

    private static final Random random = new Random();
    private int specialAttackTimer = 0;
    private boolean isEnraged = false; // สถานะคลั่ง
    private float glowEffect = 0; // เอฟเฟกต์เรืองแสง
    private boolean glowIncreasing = true;

    // ตัวแปรสำหรับระบบ 2 เฟส
    private boolean isPhase2 = false;
    private Image phase1Image;
    private Image phase2Image;
    private boolean isTransforming = false; // กำลังเปลี่ยนเฟสอยู่หรือไม่
    private int transformTimer = 0; // เวลาในการเปลี่ยนเฟส
    private static final int TRANSFORM_DURATION = 180; // 3 วินาที (60 FPS)

    // รูปแบบการโจมตีพิเศษในเฟส 2
    private int phase2AttackPattern = 0;
    private int phase2AttackTimer = 0;
    private List<Point> bulletSpawnPoints = new ArrayList<>(); // จุดที่จะเกิดกระสุน
    private List<Point> safeZones = new ArrayList<>(); // พื้นที่ปลอดภัย

    /**
     * สร้างบอสด่าน 5 (บอสสุดท้าย) ใหม่
     *
     * @param x ตำแหน่ง x
     * @param y ตำแหน่ง y
     * @param level ระดับความยาก
     */
    public Boss5(int x, int y, int level) {
        super(x, y, level);
        // ตัวเดิมมีพลังชีวิต 600*level เพิ่มเป็น 1000*level
        this.width = 140;
        this.height = 140;
        this.health = 1000 * level;
        this.maxHealth = 1000 * level;
        this.damage = 40; // เพิ่มจาก 35

        // โหลดรูปภาพทั้งสองเฟส
        this.phase1Image = ImageManager.getImage("L5_Boss_Phase1");
        this.phase2Image = ImageManager.getImage("L5_Boss_Phase2");

        // ใช้รูปแบบสำรองหากไม่พบไฟล์
        if (phase1Image == null) {
            phase1Image = ImageManager.getImage("boss5");
        }
        if (phase2Image == null) {
            phase2Image = phase1Image; // ใช้รูปเดิมถ้าไม่พบรูปเฟส 2
        }
    }

    @Override
    public void update() {
        updateCooldowns();

        // ถ้ากำลังเปลี่ยนเฟส ให้อัพเดตเฉพาะแอนิเมชันการเปลี่ยนเฟส
        if (isTransforming) {
            updateTransformation();
            return;
        }

        // เอฟเฟกต์เรืองแสง
        updateGlowEffect();

        phaseCounter++;
        if (phaseCounter >= (isPhase2 ? 90 : 150)) { // ลดเวลาลงจาก 120 และ 180
            phaseCounter = 0;
            phase = (phase + 1) % (isPhase2 ? 5 : 4);
            attackPattern = random.nextInt(isPhase2 ? 5 : 4);
        }

        // อัพเดตหน่วยเวลาพิเศษ
        specialAttackTimer++;
        phase2AttackTimer++;

        // เพิ่มการฟื้นพลังชีวิตในบางกรณี - สุ่มเรียกเมื่ออยู่ในเฟส 2
        if (isPhase2 && random.nextInt(300) == 0) { // โอกาส 1/300 ต่อเฟรม
            heal();
        }

        // การเคลื่อนที่แตกต่างกันตามเฟส
        if (isPhase2) {
            updatePhase2Movement();
        } else {
            updatePhase1Movement();
        }

        // จำกัดตำแหน่งไม่ให้ออกนอกจอ
        x = Math.max(0, Math.min(x, GamePanel.WIDTH - width - 10));
        y = Math.max(30, Math.min(y, isPhase2 ? 400 : 300));
    }

    /**
     * อัพเดตการเคลื่อนที่ในเฟส 1
     */
    private void updatePhase1Movement() {
        switch (phase) {
            case 0:
                // เคลื่อนที่จากซ้ายไปขวาเร็วและกว้างขึ้น
                x += speed * 1.5f * moveDirection;
                if (x <= 0 || x >= GamePanel.WIDTH - width) {
                    moveDirection *= -1;
                }
                break;
            case 1:
                // เคลื่อนที่เป็นรูปคลื่นวงกลม
                x += Math.cos(phaseCounter * 0.1) * speed * 2.0f;
                y += Math.sin(phaseCounter * 0.1) * speed * 1.5f;
                break;
            case 2:
                // เคลื่อนที่แบบวูบวาบ โผล่วับๆ หายๆ
                if (phaseCounter % 20 < 10) {
                    x += random.nextInt(5) - 2;
                    y += random.nextInt(5) - 2;
                } else {
                    // พุ่งเข้าหาตรงกลางจอ
                    double dx = GamePanel.WIDTH / 2 - (x + width / 2);
                    double dy = 150 - (y + height / 2);
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    if (dist > 0) {
                        x += (dx / dist) * speed * 2;
                        y += (dy / dist) * speed * 2;
                    }
                }
                break;
            case 3:
                // หยุดนิ่ง (เตรียมโจมตีพิเศษ)
                // ไม่มีการเคลื่อนที่
                break;
        }
    }

    /**
     * อัพเดตการเคลื่อนที่ในเฟส 2 (เร็วและคาดเดายากกว่า)
     */
    private void updatePhase2Movement() {
        switch (phase) {
            case 0:
                // เคลื่อนที่จากซ้ายไปขวาเร็วขึ้นมาก
                x += speed * 3.0f * moveDirection; // เพิ่มจาก 2.5f เป็น 3.0f
                if (x <= 0 || x >= GamePanel.WIDTH - width) {
                    moveDirection *= -1;
                }
                break;
            case 1:
                // เคลื่อนที่วนเป็น 8 เร็วขึ้น
                float t = phaseCounter * 0.08f; // เพิ่มจาก 0.05f เป็น 0.08f
                x = GamePanel.WIDTH / 2 - width / 2 + (float) (Math.sin(t) * 250); // เพิ่มรัศมีจาก 200 เป็น 250
                y = 200 + (float) (Math.sin(2 * t) * 120); // เพิ่มรัศมีจาก 100 เป็น 120
                break;
            case 2:
                // เคลื่อนที่แบบซิกแซกซับซ้อนยิ่งขึ้น
                x += Math.cos(phaseCounter * 0.15) * speed * 3.0f; // เพิ่มจาก 0.1 และ 2.5f
                y += Math.abs(Math.sin(phaseCounter * 0.3)) * speed * 2.5f; // เพิ่มจาก 0.2 และ 2.0f
                break;
            case 3:
                // วาร์ปรอบๆ จอถี่ขึ้น
                if (phaseCounter % 25 == 0) { // ลดจาก 40 เป็น 25
                    // สุ่มตำแหน่งใหม่
                    x = random.nextInt(GamePanel.WIDTH - width - 20) + 10;
                    y = random.nextInt(300) + 50;
                }
                break;
            case 4:
                // เพิ่มความถี่ในการโจมตีครั้งสุดท้าย
                if (phase2AttackTimer >= 240) { // ลดจาก 300 เป็น 240 (4 วินาที)
                    phase2AttackTimer = 0;
                    prepareUltimateAttack(); // เตรียมการโจมตีสุดท้าย
                }
                break;
        }
    }

    /**
     * อัพเดตเอฟเฟกต์เรืองแสง
     */
    private void updateGlowEffect() {
        if (glowIncreasing) {
            glowEffect += 0.05f;
            if (glowEffect >= 1.0f) {
                glowEffect = 1.0f;
                glowIncreasing = false;
            }
        } else {
            glowEffect -= 0.05f;
            if (glowEffect <= 0.1f) {
                glowEffect = 0.1f;
                glowIncreasing = true;
            }
        }
    }

    /**
     * อัพเดตแอนิเมชันการเปลี่ยนเฟส
     */
    private void updateTransformation() {
        transformTimer++;

        // เสร็จสิ้นการเปลี่ยนเฟสเมื่อครบกำหนดเวลา
        if (transformTimer >= TRANSFORM_DURATION) {
            isTransforming = false;
            transformTimer = 0;

            // ผลกระทบหลังเปลี่ยนเฟส
            speed = 5;
            damage = damage * 3;

            // เล่นเสียงเมื่อเปลี่ยนเฟสเสร็จ
            SoundManager.playSound("boss_transform");
        }
    }

    /**
     * เตรียมการโจมตีสุดท้ายของเฟส 2
     */
    private void prepareUltimateAttack() {
        phase2AttackPattern = random.nextInt(4);

        // รีเซ็ตจุดที่จะเกิดกระสุนและพื้นที่ปลอดภัย
        bulletSpawnPoints.clear();
        safeZones.clear();

        switch (phase2AttackPattern) {
            case 0: // "วงกลมแห่งความพินาศ"
                // เตรียมพื้นที่สำหรับ Ultimate Attack แบบ 1
                break;
            case 1: // "พายุหมุนกระสุน"
                // เตรียมพื้นที่สำหรับ Ultimate Attack แบบ 2 
                break;
            case 2: // "กำแพงกระสุน"
                // เตรียมพื้นที่สำหรับ Ultimate Attack แบบ 3
                break;
            case 3: // "การพิพากษาครั้งสุดท้าย"
                // เตรียมพื้นที่สำหรับ Ultimate Attack แบบ 4
                for (int i = 0; i < 5; i++) {
                    // สร้างพื้นที่ปลอดภัยแบบวงกลมสุ่ม 5 จุด
                    int safeX = random.nextInt(GamePanel.WIDTH - 100) + 50;
                    int safeY = random.nextInt(GamePanel.HEIGHT - 200) + 100;
                    safeZones.add(new Point(safeX, safeY));
                }
                break;
        }
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Composite originalComposite = g2d.getComposite();

        // ถ้ากำลังเปลี่ยนเฟส ให้แสดงเอฟเฟกต์พิเศษ
        if (isTransforming) {
            renderTransformation(g2d);

            // วาดแถบพลังชีวิต
            renderHealthBar(g2d);

            g2d.setComposite(originalComposite);
            return;
        }

        // เลือกรูปภาพตามเฟส
        Image bossImage = isPhase2 ? phase2Image : phase1Image;

        // วาดตัวบอส
        if (bossImage != null) {
            g2d.drawImage(bossImage, (int) x, (int) y, width, height, null);
        } else {
            // ถ้าไม่มีรูปภาพ ให้วาดรูปทรงตามเฟส
            renderFallbackBossImage(g2d, isPhase2);
        }

        // วาดแถบพลังชีวิต
        renderHealthBar(g2d);

        // คืนค่า composite เดิม
        g2d.setComposite(originalComposite);

        // วาดพื้นที่ปลอดภัยถ้าอยู่ในเฟส 2 และกำลังเตรียมโจมตีสุดท้าย
        if (isPhase2 && phase == 4 && !safeZones.isEmpty()) {
            renderSafeZones(g2d);
        }
    }

    /**
     * วาดแอนิเมชันการเปลี่ยนเฟส
     */
    private void renderTransformation(Graphics2D g2d) {
        // ใช้ภาพที่เลือนจากเฟส 1 ไปเป็นเฟส 2
        if (transformTimer > TRANSFORM_DURATION / 3 && transformTimer < TRANSFORM_DURATION * 2 / 3) {
            float blendFactor = (float) (transformTimer - TRANSFORM_DURATION / 3) / (TRANSFORM_DURATION / 3);

            // วาดภาพเฟส 1 ด้วยความโปร่งใสที่ลดลง
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - blendFactor));
            g2d.drawImage(phase1Image, (int) x, (int) y, width, height, null);

            // วาดภาพเฟส 2 ด้วยความโปร่งใสที่เพิ่มขึ้น
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blendFactor));
            g2d.drawImage(phase2Image, (int) x, (int) y, width, height, null);
        } else if (transformTimer <= TRANSFORM_DURATION / 3) {
            // วาดภาพเฟส 1 ในช่วงแรก
            g2d.drawImage(phase1Image, (int) x, (int) y, width, height, null);
        } else {
            // วาดภาพเฟส 2 ในช่วงท้าย
            g2d.drawImage(phase2Image, (int) x, (int) y, width, height, null);
        }

        // เอฟเฟกต์แสงรอบตัวระหว่างเปลี่ยนเฟส
        if (transformTimer >= TRANSFORM_DURATION / 3 && transformTimer <= TRANSFORM_DURATION * 2 / 3) {
            // พลังงานระเบิดออกมา
            int glowSize = (int) (Math.min(200, transformTimer % 20 * 10));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(new Color(255, 50, 50, 100));
            g2d.fillOval((int) (x + width / 2 - glowSize / 2), (int) (y + height / 2 - glowSize / 2), glowSize, glowSize);
        }
    }

    /**
     * วาดรูปทรงบอสทดแทนในกรณีที่ไม่มีรูปภาพ
     */
    private void renderFallbackBossImage(Graphics2D g2d, boolean isPhase2) {
        if (isPhase2) {
            // รูปร่างเฟส 2 (มืดและอันตรายมากขึ้น)
            g2d.setColor(new Color(80, 0, 0)); // สีแดงเข้มเกือบดำ
            g2d.fillOval((int) x, (int) y, width, height);

            // ตาสีแดงเรืองแสง
            g2d.setColor(Color.RED);
            g2d.fillOval((int) (x + width / 4), (int) (y + height / 3), width / 5, height / 5);
            g2d.fillOval((int) (x + width * 3 / 5), (int) (y + height / 3), width / 5, height / 5);
        } else {
            // รูปร่างเฟส 1 (เหมือนเดิม)
            g2d.setColor(new Color(30, 0, 50)); // สีม่วงเข้มเกือบดำ
            g2d.fillOval((int) x, (int) y, width, height);

            // ตาสีแดงเรืองแสง
            g2d.setColor(Color.RED);
            g2d.fillOval((int) (x + width / 4), (int) (y + height / 3), width / 6, height / 6);
            g2d.fillOval((int) (x + width * 3 / 5), (int) (y + height / 3), width / 6, height / 6);
        }
    }

    /**
     * วาดแถบพลังชีวิต
     */
    private void renderHealthBar(Graphics2D g2d) {
        // แถบพลังชีวิต
        g2d.setColor(Color.RED);
        g2d.fillRect((int) x, (int) y - 25, width, 20);
        g2d.setColor(isPhase2 ? Color.ORANGE : (isEnraged ? Color.YELLOW : Color.GREEN));
        int healthBarWidth = (int) ((double) health / (isPhase2 ? 300 * level : 600 * level) * width);
        g2d.fillRect((int) x, (int) y - 25, healthBarWidth, 20);

        // กรอบแถบพลังชีวิต
        g2d.setColor(Color.WHITE);
        g2d.drawRect((int) x, (int) y - 25, width, 20);

        // พิมพ์ข้อความแสดงสถานะ - เปลี่ยนเป็น Boss Lv.5 แทนที่จะใช้ค่า level
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String statusText = "FINAL BOSS Lv.5 HP:" + health;
        if (isPhase2) {
            statusText += " [PHASE 2]";
        } else if (isEnraged) {
            statusText += " [ENRAGED]";
        }
        g2d.drawString(statusText, (int) x, (int) y + height + 25);
    }

    /**
     * วาดพื้นที่ปลอดภัยในการโจมตีสุดท้าย
     */
    private void renderSafeZones(Graphics2D g2d) {
        // วาดพื้นที่ปลอดภัย
        for (Point safeZone : safeZones) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(Color.GREEN);
            int safeZoneSize = 120;
            g2d.fillOval(safeZone.x - safeZoneSize / 2, safeZone.y - safeZoneSize / 2, safeZoneSize, safeZoneSize);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawOval(safeZone.x - safeZoneSize / 2, safeZone.y - safeZoneSize / 2, safeZoneSize, safeZoneSize);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack() || isTransforming) {
            return null;
        }

        // การโจมตีจะแตกต่างกันในแต่ละเฟส
        if (isPhase2) {
            return attackPhase2();
        } else {
            return attackPhase1();
        }
    }

    /**
     * การโจมตีปกติในเฟส 1
     */
    private EnemyBullet attackPhase1() {
        switch (attackPattern) {
            case 0:
                // ยิงกระสุนใหญ่ตรงลงมา
                resetShootCooldown(isEnraged ? 15 : 25);
                return new EnemyBullet((int) x + width / 2 - 12, (int) y + height, 25, 25, Math.PI / 2, 5, damage * (isEnraged ? 2 : 1));
            case 1:
                // ยิงทแยงมุมหลากหลายทิศทาง
                resetShootCooldown(isEnraged ? 30 : 45);
                double angle = Math.PI / 4 + random.nextDouble() * Math.PI / 2;
                return new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, angle, 6, damage);
            case 2:
                // ยิงตรงไปที่ผู้เล่นแบบแม่นยำและเร็ว
                resetShootCooldown(isEnraged ? 10 : 15);
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height / 2, 20, 20, targetAngle, 7, damage);
            case 3:
                // กระสุนพิเศษที่เคลื่อนที่เป็นเส้นโค้ง
                resetShootCooldown(isEnraged ? 40 : 60);
                EnemyBullet bullet = new EnemyBullet((int) x + width / 2 - 15, (int) y + height / 2, 30, 30, Math.PI / 2, 3, damage * 2);
                return bullet;
            default:
                resetShootCooldown(isEnraged ? 20 : 30);
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height, 20, 20, Math.PI / 2, 4, damage);
        }
    }

    /**
     * การโจมตีปกติในเฟส 2 (รุนแรงและซับซ้อนมากขึ้น)
     */
    private EnemyBullet attackPhase2() {
        switch (attackPattern) {
            case 0:
                // ยิงกระสุนพลังงานขนาดใหญ่เร็วขึ้น
                resetShootCooldown(7); // ลดจาก 10
                return new EnemyBullet((int) x + width / 2 - 15, (int) y + height, 35, 35, Math.PI / 2, 9, damage * 2);
            case 1:
                // ยิงแบบพัดกระจาย 3 นัด เร็วขึ้น
                resetShootCooldown(15); // ลดจาก 20
                double baseAngle = Math.PI / 2;
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height / 2, 25, 25, baseAngle, 7, (int) (damage * 2.0));
            case 2:
                // ยิงล็อคเป้าผู้เล่นเร็วขึ้นมาก
                resetShootCooldown(5); // ลดจาก 7
                int targetX = GamePanel.WIDTH / 2;
                int targetY = GamePanel.HEIGHT - 100;
                double dx = targetX - (x + width / 2);
                double dy = targetY - (y + height / 2);
                double targetAngle = Math.atan2(dy, dx);
                return new EnemyBullet((int) x + width / 2 - 12, (int) y + height / 2, 24, 24, targetAngle, 11, (int) (damage * 2.0));
            case 3:
                // กระสุนแตกกระจายเร็วขึ้น
                resetShootCooldown(20); // ลดจาก 25
                return new EnemyBullet((int) x + width / 2 - 20, (int) y + height / 2, 45, 45, Math.PI / 2, 6, damage * 3);
            case 4:
                // กระสุนดูดเข้าตัวผู้เล่น
                resetShootCooldown(25); // ลดจาก 30
                double randomAngle = Math.random() * Math.PI * 2;
                return new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, randomAngle, 4, damage * 2);
            default:
                resetShootCooldown(10); // ลดจาก 15
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height, 20, 20, Math.PI / 2, 7, damage);
        }
    }

    @Override
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack() || isTransforming) {
            return null;
        }

        List<EnemyBullet> bullets = new ArrayList<>();

        // การโจมตีพิเศษแตกต่างกันในแต่ละเฟส
        if (isPhase2) {
            // รูปแบบการโจมตีพิเศษเฟส 2
            specialAttackPhase2(bullets);
        } else {
            // รูปแบบการโจมตีพิเศษเฟส 1
            specialAttackPhase1(bullets);
        }

        return bullets;
    }

    /**
     * การโจมตีพิเศษในเฟส 1
     */
    private void specialAttackPhase1(List<EnemyBullet> bullets) {
        resetShootCooldown(isEnraged ? 60 : 90);

        // เลือกการโจมตีพิเศษตามความเหมาะสม
        int specialAttackType = random.nextInt(isEnraged ? 4 : 3);

        switch (specialAttackType) {
            case 0:
                // ยิงเป็นวงกลมรอบตัว 20 ทิศทาง
                for (int i = 0; i < 20; i++) {
                    double angle = Math.PI * 2 * i / 20;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, angle, 5, damage));
                }
                break;
            case 1:
                // ยิงเป็นรูปกากบาท 2 ชั้น
                for (int i = 0; i < 8; i++) {
                    double angle = Math.PI * i / 4;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 10, (int) y + height / 2, 20, 20, angle, 5, damage * 2));
                }

                // รอบที่สอง (ชั้นที่ 2)
                for (int i = 0; i < 8; i++) {
                    double angle = Math.PI * i / 4 + Math.PI / 8;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 10, (int) y + height / 2, 20, 20, angle, 4, damage * 2));
                }
                break;
            case 2:
                // กระสุนเลเซอร์ใหญ่ 3 ทิศทาง พุ่งเป็นเส้นตรงลงมา
                bullets.add(new EnemyBullet((int) x + width / 2 - 25, (int) y + height, 50, 50, Math.PI / 2, 8, damage * 3));
                bullets.add(new EnemyBullet((int) x + width / 4 - 15, (int) y + height, 30, 30, Math.PI / 2, 7, damage * 2));
                bullets.add(new EnemyBullet((int) x + width * 3 / 4 - 15, (int) y + height, 30, 30, Math.PI / 2, 7, damage * 2));
                break;
            case 3:
                // โจมตีสุดท้าย (เฉพาะเมื่อคลั่ง): ยิงกระสุนทุกทิศทาง
                for (int i = 0; i < 36; i++) {
                    double angle = Math.PI * 2 * i / 36;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 6, (int) y + height / 2, 12, 12, angle, 6, damage));
                }
                break;
        }
    }

    /**
     * การโจมตีพิเศษในเฟส 2 (สุดโหด)
     */
    private void specialAttackPhase2(List<EnemyBullet> bullets) {
        resetShootCooldown(30); // ลดจาก 40

        // เลือกการโจมตีพิเศษตามความเหมาะสม
        int specialAttackType = random.nextInt(4);

        switch (specialAttackType) {
            case 0:
                // "ราวกระสุนแห่งความมืด" - ยิงเป็นวงกลม 2 ชั้น
                for (int i = 0; i < 42; i++) { // เพิ่มจาก 36
                    double angle = Math.PI * 2 * i / 42;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 6, (int) y + height / 2, 12, 12, angle, 7, damage));
                    // เพิ่มความเร็ว

                    // ชั้นนอก ช้ากว่า
                    bullets.add(new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, angle, 5, damage * 2));
                }
                break;
            case 1:
                // "พายุเชิงซ้อน" - วงกลมซ้อนกับกากบาท
                // วงกลม
                for (int i = 0; i < 30; i++) { // เพิ่มจาก 24
                    double angle = Math.PI * 2 * i / 30;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 6, (int) y + height / 2, 12, 12, angle, 6, damage));
                }

                // กากบาท - ใหญ่และเร็วกว่า
                for (int i = 0; i < 8; i++) { // เพิ่มจาก 4
                    double angle = Math.PI * i / 4;
                    bullets.add(new EnemyBullet((int) x + width / 2 - 15, (int) y + height / 2, 30, 30, angle, 9, damage * 3));
                }
                break;
            case 2:
                // "ตาข่ายเพชร" - รูปแบบตาข่ายสี่เหลี่ยมข้าวหลามตัด
                for (int i = 0; i < 4; i++) {
                    double baseAngle = Math.PI * i / 2;
                    // แต่ละเส้นมีหลายกระสุน
                    for (int j = 1; j <= 15; j++) { // เพิ่มจาก 10
                        bullets.add(new EnemyBullet(
                                (int) (x + width / 2 - 8 + Math.cos(baseAngle) * j * 20),
                                (int) (y + height / 2 - 8 + Math.sin(baseAngle) * j * 20),
                                16, 16, baseAngle + Math.PI, 8, (int) (damage * 1.5)));
                    }
                }

                // เส้นตรงกลาง
                for (int i = 0; i < 4; i++) {
                    double baseAngle = Math.PI * i / 2 + Math.PI / 4;
                    for (int j = 1; j <= 8; j++) { // เพิ่มจาก 6
                        bullets.add(new EnemyBullet(
                                (int) (x + width / 2 - 8 + Math.cos(baseAngle) * j * 30),
                                (int) (y + height / 2 - 8 + Math.sin(baseAngle) * j * 30),
                                16, 16, baseAngle + Math.PI, 7, damage
                        ));
                    }
                }
                break;
            case 3:
                // "การพิพากษาครั้งสุดท้าย" - ล็อคเป้าผู้เล่น 7 กระสุนขนาดใหญ่
                double playerAngle = Math.PI / 2; // ในเกมจริงควรคำนวณจากตำแหน่งผู้เล่น

                // กระสุนกลาง - ใหญ่และเร็วมาก
                bullets.add(new EnemyBullet((int) x + width / 2 - 25, (int) y + height / 2, 50, 50, playerAngle, 10, damage * 4));

                // กระสุนซ้าย-ขวา - เล็กกว่าแต่เร็ว
                bullets.add(new EnemyBullet((int) x + width / 2 - 15, (int) y + height / 2, 30, 30, playerAngle - 0.2, 11, damage * 3));
                bullets.add(new EnemyBullet((int) x + width / 2 - 15, (int) y + height / 2, 30, 30, playerAngle + 0.2, 11, damage * 3));

                // กระสุนซ้าย-ขวาสุด - เล็กและเร็ว
                bullets.add(new EnemyBullet((int) x + width / 2 - 10, (int) y + height / 2, 20, 20, playerAngle - 0.4, 12, damage * 2));
                bullets.add(new EnemyBullet((int) x + width / 2 - 10, (int) y + height / 2, 20, 20, playerAngle + 0.4, 12, damage * 2));

                // เพิ่มกระสุนใหม่
                bullets.add(new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, playerAngle - 0.6, 13, damage));
                bullets.add(new EnemyBullet((int) x + width / 2 - 8, (int) y + height / 2, 16, 16, playerAngle + 0.6, 13, damage));
                break;
        }
    }

    public void heal() {
        // ฟื้นพลังชีวิตเฉพาะเมื่ออยู่ในเฟส 2 และเลือดเหลือน้อยกว่า 40%
        if (isPhase2 && health < maxHealth * 0.4) {
            // ฟื้น 10% ของพลังชีวิตสูงสุด
            int healAmount = (int) (maxHealth * 0.1);
            health = Math.min(health + healAmount, maxHealth);

            // เพิ่มเอฟเฟกต์แสงสว่างรอบตัวเมื่อฟื้นพลังชีวิต
            glowEffect = 1.0f;
            glowIncreasing = false;
        }
    }

    /**
     * การโจมตีสุดท้ายในเฟส 2 (ใช้จาก GamePanel เมื่อบอสอยู่ในเฟส 2
     * และตรงตามเงื่อนไข)
     */
    public List<EnemyBullet> executeUltimateAttack() {
        List<EnemyBullet> bullets = new ArrayList<>();

        switch (phase2AttackPattern) {
            case 0: // "วงกลมแห่งความพินาศ"
                // ยิงวงกลมจากขอบจอทั้ง 4 ด้าน
                executeCircleOfDoom(bullets);
                break;
            case 1: // "พายุหมุนกระสุน"
                // สร้างลมหมุนกระสุนหลายจุด
                executeBulletTornado(bullets);
                break;
            case 2: // "กำแพงกระสุน"
                // สร้างกำแพงกระสุนจากทั้ง 4 ทิศ บีบพื้นที่
                executeBulletWall(bullets);
                break;
            case 3: // "การพิพากษาครั้งสุดท้าย"
                // เติมพื้นที่เกือบทั้งจอด้วยกระสุนยกเว้นพื้นที่ปลอดภัย
                executeFinaljudgment(bullets);
                break;
        }

        return bullets;
    }

    /**
     * การโจมตี: วงกลมแห่งความพินาศ
     */
    private void executeCircleOfDoom(List<EnemyBullet> bullets) {
        // สร้างวงกลมจากขอบจอทั้ง 4 ด้าน
        int[] centerX = {0, GamePanel.WIDTH, GamePanel.WIDTH / 2, GamePanel.WIDTH / 2};
        int[] centerY = {GamePanel.HEIGHT / 2, GamePanel.HEIGHT / 2, 0, GamePanel.HEIGHT};

        for (int c = 0; c < 4; c++) {
            for (int i = 0; i < 36; i++) {
                double angle = Math.PI * 2 * i / 36;
                double bulletX = centerX[c] + Math.cos(angle) * 150;
                double bulletY = centerY[c] + Math.sin(angle) * 150;

                double moveAngle = Math.atan2(
                        GamePanel.HEIGHT / 2 - bulletY,
                        GamePanel.WIDTH / 2 - bulletX
                );

                bullets.add(new EnemyBullet(
                        (int) bulletX, (int) bulletY,
                        15, 15, moveAngle, 5, damage * 2
                ));
            }
        }
    }

    /**
     * การโจมตี: พายุหมุนกระสุน
     */
    private void executeBulletTornado(List<EnemyBullet> bullets) {
        // สร้างพายุหมุน 3 จุด
        int[][] tornadoCenters = {
            {GamePanel.WIDTH / 4, GamePanel.HEIGHT / 3},
            {GamePanel.WIDTH * 3 / 4, GamePanel.HEIGHT / 3},
            {GamePanel.WIDTH / 2, GamePanel.HEIGHT * 2 / 3}
        };

        for (int[] center : tornadoCenters) {
            for (int i = 0; i < 24; i++) {
                double angle = Math.PI * 2 * i / 24;
                double distance = 50 + (i % 3) * 20;

                double bulletX = center[0] + Math.cos(angle) * distance;
                double bulletY = center[1] + Math.sin(angle) * distance;

                // กระสุนจะวนเข้าหาศูนย์กลาง
                double moveAngle = angle + Math.PI / 2;

                bullets.add(new EnemyBullet(
                        (int) bulletX, (int) bulletY,
                        10, 10, moveAngle, 3, damage
                ));
            }
        }
    }

    /**
     * การโจมตี: กำแพงกระสุน
     */
    private void executeBulletWall(List<EnemyBullet> bullets) {
        // สร้างกำแพงจาก 4 ทิศ
        // ทิศบน
        for (int x = 50; x < GamePanel.WIDTH; x += 30) {
            bullets.add(new EnemyBullet(x, 0, 20, 20, Math.PI / 2, 4, damage));
        }

        // ทิศล่าง
        for (int x = 30; x < GamePanel.WIDTH; x += 30) {
            bullets.add(new EnemyBullet(x, GamePanel.HEIGHT, 20, 20, -Math.PI / 2, 4, damage));
        }

        // ทิศซ้าย
        for (int y = 50; y < GamePanel.HEIGHT; y += 30) {
            bullets.add(new EnemyBullet(0, y, 20, 20, 0, 4, damage));
        }

        // ทิศขวา
        for (int y = 30; y < GamePanel.HEIGHT; y += 30) {
            bullets.add(new EnemyBullet(GamePanel.WIDTH, y, 20, 20, Math.PI, 4, damage));
        }
    }

    /**
     * การโจมตี: การพิพากษาครั้งสุดท้าย
     */
    private void executeFinaljudgment(List<EnemyBullet> bullets) {
        // ยิงกระสุนเต็มจอ ยกเว้นพื้นที่ปลอดภัย
        for (int x = 40; x < GamePanel.WIDTH; x += 60) {
            for (int y = 40; y < GamePanel.HEIGHT; y += 60) {
                // ตรวจสอบว่าอยู่ในพื้นที่ปลอดภัยหรือไม่
                boolean inSafeZone = false;

                for (Point safeZone : safeZones) {
                    double distance = Math.sqrt(
                            Math.pow(x - safeZone.x, 2)
                            + Math.pow(y - safeZone.y, 2)
                    );

                    if (distance < 60) { // รัศมีปลอดภัย
                        inSafeZone = true;
                        break;
                    }
                }

                if (!inSafeZone) {
                    bullets.add(new EnemyBullet(x, 0, 15, 15, Math.PI / 2, 6, damage));
                }
            }
        }
    }

    @Override
    public void takeDamage(int damage) {
        // หากกำลังเปลี่ยนเฟส จะไม่รับความเสียหาย
        if (isTransforming) {
            return;
        }

        // รับความเสียหายตามปกติ
        super.takeDamage(damage);

        // เมื่อพลังชีวิตเหลือน้อยกว่า 60% ให้บอสเร็วขึ้นและโจมตีแรงขึ้น (เฟส 1)
        if (health <= 600 * level && !isEnraged && !isPhase2) {
            speed = 2;
            this.damage = this.damage * 2;
            isEnraged = true; // เข้าสู่โหมดคลั่ง
        }

        // เมื่อพลังชีวิตเหลือน้อยกว่า 35% (เดิม 30%) และยังไม่เปลี่ยนเป็นเฟส 2
        if (health <= 350 * level && !isPhase2) {
            // เข้าสู่เฟส 2
            isPhase2 = true;
            isTransforming = true;
            transformTimer = 0;

            // หยุดการโจมตีชั่วคราวระหว่างเปลี่ยนเฟส
            resetShootCooldown(TRANSFORM_DURATION + 30);

            // ให้เลือดเฟส 2 เต็มขึ้นมากกว่าเดิมเป็น 400*level (เดิม 300*level)
            health = 400 * level;

            // เล่นเสียงเมื่อเริ่มเปลี่ยนเฟส
            SoundManager.playSound("boss_transform_start");
        }
    }

    /**
     * ตรวจสอบว่าบอสอยู่ในเฟส 2 หรือไม่
     */
    public boolean isInPhase2() {
        return isPhase2;
    }

    /**
     * ตรวจสอบว่าบอสกำลังในเปลี่ยนเฟสหรือไม่
     */
    public boolean isTransforming() {
        return isTransforming;
    }
}
