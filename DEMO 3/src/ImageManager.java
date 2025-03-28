
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * คลาสจัดการรูปภาพในเกมทั้งหมด
 * รับผิดชอบการโหลดและการสร้างรูปภาพทั้งหมดที่ใช้ในเกม
 */
public class ImageManager {

    private static final HashMap<String, Image> images = new HashMap<>();
    private static boolean imagesLoaded = false;

    /**
     * โหลดรูปภาพทั้งหมดที่จำเป็นสำหรับเกม
     */
    public static void loadImages() {
        try {
            // ตรวจสอบว่าโฟลเดอร์ resources/images/ มีหรือไม่
            File resourceDir = new File("resources/images");
            if (!resourceDir.exists()) {
                resourceDir.mkdirs(); // สร้างโฟลเดอร์ถ้ายังไม่มี
                System.out.println("สร้างโฟลเดอร์ resources/images เรียบร้อย โปรดนำรูปภาพไปใส่ในโฟลเดอร์นี้");
            }

            try {
                File playerFile = new File("resources/images/player.png");
                if (playerFile.exists()) {
                    images.put("player", ImageIO.read(playerFile));
                    System.out.println("โหลดรูปภาพผู้เล่นสำเร็จ");
                } else {
                    // สร้างภาพแทนถ้าไม่มีไฟล์
                    createDefaultPlayerImage();
                }

                // โหลดรูปภาพมอนสเตอร์ - เปลี่ยนจาก enemy.png เป็น L1_Enemy.png
                File monsterFile = new File("resources/images/L1_Enemy.png");
                if (monsterFile.exists()) {
                    images.put("monster", ImageIO.read(monsterFile));
                    System.out.println("โหลดรูปภาพมอนสเตอร์สำเร็จ");
                } else {
                    // สร้างภาพแทนถ้าไม่มีไฟล์
                    createDefaultMonsterImage();
                }

                // โหลดรูปภาพบอส - เปลี่ยนจาก boss.png เป็น L1_Boss.png
                File bossFile = new File("resources/images/L1_Boss.png");
                if (bossFile.exists()) {
                    images.put("boss", ImageIO.read(bossFile));
                    System.out.println("โหลดรูปภาพบอสสำเร็จ");
                } else {
                    // สร้างภาพแทนถ้าไม่มีไฟล์
                    createDefaultBossImage();
                }

                // [โค้ดส่วนที่เหลือ]
                // โหลดรูปภาพพื้นหลังด่าน 1
                File level1BgFile = new File("resources/images/level1_bg.png");
                if (level1BgFile.exists()) {
                    images.put("level1_bg", ImageIO.read(level1BgFile));
                    System.out.println("โหลดรูปภาพฉากด่าน 1 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพฉากด่าน 1");
                }

                // โหลดรูปภาพปืนและเอฟเฟค
                File gunFile = new File("resources/images/gun.png");
                if (gunFile.exists()) {
                    images.put("gun", ImageIO.read(gunFile));
                    System.out.println("โหลดรูปภาพปืนสำเร็จ");
                } else {
                    // สร้างรูปปืนพื้นฐานถ้าไม่มีไฟล์
                    createDefaultGunImage();
                }

                File muzzleFlashFile = new File("resources/images/muzzle_flash.png");
                if (muzzleFlashFile.exists()) {
                    images.put("muzzle_flash", ImageIO.read(muzzleFlashFile));
                    System.out.println("โหลดรูปภาพเอฟเฟคปืนสำเร็จ");
                } else {
                    // สร้างรูปเอฟเฟคพื้นฐานถ้าไม่มีไฟล์
                    createDefaultMuzzleFlashImage();
                }

                // โหลดรูปหัวใจปกติและหัวใจแตก
                File heartFile = new File("resources/images/heart.png");
                if (heartFile.exists()) {
                    images.put("heart", ImageIO.read(heartFile));
                    System.out.println("โหลดรูปภาพหัวใจสำเร็จ");
                } else {
                    createDefaultHeartImage();
                }

                File brokenHeartFile = new File("resources/images/broken_heart.png");
                if (brokenHeartFile.exists()) {
                    images.put("broken_heart", ImageIO.read(brokenHeartFile));
                    System.out.println("โหลดรูปภาพหัวใจแตกสำเร็จ");
                } else {
                    createDefaultBrokenHeartImage();
                }
                File volumeUpFile = new File("resources/images/volume_up.png");
                if (volumeUpFile.exists()) {
                    images.put("volume_up", ImageIO.read(volumeUpFile));
                } else {
                    createDefaultVolumeButton(true);
                }

                File volumeDownFile = new File("resources/images/volume_down.png");
                if (volumeDownFile.exists()) {
                    images.put("volume_down", ImageIO.read(volumeDownFile));
                } else {
                    createDefaultVolumeButton(false);
                }

                // โหลดหรือสร้างรูปภาพอื่นๆ ที่จำเป็น
                createOtherDefaultImages();

                imagesLoaded = true;
                System.out.println("โหลดรูปภาพทั้งหมดสำเร็จ!");
            } catch (IOException e) {
                System.err.println("ไม่สามารถโหลดรูปภาพได้: " + e.getMessage());
                createAllDefaultImages();
            }
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการโหลดรูปภาพ: " + e.getMessage());
            createAllDefaultImages();
        }
    }

