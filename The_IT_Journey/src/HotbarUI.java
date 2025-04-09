
import java.awt.*;
import java.util.*;

public final class HotbarUI {

    private final int original_width = 240;
    private final int original_height = 40;

    private final double multiplier = 1.9; // << ถ้าจะปรับขนาดให้ปรับตรงนี้
    // ขนาดบนจอ
    private final int width = (int) (original_width * multiplier);
    private final int height = (int) (original_height * multiplier);
    // ตำแหน่งบนจอ (กลาง ล่าง)
    private final int x = GamePanel.WIDTH / 2 - (width / 2);
    private final int y = GamePanel.HEIGHT - (height + (int) (20 * multiplier));
    private final int MAX_SLOTS = 8;
    // ขนาดช่อง สำหรับ class HotbarSlot
    private final int margin_x = (int) (10 * multiplier); // เว้นขอบซ้าย/ขวา
    private final int margin_y = (int) (7 * multiplier);  // เว้นขอบบน/ล่าง
    private final int padding = (int) (2 * multiplier);
    private final int total_padding = padding * (MAX_SLOTS - 1);
    private final int slot_width = (int) ((width - (margin_x * 2) - total_padding) / MAX_SLOTS);
    private final int slot_height = (int) (height - (margin_y * 2));

    private final WeaponManager weaponManager;
    private final ArrayList slots;
    private final Image hotbarImage;

    private final boolean debugging = false;

    public HotbarUI(Player player) {
        this.slots = new ArrayList();
        this.hotbarImage = ImageManager.getImage("hotbar");
        this.weaponManager = GamePanel.getWeaponManager();

        // สร้างช่อง Hotbar
        updateSlots();

        //debug();
    }

    public void updateSlots() {
        slots.clear();
        Map<Integer, WeaponType> weapons = weaponManager.getAvailableWeapon();
        WeaponType activeWeaponType = weaponManager.getActiveWeaponType();

        int startX = x + margin_x;
        int startY = y + margin_y;

        for (int i = 0; i < weapons.size(); i++) {
            int slotX = startX + i * (slot_width + padding);
            int slotY = startY;
            WeaponType weaponType = (WeaponType) weapons.get(i);
            HotbarSlot slot = new HotbarSlot(slotX, slotY, weaponType, slot_width, slot_height, multiplier);
            slot.setActive(activeWeaponType == weaponType);
            // กำหนดเวลาที่เหลือสำหรับทุกอาวุธ
            float lifespan = weaponManager.getWeaponLifespanPercentage(weaponType);
            slot.setLifespan(lifespan);
            slots.add(slot);
        }
    }

    public void render(Graphics g) {
        // วาดพื้นหลังของ Hotbar
        if (hotbarImage != null) {
            g.drawImage(hotbarImage, x, y, width, height, null);
        }

        WeaponType activeWeaponType = weaponManager.getActiveWeaponType();

        if (debugging) {
            // ตีกรอบไว้ดูระยะ hotbar ตอน debug
            g.setColor(Color.RED);
            g.drawRect(x, y, width, height);
            g.setColor(Color.GREEN);
            g.drawLine(GamePanel.WIDTH / 2, y, GamePanel.WIDTH / 2, y + height); // แสดงจุดกึ่งกลางหน้าจอ
        }

        for (int i = 0; i < slots.size(); i++) {
            HotbarSlot slot = (HotbarSlot) slots.get(i);
            WeaponType slotType = slot.getWeaponType();
            slot.setActive(slotType == activeWeaponType);
        }

        // วาดแต่ละช่อง
        for (int i = 0; i < slots.size(); i++) {
            HotbarSlot slot = (HotbarSlot) slots.get(i);
            WeaponType type = slot.getWeaponType();
            int amount = weaponManager.getWeaponCount(type);
            slot.render(g, amount);
        }
    }

    public void updateSelection(int selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < slots.size()) {
            for (int i = 0; i < slots.size(); i++) {
                HotbarSlot slot = (HotbarSlot) slots.get(i);
                slot.setActive(i == selectedIndex);
            }
        }
    }

    public double getMultiplier() {
        return multiplier;
    }
}
