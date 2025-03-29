
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private final List<Block> blocks;
    private String mapName;
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
            case "level2" ->
                createLevel2Map(); // เพิ่มการเรียกเมธอดนี้
            default -> // สร้างแผนที่เริ่มต้น
                createDefaultMap();
        }
    }

    private void createLevel1Map() {
        // สร้างขอบของแผนที่
        blocks.add(new Block(0, 0, 800, 10)); // ขอบบน
        blocks.add(new Block(0, 550, 800, 50)); // ขอบล่าง
        blocks.add(new Block(0, 0, 10, 600)); // ขอบซ้าย
        blocks.add(new Block(770, 0, 30, 600)); // ขอบขวา

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
        blocks.add(new Block(0, 550, 800, 50)); // ขอบล่าง
        blocks.add(new Block(0, 0, 10, 600)); // ขอบซ้าย
        blocks.add(new Block(770, 0, 30, 600)); // ขอบขวา
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
    // เพิ่มเมธอดใหม่สำหรับสร้างแมพด่าน 2

    private void createLevel2Map() {
        System.out.println("กำลังสร้างแผนที่ด่าน 2...");

        try {
            // สร้างขอบของแผนที่ (ใช้ซ้ำกับทุกด่าน)
            blocks.add(new Block(0, 0, 800, 10));     // ขอบบน
            blocks.add(new Block(0, 550, 800, 50));   // ขอบล่าง
            blocks.add(new Block(0, 0, 10, 600));     // ขอบซ้าย
            blocks.add(new Block(770, 0, 30, 600));   // ขอบขวา

            // สร้างสิ่งกีดขวางภายในฉาก
            blocks.add(new Block(150, 120, 150, 90)); // โต๊ะเอียงด้านซ้ายบน
            blocks.add(new Block(120, 430, 80, 40));  // โต๊ะด้านล่างซ้าย
            blocks.add(new Block(350, 200, 100, 50)); // โต๊ะตรงกลางด้านบน
            blocks.add(new Block(300, 400, 60, 30));  // โต๊ะตรงกลางด้านล่าง
            blocks.add(new Block(600, 250, 50, 100)); // โต๊ะด้านขวา

            // จุดกลมด้านล่างซ้าย (อาจเป็นเก้าอี้)
            blocks.add(new Block(50, 550, 30, 30));

            // จุดกลมด้านขวาบน (อาจเป็นถังหรือของตกแต่ง)
            blocks.add(new Block(650, 100, 30, 30));

            if (DEBUG_MODE) {
                for (Block block : blocks) {
                    block.setVisible(true);
                }
            }

            System.out.println("สร้างแผนที่ด่าน 2 สำเร็จ: " + blocks.size() + " บล็อก");
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการสร้างแผนที่ด่าน 2: " + e.getMessage());
            e.printStackTrace();
            // สร้างแผนที่เริ่มต้นเพื่อไม่ให้เกมพัง
            createDefaultMap();
        }
    }

    // แก้ไขเมธอด getName() ใน GameMap.java เป็น
    public Object getName() {
        return mapName; // ส่งค่า mapName กลับไปเป็น Object
    }

    // เพิ่มเมธอดใหม่ใน GameMap.java
    public void changeMap(String newMapName) {
        this.mapName = newMapName;
        blocks.clear(); // ล้างบล็อกเก่าทั้งหมด
        loadMap(); // โหลดแผนที่ใหม่
    }
}