    /**
     * สร้างภาพพื้นฐานทั้งหมดเมื่อเกิดข้อผิดพลาดในการโหลดรูปภาพ
     */
    private static void createAllDefaultImages() {
        createDefaultPlayerImage();
        createDefaultMonsterImage();
        createDefaultBossImage();
        createDefaultGunImage();
        createDefaultMuzzleFlashImage();
        createDefaultHeartImage();
        createDefaultBrokenHeartImage();
        createOtherDefaultImages();
    }

    /**
     * สร้างภาพผู้เล่นพื้นฐาน
     */
    private static void createDefaultPlayerImage() {
        BufferedImage playerImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = playerImg.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 32, 32);
        g2d.dispose();
        images.put("player", playerImg);
    }

    /**
     * สร้างภาพมอนสเตอร์พื้นฐาน
     */
    private static void createDefaultMonsterImage() {
        BufferedImage monsterImg = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = monsterImg.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 30, 30);
        g2d.dispose();
        images.put("monster", monsterImg);
    }

    /**
     * สร้างภาพบอสพื้นฐาน
     */
    private static void createDefaultBossImage() {
        BufferedImage bossImg = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bossImg.createGraphics();
        g2d.setColor(new Color(180, 0, 0));
        g2d.fillRect(0, 0, 80, 80);
        g2d.dispose();
        images.put("boss", bossImg);
    }

    /**
     * สร้างรูปปืนพื้นฐาน
     */
    private static void createDefaultGunImage() {
        BufferedImage gunImg = new BufferedImage(20, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = gunImg.createGraphics();
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, 15, 4);
        g2d.fillRect(10, 0, 5, 10);
        g2d.dispose();
        images.put("gun", gunImg);
    }

    /**
     * สร้างเอฟเฟคปืนพื้นฐาน
     */
    private static void createDefaultMuzzleFlashImage() {
        BufferedImage flashImg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flashImg.createGraphics();
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, 10, 10);
        g2d.setColor(Color.ORANGE);
        g2d.fillOval(2, 2, 6, 6);
        g2d.dispose();
        images.put("muzzle_flash", flashImg);
    }

    /**
     * สร้างรูปหัวใจปกติ
     */
    private static void createDefaultHeartImage() {
        BufferedImage heartImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = heartImg.createGraphics();
        g2d.setColor(Color.RED);

        // วาดหัวใจอย่างง่าย (สร้างรูปหัวใจแบบพื้นฐาน)
        int[] xPoints = {10, 15, 20, 15, 10, 5, 0, 5};
        int[] yPoints = {5, 0, 5, 10, 20, 10, 5, 0};
        g2d.fillPolygon(xPoints, yPoints, 8);

        g2d.dispose();
        images.put("heart", heartImg);
    }

    /**
     * สร้างรูปหัวใจแตก
     */
    private static void createDefaultBrokenHeartImage() {
        BufferedImage brokenHeartImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = brokenHeartImg.createGraphics();
        g2d.setColor(new Color(128, 0, 0)); // สีแดงเข้ม

        // วาดหัวใจแตกอย่างง่าย
        int[] xPoints = {10, 15, 20, 15, 10, 5, 0, 5};
        int[] yPoints = {5, 0, 5, 10, 20, 10, 5, 0};
        g2d.fillPolygon(xPoints, yPoints, 8);

        // วาดรอยแตก
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(10, 5, 10, 20);
        g2d.drawLine(7, 10, 13, 15);

        g2d.dispose();
        images.put("broken_heart", brokenHeartImg);
    }

    /**
     * สร้างภาพอื่นๆ ที่จำเป็น
     */
    private static void createOtherDefaultImages() {
        // กระสุนผู้เล่น
        BufferedImage playerBulletImg = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = playerBulletImg.createGraphics();
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, 8, 8);
        g2d.dispose();
        images.put("playerBullet", playerBulletImg);

        // กระสุนศัตรู
        BufferedImage enemyBulletImg = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        g2d = enemyBulletImg.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillOval(0, 0, 8, 8);
        g2d.dispose();
        images.put("enemyBullet", enemyBulletImg);

        // พาวเวอร์อัพ
        // Health
        BufferedImage healthImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        g2d = healthImg.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.fillOval(0, 0, 20, 20);
        g2d.dispose();
        images.put("powerupHealth", healthImg);

        // Speed
        BufferedImage speedImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        g2d = speedImg.createGraphics();
        g2d.setColor(Color.CYAN);
        g2d.fillOval(0, 0, 20, 20);
        g2d.dispose();
        images.put("powerupSpeed", speedImg);

        // Damage
        BufferedImage damageImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        g2d = damageImg.createGraphics();
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, 20, 20);
        g2d.dispose();
        images.put("powerupDamage", damageImg);

        // พื้นหลัง
        BufferedImage bgImg = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        g2d = bgImg.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, 800, 600);

        // เพิ่มดาวเป็นจุดขาวๆ
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = (int) (Math.random() * 800);
            int y = (int) (Math.random() * 600);
            g2d.fillRect(x, y, 1, 1);
        }
        g2d.dispose();
        images.put("background", bgImg);
    }

    private static void createDefaultVolumeButton(boolean isIncrease) {
        BufferedImage buttonImg = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buttonImg.createGraphics();

        // วาดพื้นหลังปุ่ม
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(0, 0, 30, 30);

        // วาดขอบปุ่ม
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, 29, 29);

        // วาดสัญลักษณ์ + หรือ -
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.WHITE);

        if (isIncrease) {
            // วาดเครื่องหมาย +
            g2d.drawLine(10, 15, 20, 15);
            g2d.drawLine(15, 10, 15, 20);
        } else {
            // วาดเครื่องหมาย -
            g2d.drawLine(10, 15, 20, 15);
        }

        g2d.dispose();
        images.put(isIncrease ? "volume_up" : "volume_down", buttonImg);
    }

    /**
     * ดึงรูปภาพตามชื่อที่กำหนด
     *
     * @param key ชื่อรูปภาพที่ต้องการ
     * @return รูปภาพที่ต้องการ หรือ null ถ้าไม่พบ
     */
    public static Image getImage(String key) {
        if (!imagesLoaded) {
            loadImages();
        }
        return images.get(key);
    }
}
