
import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;

public class SoundManager {

    // ตัวแปรสำหรับเก็บเสียงและสถานะต่างๆ
    private static final HashMap<String, Clip> sounds = new HashMap<>();
    private static Clip backgroundMusic = null;
    private static boolean soundsLoaded = false;
    private static boolean musicMuted = false;

    /**
     * โหลดไฟล์เสียงทั้งหมดที่จำเป็นสำหรับเกม
     */
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

            // เพิ่มการโหลดเพลงสำหรับ Level 1
            loadMusic("level1_music", "resources/sounds/level1_music.wav");

            soundsLoaded = true;
            System.out.println("โหลดไฟล์เสียงทั้งหมดสำเร็จ!");
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการโหลดไฟล์เสียง: " + e.getMessage());
        }
    }

    /**
     * โหลดไฟล์เสียงเอฟเฟค
     *
     * @param key ชื่อสำหรับอ้างอิงเสียง
     * @param filePath ที่อยู่ของไฟล์เสียง
     */
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
        } catch (Exception e) {
            System.err.println("ไม่สามารถโหลดไฟล์เสียงได้: " + e.getMessage());
        }
    }

    /**
     * โหลดไฟล์เพลงประกอบ
     *
     * @param key ชื่อสำหรับอ้างอิงเพลง
     * @param filePath ที่อยู่ของไฟล์เพลง
     */
    private static void loadMusic(String key, String filePath) {
        try {
            File musicFile = new File(filePath);
            if (musicFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                sounds.put(key, clip);
                System.out.println("โหลดไฟล์เพลง " + key + " สำเร็จ");
            } else {
                System.out.println("ไม่พบไฟล์เพลง: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("ไม่สามารถโหลดไฟล์เพลงได้: " + e.getMessage());
        }
    }

    /**
     * เริ่มเล่นเพลงประกอบ
     *
     * @param key ชื่อของเพลงที่ต้องการเล่น
     */
    public static void playBackgroundMusic(String key) {
        if (musicMuted) {
            return;
        }

        // หยุดเพลงปัจจุบัน (ถ้ามี)
        stopBackgroundMusic();

        // เริ่มเล่นเพลงใหม่
        Clip clip = sounds.get(key);
        if (clip != null) {
            backgroundMusic = clip;
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * หยุดเพลงประกอบที่กำลังเล่นอยู่
     */
    public static void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    /**
     * สลับสถานะเปิด/ปิดเสียงดนตรี
     */
    public static void toggleMusic() {
        musicMuted = !musicMuted;
        if (musicMuted) {
            stopBackgroundMusic();
        }
    }

    /**
     * เล่นเสียงเอฟเฟค
     *
     * @param key ชื่อของเสียงที่ต้องการเล่น
     */
    public static void playSound(String key) {
        if (!soundsLoaded) {
            loadSounds();
        }

        try {
            Clip clip = sounds.get(key);
            if (clip != null) {
                // สร้าง clip ใหม่ทุกครั้งที่เล่น
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("resources/sounds/" + key + ".wav"));
                Clip newClip = AudioSystem.getClip();
                newClip.open(audioIn);

                // ปรับความดังของเสียงให้เบาลง (ค่าระหว่าง -80.0f ถึง 6.0f โดย 0.0f คือปกติ)
                if (newClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) newClip.getControl(FloatControl.Type.MASTER_GAIN);
                    // ปรับลดเหลือ 30% ของความดังปกติ (ประมาณ -10.5 dB)
                    gainControl.setValue(-10.5f);
                }

                newClip.start();

                // ลบ clip เก่าเมื่อเล่นจบ
                newClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        newClip.close();
                    }
                });
            }
        } catch (Exception e) {
            // จัดการข้อผิดพลาดแบบเงียบๆ เพื่อไม่ให้เกมค้าง
            System.err.println("ไม่สามารถเล่นเสียง " + key + ": " + e.getMessage());
        }
    }
}
