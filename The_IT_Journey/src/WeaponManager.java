
import java.awt.Graphics;
import java.util.*;

public class WeaponManager {

    private final List<Weapon> deployedWeapons;
    private final Map<WeaponType, List<Weapon>> availableWeapons;
    private final List<WeaponType> weaponOrder; // เก็บลำดับการได้รับอาวุธ
    private WeaponType activeWeaponType = null;  // ประเภทอาวุธที่กำลังใช้งาน
    
    public final List<WeaponType> tier2Weapon = Arrays.asList(WeaponType.AK47, WeaponType.SHOTGUN);
    public final List<WeaponType> tier3Weapon = Arrays.asList(WeaponType.GATLING_GUN, WeaponType.TURRET, WeaponType.SHURIKEN);

    public WeaponManager() {
        deployedWeapons = new ArrayList<>();
        availableWeapons = new HashMap<>();
        weaponOrder = new ArrayList<>();
    }

    public void addWeapon(WeaponType type) {
        Weapon weapon = createWeapon(type);
        if (weapon != null) {
            if (!availableWeapons.containsKey(type)) {
                // ถ้ายังไม่มีให้เพิ่มอาวุธใหม่
                ArrayList newWeapon = new ArrayList<>();
                newWeapon.add(weapon);
                availableWeapons.put(type, newWeapon);
                weaponOrder.add(type);
            } else {
                if (type.equals(WeaponType.TURRET)) {
                    // ถ้าเป็นป้อมปืนให้เพิ่มจำนวน
                    availableWeapons.get(type).add(weapon);
                } else {
                    // ถ้ามีอาวุธประเภทนี้อยู่แล้ว ให้รีเซ็ตเวลาที่เหลือ
                    Weapon existingWeapon = availableWeapons.get(type).get(0);
                    existingWeapon.resetLifespan();
                }
            }
        }
    }

    public void activateWeapon(WeaponType type) {
        // กรณีกดเลือกอาวุธเดิมที่กำลังใช้อยู่ ให้เลิกใช้
        if (activeWeaponType == type) {
            if (availableWeapons.containsKey(activeWeaponType) && !availableWeapons.get(activeWeaponType).isEmpty()) {
                availableWeapons.get(activeWeaponType).get(0).setUsing(false);
            }
            activeWeaponType = null;
            Player.resetBulletDamage();
            return; // ออกจากเมธอดเลย ไม่ต้องทำส่วนที่เหลือ
        }

        // ยกเลิกการใช้อาวุธปัจจุบัน (ถ้ามี)
        if (activeWeaponType != null && availableWeapons.containsKey(activeWeaponType)) {
            List<Weapon> weapons = availableWeapons.get(activeWeaponType);
            if (!weapons.isEmpty()) {
                Weapon weapon = weapons.get(0);
                weapon.setUsing(false);
            }
        }
        // เริ่มใช้อาวุธใหม่
        if (availableWeapons.containsKey(type) && !availableWeapons.get(type).isEmpty()) {
            availableWeapons.get(type).get(0).setUsing(true);
            activeWeaponType = type;
            Player.setBulletDamage(createWeapon(type).getDamage());
        } else {
            activeWeaponType = null;
            Player.resetBulletDamage();
        }
    }

    // วางป้อมปืน
    public void deployWeapon(WeaponType type, int x, int y) {
        if (availableWeapons.containsKey(type) && !availableWeapons.get(type).isEmpty()) {
            List<Weapon> weapons = availableWeapons.get(type);
            if (!weapons.isEmpty() && weapons.get(0).isDeployable()) {
                // สร้างอาวุธใหม่เพื่อวาง
                Weapon weapon = createWeapon(type);
                ((Turret)weapon).setLocation(x, y);
                if (weapon != null) {
                    weapon.setDeployed(true);
                    deployedWeapons.add(weapon);

                    // ลดจำนวนอาวุธที่มีอยู่
                    weapons.remove(0);
                    if (weapons.isEmpty()) {
                        // ถ้าเป็นชิ้นสุดท้าย
                        availableWeapons.remove(type);
                        weaponOrder.remove(type);
                        // ถ้าใช้อยู่ให้เลิกใช้
                        if (activeWeaponType == type) {
                            activeWeaponType = null;
                        }
                    }
                }
            }
        }
    }

