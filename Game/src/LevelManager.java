
import java.util.Random;

public class LevelManager {

    private int currentLevel = 1;
    private int monstersToKill = 10;
    private int monstersKilled = 0;
    private boolean bossSpawned = false;
    private Random random = new Random();

    public LevelManager() {
        monstersToKill = 10 * currentLevel;
    }

    public void monsterKilled() {
        monstersKilled++;
    }

    public void bossKilled() {
        currentLevel++;
        monstersToKill = 10 * currentLevel;
        monstersKilled = 0;
        bossSpawned = false;
    }

    public boolean shouldSpawnBoss() {
        return monstersKilled >= monstersToKill && !bossSpawned;
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
            x = 800 - 30; // ลบด้วยความกว้างของมอนสเตอร์
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
            y = 600 - 30; // ลบด้วยความสูงของมอนสเตอร์
            }
    }
    
        return new int[]{x, y};
    }

    public int[] getBossPosition() {
        return new int[]{400 - 40, 50};
    }

    public int getMonsterSpawnRate() {
        return Math.max(60 - (currentLevel * 5), 20);
    }

    public double getDifficultyMultiplier() {
        return 1.0 + (currentLevel - 1) * 0.2;
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
