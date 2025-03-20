
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bird Eye View Shooter");
            GamePanel gamePanel = new GamePanel();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);

            frame.add(gamePanel);

            frame.pack();
            frame.setSize(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT);
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);

            SwingUtilities.invokeLater(() -> {
                gamePanel.setupGame();
                gamePanel.requestFocus();
                gamePanel.startGame();
            });

            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    gamePanel.requestFocus();
                }
            });

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gamePanel.stopGame();
                }
            });
        });
    }
}
