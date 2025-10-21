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
    BufferedImage pathUp;
    BufferedImage pathSide;
    BufferedImage path3Up;
    BufferedImage path3Down;
    BufferedImage path3Left;
    BufferedImage path3Right;
    BufferedImage pathCross;
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
            pathUp = ImageIO.read(new File("res\\maps\\tiles\\path\\verticalPath.png"));
            pathSide = ImageIO.read(new File("res\\maps\\tiles\\path\\sidePath.png"));
            path3Up = ImageIO.read(new File("res\\maps\\tiles\\path\\threeWayUp.png")); 
            path3Down = ImageIO.read(new File("res\\maps\\tiles\\path\\threeWayDown.png"));
            path3Left = ImageIO.read(new File("res\\maps\\tiles\\path\\threeWayLeft.png"));
            path3Right = ImageIO.read(new File("res\\maps\\tiles\\path\\threeWayRight.png"));
            pathCross = ImageIO.read(new File("res\\maps\\tiles\\path\\crossPath.png"));
            tree1 = ImageIO.read(new File("res\\maps\\objects\\tree1.png"));
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
        //<----pathways---->
        g.drawImage(path3Up, (gp.tileSize*25) - gp.mapX, (gp.tileSize*25) - gp.mapY,
            gp.tileSize, gp.tileSize, null);
        g.drawImage(path3Left, (gp.tileSize*30) - gp.mapX, (gp.tileSize*25) - gp.mapY,
            gp.tileSize, gp.tileSize, null);
        for (int i = 12; i < 25; i++) {
            g.drawImage(pathSide, (gp.tileSize*i) - gp.mapX, (gp.tileSize*25) - gp.mapY,
                gp.tileSize, gp.tileSize, null);
        }for (int i = 26; i < 30; i++) {
            g.drawImage(pathSide, (gp.tileSize*i) - gp.mapX, (gp.tileSize*25) - gp.mapY,
                gp.tileSize, gp.tileSize, null);
        }
        for (int i = 26; i < 37; i++) {
            g.drawImage(pathUp, (gp.tileSize*30) - gp.mapX, (gp.tileSize*i) - gp.mapY,
                gp.tileSize, gp.tileSize, null);
        }
        //<----pathways---->
        gp.borderCollision.setBounds((gp.tileSize*15)-gp.mapX, (gp.tileSize*8)-gp.mapY, gp.tileSize, gp.tileSize);
        g.drawRect((gp.tileSize*15)-gp.mapX, (gp.tileSize*8)-gp.mapY, gp.tileSize, gp.tileSize);

        gp.tree1Collision.setBounds((gp.tileSize*15)-gp.mapX, (gp.tileSize*16)-gp.mapY, gp.tileSize, gp.tileSize);
        g.drawRect((gp.tileSize*15)-gp.mapX, (gp.tileSize*16)-gp.mapY, gp.tree1Collision.width, gp.tree1Collision.height);
        g.drawImage(tree1, (gp.tileSize*15)-gp.mapX, (gp.tileSize*15)-gp.mapY, tree1.getWidth(), tree1.getHeight(), null);
    }
}