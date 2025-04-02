
public interface Damageable {

    // รับความเสียหาย
    void takeDamage(int damage);

    // ตรวจสอบว่ายังมีชีวิตอยู่หรือไม่
    boolean isAlive();

    // เมื่อพลังชีวิตหมด
    void die();
}
