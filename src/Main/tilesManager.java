import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class tilesManager {
    GamePanel gp;
    BufferedImage grassTile;
    BufferedImage grassTopLeft;
    BufferedImage grassTopRight;
    BufferedImage grassTop;
    BufferedImage grassLeft;
    BufferedImage grassRight;
    BufferedImage grassBottomLeft;
    BufferedImage grassBottomRight;
    BufferedImage grassBottom;
    BufferedImage tree1;

    public tilesManager(GamePanel gp) {
        this.gp = gp;
        loadTileSprite();
    }

    public void loadTileSprite() {

        try {
            grassTile = ImageIO.read(new File("res\\maps\\tiles\\grass1.png"));
            grassTop = ImageIO.read(new File("res\\maps\\tiles\\border\\grassTopCorner.png"));
            grassTopLeft = ImageIO.read(new File("res\\maps\\tiles\\border\\grassTopLeftCorner.png"));
            grassTopRight = ImageIO.read(new File("res\\maps\\tiles\\border\\grassTopRightCorner.png"));
            grassLeft = ImageIO.read(new File("res\\maps\\tiles\\border\\grassLeftCorner.png"));
            grassRight = ImageIO.read(new File("res\\maps\\tiles\\border\\grassRightCorner.png"));
            grassBottom = ImageIO.read(new File("res\\maps\\tiles\\border\\grassBottomCorner.png"));
            grassBottomLeft = ImageIO.read(new File("res\\maps\\tiles\\border\\grassBottomLeftCorner.png"));
            grassBottomRight = ImageIO.read(new File("res\\maps\\tiles\\border\\grassBottomRightCorner.png"));
            tree1 = ImageIO.read(new File("res\\Maps\\tree1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        gp.entitiesCollision();
        for (int i = 0; i < gp.tileRow; i++) {
            for (int j = 0; j < gp.tileCol; j++) {
                // <-----Base Layer----->
                g.drawImage(grassTile, (i * 32) - gp.mapX, (j * 32) - gp.mapY,
                    gp.tileSize, gp.tileSize, null);
            }
        }
        //Draw map border
        for(int i=12;i<gp.tileRow-12;i++){
            g.drawImage(grassTop, (gp.tileSize*i) - gp.mapX, (gp.tileSize*8) - gp.mapY,
                gp.tileSize, gp.tileSize, null);

            g.drawImage(grassBottom, (gp.tileSize*i) - gp.mapX, (gp.tileSize*41) - gp.mapY,
                gp.tileSize, gp.tileSize, null);
        }
        for(int i=9;i<gp.tileCol-9;i++){
            g.drawImage(grassLeft, (gp.tileSize*11) - gp.mapX, (gp.tileSize*i) - gp.mapY,
                gp.tileSize, gp.tileSize, null);
            g.drawImage(grassRight, (gp.tileSize*38) - gp.mapX, (gp.tileSize*i) - gp.mapY,
                gp.tileSize, gp.tileSize, null);
        }
        g.drawImage(grassTopLeft, (gp.tileSize*11) - gp.mapX, (gp.tileSize*8) - gp.mapY,
            gp.tileSize, gp.tileSize, null);
        g.drawImage(grassBottomLeft, (gp.tileSize*11) - gp.mapX, (gp.tileSize*41) - gp.mapY,
            gp.tileSize, gp.tileSize, null);
        g.drawImage(grassTopRight, (gp.tileSize*38) - gp.mapX, (gp.tileSize*8) - gp.mapY,
            gp.tileSize, gp.tileSize, null);
        g.drawImage(grassBottomRight, (gp.tileSize*38) - gp.mapX, (gp.tileSize*41) - gp.mapY,
            gp.tileSize, gp.tileSize, null);
        //<----map border---->
        //<----trees---->
        g.drawImage(grassBottomRight, (gp.tileSize*38) - gp.mapX, (gp.tileSize*41) - gp.mapY,
            gp.tileSize, gp.tileSize, null);
        //<----trees---->
    }
}