
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
        // โหลดแผนที่
        switch (mapName) {
            case "level1" ->
                createLevel1Map();
            case "level2" ->
                createLevel2Map();
            case "level3" ->
                createLevel3Map();
            case "level4" ->
                createLevel4Map();
            case "level5" ->
                createLevel5Map();
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

        blocks.add(new Block(220, 100, 560, 380));

        // เพิ่มสะพานและทางเดิน
        blocks.add(new Block(0, 300, 220, 10));
        blocks.add(new Block(600, 480, 200, 10));

        blocks.add(new Block(220, 100, 10, 200));
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

    private void createLevel2Map() {
        System.out.println("กำลังสร้างแผนที่ด่าน 2...");

        try {
            // ล้างบล็อกเก่าก่อนเพื่อป้องกันการซ้อนทับ
            blocks.clear();

            // สร้างขอบของแผนที่ (ใช้ซ้ำกับทุกด่าน)
            blocks.add(new Block(0, 0, 800, 10));     // ขอบบน
            blocks.add(new Block(0, 550, 800, 50));   // ขอบล่าง
            blocks.add(new Block(0, 0, 10, 600));     // ขอบซ้าย
            blocks.add(new Block(770, 0, 30, 600));   // ขอบขวา

            blocks.add(new Block(150, 120, 150, 90));
            blocks.add(new Block(120, 430, 80, 40));
            blocks.add(new Block(350, 200, 100, 50));
            blocks.add(new Block(300, 400, 60, 30));
            blocks.add(new Block(600, 250, 50, 100));

            if (DEBUG_MODE) {
                for (Block block : blocks) {
                    block.setVisible(true);
                }
            }

            System.out.println("สร้างแผนที่ด่าน 2 สำเร็จ: " + blocks.size() + " บล็อก");
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการสร้างแผนที่ด่าน 2: " + e.getMessage());
            createDefaultMap();
        }
    }

    private void createLevel3Map() {
        System.out.println("กำลังสร้างแผนที่ด่าน 3...");

        try {
            blocks.clear();

            // สร้างขอบของแผนที่
            blocks.add(new Block(0, 0, 800, 10));     // ขอบบน
            blocks.add(new Block(0, 550, 800, 50));   // ขอบล่าง
            blocks.add(new Block(0, 0, 10, 600));     // ขอบซ้าย
            blocks.add(new Block(770, 0, 30, 600));   // ขอบขวา

            blocks.add(new Block(150, 120, 200, 90));
            blocks.add(new Block(380, 180, 120, 70));
            blocks.add(new Block(550, 250, 60, 120));
            blocks.add(new Block(100, 350, 150, 100));
            blocks.add(new Block(300, 400, 60, 30));
            blocks.add(new Block(700, 350, 60, 60));

            for (int x = 600; x < 800; x += 30) {
                for (int y = 0; y < 320; y += 30) {
                    if (Math.random() < 0.25) {
                        blocks.add(new Block(x, y, 30, 30));
                    }
                }
            }

            if (DEBUG_MODE) {
                for (Block block : blocks) {
                    block.setVisible(true);
                }
            }

            System.out.println("สร้างแผนที่ด่าน 3 สำเร็จ: " + blocks.size() + " บล็อก");
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการสร้างแผนที่ด่าน 3: " + e.getMessage());
            createDefaultMap();
        }
    }

    private void createLevel4Map() {
        System.out.println("กำลังสร้างแผนที่ด่าน 4...");

        try {
            blocks.clear();

            // สร้างขอบของแผนที่
            blocks.add(new Block(0, 0, 800, 10));     // ขอบบน
            blocks.add(new Block(0, 550, 800, 50));   // ขอบล่าง
            blocks.add(new Block(0, 0, 10, 600));     // ขอบซ้าย
            blocks.add(new Block(770, 0, 30, 600));   // ขอบขวา

            blocks.add(new Block(300, 150, 200, 80));

            blocks.add(new Block(100, 80, 120, 60));

            blocks.add(new Block(600, 80, 80, 160));

            blocks.add(new Block(50, 300, 70, 40));

            blocks.add(new Block(650, 300, 70, 40));

            blocks.add(new Block(150, 430, 500, 30));

            if (DEBUG_MODE) {
                for (Block block : blocks) {
                    block.setVisible(true);
                }
            }

            System.out.println("สร้างแผนที่ด่าน 4 สำเร็จ: " + blocks.size() + " บล็อก");
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการสร้างแผนที่ด่าน 4: " + e.getMessage());
            createDefaultMap();
        }
    }

    private void createLevel5Map() {
        System.out.println("กำลังสร้างแผนที่ด่าน 5...");

        try {
            blocks.clear();

            // สร้างขอบของแผนที่
            blocks.add(new Block(0, 0, 800, 10));     // ขอบบน
            blocks.add(new Block(0, 550, 800, 50));   // ขอบล่าง
            blocks.add(new Block(0, 0, 10, 600));     // ขอบซ้าย
            blocks.add(new Block(770, 0, 30, 600));   // ขอบขวา

            blocks.add(new Block(350, 200, 100, 100));

            blocks.add(new Block(150, 100, 40, 40));

            blocks.add(new Block(600, 100, 40, 40));

            blocks.add(new Block(150, 400, 40, 40));

            blocks.add(new Block(600, 400, 40, 40));

            blocks.add(new Block(220, 170, 60, 60));

            blocks.add(new Block(520, 170, 60, 60));

            blocks.add(new Block(220, 370, 60, 60));

            blocks.add(new Block(520, 370, 60, 60));

            if (DEBUG_MODE) {
                for (Block block : blocks) {
                    block.setVisible(true);
                }
            }

            System.out.println("สร้างแผนที่ด่าน 5 สำเร็จ: " + blocks.size() + " บล็อก");
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการสร้างแผนที่ด่าน 5: " + e.getMessage());
            createDefaultMap();
        }
    }

    public Object getName() {
        return mapName; // ส่งค่า mapName กลับไปเป็น Object
    }

    public void changeMap(String newMapName) {
        this.mapName = newMapName;
        blocks.clear(); // ล้างบล็อกเก่าทั้งหมด
        loadMap(); // โหลดแผนที่ใหม่
    }
}
