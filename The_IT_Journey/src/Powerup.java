
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.util.Random;

public class Powerup implements GameObject {

    // ค่าคงที่สำหรับประเภทของบัฟ
    public static final int CATEGORY_CRAZY = 1; // บัฟสุดโหด
    public static final int CATEGORY_PERMANENT = 2; // บัฟถาวร
    public static final int CATEGORY_TEMPORARY = 3; // บัฟชั่วคราว

    // ชนิดของบัฟสุดโหด
    public static final int TYPE_CRAZY_SHOOTING = 0;
    public static final int TYPE_STOP_TIME = 1;

    // ชนิดของบัฟถาวรและชั่วคราว
    public static final int TYPE_INCREASE_DAMAGE = 0;
    public static final int TYPE_INCREASE_SPEED = 1;
    public static final int TYPE_INCREASE_SHOOTING_SPEED = 2;
    public static final int TYPE_KNOCKBACK = 3;
    public static final int TYPE_MORE_HEART = 4;
    public static final int TYPE_MULTIPLE_BULLETS = 5;
    public static final int TYPE_HEALING = 6; // เฉพาะบัฟชั่วคราว

    private final float x;

    private final float y;
    private final int width;
    private final int height;
    private final int category; // ประเภทใหญ่ของบัฟ
    private final int type; // ประเภทย่อยของบัฟ
    private int value; // ค่าของบัฟ
    private boolean active = true;
    private int duration = -1; // ระยะเวลาของบัฟ (-1 = ถาวร)
    private Image icon;
    private static final Random random = new Random();

    // Constructor หลักที่รองรับบัฟทุกประเภท
    public Powerup(int x, int y, int category, int type) {
        this.x = x;
        this.y = y;
        this.width = 30;
        this.height = 30;
        this.category = category;
        this.type = type;

        // กำหนดค่าตามประเภทของบัฟ
        initializeValues();

        // โหลดไอคอน
        loadIcon();
    }

    // กำหนดค่าตามประเภทของบัฟ
    private void initializeValues() {
        switch (category) {
            case CATEGORY_CRAZY -> {
                duration = 300; // 5 วินาที
                switch (type) {
                    case TYPE_CRAZY_SHOOTING ->
                        value = 1; // โหมดยิงบ้าคลั่ง
                    case TYPE_STOP_TIME ->
                        value = 1; // โหมดหยุดเวลา
                }
            }
            case CATEGORY_PERMANENT -> {
                duration = -1; // ไม่มีกำหนดเวลา (ถาวร)
                switch (type) {
                    case TYPE_INCREASE_DAMAGE ->
                        value = 5; // เพิ่มความเสียหาย
                    case TYPE_INCREASE_SPEED ->
                        value = 1; // เพิ่มความเร็ว
                    case TYPE_INCREASE_SHOOTING_SPEED ->
                        value = 20; // ลดเวลาคูลดาวน์ (ms)
                    case TYPE_KNOCKBACK ->
                        value = 1; // เปิดใช้งาน knockback
                    case TYPE_MORE_HEART ->
                        value = 1; // เพิ่มหัวใจ
                    case TYPE_MULTIPLE_BULLETS ->
                        value = 1; // เพิ่มจำนวนกระสุน
                }
            }
            case CATEGORY_TEMPORARY -> {
                duration = 600; // 10 วินาที
                switch (type) {
                    case TYPE_INCREASE_DAMAGE ->
                        value = 10; // เพิ่มความเสียหายมากกว่าถาวร
                    case TYPE_INCREASE_SPEED ->
                        value = 2; // เพิ่มความเร็วมากกว่าถาวร
                    case TYPE_INCREASE_SHOOTING_SPEED ->
                        value = 50; // ลดเวลาคูลดาวน์มากกว่าถาวร
                    case TYPE_KNOCKBACK ->
                        value = 2; // knockback แรงกว่าถาวร
                    case TYPE_MULTIPLE_BULLETS ->
                        value = 2; // กระสุนมากกว่าถาวร
                    case TYPE_HEALING -> {
                        value = 50; // ฮีลเลือด 50 หน่วย
                        duration = 0; // ใช้แล้วหมดทันที
                    }
                }
            }
        }
    }

