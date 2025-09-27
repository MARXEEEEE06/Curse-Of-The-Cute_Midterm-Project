import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    PlayerStatus ps = new PlayerStatus(this);
    public int speed = 5;
    public int whatFrame = 0;
    public final int frameSpeed = 3; // higher = slower animation
    public int frameDelay = 0; // counts frames for delay
    public int playerX = 0;//((tileRow/tileSize) - playerSizeW) / 2;
    public int playerY = 0;//((tileRow/tileSize) - playerSizeH) / 2;
    public int playerPX = playerX, playerPY = playerY;
    // tile
    public int tileRow = 50; // rows of grid
    public int tileCol = 50; // columns of grid
    public int baseLayerGrid[][] = new int[tileRow][tileCol];
    public int objectLayerGrid[][] = new int[tileRow][tileCol];
    tilesManager tiles = new tilesManager(this);
    // map
    public final int tileSize = 32;
    public final int gamePanelSizeX = 800;
    public final int gamePanelSizeY = 600;
    public int mapX = playerX*2;
    public int mapY = playerY*2;
    @SuppressWarnings("unused")
    private final int scaleMultiplier = 2;
    public BufferedImage grassTile;
    public BufferedImage tree1;
    // collisions
    Collisions playerCollision;
    Collisions tree1Collision;
    @SuppressWarnings("unused")
    Collisions rockCollision;
    @SuppressWarnings("unused")
    Collisions structureCollision;

    public void entitiesCollision() {
        playerCollision = new Collisions(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        tree1Collision = new Collisions(-64, -64, tileSize, tileSize);
        //rockCollision = new Collisions(0, 0, tileSize, tileSize);
        //structureCollision = new Collisions(0, 0, tileSize * 2, tileSize * 2);
    }

    public GamePanel() {
        loadSprites();
        KeyHandler keyH = new KeyHandler(this);
        this.setFocusable(true);
        this.addKeyListener(keyH);
        this.setPreferredSize(new Dimension(gamePanelSizeX, gamePanelSizeY));
        this.setBackground(Color.gray);
        keyH.startGameLoop();
        playerX = (gamePanelSizeX-ps.playerSizeW)/2;
        playerY = (gamePanelSizeY-ps.playerSizeH)/2;
    };

    // character movement
    public void moveUp() {
        // map border collision
        if (playerCollision.intersects(tree1Collision)) {
            System.out.println("Collision Detected");
            mapY = playerPY;
            playerCollision.setBounds(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        } else {
            mapY -= speed;
            playerPY = mapY + 10;
            playerCollision.setBounds(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        }
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            ps.currentImage = ps.upAnimation[whatFrame];
            frameDelay = 0;
        }

        repaint();
    }

    public void moveDown() {
        if (playerCollision.intersects(tree1Collision)) {
            System.out.println("Collision Detected");
            mapY = playerPY;
            playerCollision.setBounds(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        } else {
            mapY += speed;
            playerPY = mapY - 10;
            playerCollision.setBounds(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        }

        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            ps.currentImage = ps.downAnimation[whatFrame];
            frameDelay = 0;
        }

        repaint();
    }

    public void moveLeft() {
        if (playerCollision.intersects(tree1Collision)) {
            System.out.println("Collision Detected");
            mapX = playerPX;
            playerCollision.setBounds(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        } else {
            mapX -= speed;
            playerPX = mapX + 10;
            playerCollision.setBounds(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        }
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            ps.currentImage = ps.leftAnimation[whatFrame];
            frameDelay = 0;
        }
        repaint();
    }

    public void moveRight() {
        if (playerCollision.intersects(tree1Collision)) {
            System.out.println("Collision Detected");
            mapX = playerPX;
            playerCollision.setBounds(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        } else {
            mapX += speed;
            playerPX = mapX - 10;
            playerCollision.setBounds(playerX + 15, playerY, ps.playerSizeW / 2, ps.playerSizeH);
        }
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            ps.currentImage = ps.rightAnimation[whatFrame];
            frameDelay = 0;
        }
        repaint();
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // sprite
    @SuppressWarnings({ "CallToPrintStackTrace", "UseSpecificCatch" })
    private void loadSprites() {
        System.out.println("ps.playerSizeW: " + ps.playerSizeW);
        System.out.println("ps.playerSizeH: " + ps.playerSizeH);
        System.out.println("playerX: " + playerX);
        System.out.println("playerY: " + playerY);
        try {
            System.out.println("Map Width: " + tileCol * tileSize);
            System.out.println("Map Height: " + tileRow * tileSize);
            System.out.println("MapX: " + mapX);
            System.out.println("MapY: " + mapY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        tiles.draw(g);
        ps.draw(g);
        ////gridLines
        // for (int x = 0; x < (tileRow * tileSize); x += tileSize) {
        //     g.setColor(Color.red); // Vertical lines
        //     g.drawLine(x - mapX, 0 - mapY, x - mapX, (tileCol * tileSize) - mapY);
        // }
        // for (int y = 0; y <= (tileCol * tileSize); y += tileSize) {
        //     g.setColor(Color.red);// Horizontal lines
        //     g.drawLine(0 - mapX, y - mapY, (tileCol * tileSize) - mapX, y - mapY); 
        // }
        // g.setColor(Color.green);
        // g.drawLine(0, gamePanelSizeY / 2, gamePanelSizeX, gamePanelSizeY / 2);
        // g.setColor(Color.green);
        // g.drawLine(gamePanelSizeX / 2, gamePanelSizeY, gamePanelSizeX / 2, 0);

        // //grid coordinates
        // g.setColor(Color.white);
        // for (int row = 0; row < tileCol; row++) {
        //     for (int col = 0; col < tileRow; col++) {
        //         int drawX = (col * tileSize) - mapX;
        //         int drawY = (row * tileSize) - mapY + 12;

        //         g.setColor(Color.white);
        //         g.drawString(col+","+row, drawX, drawY);
        //     }
        // }
    }
}