import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class PlayerStatus {
    GamePanel gp;
    public BufferedImage currentImage;
    public final BufferedImage[] upAnimation = new BufferedImage[4];
    public final BufferedImage[] downAnimation = new BufferedImage[4];
    public final BufferedImage[] rightAnimation = new BufferedImage[4];
    public final BufferedImage[] leftAnimation = new BufferedImage[4];
    int playerSizeW;
    int playerSizeH;
    
    // Health system
    public int maxHealth = 100;
    public int currentHealth = 100;

    public PlayerStatus(GamePanel gp) {
        this.gp = gp;
        LoadPlayerSprite();
    }
    private void LoadPlayerSprite(){
        for (int i = 0; i < 4; i++) {
            try {
                currentImage = ImageIO.read(new File("res\\entities\\player\\felis_Down1.png"));
                upAnimation[i] = ImageIO.read(new File("res\\entities\\player\\felis_Up" + (i + 1) + ".png"));
                downAnimation[i] = ImageIO.read(new File("res\\entities\\player\\felis_Down" + (i + 1) + ".png"));
                leftAnimation[i] = ImageIO.read(new File("res\\entities\\player\\felis_Left" + (i + 1) + ".png"));
                rightAnimation[i] = ImageIO.read(new File("res\\entities\\player\\felis_Right" + (i + 1) + ".png"));
                playerSizeW = currentImage.getWidth();
                playerSizeH = currentImage.getHeight();
                // center character to gamepanel on start
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void draw(Graphics g){
        // draw player image (collision box visualization removed)
        if (currentImage != null) {
            g.drawImage(currentImage, gp.playerX, gp.playerY, playerSizeW, playerSizeH, null);
        }
        
        // draw health indicator
        drawHealthBar(g);
    }
    
    private void drawHealthBar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int healthBarWidth = 150;
        int healthBarHeight = 20;
        int x = 10;
        int y = 10;
        int borderThickness = 2;
        
        // Draw background (dark)
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(x, y, healthBarWidth, healthBarHeight);
        
        // Draw health (green)
        float healthPercent = (float) currentHealth / maxHealth;
        int healthWidth = (int) (healthBarWidth * healthPercent);
        g2d.setColor(new Color(0, 200, 0));
        g2d.fillRect(x + borderThickness, y + borderThickness, healthWidth - 2 * borderThickness, healthBarHeight - 2 * borderThickness);
        
        // Draw border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(borderThickness));
        g2d.drawRect(x, y, healthBarWidth, healthBarHeight);
        
        // Draw health text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(currentHealth + "/" + maxHealth, x + 5, y + 15);
    }
}
