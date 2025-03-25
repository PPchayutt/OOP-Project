
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SoundManager {

    private static final HashMap<String, Clip> sounds = new HashMap<>();
    private static boolean soundsLoaded = false;

    public static void loadSounds() {
        try {
            // ตรวจสอบว่าโฟลเดอร์ resources/sounds/ มีหรือไม่
            File resourceDir = new File("resources/sounds");
            if (!resourceDir.exists()) {
                resourceDir.mkdirs();
                System.out.println("สร้างโฟลเดอร์ resources/sounds เรียบร้อย โปรดนำไฟล์เสียงไปใส่ในโฟลเดอร์นี้");
            }

            // โหลดไฟล์เสียงยิงปืน
            loadSound("gun_shot", "resources/sounds/gun_shot.wav");

            soundsLoaded = true;
            System.out.println("โหลดไฟล์เสียงทั้งหมดสำเร็จ!");
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการโหลดไฟล์เสียง: " + e.getMessage());
        }
    }

    private static void loadSound(String key, String filePath) {
        try {
            File soundFile = new File(filePath);
            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                sounds.put(key, clip);
                System.out.println("โหลดไฟล์เสียง " + key + " สำเร็จ");
            } else {
                System.out.println("ไม่พบไฟล์เสียง: " + filePath);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("ไม่สามารถโหลดไฟล์เสียงได้: " + e.getMessage());
        }
    }

    public static void playSound(String key) {
        if (!soundsLoaded) {
            loadSounds();
        }

        Clip clip = sounds.get(key);
        if (clip != null) {
            // หยุดและย้อนกลับไปที่จุดเริ่มต้น
            clip.stop();
            clip.setFramePosition(0);
            // เล่นเสียง
            clip.start();
        }
    }
}
