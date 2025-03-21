
import java.awt.*;

public interface GameObject {

    // อัพเดทสถานะของวัตถุ
    void update();

    // วาดกราฟิกของวัตถุ
    void render(Graphics g);

    // ขอบเขตสำหรับการตรวจสอบการชน
    Rectangle getBounds();
}
