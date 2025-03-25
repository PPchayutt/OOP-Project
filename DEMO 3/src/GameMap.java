
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private final List<Block> blocks;
    private final String mapName;
    private static final boolean DEBUG_MODE = false;

    public GameMap(String mapName) {
        this.mapName = mapName;
        blocks = new ArrayList<>();
        loadMap();
    }

    private void loadMap() {
        // ใช้ชื่อแผนที่เพื่อโหลดรูปแบบที่แตกต่างกัน
        switch (mapName) {
            case "level1" ->
                createLevel1Map();
            case "level2" -> {
            }
            default -> // สร้างแผนที่เริ่มต้น
                createDefaultMap();
        }
        // สร้างแผนที่ level 2
    }

    private void createLevel1Map() {
        // สร้างขอบของแผนที่
        blocks.add(new Block(0, 0, 800, 10)); // ขอบบน
        blocks.add(new Block(0, 590, 800, 10)); // ขอบล่าง
        blocks.add(new Block(0, 0, 10, 600)); // ขอบซ้าย
        blocks.add(new Block(790, 0, 10, 600)); // ขอบขวา

        // สร้างบล็อคน้ำตามที่เห็นในรูป
        blocks.add(new Block(220, 100, 560, 380)); // บริเวณน้ำตรงกลาง

        // เพิ่มสะพานและทางเดิน
        blocks.add(new Block(0, 300, 220, 10)); // สะพานซ้าย
        blocks.add(new Block(600, 480, 200, 10)); // สะพานล่าง

        // เพิ่มกำแพงหรือขอบต่างๆ ตามที่เห็นในรูป
        blocks.add(new Block(220, 100, 10, 200)); // กำแพงตรงกลางซ้าย
        // (เพิ่มบล็อคอื่นๆ ตามที่เห็นในรูป)
        if (DEBUG_MODE) {
            for (Block block : blocks) {
                block.setVisible(true);
            }
        }
    }

    private void createDefaultMap() {
        // สร้างขอบของแผนที่เท่านั้น
        blocks.add(new Block(0, 0, 800, 10)); // ขอบบน
        blocks.add(new Block(0, 590, 800, 10)); // ขอบล่าง
        blocks.add(new Block(0, 0, 10, 600)); // ขอบซ้าย
        blocks.add(new Block(790, 0, 10, 600)); // ขอบขวา
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public boolean checkCollision(GameObject obj) {
        for (Block block : blocks) {
            if (obj.getBounds().intersects(block.getBounds())) {
                return true;
            }
        }
        return false;
    }

    public void render(Graphics g) {
        for (Block block : blocks) {
            block.render(g);
        }
    }
}
