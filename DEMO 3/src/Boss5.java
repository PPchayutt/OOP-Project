
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Boss5 คือบอสสุดท้ายของเกม มีลักษณะเป็นปีศาจโบราณ
 */
public class Boss5 extends Boss {

    private static final Random random = new Random();
    private int specialAttackTimer = 0;
    private boolean isEnraged = false; // สถานะคลั่ง
    private float glowEffect = 0; // เอฟเฟกต์เรืองแสง
    private boolean glowIncreasing = true;

    /*
     * สร้างบอสด่าน 5 (บอสสุดท้าย) ใหม่
     * 
     * @param x ตำแหน่ง x
     * @param y ตำแหน่ง y
     * @param level ระดับความยาก
     */
    public Boss5(int x, int y, int level) {
        super(x, y, level);

        // ปรับให้บอสสุดท้ายแข็งแกร่งที่สุด
        this.width = 140;
        this.height = 140;
        this.health = 500 * level;
        this.maxHealth = 500 * level;
        this.damage = 35;
    }

    @Override
    public void update() {
        updateCooldowns();

        // เอฟเฟกต์เรืองแสง
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

        phaseCounter++;
        if (phaseCounter >= 180) {
            phaseCounter = 0;
            phase = (phase + 1) % 4; // เพิ่มเป็น 4 เฟส
            attackPattern = random.nextInt(5); // เพิ่มเป็น 5 รูปแบบการโจมตี
        }

        // รูปแบบการเคลื่อนที่ของบอส
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
                x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
                y = Math.max(50, Math.min(y, 300));
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
                specialAttackTimer++;
                // ทำการโจมตีพิเศษแบบอัตโนมัติทุก 1 วินาที
                if (specialAttackTimer >= 60) {
                    specialAttackTimer = 0;
                }
                break;
        }

        // จำกัดตำแหน่งไม่ให้ออกนอกจอ
        x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
        y = Math.max(50, Math.min(y, 300));
    }

    @Override
    public void render(Graphics g) {
        // ใช้รูปภาพจาก ImageManager
        Image boss5Image = ImageManager.getImage("boss5");
        if (boss5Image != null) {
            // วาดเอฟเฟกต์เรืองแสงรอบบอส (ถ้าอยู่ในโหมดคลั่ง)
            if (isEnraged) {
                int glowSize = (int) (50 * glowEffect);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f * glowEffect));
                g2d.setColor(Color.RED);
                g2d.fillOval((int) x - glowSize / 2, (int) y - glowSize / 2, width + glowSize, height + glowSize);
                g2d.dispose();
            }

            g.drawImage(boss5Image, (int) x, (int) y, width, height, null);
        } else {
            // ถ้าไม่มีรูปภาพให้วาดรูปทรงพื้นฐานแทน
            // วาดเอฟเฟกต์เรืองแสงรอบบอส (ถ้าอยู่ในโหมดคลั่ง)
            if (isEnraged) {
                int glowSize = (int) (50 * glowEffect);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f * glowEffect));
                g2d.setColor(Color.RED);
                g2d.fillOval((int) x - glowSize / 2, (int) y - glowSize / 2, width + glowSize, height + glowSize);
                g2d.dispose();
            }

            // วาดตัวบอสเป็นปีศาจมืด
            g.setColor(new Color(30, 0, 50)); // สีม่วงเข้มเกือบดำ
            g.fillOval((int) x, (int) y, width, height);

            // กรณีโกรธ จะมีสีแดงปน
            if (isEnraged) {
                g.setColor(new Color(100, 0, 0, 150));
                g.fillOval((int) x + 10, (int) y + 10, width - 20, height - 20);
            }

            // ตาสีแดงเรืองแสง
            g.setColor(Color.RED);
            g.fillOval((int) (x + width / 4), (int) (y + height / 3), width / 6, height / 6);
            g.fillOval((int) (x + width * 3 / 5), (int) (y + height / 3), width / 6, height / 6);

            // เขาหรือเงาด้านบน
            g.setColor(Color.BLACK);
            int[] xPoints = {(int) (x + width / 4), (int) (x + width / 2), (int) (x + width * 3 / 4)};
            int[] yPoints = {(int) y, (int) (y - height / 4), (int) y};
            g.fillPolygon(xPoints, yPoints, 3);

            // รายละเอียดอื่นๆ บนตัวบอส
            if (isEnraged) {
                g.setColor(Color.RED);
            } else {
                g.setColor(new Color(100, 0, 150));
            }
            g.drawLine((int) (x + width / 2), (int) (y + height / 2), (int) (x + width / 2), (int) (y + height * 3 / 4));
            g.drawOval((int) (x + width / 4), (int) (y + height / 2), width / 2, height / 3);
        }

        // แถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 25, width, 20);
        g.setColor(isEnraged ? Color.ORANGE : Color.GREEN);
        int healthBarWidth = (int) ((double) health / (500 * level) * width);
        g.fillRect((int) x, (int) y - 25, healthBarWidth, 20);

        // กรอบแถบพลังชีวิต
        g.setColor(Color.WHITE);
        g.drawRect((int) x, (int) y - 25, width, 20);

        // พิมพ์ข้อความแสดงสถานะใต้บอส
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String statusText = "FINAL BOSS Lv." + level + " HP:" + health;
        if (isEnraged) {
            statusText += " [ENRAGED]";
        }
        g.drawString(statusText, (int) x, (int) y + height + 25);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

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
                // ตั้งค่าพิเศษสำหรับกระสุนที่เคลื่อนที่แบบพิเศษได้ในอนาคต
                return bullet;
            case 4:
                // กระสุนพิเศษที่แยกตัวเป็นหลายกระสุน
                resetShootCooldown(isEnraged ? 50 : 80);
                return new EnemyBullet((int) x + width / 2 - 20, (int) y + height / 2, 40, 40, Math.PI / 2, 2, damage * 3);
            default:
                resetShootCooldown(isEnraged ? 20 : 30);
                return new EnemyBullet((int) x + width / 2 - 10, (int) y + height, 20, 20, Math.PI / 2, 4, damage);
        }
    }

    @Override
    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(isEnraged ? 60 : 90);
        List<EnemyBullet> bullets = new ArrayList<>();

        // เลือกการโจมตีพิเศษตามความเหมาะสม
        int specialAttackType = random.nextInt(isEnraged ? 4 : 3); // เพิ่มประเภทการโจมตีเมื่อคลั่ง

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

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        // เมื่อพลังชีวิตเหลือน้อยกว่า 60% ให้บอสเร็วขึ้นและโจมตีแรงขึ้น
        if (health <= 300 * level && speed == 1) {
            speed = 2;
            this.damage = this.damage * 2;
        }

        // เมื่อพลังชีวิตเหลือน้อยกว่า 30% ให้บอสคลั่ง
        if (health <= 150 * level && !isEnraged) {
            isEnraged = true;
            speed = 4;
            this.damage = this.damage * 2;

            // เอฟเฟกต์พิเศษเมื่อคลั่ง
            // (จะแสดงในเมธอด render)
        }
    }
}
