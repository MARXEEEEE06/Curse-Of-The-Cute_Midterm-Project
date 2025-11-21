import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Menu {
    private final GamePanel gp;
    private final JFrame window;
    private final ArrayList<BufferedImage> frames = new ArrayList<>();
    private int frameIndex = 0;
    private Timer animTimer;
    private boolean showing = true;
    private Rectangle startButton;
    private Rectangle quitButton;

    public Menu(GamePanel gp, JFrame window) {
        this.gp = gp;
        this.window = window;
        loadFrames();
        startAnimation();
        // Define button positions based on sprite positions, offset by image centering
        if (!frames.isEmpty()) {
            BufferedImage img = frames.get(0);
            int imgW = img.getWidth();
            int imgH = img.getHeight();
            int offsetX = (gp.gamePanelSizeX - imgW) / 2;
            int offsetY = (gp.gamePanelSizeY - imgH) / 2;
            startButton = new Rectangle(offsetX + 303, offsetY + 312, 180, 49);
            quitButton = new Rectangle(offsetX + 303, offsetY + 512, 180, 49);
        } else {
            // Fallback positions
            int buttonWidth = 200;
            int buttonHeight = 50;
            int buttonY = gp.gamePanelSizeY - 100;
            startButton = new Rectangle(100, buttonY, buttonWidth, buttonHeight);
            quitButton = new Rectangle(gp.gamePanelSizeX - 300, buttonY, buttonWidth, buttonHeight);
        }
    }

    private void loadFrames() {
        // Try loading mainmenu1..20.png from res/Entities/UI
        for (int i = 1; i <= 20; i++) {
            try {
                File f = new File("res/Entities/UI/mainmenu" + i + ".png");
                if (f.exists()) {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) frames.add(img);
                }
            } catch (Exception e) {
                // ignore single-frame failures
            }
        }
    }

    private void startAnimation() {
        if (frames.size() <= 1) return;
        animTimer = new Timer(100, e -> {
            frameIndex = (frameIndex + 1) % frames.size();
            gp.repaint();
        });
        animTimer.start();
    }

    public boolean isShowing() {
        return showing;
    }

    public void startGame() {
        showing = false;
        if (animTimer != null) animTimer.stop();
        // give a single repaint to switch to game view
        gp.repaint();
    }

    /**
     * Show the menu again (e.g., when pressing ESC)
     */
    public void showMenu() {
        showing = true;
        frameIndex = 0;
        if (animTimer == null) startAnimation();
        else if (animTimer != null && frames.size() > 1) animTimer.start();
        gp.repaint();
    }

    public void onMouseClicked(int x, int y) {
        if (startButton.contains(x, y)) {
            startGame();
        } else if (quitButton.contains(x, y)) {
            System.exit(0);
        }
    }

    public void onMouseMoved(int x, int y) {
        if (startButton.contains(x, y) || quitButton.contains(x, y)) {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            gp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.gamePanelSizeX, gp.gamePanelSizeY);

        if (!frames.isEmpty()) {
            BufferedImage img = frames.get(frameIndex);
            int x = (gp.gamePanelSizeX - img.getWidth()) / 2;
            int y = (gp.gamePanelSizeY - img.getHeight()) / 2;
            g2.drawImage(img, x, y, null);
        } else {
            // fallback menu
            g2.setColor(new Color(25, 25, 25));
            g2.fillRect(0, 0, gp.gamePanelSizeX, gp.gamePanelSizeY);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            String title = "Curse of the Cute";
            int tw = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (gp.gamePanelSizeX - tw) / 2, gp.gamePanelSizeY / 2 - 20);

            // Draw Start button in fallback
            g2.setColor(Color.GREEN);
            g2.fillRect(startButton.x, startButton.y, startButton.width, startButton.height);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String startText = "Start";
            int startTextW = g2.getFontMetrics().stringWidth(startText);
            int startTextH = g2.getFontMetrics().getAscent();
            g2.drawString(startText, startButton.x + (startButton.width - startTextW) / 2, startButton.y + (startButton.height + startTextH) / 2);

            // Draw Quit button in fallback
            g2.setColor(Color.RED);
            g2.fillRect(quitButton.x, quitButton.y, quitButton.width, quitButton.height);
            g2.setColor(Color.BLACK);
            String quitText = "Quit";
            int quitTextW = g2.getFontMetrics().stringWidth(quitText);
            int quitTextH = g2.getFontMetrics().getAscent();
            g2.drawString(quitText, quitButton.x + (quitButton.width - quitTextW) / 2, quitButton.y + (quitButton.height + quitTextH) / 2);
        }
    }
}
