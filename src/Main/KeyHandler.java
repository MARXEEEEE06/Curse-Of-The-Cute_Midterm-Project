import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

public class KeyHandler implements KeyListener {
    private final GamePanel gp;
    boolean upIsPressed = false;
    boolean downIsPressed = false;
    boolean leftIsPressed = false;
    boolean rightIsPressed = false;
    Timer gameTimer;

    public void startGameLoop() {
        gameTimer = new Timer(15, _ -> update());
        gameTimer.start();
    }

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        // Update combat animations if active
        if (gp.combat != null) {
            gp.combat.update();
        }

        if (upIsPressed)
            gp.moveUp();
        else if (downIsPressed)
            gp.moveDown();
        else if (leftIsPressed)
            gp.moveLeft();
        else if (rightIsPressed)
            gp.moveRight();
        else if (!upIsPressed && !downIsPressed && !leftIsPressed && !rightIsPressed)
            // player.idle();

            gp.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        System.out.println("MapX: " + gp.mapX);
        System.out.println("MapY: " + gp.mapY);
        System.out.println("PlayerX: " + gp.playerX);
        System.out.println("PlayerY: " + gp.playerY);

        // If 'M' pressed while in game, open main menu
        if (code == KeyEvent.VK_M) {
            if (gp.menu != null) gp.menu.showMenu();
            return;
        }

        // If menu is showing, allow Enter/Space to start the game and block other keys
        if (gp.menu != null && gp.menu.isShowing()) {
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                gp.menu.startGame();
            }
            return;
        }

        // Toggle combat with 'K' for testing
        if (code == KeyEvent.VK_K) {
            if (gp.combat != null) {
                if (gp.combat.isActive()) {
                    gp.combat.endCombat();
                } else {
                    gp.combat.startCombat();
                }
                gp.repaint();
            }
            return;
        }

        // Use skills during combat with 1,2,3 keys
        if (gp.combat != null && gp.combat.isActive()) {
            switch (code) {
                case KeyEvent.VK_1 -> gp.combat.onSkillPressed(1);
                case KeyEvent.VK_2 -> gp.combat.onSkillPressed(2);
                case KeyEvent.VK_3 -> gp.combat.onSkillPressed(3);
                default -> {
                }
            }
            return;
        }

        // Toggle inventory with E key
        if (code == KeyEvent.VK_E) {
            gp.inventory.toggleInventory();
            return; // Don't process other keys while opening/closing inventory
        }

        // If inventory is open, don't process movement keys
        if (gp.inventory.isOpen()) {
            return;
        }

        if (e.isShiftDown()){
            gp.setSpeed(10);
        }
        else
            gp.setSpeed(3);
        switch (code) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> upIsPressed = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> downIsPressed = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> leftIsPressed = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightIsPressed = true;
            default -> {
            }
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