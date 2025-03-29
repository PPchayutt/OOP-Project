
import java.util.Random;

public class LevelManager {

    private int currentLevel = 1;
    private int monstersToKill;
    private int monstersKilled = 0;
    private boolean bossSpawned = false;
    private static final Random random = new Random();
    private boolean levelJustChanged = false;
    private boolean isTransitioning = false;
    private int transitionTimer = 0;
    private boolean needMapChange = false;
    private boolean levelReadyToPlay = false;

    public LevelManager() {
        monstersToKill = 20 + (currentLevel - 1) * 5;
    }

    public void monsterKilled() {
        monstersKilled++;
    }

    public void bossKilled() {
        currentLevel++;
        // เพิ่มการเช็คว่าต้องเป็นด่าน 1-5 เท่านั้น
        if (currentLevel > 5) {
            currentLevel = 5; // จำกัดเป็นด่าน 5 ด่านตามโจทย์
            return;
        }

        // ถ้าเป็นด่าน 2 ให้ลดจำนวนมอนสเตอร์ลง
        if (currentLevel == 2) {
            monstersToKill = 15; // ลดจาก 30 (20+10) เป็น 15
        } else {
            monstersToKill = 20 + (currentLevel - 1) * 10;
        }
        monstersKilled = 0;
        bossSpawned = false;
        isTransitioning = true;
        transitionTimer = 180; // เพิ่มเวลาเป็น 3 วินาที เพื่อให้มีเวลาโหลดทรัพยากรได้เพียงพอ
        levelJustChanged = true;
        needMapChange = true; // ตั้งค่าให้เปลี่ยนแผนที่

        System.out.println("กำลังเริ่มการเปลี่ยนด่านเป็นด่าน " + currentLevel);
    }

    public boolean needsMapChange() {
        if (needMapChange) {
            needMapChange = false; // รีเซ็ตค่าหลังจากอ่านแล้ว
            return true;
        }
        return false;
    }

    // เพิ่มเมธอดตรวจสอบว่ากำลัง transition หรือไม่
    public boolean isTransitioning() {
        return isTransitioning;
    }

    // เพิ่มเมธอดอัพเดท transition timer
    public void updateTransition() {
        if (isTransitioning && transitionTimer > 0) {
            transitionTimer--;
            if (transitionTimer <= 0) {
                isTransitioning = false;
                levelReadyToPlay = true;
                // อาจเพิ่มการส่งสัญญาณให้ GamePanel ขอโฟกัสกลับมา
            }
        }
    }

    public boolean isLevelReadyToPlay() {
        if (levelReadyToPlay) {
            levelReadyToPlay = false; // รีเซ็ตค่าหลังอ่านแล้ว
            return true;
        }
        return false;
    }

    // เพิ่มเมธอดสำหรับสปอนมอนสเตอร์ตามด่าน
    public Monster spawnMonsterForLevel(int[] pos, Player player) {
        if (currentLevel == 1) {
            return new Monster(pos[0], pos[1], player);
        } else {
            return new Monster2(pos[0], pos[1], player);
        }
    }

    // เพิ่มเมธอดสำหรับสปอนบอสตามด่าน
    public Enemy spawnBossForLevel(int[] pos) {
        if (currentLevel == 1) {
            return new Boss(pos[0], pos[1], currentLevel);
        } else {
            return new Boss2(pos[0], pos[1], currentLevel - 1); // ลดระดับลง 1 เพื่อสมดุล
        }
    }

    public boolean shouldSpawnBoss() {
        return monstersKilled >= monstersToKill && !bossSpawned;
    }

    public int[] getBossPosition() {
        return new int[]{GamePanel.WIDTH / 2 - 40, 50};
    }

    public void bossSpawned() {
        bossSpawned = true;
    }

    public int[] getRandomMonsterPosition() {
        // สุ่มว่าจะเกิดทางไหน (0 = ซ้าย, 1 = ขวา, 2 = บน, 3 = ล่าง)
        int side = random.nextInt(4);
        int x, y;

        switch (side) {
            case 0 -> {
                // ด้านซ้าย
                x = 0;
                y = random.nextInt(500) + 50; // ระหว่าง 50-550
            }
            case 1 -> {
                // ด้านขวา
                x = GamePanel.WIDTH - 30; // ลบด้วยความกว้างของมอนสเตอร์
                y = random.nextInt(500) + 50;
            }
            case 2 -> {
                // ด้านบน
                x = random.nextInt(700) + 50;
                y = 0;
            }
            default -> {
                // ด้านล่าง
                x = random.nextInt(700) + 50;
                y = GamePanel.HEIGHT - 30; // ลบด้วยความสูงของมอนสเตอร์
            }
        }

        return new int[]{x, y};
    }

    public boolean isLevelJustChanged() {
        boolean result = levelJustChanged;
        levelJustChanged = false; // รีเซ็ตสถานะหลังจากอ่านค่าแล้ว
        return result;
    }

    public int getMonsterSpawnInterval() {
        // ลดเวลาสปอน์มอนสเตอร์ตามระดับความยาก
        return 1000 - (currentLevel - 1) * 100; // มิลลิวินาที
    }

    public int getMonsterSpawnRate() {
        // ความถี่ในการเกิดของมอนสเตอร์ (จำนวนเฟรมระหว่างการเกิด)
        return Math.max(60 - (currentLevel * 5), 20);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getMonstersToKill() {
        return monstersToKill;
    }

    public int getMonstersKilled() {
        return monstersKilled;
    }

    public int getTransitionTimer() {
        return transitionTimer;
    }

    public LevelManager getLevelManager() {
        return this; // แก้จาก return levelManager; เป็น return this;
    }
}
