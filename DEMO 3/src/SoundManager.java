
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SoundManager {

    // ตัวแปรสำหรับเก็บเสียงและสถานะต่างๆ
    private static final HashMap<String, Clip> sounds = new HashMap<>();
    private static Clip backgroundMusic = null;
    private static boolean soundsLoaded = false;
    private static boolean musicMuted = false;
    private static float effectVolume = 0.5f;

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

            // โหลดไฟล์เสียงเก็บบัฟ
            loadSound("get_skill", "resources/sounds/get_skill.wav");

            // เพิ่มการโหลดเพลงสำหรับ Level 1
            loadMusic("level1_music", "resources/sounds/level1_music.wav");

            // เพิ่มการโหลดเพลงสำหรับ Level 2
            loadMusic("level2_music", "resources/sounds/level2_music.wav");
            
            // เพิ่มการโหลดเพลงสำหรับ Level 3
            loadMusic("level3_music", "resources/sounds/level3_music.wav");

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
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
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
                // สร้าง clip ว่าง ๆ เพื่อป้องกัน NullPointerException
                if (key.equals("level2_music")) {
                    // ใช้เพลงด่าน 1 แทนหากไม่พบเพลงด่าน 2
                    sounds.put(key, sounds.get("level1_music"));
                    System.out.println("ใช้เพลงด่าน 1 แทนเพลงด่าน 2");
                }
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
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

        // ตรวจสอบว่ามีเพลงตามที่ต้องการหรือไม่
        Clip clip = sounds.get(key);
        if (clip == null) {
            System.out.println("ไม่พบเพลง " + key + " จะใช้เพลงเริ่มต้นแทน");
            // ใช้เพลงด่าน 1 เป็นเพลงเริ่มต้น
            clip = sounds.get("level1_music");
            if (clip == null) {
                return; // ถ้าไม่มีเพลงเริ่มต้นให้ยกเลิก
            }
        }

        // หยุดเพลงปัจจุบัน (ถ้ามี)
        stopBackgroundMusic();

        // เริ่มเล่นเพลงใหม่
        backgroundMusic = clip;
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * หยุดเพลงประกอบที่กำลังเล่นอยู่
     */
    public static void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            if (backgroundMusic.isRunning()) {
                backgroundMusic.stop();
            }
            // เพิ่มบรรทัดนี้เพื่อรีเซ็ตตำแหน่งการเล่น
            backgroundMusic.setFramePosition(0);
        }

        // เพิ่มการเช็คและทำความสะอาดทุก clip ที่อาจยังเล่นอยู่
        for (Clip clip : sounds.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.setFramePosition(0);
            }
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

    public static float getEffectVolume() {
        return effectVolume;
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
                    // แปลงค่า 0.0-1.0 เป็นค่า dB (-80.0 ถึง 6.0)
                    // สูตร: dB = 20 * log10(volume)
                    // เมื่อ volume = 0 จะได้ -Infinity dB ซึ่งเป็นการปิดเสียง
                    float dB;
                    if (effectVolume > 0.0f) {
                        dB = (float) (20.0f * Math.log10(effectVolume));
                        gainControl.setValue(Math.max(-80.0f, dB));
                    }
                }

                newClip.start();

                // ลบ clip เก่าเมื่อเล่นจบ
                newClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        newClip.close();
                    }
                });
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            // จัดการข้อผิดพลาดแบบเงียบๆ เพื่อไม่ให้เกมค้าง
            System.err.println("ไม่สามารถเล่นเสียง " + key + ": " + e.getMessage());
        }
    }

    public static void setEffectVolume(float volume) {
        effectVolume = Math.max(0.0f, Math.min(1.0f, volume));
        System.out.println("ระดับเสียงปัจจุบัน: " + (effectVolume * 100) + "%");
    }

    public static void increaseEffectVolume(float amount) {
        setEffectVolume(effectVolume + amount);
    }

    public static void decreaseEffectVolume(float amount) {
        setEffectVolume(effectVolume - amount);
    }

    public static void stopAllEffects() {
        // วนลูปผ่านทุก clip และหยุดการเล่นถ้ากำลังเล่นอยู่
        // ยกเว้น backgroundMusic ซึ่งจัดการแยกต่างหาก
        for (String key : sounds.keySet()) {
            Clip clip = sounds.get(key);
            if (clip != null && clip != backgroundMusic && clip.isRunning()) {
                clip.stop();
                clip.setFramePosition(0);
            }
        }
    }

    public static boolean isMusicMuted() {
        return musicMuted;
    }

    // เพิ่มเมธอดสำหรับตั้งค่าสถานะการปิดเสียงเพลง
    public static void setMusicMuted(boolean muted) {
        musicMuted = muted;
        if (musicMuted) {
            stopBackgroundMusic();
        } else if (backgroundMusic != null) {
            // ลองเล่นเพลงปัจจุบันถ้ามี
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}
