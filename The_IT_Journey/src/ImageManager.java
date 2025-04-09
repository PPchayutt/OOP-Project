
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
import javax.swing.ImageIcon;

public class ImageManager {

    private static final HashMap<String, Image> images = new HashMap<>();
    private static boolean imagesLoaded = false;

    public static void loadImages() {
        try {
            File resourceDir = new File("resources/images");
            if (!resourceDir.exists()) {
                resourceDir.mkdirs(); // สร้างโฟลเดอร์ถ้ายังไม่มี
            }

            try {
                File playerFile = new File("resources/images/player.gif");
                if (playerFile.exists()) {
                    ImageIcon playerIcon = new ImageIcon(playerFile.getPath());
                    images.put("player", playerIcon.getImage());
                } else {
                    createDefaultPlayerImage();
                }
                
                loadPowerupImages();
                
                /*
                ======โหลดรูปพื้นหลังด่าน======
                */
                File level1BgFile = new File("resources/images/level1_bg.png");
                if (level1BgFile.exists()) {
                    images.put("level1_bg", ImageIO.read(level1BgFile));
                }
                
                File level2BgFile = new File("resources/images/level2_bg.png");
                if (level2BgFile.exists()) {
                    images.put("level2_bg", ImageIO.read(level2BgFile));
                } else {
                    createLevel2BackgroundImage();
                }

                File level3BgFile = new File("resources/images/level3_bg.png");
                if (level3BgFile.exists()) {
                    images.put("level3_bg", ImageIO.read(level3BgFile));
                } else {
                    createLevel3BackgroundImage();
                }
                
                File level4BgFile = new File("resources/images/level4_bg.png");
                if (level4BgFile.exists()) {
                    images.put("level4_bg", ImageIO.read(level4BgFile));
                } else {
                    createLevel4BackgroundImage();
                }
                
                File level5BgFile = new File("resources/images/level5_bg.png");
                if (level5BgFile.exists()) {
                    images.put("level5_bg", ImageIO.read(level5BgFile));
                } else {
                    createLevel5BackgroundImage();
                }
                
                /*
                ======โหลดรูปศัตรู======
                */
                File monsterFile = new File("resources/images/L1_Enemy.png");
                if (monsterFile.exists()) {
                    images.put("monster", ImageIO.read(monsterFile));
                } else {
                    createDefaultMonsterImage();
                }
                
                File monster2File = new File("resources/images/L2_Enemy.png");
                if (monster2File.exists()) {
                    images.put("monster2", ImageIO.read(monster2File));
                } else {
                    createDefaultMonster2Image();
                }
                
                File monster3File = new File("resources/images/L3_Enemy.png");
                if (monster3File.exists()) {
                    images.put("monster3", ImageIO.read(monster3File));
                } else {
                    createDefaultMonster3Image();
                }
                
                File monster4File = new File("resources/images/L4_Enemy.png");
                if (monster4File.exists()) {
                    images.put("monster4", ImageIO.read(monster4File));
                } else {
                    createDefaultMonster4Image();
                }
                
                File monster5File = new File("resources/images/L5_Enemy.png");
                if (monster5File.exists()) {
                    images.put("monster5", ImageIO.read(monster5File));
                } else {
                    createDefaultMonster5Image();
                }
                
                /*
                ======โหลดรูปบอส======
                */
                File bossFile = new File("resources/images/L1_Boss.png");
                if (bossFile.exists()) {
                    images.put("boss", ImageIO.read(bossFile));
                } else {
                    createDefaultBossImage();
                }

                File boss2File = new File("resources/images/L2_Boss.png");
                if (boss2File.exists()) {
                    images.put("boss2", ImageIO.read(boss2File));
                } else {
                    createDefaultBoss2Image();
                }

                File boss3File = new File("resources/images/L3_Boss.png");
                if (boss3File.exists()) {
                    images.put("boss3", ImageIO.read(boss3File));
                } else {
                    createDefaultBoss3Image();
                }
                
                File boss4File = new File("resources/images/L4_Boss.png");
                if (boss4File.exists()) {
                    images.put("boss4", ImageIO.read(boss4File));
                } else {
                    createDefaultBoss4Image();
                }
                
                File boss5Phase1File = new File("resources/images/L5_Boss_Phase1.png");
                if (boss5Phase1File.exists()) {
                    images.put("L5_Boss_Phase1", ImageIO.read(boss5Phase1File));
                } else {
                    images.put("L5_Boss_Phase1", images.get("boss5"));
                }

                File boss5Phase2File = new File("resources/images/L5_Boss_Phase2.png");
                if (boss5Phase2File.exists()) {
                    images.put("L5_Boss_Phase2", ImageIO.read(boss5Phase2File));
                } else {
                    try {
                        BufferedImage bossPh1 = (BufferedImage) images.get("boss5");
                        if (bossPh1 != null) {
                            BufferedImage bossPh2 = new BufferedImage(
                                    bossPh1.getWidth(), bossPh1.getHeight(),
                                    BufferedImage.TYPE_INT_ARGB);

                            for (int x = 0; x < bossPh1.getWidth(); x++) {
                                for (int y = 0; y < bossPh1.getHeight(); y++) {
                                    int rgb = bossPh1.getRGB(x, y);
                                    if (rgb != 0) {
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
                        createDefaultBoss5Phase2Image();
                    }
                }

                /*
                ======โหลดรูปภาพ hotbar======
                */
                File hotbarUIFile = new File("resources/images/weapon bar.png");
                if (hotbarUIFile.exists()) {
                    images.put("hotbar", ImageIO.read(hotbarUIFile));
                } else {
                    createDefaultHotbarImage();
                }

                File activeFrameFile = new File("resources/images/Weapon selection framework.png");
                if (activeFrameFile.exists()) {
                    images.put("activeFrame", ImageIO.read(activeFrameFile));
                } else {
                    createDefaultActiveFrame();
                }
                
                /*
                ======โหลดรูปภาพอาวุธและเอฟเฟค======
                */
                File muzzleFlashFile = new File("resources/images/muzzle_flash.png");
                if (muzzleFlashFile.exists()) {
                    images.put("muzzle_flash", ImageIO.read(muzzleFlashFile));
                } else {
                    createDefaultMuzzleFlashImage();
                }
                
                File pistolFile = new File("resources/images/Pistol.png"); //ปืนพก
                if (pistolFile.exists()) {
                    images.put("pistol", ImageIO.read(pistolFile));
                } else {
                    createDefaultGunImage();
                }
                
                File turretHeadFile = new File("resources/images/Turret Head.png");
                if (turretHeadFile.exists()) {
                    images.put("turretHead", ImageIO.read(turretHeadFile));
                }

                File turretBaseFile = new File("resources/images/Turret base.png");
                if (turretHeadFile.exists()) {
                    images.put("turretBase", ImageIO.read(turretBaseFile));
                }

                File ak47File = new File("resources/images/AK47.png");
                if (ak47File.exists()) {
                    images.put("ak47", ImageIO.read(ak47File));
                }

                File gatlingGunFile = new File("resources/images/Gatling_Gun.png");
                if (gatlingGunFile.exists()) {
                    images.put("gatlingGun", ImageIO.read(gatlingGunFile));
                }
                
                File ShurikenFile = new File("resources/images/Shuriken.png");
                if (ShurikenFile.exists()) {
                    images.put("shuriken", ImageIO.read(ShurikenFile));
                    System.out.println("SHURIKEN");
                }
                
                File ShotgunFile = new File("resources/images/Shotgun.png");
                if (ShotgunFile.exists()) {
                    images.put("shotgun", ImageIO.read(ShotgunFile));
                }
                
                /*
                ======โหลดรูปหัวใจปกติและหัวใจแตก======
                */
                File heartFile = new File("resources/images/heart.png");
                if (heartFile.exists()) {
                    images.put("heart", ImageIO.read(heartFile));
                } else {
                    createDefaultHeartImage();
                }

                File brokenHeartFile = new File("resources/images/broken_heart.png");
                if (brokenHeartFile.exists()) {
                    images.put("broken_heart", ImageIO.read(brokenHeartFile));
                } else {
                    createDefaultBrokenHeartImage();
                }
                
                createOtherDefaultImages();

                imagesLoaded = true;
                System.out.println("โหลดรูปภาพทั้งหมดสำเร็จ!");
            } catch (IOException e) {
                throw e;
            }
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการโหลดรูปภาพ: " + e.getMessage());
            createAllDefaultImages();
        }
    }

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

    private static void createDefaultPlayerImage() {
        BufferedImage playerImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = playerImg.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 32, 32);
        g2d.dispose();
        images.put("player", playerImg);
    }

    private static void createDefaultMonsterImage() {
        BufferedImage monsterImg = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = monsterImg.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 30, 30);
        g2d.dispose();
        images.put("monster", monsterImg);
    }

    private static void createDefaultBossImage() {
        BufferedImage bossImg = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bossImg.createGraphics();
        g2d.setColor(new Color(180, 0, 0));
        g2d.fillRect(0, 0, 80, 80);
        g2d.dispose();
        images.put("boss", bossImg);
    }

    private static void createDefaultGunImage() {
        BufferedImage gunImg = new BufferedImage(20, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = gunImg.createGraphics();
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, 15, 4);
        g2d.fillRect(10, 0, 5, 10);
        g2d.dispose();
        images.put("gun", gunImg);
    }

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

    private static void createDefaultHotbarImage() {
        BufferedImage hotbarBg = new BufferedImage(400, 60, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = hotbarBg.createGraphics();
        g2d.setColor(new Color(50, 50, 50, 200));
        g2d.fillRoundRect(0, 0, 400, 60, 10, 10);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(0, 0, 399, 59, 10, 10);
        g2d.dispose();
        images.put("hotbar", hotbarBg);
    }

    private static void createDefaultActiveFrame() {
        BufferedImage frameImg = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = frameImg.createGraphics();
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(2, 2, 46, 46, 10, 10);
        g2d.setColor(new Color(255, 0, 0, 80));
        g2d.fillRoundRect(2, 2, 46, 46, 10, 10);
        g2d.dispose();
        images.put("activeFrame", frameImg);
    }

    private static void createDefaultHeartImage() {
        BufferedImage heartImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = heartImg.createGraphics();
        g2d.setColor(Color.RED);

        int[] xPoints = {10, 15, 20, 15, 10, 5, 0, 5};
        int[] yPoints = {5, 0, 5, 10, 20, 10, 5, 0};
        g2d.fillPolygon(xPoints, yPoints, 8);

        g2d.dispose();
        images.put("heart", heartImg);
    }

    private static void createDefaultBrokenHeartImage() {
        BufferedImage brokenHeartImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = brokenHeartImg.createGraphics();
        g2d.setColor(new Color(128, 0, 0));

        int[] xPoints = {10, 15, 20, 15, 10, 5, 0, 5};
        int[] yPoints = {5, 0, 5, 10, 20, 10, 5, 0};
        g2d.fillPolygon(xPoints, yPoints, 8);

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

        g2d.setColor(new Color(150, 120, 90));
        g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2d.setColor(new Color(130, 100, 70));
        for (int i = 0; i < 800; i += 200) {
            g2d.drawLine(i, 0, i, 600);
        }
        for (int j = 0; j < 600; j += 200) {
            g2d.drawLine(0, j, 800, j);
        }

        g2d.dispose();
        images.put("level2_bg", level2Bg);
    }

    private static void createLevel3BackgroundImage() {
        BufferedImage level3Bg = new BufferedImage(GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = level3Bg.createGraphics();

        g2d.setColor(new Color(165, 130, 95));
        g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2d.setColor(new Color(145, 110, 75));
        for (int y = 0; y < GamePanel.HEIGHT; y += 30) {
            g2d.drawLine(0, y, GamePanel.WIDTH, y);
        }

        for (int x = 0; x < GamePanel.WIDTH; x += 60) {
            g2d.drawLine(x, 0, x, GamePanel.HEIGHT);
        }

        g2d.setColor(new Color(220, 220, 220));
        for (int x = 600; x < GamePanel.WIDTH; x += 30) {
            for (int y = 0; y < 320; y += 30) {
                g2d.fillRect(x, y, 30, 30);
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRect(x, y, 30, 30);
                g2d.setColor(new Color(220, 220, 220));
            }
        }

        createGlowEffect(g2d, 150, 200, 70);
        createGlowEffect(g2d, 450, 350, 80);
        createGlowEffect(g2d, 650, 120, 60);
        createGlowEffect(g2d, 300, 450, 75);

        g2d.setColor(new Color(150, 120, 90));
        int[] xPoints1 = {200, 350, 320, 170};
        int[] yPoints1 = {100, 150, 250, 200};
        g2d.fillPolygon(xPoints1, yPoints1, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints1, yPoints1, 4);

        g2d.setColor(new Color(160, 130, 100));
        g2d.fillRect(380, 180, 120, 70);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(380, 180, 120, 70);

        g2d.setColor(new Color(140, 110, 80));
        g2d.fillRect(100, 350, 150, 100);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(100, 350, 150, 100);

        g2d.setColor(new Color(150, 120, 90));
        g2d.fillRect(550, 250, 60, 120);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(550, 250, 60, 120);

        g2d.setColor(new Color(100, 100, 100));
        g2d.fillOval(700, 350, 60, 60);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(700, 350, 60, 60);

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
        g2d.setColor(new Color(50, 150, 50));
        g2d.fillRect(0, 0, 35, 35);

        g2d.setColor(Color.RED);
        g2d.fillOval(5, 5, 10, 10);
        g2d.fillOval(20, 5, 10, 10);

        g2d.dispose();
        images.put("monster2", monster2Img);
    }

    private static void createDefaultMonster3Image() {
        BufferedImage monster3Img = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = monster3Img.createGraphics();

        g2d.setColor(new Color(60, 170, 210));
        g2d.fillOval(0, 0, 35, 35);

        g2d.setColor(new Color(0, 0, 50));
        g2d.fillOval(7, 10, 7, 7);
        g2d.fillOval(21, 10, 7, 7);

        g2d.drawArc(10, 20, 15, 8, 0, 180);

        g2d.dispose();
        images.put("monster3", monster3Img);
    }

    private static void createGlowEffect(Graphics2D g2d, int x, int y, int radius) {
        // สร้างรัศมีแสงโดยใช้สีเหลืองอ่อนโปร่งใส
        for (int i = 0; i < 5; i++) {
            int alpha = 50 - (i * 10);
            g2d.setColor(new Color(255, 255, 200, alpha));
            int currentRadius = radius - (i * 10);
            g2d.fillOval(x - currentRadius, y - currentRadius, currentRadius * 2, currentRadius * 2);
        }
    }

    private static void createDefaultBoss2Image() {
        BufferedImage boss2Img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boss2Img.createGraphics();
        g2d.setColor(new Color(75, 0, 130));
        g2d.fillOval(0, 0, 100, 100);

        g2d.setColor(Color.GREEN);
        g2d.fillOval(20, 25, 20, 20);
        g2d.fillOval(60, 25, 20, 20);

        g2d.dispose();
        images.put("boss2", boss2Img);
    }

    private static void createDefaultBoss3Image() {
        BufferedImage boss3Img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boss3Img.createGraphics();

        g2d.setColor(new Color(255, 213, 170));
        g2d.fillOval(0, 0, 100, 100);

        g2d.setColor(Color.BLACK);
        g2d.fillArc(0, 0, 100, 60, 0, 180);

        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(25, 35, 20, 3);
        g2d.fillRect(55, 35, 20, 3);

        g2d.setColor(Color.BLACK);
        g2d.drawLine(25, 45, 45, 45);
        g2d.drawLine(55, 45, 75, 45);

        g2d.setColor(new Color(220, 170, 140));
        int[] xPointsNose = {50, 45, 55};
        int[] yPointsNose = {60, 70, 70};
        g2d.fillPolygon(xPointsNose, yPointsNose, 3);

        g2d.setColor(new Color(200, 120, 120));
        g2d.drawArc(35, 75, 30, 10, 0, 180);

        g2d.dispose();
        images.put("boss3", boss3Img);
    }

    private static void createLevel4BackgroundImage() {
        BufferedImage level4Bg = new BufferedImage(GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = level4Bg.createGraphics();

        g2d.setColor(new Color(220, 220, 220));
        g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2d.setColor(new Color(200, 200, 200));
        for (int i = 0; i < GamePanel.WIDTH; i += 40) {
            g2d.drawLine(i, 0, i, GamePanel.HEIGHT);
        }
        for (int j = 0; j < GamePanel.HEIGHT; j += 40) {
            g2d.drawLine(0, j, GamePanel.WIDTH, j);
        }

        g2d.setColor(new Color(180, 180, 180));
        g2d.fillRect(300, 150, 200, 80);
        g2d.setColor(new Color(120, 120, 120));
        g2d.drawRect(300, 150, 200, 80);

        g2d.setColor(new Color(100, 100, 200));
        g2d.fillRect(320, 160, 30, 20);
        g2d.setColor(new Color(200, 100, 100));
        g2d.fillRect(370, 165, 25, 25);
        g2d.setColor(new Color(100, 200, 100));
        g2d.fillRect(420, 160, 40, 15);

        g2d.setColor(new Color(100, 100, 100));
        g2d.drawOval(100, 80, 60, 60);
        g2d.drawOval(600, 80, 60, 60);

        g2d.dispose();
        images.put("level4_bg", level4Bg);
    }

    private static void createLevel5BackgroundImage() {
        BufferedImage level5Bg = new BufferedImage(GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = level5Bg.createGraphics();

        g2d.setColor(new Color(20, 0, 30));
        g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g2d.setColor(new Color(255, 255, 255));
        Random random = new Random(5);
        for (int i = 0; i < 200; i++) {
            int starX = random.nextInt(GamePanel.WIDTH);
            int starY = random.nextInt(GamePanel.HEIGHT);
            int starSize = random.nextInt(3) + 1;
            g2d.fillRect(starX, starY, starSize, starSize);
        }

        createGlowEffect(g2d, 150, 100, 60, new Color(80, 0, 120));
        createGlowEffect(g2d, 600, 120, 70, new Color(120, 0, 80));
        createGlowEffect(g2d, 200, 400, 50, new Color(80, 0, 120));
        createGlowEffect(g2d, 550, 380, 65, new Color(120, 0, 80));

        g2d.setColor(new Color(60, 30, 60));
        g2d.fillRect(350, 200, 100, 100);
        g2d.setColor(new Color(100, 50, 100));
        g2d.drawRect(350, 200, 100, 100);

        g2d.setColor(new Color(80, 0, 120, 100));
        g2d.drawOval(250, 100, 300, 300);
        g2d.drawOval(275, 125, 250, 250);

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

        g2d.setColor(new Color(150, 150, 200));
        g2d.fillRect(0, 0, 40, 40);

        g2d.setColor(Color.RED);
        g2d.fillOval(8, 8, 8, 8);
        g2d.fillOval(24, 8, 8, 8);

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(10, 25, 20, 5);
        g2d.drawRect(5, 5, 30, 30);

        g2d.dispose();
        images.put("monster4", monster4Img);
    }

    private static void createDefaultMonster5Image() {
        BufferedImage monster5Img = new BufferedImage(45, 45, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = monster5Img.createGraphics();

        g2d.setColor(new Color(30, 0, 50));
        g2d.fillOval(0, 0, 45, 45);

        g2d.setColor(new Color(120, 0, 200, 100));
        g2d.fillOval(-5, -5, 55, 55);

        g2d.setColor(Color.RED);
        g2d.fillOval(10, 13, 8, 8);
        g2d.fillOval(27, 13, 8, 8);

        g2d.dispose();
        images.put("monster5", monster5Img);
    }

    private static void createDefaultBoss4Image() {
        BufferedImage boss4Img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boss4Img.createGraphics();

        g2d.setColor(new Color(120, 120, 180));
        g2d.fillRect(0, 0, 120, 120);

        g2d.setColor(new Color(80, 80, 100));
        g2d.fillRect(30, 30, 60, 60);

        g2d.setColor(Color.RED);
        g2d.fillOval(30, 30, 20, 20);
        g2d.fillOval(70, 30, 20, 20);

        g2d.setColor(new Color(100, 100, 140));
        g2d.fillRect(-30, 40, 30, 20);
        g2d.fillRect(120, 40, 30, 20);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 119, 119);

        g2d.dispose();
        images.put("boss4", boss4Img);
    }

    private static void createDefaultBoss5Phase2Image() {
        BufferedImage boss5Ph2 = new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boss5Ph2.createGraphics();

        // สร้างรูปเฟส 2 ที่ดูอันตรายกว่าเฟส 1
        g2d.setColor(new Color(150, 0, 20));
        g2d.fillOval(0, 0, 140, 140);

        g2d.setColor(new Color(255, 30, 0, 80));
        g2d.fillOval(-10, -10, 160, 160);

        g2d.setColor(Color.YELLOW);
        g2d.fillOval(35, 40, 25, 25);
        g2d.fillOval(80, 40, 25, 25);

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

    private static void createGlowEffect(Graphics2D g2d, int x, int y, int radius, Color color) {
        for (int i = 0; i < 5; i++) {
            int alpha = 50 - (i * 10);
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            int currentRadius = radius - (i * 10);
            g2d.fillOval(x - currentRadius, y - currentRadius, currentRadius * 2, currentRadius * 2);
        }
    }

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
            } catch (IOException e) {
                System.out.println("ไม่สามารถโหลดรูปภาพบัฟ " + name + ": " + e.getMessage());
                createDefaultPowerupIcon(name);
            }
        }
    }

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

        g.fillOval(0, 0, 50, 50);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawOval(0, 0, 49, 49);

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

        FontMetrics fm = g.getFontMetrics();
        int textX = (50 - fm.stringWidth(symbol)) / 2;
        int textY = (50 - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(symbol, textX, textY);

        g.dispose();
        images.put(powerupName, iconImage);
    }

    public static Image getImage(String key) {
        if (!imagesLoaded) {
            loadImages();
        }
        return images.get(key);
    }
}
