import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Menu {
    private final GamePanel gp;
    private final ArrayList<BufferedImage> frames = new ArrayList<>();
    private int frameIndex = 0;
    private Timer animTimer;
    private boolean showing = true;

    public Menu(GamePanel gp) {
        this.gp = gp;
        loadFrames();
        startAnimation();
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
        // any click starts the game
        startGame();
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

            // draw hint
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.ITALIC, 14));
            String hint = "Press Enter or Click to Start";
            int hintW = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, (gp.gamePanelSizeX - hintW) / 2, y + img.getHeight() + 30);
        } else {
            // fallback menu
            g2.setColor(new Color(25, 25, 25));
            g2.fillRect(0, 0, gp.gamePanelSizeX, gp.gamePanelSizeY);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            String title = "Curse of the Cute";
            int tw = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (gp.gamePanelSizeX - tw) / 2, gp.gamePanelSizeY / 2 - 20);
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            String hint = "Press Enter or Click to Start";
            int hw = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, (gp.gamePanelSizeX - hw) / 2, gp.gamePanelSizeY / 2 + 20);
        }
    }
}