    public float getWeaponLifespanPercentage(WeaponType type) {
        if (createWeapon(type).isPermanent()) {
            return 1.0f;
        }
        if (availableWeapons.containsKey(type) && !availableWeapons.get(type).isEmpty()) {
            List<Weapon> weapons = availableWeapons.get(type);
            if (!weapons.isEmpty()) {
                return weapons.get(0).getLifespanPercentage();
            }
        }
        return 0f;
    }

    public Weapon createWeapon(WeaponType type) {
        return switch (type) {
            // tier 2
            case AK47 ->
                new Weapon(20, 15, 3, true, 0.07, 140, 100, type, false, true); 
            case SHOTGUN ->
                new Weapon(30, 20, 5, false, 0.25, 450, 100, type, false, true);
            // tier 3
            case TURRET ->
                new Turret();
            case GATLING_GUN ->
                new Weapon(35, 25, 4, true, 0.12, 90, 300, type, false, false);
            case SHURIKEN ->
                new Weapon(35, 27, 3, false, 0.01, 300, 100, type, false, true);
            default ->
                null;
        };
    }

    public void update(Player player, List<Enemy> monsters, List<Boss> bosses) {
        // อัปเดตอาวุธที่วางบนพื้น
        if (!deployedWeapons.isEmpty()) {
            Iterator<Weapon> it = deployedWeapons.iterator();
            while (it.hasNext()) {
                Weapon weapon = it.next();
                weapon.update();

                // หมดเวลาให้เอาออก
                if (!weapon.isActive()) {
                    System.out.println("timeout");
                    it.remove();
                    continue;
                }

                // กรณีป้อมปืน
                if (weapon.isDeployed() && weapon instanceof Turret) {
                    List<PlayerBullet> bullets = ((Turret) weapon).shoot(monsters, bosses);
                    if (bullets != null) {
                        player.addBullets(bullets);
                    }
                }
            }
        }
        // อัปเดตอาวุธที่กำลังใช้
        if (activeWeaponType != null && availableWeapons.containsKey(activeWeaponType)) {
            List<Weapon> weapons = availableWeapons.get(activeWeaponType);
            if (!weapons.isEmpty()) {
                Weapon weapon = weapons.get(0);
                weapon.update();

                if (!weapon.isActive() && !weapon.isPermanent()) {
                    // หมดเวลาให้เอาออก
                    weapons.remove(0);

                    if (weapons.isEmpty()) {
                        availableWeapons.remove(activeWeaponType);
                        weaponOrder.remove(activeWeaponType);
                        activeWeaponType = null;
                    }
                }
            }
        }
    }

    public void clearWeapon() {
        deployedWeapons.clear();
        weaponOrder.clear();
        availableWeapons.clear();
        activeWeaponType = null;
    }

    public void clearDeployedWeapon() {
        deployedWeapons.clear();
    }

    public void render(Graphics g) {
        // วาดเฉพาะอาวุธที่ถูกวางบนพื้น
        for (Weapon weapon : deployedWeapons) {
            weapon.render(g);
        }
    }

    public boolean hasWeapon(WeaponType type) {
        return availableWeapons.containsKey(type) && !availableWeapons.get(type).isEmpty();
    }

    public int getWeaponCount(WeaponType type) {
        if (availableWeapons.containsKey(type)) {
            return availableWeapons.get(type).size();
        }
        return 0;
    }

    public List<WeaponType> getAvailableWeaponTypes() {
        return new ArrayList<>(weaponOrder);
    }

    public WeaponType getSelectedWeaponType() {
        return activeWeaponType;
    }

    // return เป็น index, ชนิดอาวุธ
    public Map<Integer, WeaponType> getAvailableWeapon() {
        Map<Integer, WeaponType> result = new HashMap<>();
        for (int i = 0; i < weaponOrder.size(); i++) {
            result.put(i, weaponOrder.get(i));
        }
        return result;
    }

    public Weapon getWeaponByIndex(int index) {
        return availableWeapons.get(weaponOrder.get(index)).get(0);
    }

    public WeaponType getActiveWeaponType() {
        return activeWeaponType;
    }
    
    public List<Weapon> getDeployedWeapon() {
        return deployedWeapons;
    }

}
