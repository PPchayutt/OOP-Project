
public enum WeaponType {
    AK47(1, "Ak47"),
    SHOTGUN(2, "Shotgun"),
    TURRET(3, "Turret"),
    GATLING_GUN(4, "Gatling Gun"),
    SHURIKEN(5, "Shuriken");

    private final int id;
    private final String name;

    WeaponType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static WeaponType getById(int id) {
        for (WeaponType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
