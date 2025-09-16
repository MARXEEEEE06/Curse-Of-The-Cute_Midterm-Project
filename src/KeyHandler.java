import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

public class KeyHandler implements KeyListener {
    private GamePanel panel;
    boolean upIsPressed = false;
    boolean downIsPressed = false;
    boolean leftIsPressed = false;
    boolean rightIsPressed = false;
    Timer gameTimer;

    public void startGameLoop() {
        gameTimer = new Timer(20, e -> update());
        gameTimer.start();
    }

    public KeyHandler(GamePanel panel) {
        this.panel = panel;
    }

    public void update() {
        if (upIsPressed) {
            panel.moveUp();
        } else if (downIsPressed) {
            panel.moveDown();
        } else if (leftIsPressed) {
            panel.moveLeft();
        } else if (rightIsPressed) {
            panel.moveRight();
        }

        panel.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (e.isShiftDown())
            panel.setSpeed(10);
        else
            panel.setSpeed(3);
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upIsPressed = true;
            System.out.println("Up key: " + upIsPressed);
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downIsPressed = true;
            System.out.println("Down key: " + downIsPressed);
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftIsPressed = true;
            System.out.println("Left key: " + leftIsPressed);
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightIsPressed = true;
            System.out.println("Right key: " + rightIsPressed);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upIsPressed = false;
            System.out.println("Up key: " + upIsPressed);
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downIsPressed = false;
            System.out.println("Down key: " + downIsPressed);
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftIsPressed = false;
            System.out.println("Left key: " + leftIsPressed);
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightIsPressed = false;
            System.out.println("Right key: " + rightIsPressed);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
