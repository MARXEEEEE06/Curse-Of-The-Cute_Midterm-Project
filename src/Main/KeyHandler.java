import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

public class KeyHandler implements KeyListener {
    private final GamePanel panel;
    boolean upIsPressed = false;
    boolean downIsPressed = false;
    boolean leftIsPressed = false;
    boolean rightIsPressed = false;
    Timer gameTimer;

    public void startGameLoop() {
        gameTimer = new Timer(30, _ -> update());
        gameTimer.start();
        panel.entitiesCollision();

    }

    public KeyHandler(GamePanel panel) {
        this.panel = panel;
    }

    public void update() {
        if (upIsPressed)
            panel.moveUp();
        if (downIsPressed)
            panel.moveDown();
        if (leftIsPressed)
            panel.moveLeft();
        if (rightIsPressed)
            panel.moveRight();
        if (!upIsPressed && !downIsPressed && !leftIsPressed && !rightIsPressed)
            // panel.idle();

            panel.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        System.out.println("PlayerX: " + panel.playerX);
        System.out.println("PlayerY: " + panel.playerY);

        if (e.isShiftDown()){
            panel.setSpeed(10);
        }
        else
            panel.setSpeed(3);
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upIsPressed = true;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downIsPressed = true;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftIsPressed = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightIsPressed = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upIsPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downIsPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftIsPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightIsPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}