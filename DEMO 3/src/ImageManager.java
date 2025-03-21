
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class ImageManager {

    private static final HashMap<String, Image> images = new HashMap<>();
    private static boolean imagesLoaded = false;

    public static void loadImages() {
        try {
            // ตรวจสอบว่าโฟลเดอร์ resources/images/ มีหรือไม่
            File resourceDir = new File("resources/images");
            if (!resourceDir.exists()) {
                resourceDir.mkdirs(); // สร้างโฟลเดอร์ถ้ายังไม่มี
                System.out.println("สร้างโฟลเดอร์ resources/images เรียบร้อย โปรดนำรูปภาพไปใส่ในโฟลเดอร์นี้");
            }

            try {
                // โหลดรูปภาพผู้เล่น (คนผมฟ้า)
                File playerFile = new File("resources/images/player.png");
                if (playerFile.exists()) {
                    images.put("player", ImageIO.read(playerFile));
                    System.out.println("โหลดรูปภาพผู้เล่นสำเร็จ");
                } else {
                    // สร้างภาพแทนถ้าไม่มีไฟล์
                    createDefaultPlayerImage();
                }

                // โหลดรูปภาพมอนสเตอร์ (ผีสีชมพู)
                File monsterFile = new File("resources/images/enemy.png");
                if (monsterFile.exists()) {
                    images.put("monster", ImageIO.read(monsterFile));
                    System.out.println("โหลดรูปภาพมอนสเตอร์สำเร็จ");
                } else {
                    // สร้างภาพแทนถ้าไม่มีไฟล์
                    createDefaultMonsterImage();
                }

                // โหลดรูปภาพบอส (คนแว่นตาไฟ)
                File bossFile = new File("resources/images/boss.png");
                if (bossFile.exists()) {
                    images.put("boss", ImageIO.read(bossFile));
                    System.out.println("โหลดรูปภาพบอสสำเร็จ");
                } else {
                    // สร้างภาพแทนถ้าไม่มีไฟล์
                    createDefaultBossImage();
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

    // สร้างภาพพื้นฐานทั้งหมด
    private static void createAllDefaultImages() {
        createDefaultPlayerImage();
        createDefaultMonsterImage();
        createDefaultBossImage();
        createOtherDefaultImages();
    }

    // สร้างภาพผู้เล่นพื้นฐาน
    private static void createDefaultPlayerImage() {
        BufferedImage playerImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = playerImg.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 32, 32);
        g2d.dispose();
        images.put("player", playerImg);
    }

    // สร้างภาพมอนสเตอร์พื้นฐาน
    private static void createDefaultMonsterImage() {
        BufferedImage monsterImg = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = monsterImg.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 30, 30);
        g2d.dispose();
        images.put("monster", monsterImg);
    }

    // สร้างภาพบอสพื้นฐาน
    private static void createDefaultBossImage() {
        BufferedImage bossImg = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = bossImg.createGraphics();
        g2d.setColor(new Color(180, 0, 0));
        g2d.fillRect(0, 0, 80, 80);
        g2d.dispose();
        images.put("boss", bossImg);
    }

    // สร้างภาพอื่นๆ ที่จำเป็น
    private static void createOtherDefaultImages() {
        // กระสุนผู้เล่น
        BufferedImage playerBulletImg = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = playerBulletImg.createGraphics();
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

    public static Image getImage(String key) {
        if (!imagesLoaded) {
            loadImages();
        }
        return images.get(key);
    }
}
