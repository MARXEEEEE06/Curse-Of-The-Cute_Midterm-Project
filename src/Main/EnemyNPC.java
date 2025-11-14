import java.awt.*;
import java.awt.image.*;
import java.io.File;
import javax.imageio.ImageIO;

public class EnemyNPC {
    private final GamePanel gp;
    private BufferedImage enemyImage = null;
    private int mapX = 0;
    private int mapY = 0;
    
    public EnemyNPC(GamePanel gp) {
        this.gp = gp;
        loadEnemyImage();
    }
    
    private void loadEnemyImage() {
        try {
            File imageFile = new File("res/Entities/Combat/enemy/lightbrowndog.png");
            if (imageFile.exists()) {
                enemyImage = ImageIO.read(imageFile);
                System.out.println("EnemyNPC: loaded lightbrowndog.png (" + enemyImage.getWidth() + "x" + enemyImage.getHeight() + ")");
            } else {
                System.out.println("EnemyNPC: enemy image not found at " + imageFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            System.out.println("EnemyNPC: failed to load enemy image - " + ex.getMessage());
        }
    }
    
    public void setMapPosition(int x, int y) {
        this.mapX = x;
        this.mapY = y;
    }
    
    public int getMapX() {
        return mapX;
    }
    
    public int getMapY() {
        return mapY;
    }
    
    public Rectangle getScreenRect() {
        if (enemyImage == null) return new Rectangle(0, 0, 0, 0);
        int screenX = mapX - gp.mapX;
        int screenY = mapY - gp.mapY;
        return new Rectangle(screenX, screenY, enemyImage.getWidth(), enemyImage.getHeight());
    }
    
    public void draw(Graphics2D g2) {
        if (enemyImage == null) return;
        
        int screenX = mapX - gp.mapX;
        int screenY = mapY - gp.mapY;
        
        // Only draw if on screen
        if (screenX + enemyImage.getWidth() < 0 || screenX > gp.gamePanelSizeX ||
            screenY + enemyImage.getHeight() < 0 || screenY > gp.gamePanelSizeY) {
            return;
        }
        
        g2.drawImage(enemyImage, screenX, screenY, null);
    }
}
