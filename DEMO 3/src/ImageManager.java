
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;

/*
 * คลาสจัดการรูปภาพในเกมทั้งหมด
 * รับผิดชอบการโหลดและการสร้างรูปภาพทั้งหมดที่ใช้ในเกม
 */
public class ImageManager {

    private static final HashMap<String, Image> images = new HashMap<>();
    private static boolean imagesLoaded = false;

    /*
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
            // ใน ImageManager.java - เพิ่มในเมธอด loadImages()
            // โหลดรูปภาพพื้นหลังด่าน 2

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

                loadPowerupImages();

                File level2BgFile = new File("resources/images/level2_bg.png");
                if (level2BgFile.exists()) {
                    images.put("level2_bg", ImageIO.read(level2BgFile));
                    System.out.println("โหลดรูปภาพฉากด่าน 2 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพฉากด่าน 2 จะสร้างภาพเองอัตโนมัติ");
                    createLevel2BackgroundImage();
                }
                File level3BgFile = new File("resources/images/level3_bg.png");
                if (level3BgFile.exists()) {
                    images.put("level3_bg", ImageIO.read(level3BgFile));
                    System.out.println("โหลดรูปภาพฉากด่าน 3 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพฉากด่าน 3 จะสร้างภาพเองอัตโนมัติ");
                    createLevel3BackgroundImage();
                }

                File monster2File = new File("resources/images/L2_Enemy.png");
                if (monster2File.exists()) {
                    images.put("monster2", ImageIO.read(monster2File));
                    System.out.println("โหลดรูปภาพมอนสเตอร์ด่าน 2 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพมอนสเตอร์ด่าน 2 จะใช้ภาพพื้นฐานแทน");
                    createDefaultMonster2Image();
                }

                // โหลดรูปภาพบอสด่าน 2
                File boss2File = new File("resources/images/L2_Boss.png");
                if (boss2File.exists()) {
                    images.put("boss2", ImageIO.read(boss2File));
                    System.out.println("โหลดรูปภาพบอสด่าน 2 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพบอสด่าน 2 จะใช้ภาพพื้นฐานแทน");
                    createDefaultBoss2Image();
                }
                File monster3File = new File("resources/images/L3_Enemy.png");
                if (monster3File.exists()) {
                    images.put("monster3", ImageIO.read(monster3File));
                    System.out.println("โหลดรูปภาพมอนสเตอร์ด่าน 3 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพมอนสเตอร์ด่าน 3 จะใช้ภาพพื้นฐานแทน");
                    createDefaultMonster3Image();
                }

                // โหลดรูปภาพบอสด่าน 3
                File boss3File = new File("resources/images/L3_Boss.png");
                if (boss3File.exists()) {
                    images.put("boss3", ImageIO.read(boss3File));
                    System.out.println("โหลดรูปภาพบอสด่าน 3 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพบอสด่าน 3 จะใช้ภาพพื้นฐานแทน");
                    createDefaultBoss3Image();
                }
                // โหลดรูปภาพฉากด่าน 4
                File level4BgFile = new File("resources/images/level4_bg.png");
                if (level4BgFile.exists()) {
                    images.put("level4_bg", ImageIO.read(level4BgFile));
                    System.out.println("โหลดรูปภาพฉากด่าน 4 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพฉากด่าน 4 จะสร้างภาพเองอัตโนมัติ");
                    createLevel4BackgroundImage();
                }

// โหลดรูปภาพฉากด่าน 5
                File level5BgFile = new File("resources/images/level5_bg.png");
                if (level5BgFile.exists()) {
                    images.put("level5_bg", ImageIO.read(level5BgFile));
                    System.out.println("โหลดรูปภาพฉากด่าน 5 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพฉากด่าน 5 จะสร้างภาพเองอัตโนมัติ");
                    createLevel5BackgroundImage();
                }

// โหลดรูปภาพมอนสเตอร์ด่าน 4
                File monster4File = new File("resources/images/L4_Enemy.png");
                if (monster4File.exists()) {
                    images.put("monster4", ImageIO.read(monster4File));
                    System.out.println("โหลดรูปภาพมอนสเตอร์ด่าน 4 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพมอนสเตอร์ด่าน 4 จะใช้ภาพพื้นฐานแทน");
                    createDefaultMonster4Image();
                }

// โหลดรูปภาพบอสด่าน 4
                File boss4File = new File("resources/images/L4_Boss.png");
                if (boss4File.exists()) {
                    images.put("boss4", ImageIO.read(boss4File));
                    System.out.println("โหลดรูปภาพบอสด่าน 4 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพบอสด่าน 4 จะใช้ภาพพื้นฐานแทน");
                    createDefaultBoss4Image();
                }

// โหลดรูปภาพมอนสเตอร์ด่าน 5
                File monster5File = new File("resources/images/L5_Enemy.png");
                if (monster5File.exists()) {
                    images.put("monster5", ImageIO.read(monster5File));
                    System.out.println("โหลดรูปภาพมอนสเตอร์ด่าน 5 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพมอนสเตอร์ด่าน 5 จะใช้ภาพพื้นฐานแทน");
                    createDefaultMonster5Image();
                }

// โหลดรูปภาพบอสด่าน 5
// เพิ่มการโหลดรูปภาพสำหรับเฟส 1 และเฟส 2 ของบอสด่าน 5
                File boss5Phase1File = new File("resources/images/L5_Boss_Phase1.png");
                if (boss5Phase1File.exists()) {
                    images.put("L5_Boss_Phase1", ImageIO.read(boss5Phase1File));
                    System.out.println("โหลดรูปภาพบอสด่าน 5 เฟส 1 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพบอสด่าน 5 เฟส 1 จะใช้ภาพพื้นฐานแทน");
                    // ใช้รูปบอส 5 ปกติถ้าไม่มีรูปเฟส 1
                    images.put("L5_Boss_Phase1", images.get("boss5"));
                }

                File boss5Phase2File = new File("resources/images/L5_Boss_Phase2.png");
                if (boss5Phase2File.exists()) {
                    images.put("L5_Boss_Phase2", ImageIO.read(boss5Phase2File));
                    System.out.println("โหลดรูปภาพบอสด่าน 5 เฟส 2 สำเร็จ");
                } else {
                    System.out.println("ไม่พบไฟล์ภาพบอสด่าน 5 เฟส 2 จะใช้ภาพพื้นฐานแทน");
                    // สร้างรูปเฟส 2 จากรูปปกติแต่เปลี่ยนสี (สีแดงเข้มกว่า)
                    try {
                        BufferedImage bossPh1 = (BufferedImage) images.get("boss5");
                        if (bossPh1 != null) {
                            BufferedImage bossPh2 = new BufferedImage(
                                    bossPh1.getWidth(), bossPh1.getHeight(),
                                    BufferedImage.TYPE_INT_ARGB);

                            // คัดลอกรูปและปรับสีให้เข้มขึ้น/น่ากลัวขึ้น
                            for (int x = 0; x < bossPh1.getWidth(); x++) {
                                for (int y = 0; y < bossPh1.getHeight(); y++) {
                                    int rgb = bossPh1.getRGB(x, y);
                                    if (rgb != 0) { // ไม่ใช่พื้นที่โปร่งใส
                                        Color c = new Color(rgb, true);
                                        Color darker = new Color(
                                                Math.min(255, c.getRed() + 50),
                                                Math.max(0, c.getGreen() - 50),
                                                Math.max(0, c.getBlue() - 50),
                                                c.getAlpha()
                                        );
                                        bossPh2.setRGB(x, y, darker.getRGB());
                                    }
                                }
                            }
                            images.put("L5_Boss_Phase2", bossPh2);
                        } else {
                            createDefaultBoss5Phase2Image();
                        }
                    } catch (Exception e) {
                        System.out.println("เกิดข้อผิดพลาดในการสร้างรูปบอสเฟส 2: " + e.getMessage());
                        createDefaultBoss5Phase2Image();
                    }
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

    /*
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

    private static void createLevel2BackgroundImage() {
        BufferedImage level2Bg = new BufferedImage(GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = level2Bg.createGraphics();

        // วาดพื้นหลังสีน้ำตาลเรียบๆ
        g2d.setColor(new Color(150, 120, 90));
        g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // วาดเส้นตารางน้อยลง เช่น 4-5 เส้นพอ หรือไม่ต้องวาดเลย
        g2d.setColor(new Color(130, 100, 70));
        for (int i = 0; i < 800; i += 200) { // เพิ่มระยะห่างเป็น 200
            g2d.drawLine(i, 0, i, 600); // วาดเฉพาะเส้นแนวตั้ง
        }
        for (int j = 0; j < 600; j += 200) { // เพิ่มระยะห่างเป็น 200
            g2d.drawLine(0, j, 800, j); // วาดเฉพาะเส้นแนวนอน
        }

        g2d.dispose();
        images.put("level2_bg", level2Bg);
    }

    private static void createLevel3BackgroundImage() {
        BufferedImage level3Bg = new BufferedImage(GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = level3Bg.createGraphics();

        // วาดพื้นไม้สีน้ำตาล
        g2d.setColor(new Color(165, 130, 95)); // สีน้ำตาลอ่อน
        g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // วาดลายไม้ในแนวนอน
        g2d.setColor(new Color(145, 110, 75)); // สีน้ำตาลเข้มกว่า
        for (int y = 0; y < GamePanel.HEIGHT; y += 30) {
            g2d.drawLine(0, y, GamePanel.WIDTH, y);
        }

        // วาดลายไม้ในแนวตั้ง
        for (int x = 0; x < GamePanel.WIDTH; x += 60) {
            g2d.drawLine(x, 0, x, GamePanel.HEIGHT);
        }

        // วาดพื้นที่กระเบื้องมุมขวาบน
        g2d.setColor(new Color(220, 220, 220)); // สีกระเบื้อง
        for (int x = 600; x < GamePanel.WIDTH; x += 30) {
            for (int y = 0; y < 320; y += 30) {
                g2d.fillRect(x, y, 30, 30);
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRect(x, y, 30, 30);
                g2d.setColor(new Color(220, 220, 220));
            }
        }

        // วาดแสงสว่างในห้อง (วงกลมเรืองแสง)
        createGlowEffect(g2d, 150, 200, 70);
        createGlowEffect(g2d, 450, 350, 80);
        createGlowEffect(g2d, 650, 120, 60);
        createGlowEffect(g2d, 300, 450, 75);

        // วาดเฟอร์นิเจอร์: โต๊ะใหญ่เอียง
        g2d.setColor(new Color(150, 120, 90));
        int[] xPoints1 = {200, 350, 320, 170};
        int[] yPoints1 = {100, 150, 250, 200};
        g2d.fillPolygon(xPoints1, yPoints1, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints1, yPoints1, 4);

        // วาดเฟอร์นิเจอร์: โต๊ะเล็กตรงกลาง
        g2d.setColor(new Color(160, 130, 100));
        g2d.fillRect(380, 180, 120, 70);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(380, 180, 120, 70);

        // วาดเฟอร์นิเจอร์: ตู้ด้านล่างซ้าย
        g2d.setColor(new Color(140, 110, 80));
        g2d.fillRect(100, 350, 150, 100);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(100, 350, 150, 100);

        // วาดเฟอร์นิเจอร์: ตู้เล็กด้านขวา
        g2d.setColor(new Color(150, 120, 90));
        g2d.fillRect(550, 250, 60, 120);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(550, 250, 60, 120);

        // วาดเฟอร์นิเจอร์: วงกลมสีเทา (อาจเป็นหม้อหรือถังน้ำ)
        g2d.setColor(new Color(100, 100, 100));
        g2d.fillOval(700, 350, 60, 60);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(700, 350, 60, 60);

        // อีกวงกลมที่มุมซ้ายบน
        g2d.setColor(new Color(90, 90, 90));
        g2d.fillOval(50, 50, 70, 70);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(50, 50, 70, 70);

        g2d.dispose();
        images.put("level3_bg", level3Bg);
    }

    private static void createDefaultMonster2Image() {
        BufferedImage monster2Img = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = monster2Img.createGraphics();
        g2d.setColor(new Color(50, 150, 50)); // สีเขียว
        g2d.fillRect(0, 0, 35, 35);

        // ตาของมอนสเตอร์
        g2d.setColor(Color.RED);
        g2d.fillOval(5, 5, 10, 10);
        g2d.fillOval(20, 5, 10, 10);

        g2d.dispose();
        images.put("monster2", monster2Img);
    }

    private static void createDefaultMonster3Image() {
        BufferedImage monster3Img = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = monster3Img.createGraphics();

        // วาดตัวมอนสเตอร์สีฟ้า
        g2d.setColor(new Color(60, 170, 210)); // สีฟ้า
        g2d.fillOval(0, 0, 35, 35);

        // วาดตา
        g2d.setColor(new Color(0, 0, 50)); // สีน้ำเงินเข้ม
        g2d.fillOval(7, 10, 7, 7);
        g2d.fillOval(21, 10, 7, 7);

        // วาดปาก
        g2d.drawArc(10, 20, 15, 8, 0, 180);

        g2d.dispose();
        images.put("monster3", monster3Img);
    }

    private static void createGlowEffect(Graphics2D g2d, int x, int y, int radius) {
        // สร้างรัศมีแสงโดยใช้สีเหลืองอ่อนโปร่งใส
        for (int i = 0; i < 5; i++) {
            int alpha = 50 - (i * 10); // ค่าความโปร่งใสลดลงตามรัศมี
            g2d.setColor(new Color(255, 255, 200, alpha));
            int currentRadius = radius - (i * 10);
            g2d.fillOval(x - currentRadius, y - currentRadius, currentRadius * 2, currentRadius * 2);
        }
    }

    private static void createDefaultBoss2Image() {
        BufferedImage boss2Img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boss2Img.createGraphics();
        g2d.setColor(new Color(75, 0, 130)); // สีม่วงเข้ม
        g2d.fillOval(0, 0, 100, 100);

        // ตาของบอส
        g2d.setColor(Color.GREEN);
        g2d.fillOval(20, 25, 20, 20);
        g2d.fillOval(60, 25, 20, 20);

        g2d.dispose();
        images.put("boss2", boss2Img);
    }

    private static void createDefaultBoss3Image() {
        BufferedImage boss3Img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boss3Img.createGraphics();

        // สร้างใบหน้าตามรูปภาพแรก (ใบหน้าคน)
        // วาดหน้าสีเนื้อ
        g2d.setColor(new Color(255, 213, 170));
        g2d.fillOval(0, 0, 100, 100);

        // วาดผมสีดำ
        g2d.setColor(Color.BLACK);
        g2d.fillArc(0, 0, 100, 60, 0, 180);

        // วาดคิ้วสีน้ำตาล
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(25, 35, 20, 3);
        g2d.fillRect(55, 35, 20, 3);

        // วาดตาที่ปิด
        g2d.setColor(Color.BLACK);
        g2d.drawLine(25, 45, 45, 45);
        g2d.drawLine(55, 45, 75, 45);

        // วาดจมูก
        g2d.setColor(new Color(220, 170, 140));
        int[] xPointsNose = {50, 45, 55};
        int[] yPointsNose = {60, 70, 70};
        g2d.fillPolygon(xPointsNose, yPointsNose, 3);

        // วาดปาก
        g2d.setColor(new Color(200, 120, 120));
        g2d.drawArc(35, 75, 30, 10, 0, 180);

        g2d.dispose();
        images.put("boss3", boss3Img);
    }

    private static void createLevel4BackgroundImage() {
        BufferedImage level4Bg = new BufferedImage(GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = level4Bg.createGraphics();

        // สร้างพื้นหลังห้องแล็บหรือห้องทดลอง
        g2d.setColor(new Color(220, 220, 220)); // สีพื้นขาวนวล
        g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // วาดเส้นตารางเล็กๆ คล้ายกระเบื้อง
        g2d.setColor(new Color(200, 200, 200));
        for (int i = 0; i < GamePanel.WIDTH; i += 40) {
            g2d.drawLine(i, 0, i, GamePanel.HEIGHT);
        }
        for (int j = 0; j < GamePanel.HEIGHT; j += 40) {
            g2d.drawLine(0, j, GamePanel.WIDTH, j);
        }

        // วาดโต๊ะทดลองตรงกลาง
        g2d.setColor(new Color(180, 180, 180));
        g2d.fillRect(300, 150, 200, 80);
        g2d.setColor(new Color(120, 120, 120));
        g2d.drawRect(300, 150, 200, 80);

        // วาดอุปกรณ์เล็กๆ บนโต๊ะทดลอง
        g2d.setColor(new Color(100, 100, 200));
        g2d.fillRect(320, 160, 30, 20);
        g2d.setColor(new Color(200, 100, 100));
        g2d.fillRect(370, 165, 25, 25);
        g2d.setColor(new Color(100, 200, 100));
        g2d.fillRect(420, 160, 40, 15);

        // วาดสัญลักษณ์วิทยาศาสตร์บนกำแพง
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawOval(100, 80, 60, 60);
        g2d.drawOval(600, 80, 60, 60);

        g2d.dispose();
        images.put("level4_bg", level4Bg);
    }

    private static void createLevel5BackgroundImage() {
        BufferedImage level5Bg = new BufferedImage(GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = level5Bg.createGraphics();

        // สร้างพื้นหลังมืดๆ สำหรับด่านสุดท้าย
        g2d.setColor(new Color(20, 0, 30)); // สีม่วงเข้มเกือบดำ
        g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // วาดดวงดาวเล็กๆ ในฉากหลัง
        g2d.setColor(new Color(255, 255, 255));
        Random random = new Random(5); // ใช้ seed คงที่เพื่อให้ดาวอยู่ตำแหน่งเดิมทุกครั้ง
        for (int i = 0; i < 200; i++) {
            int starX = random.nextInt(GamePanel.WIDTH);
            int starY = random.nextInt(GamePanel.HEIGHT);
            int starSize = random.nextInt(3) + 1;
            g2d.fillRect(starX, starY, starSize, starSize);
        }

        // วาดวงกลมเรืองแสงมุมต่างๆ ของห้อง
        createGlowEffect(g2d, 150, 100, 60, new Color(80, 0, 120));
        createGlowEffect(g2d, 600, 120, 70, new Color(120, 0, 80));
        createGlowEffect(g2d, 200, 400, 50, new Color(80, 0, 120));
        createGlowEffect(g2d, 550, 380, 65, new Color(120, 0, 80));

        // วาดแท่นบูชาตรงกลาง
        g2d.setColor(new Color(60, 30, 60));
        g2d.fillRect(350, 200, 100, 100);
        g2d.setColor(new Color(100, 50, 100));
        g2d.drawRect(350, 200, 100, 100);

        // วาดรูปวงแหวนบนพื้น
        g2d.setColor(new Color(80, 0, 120, 100));
        g2d.drawOval(250, 100, 300, 300);
        g2d.drawOval(275, 125, 250, 250);

        // วาดสัญลักษณ์พิธีกรรมบนพื้น
        g2d.setColor(new Color(150, 0, 150, 80));
        int[] xPoints = {400, 350, 400, 450};
        int[] yPoints = {150, 250, 350, 250};
        g2d.fillPolygon(xPoints, yPoints, 4);

        g2d.dispose();
        images.put("level5_bg", level5Bg);
    }

    private static void createDefaultMonster4Image() {
        BufferedImage monster4Img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = monster4Img.createGraphics();

        // วาดตัวมอนสเตอร์หุ่นยนต์สีฟ้าเทา
        g2d.setColor(new Color(150, 150, 200));
        g2d.fillRect(0, 0, 40, 40);

        // ตาสีแดง
        g2d.setColor(Color.RED);
        g2d.fillOval(8, 8, 8, 8);
        g2d.fillOval(24, 8, 8, 8);

        // ส่วนต่างๆ ของหุ่นยนต์
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(10, 25, 20, 5);
        g2d.drawRect(5, 5, 30, 30);

        g2d.dispose();
        images.put("monster4", monster4Img);
    }

    private static void createDefaultMonster5Image() {
        BufferedImage monster5Img = new BufferedImage(45, 45, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = monster5Img.createGraphics();

        // วาดตัวมอนสเตอร์ปีศาจมืด
        g2d.setColor(new Color(30, 0, 50)); // สีม่วงเข้มเกือบดำ
        g2d.fillOval(0, 0, 45, 45);

        // เอฟเฟกต์คล้ายเปลวไฟสีม่วงรอบตัว
        g2d.setColor(new Color(120, 0, 200, 100)); // สีม่วงโปร่งใส
        g2d.fillOval(-5, -5, 55, 55);

        // ตาสีแดงเรืองแสง
        g2d.setColor(Color.RED);
        g2d.fillOval(10, 13, 8, 8);
        g2d.fillOval(27, 13, 8, 8);

        g2d.dispose();
        images.put("monster5", monster5Img);
    }

    private static void createDefaultBoss4Image() {
        BufferedImage boss4Img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boss4Img.createGraphics();

        // วาดหุ่นยนต์ขนาดใหญ่
        g2d.setColor(new Color(120, 120, 180));
        g2d.fillRect(0, 0, 120, 120);

        // ส่วนต่างๆ ของหุ่นยนต์
        g2d.setColor(new Color(80, 80, 100));
        g2d.fillRect(30, 30, 60, 60);

        // ตาเรืองแสง
        g2d.setColor(Color.RED);
        g2d.fillOval(30, 30, 20, 20);
        g2d.fillOval(70, 30, 20, 20);

        // แขนกล
        g2d.setColor(new Color(100, 100, 140));
        g2d.fillRect(-30, 40, 30, 20);
        g2d.fillRect(120, 40, 30, 20);

        // ขอบสีดำรอบตัว
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 119, 119);

        g2d.dispose();
        images.put("boss4", boss4Img);
    }

    // เพิ่มเมธอดสำหรับสร้างรูปบอสเฟส 2 แบบพื้นฐาน
    private static void createDefaultBoss5Phase2Image() {
        BufferedImage boss5Ph2 = new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boss5Ph2.createGraphics();

        // สร้างรูปปีศาจเฟส 2 ที่ดูอันตรายกว่าเฟส 1
        g2d.setColor(new Color(150, 0, 20)); // สีแดงเข้ม
        g2d.fillOval(0, 0, 140, 140);

        // เอฟเฟกต์เรืองแสงรอบตัว
        g2d.setColor(new Color(255, 30, 0, 80));
        g2d.fillOval(-10, -10, 160, 160);

        // ตาสีแดงเรืองแสง
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(35, 40, 25, 25);
        g2d.fillOval(80, 40, 25, 25);

        // รายละเอียดอื่นๆ
        g2d.setColor(new Color(255, 100, 0));
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            int x1 = 70 + (int) (Math.cos(angle) * 60);
            int y1 = 70 + (int) (Math.sin(angle) * 60);
            g2d.drawLine(70, 70, x1, y1);
        }

        g2d.dispose();
        images.put("L5_Boss_Phase2", boss5Ph2);
    }

// เพิ่มเมธอด createGlowEffect ที่ใช้โดย createLevel5BackgroundImage
    private static void createGlowEffect(Graphics2D g2d, int x, int y, int radius, Color color) {
        // สร้างรัศมีแสงโดยใช้สีที่กำหนดแบบโปร่งใส
        for (int i = 0; i < 5; i++) {
            int alpha = 50 - (i * 10); // ค่าความโปร่งใสลดลงตามรัศมี
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            int currentRadius = radius - (i * 10);
            g2d.fillOval(x - currentRadius, y - currentRadius, currentRadius * 2, currentRadius * 2);
        }
    }

    /*
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

    private static void loadPowerupImages() {
        String[] powerupNames = {
            // บัฟสุดโหด (Crazy)
            "crazy_shooting", "stop_time",
            // บัฟถาวร (Permanent)
            "increase_bullet_damage", "increase_movement_speed", "increase_shooting_speed",
            "knockback", "plus_more_heart", "shoot_multiple_bullets",
            // บัฟชั่วคราว (Temporary)
            "increase_bullet_damage(temp)", "increase_movement_speed(temp)", "increase_shooting_speed(temp)",
            "knockback(temp)", "fires_multiple_bullets(temp)", "healing"
        };

        for (String name : powerupNames) {
            try {
                File imageFile = new File("resources/images/" + name + ".png");
                if (imageFile.exists()) {
                    images.put(name, ImageIO.read(imageFile));
                    System.out.println("โหลดรูปภาพบัฟ " + name + " สำเร็จ");
                } else {
                    createDefaultPowerupIcon(name);
                }
            } catch (Exception e) {
                System.out.println("ไม่สามารถโหลดรูปภาพบัฟ " + name + ": " + e.getMessage());
                createDefaultPowerupIcon(name);
            }
        }
    }

    /**
     * สร้างไอคอนพื้นฐานสำหรับบัฟที่ไม่มีรูปภาพ
     *
     * @param powerupName ชื่อบัฟ
     */
    private static void createDefaultPowerupIcon(String powerupName) {
        BufferedImage iconImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = iconImage.createGraphics();

        // กำหนดสีตามประเภทของบัฟ
        if (powerupName.contains("crazy")) {
            g.setColor(new Color(200, 50, 50)); // สีแดง
        } else if (powerupName.contains("temp")) {
            g.setColor(new Color(200, 130, 50)); // สีส้ม
        } else {
            g.setColor(new Color(130, 50, 200)); // สีม่วง
        }

        // วาดวงกลม
        g.fillOval(0, 0, 50, 50);

        // วาดขอบ
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawOval(0, 0, 49, 49);

        // วาดตัวอักษรตามชื่อบัฟ
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        String symbol = "";
        if (powerupName.contains("damage")) {
            symbol = "D";
        } else if (powerupName.contains("movement_speed")) {
            symbol = "S";
        } else if (powerupName.contains("shooting_speed")) {
            symbol = "F";
        } else if (powerupName.contains("knock")) {
            symbol = "K";
        } else if (powerupName.contains("heart") || powerupName.contains("healing")) {
            symbol = "H";
        } else if (powerupName.contains("bullet")) {
            symbol = "M";
        } else if (powerupName.contains("crazy")) {
            symbol = "C";
        } else if (powerupName.contains("stop_time")) {
            symbol = "T";
        }

        // วาดตัวอักษรตรงกลาง
        FontMetrics fm = g.getFontMetrics();
        int textX = (50 - fm.stringWidth(symbol)) / 2;
        int textY = (50 - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(symbol, textX, textY);

        g.dispose();
        images.put(powerupName, iconImage);
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
