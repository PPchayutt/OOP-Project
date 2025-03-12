
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
        int x = random.nextInt(700) + 50;
        return new int[]{x, 0};
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