    // โหลดไอคอนตามประเภทของบัฟ
    private void loadIcon() {
        String imagePath = "resources/images/";

        switch (category) {
            case CATEGORY_CRAZY -> {
                switch (type) {
                    case TYPE_CRAZY_SHOOTING ->
                        imagePath += "crazy_shooting.png";
                    case TYPE_STOP_TIME ->
                        imagePath += "stop_time.png";
                }
            }
            case CATEGORY_PERMANENT -> {
                switch (type) {
                    case TYPE_INCREASE_DAMAGE ->
                        imagePath += "increase_bullet_damage.png";
                    case TYPE_INCREASE_SPEED ->
                        imagePath += "increase_movement_speed.png";
                    case TYPE_INCREASE_SHOOTING_SPEED ->
                        imagePath += "increase_shooting_speed.png";
                    case TYPE_KNOCKBACK ->
                        imagePath += "knockback.png";
                    case TYPE_MORE_HEART ->
                        imagePath += "plus_more_heart.png";
                    case TYPE_MULTIPLE_BULLETS ->
                        imagePath += "shoot_multiple_bullets.png";
                }
            }
            case CATEGORY_TEMPORARY -> {
                switch (type) {
                    case TYPE_INCREASE_DAMAGE ->
                        imagePath += "increase_bullet_damage(temp).png";
                    case TYPE_INCREASE_SPEED ->
                        imagePath += "increase_movement_speed(temp).png";
                    case TYPE_INCREASE_SHOOTING_SPEED ->
                        imagePath += "increase_shooting_speed(temp).png";
                    case TYPE_KNOCKBACK ->
                        imagePath += "knockback(temp).png";
                    case TYPE_MULTIPLE_BULLETS ->
                        imagePath += "fires_multiple_bullets(temp).png";
                    case TYPE_HEALING ->
                        imagePath += "healing.png";
                }
            }
        }

        // พยายามโหลดไอคอน
        try {
            icon = new ImageIcon(imagePath).getImage();
            if (icon == null || icon.getWidth(null) <= 0) {
                System.out.println("type : " + type);
                createDefaultIcon();
            }
        } catch (Exception e) {
            createDefaultIcon();
        }
    }

    private void createDefaultIcon() {
        BufferedImage defaultIcon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultIcon.createGraphics();

        switch (category) {
            case CATEGORY_CRAZY ->
                g2d.setColor(Color.RED);
            case CATEGORY_PERMANENT ->
                g2d.setColor(new Color(128, 0, 128)); // สีม่วง
            case CATEGORY_TEMPORARY ->
                g2d.setColor(Color.ORANGE);
        }

        g2d.fillOval(0, 0, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawOval(0, 0, width - 1, height - 1);
        g2d.dispose();

        icon = defaultIcon;
    }

    @Override
    public void update() {
        // อัปเดตระยะเวลาของบัฟถ้าถูกเก็บแล้ว
        if (!active && duration > 0) {
            duration--;
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(icon, (int) x, (int) y, width, height, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // Getters และ Setters
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCategory() {
        return category;
    }

    public int getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Image getIcon() {
        return icon;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    // ชื่อของบัฟสำหรับแสดงในเกม
    public String getName() {
        return switch (category) {
            case CATEGORY_CRAZY ->
                switch (type) {
                    case TYPE_CRAZY_SHOOTING ->
                        "Crazy Shooting";
                    case TYPE_STOP_TIME ->
                        "Stop Time";
                    default ->
                        "Unknown";
                };
            case CATEGORY_PERMANENT ->
                switch (type) {
                    case TYPE_INCREASE_DAMAGE ->
                        "Damage+";
                    case TYPE_INCREASE_SPEED ->
                        "Speed+";
                    case TYPE_INCREASE_SHOOTING_SPEED ->
                        "Fire Rate+";
                    case TYPE_KNOCKBACK ->
                        "Knockback";
                    case TYPE_MORE_HEART ->
                        "Extra Heart";
                    case TYPE_MULTIPLE_BULLETS ->
                        "Multi Shot";
                    default ->
                        "Unknown";
                };
            case CATEGORY_TEMPORARY ->
                switch (type) {
                    case TYPE_INCREASE_DAMAGE ->
                        "Damage Boost";
                    case TYPE_INCREASE_SPEED ->
                        "Speed Boost";
                    case TYPE_INCREASE_SHOOTING_SPEED ->
                        "Rapid Fire";
                    case TYPE_KNOCKBACK ->
                        "Super Knockback";
                    case TYPE_MULTIPLE_BULLETS ->
                        "Burst Shot";
                    case TYPE_HEALING ->
                        "Healing";
                    default ->
                        "Unknown";
                };
            default ->
                "Unknown";
        };
    }

    // สร้างบัฟแบบสุ่ม
    public static Powerup createRandomPowerup(int x, int y) {
        double randomValue = random.nextDouble();

        // บัฟสุดโหด (Crazy) - ดรอปน้อยสุด
        if (randomValue < 0.10) {
            int type = random.nextBoolean() ? TYPE_CRAZY_SHOOTING : TYPE_STOP_TIME;
            return new Powerup(x, y, CATEGORY_CRAZY, type);
        } // บัฟถาวร (Permanent) - ดรอปปานกลาง
        else if (randomValue < 0.40) {
            int type = random.nextInt(6);
            return new Powerup(x, y, CATEGORY_PERMANENT, type);
        } // บัฟชั่วคราว (Temporary) - ดรอปเยอะสุด
        else if (randomValue < 0.70) {
            int type = random.nextInt(7);
            while (type == 4) { // กรณีได้ type ที่ไม่อยู่ใน category temporary
                type = random.nextInt(7);
            }
            return new Powerup(x, y, CATEGORY_TEMPORARY, type);
        }
        return null; // ไม่ดรอปอะไร
    }
}
