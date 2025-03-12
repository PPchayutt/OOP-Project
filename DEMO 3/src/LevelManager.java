import java.util.Random;

public class LevelManager {

    private int currentLevel = 1;
    private int monstersToKill;
    private int monstersKilled = 0;
    private boolean bossSpawned = false;
    private static final Random random = new Random();
    
    public LevelManager() {
        monstersToKill = 20 + (currentLevel - 1) * 5;
    }
    
    public void monsterKilled() {
        monstersKilled++;
    }
    
    public void bossKilled() {
        currentLevel++;
        if (currentLevel > 5) {
            // เล่นจบเกม (จัดการเมื่อชนะเกม)
        }
        monstersToKill = 20 + (currentLevel - 1) * 5;
        monstersKilled = 0;
        bossSpawned = false;
    }
    
    public boolean shouldSpawnBoss() {
        return monstersKilled >= monstersToKill && !bossSpawned;
    }
    
    public void bossSpawned() {
        bossSpawned = true;
    }
    
    public int getMonsterSpawnInterval() {
        // ลดเวลาสปอน์มอนสเตอร์ตามระดับความยาก
        return 1000 - (currentLevel - 1) * 100; // มิลลิวินาที
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
}