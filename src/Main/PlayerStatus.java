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
        // character
        if (currentImage != null) {
            g.setColor(Color.RED);
            g.drawRect(gp.playerCollision.x, gp.playerCollision.y, gp.playerCollision.width, gp.playerCollision.height);
            g.drawImage(currentImage, gp.playerX, gp.playerY, playerSizeW, playerSizeH, null);
        }
    }
}
