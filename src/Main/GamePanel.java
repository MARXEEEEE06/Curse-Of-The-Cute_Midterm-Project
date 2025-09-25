import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel {
    // movement
    private int speed = 5;
    private int whatFrame = 0;
    private final int frameSpeed = 3; // higher = slower animation
    private int frameDelay = 0; // counts frames for delay
    public int playerX = 0, playerY = 0;
    public int playerPX = playerX, playerPY = playerY;
    private BufferedImage currentImage;
    private final BufferedImage[] upAnimation = new BufferedImage[4];
    private final BufferedImage[] downAnimation = new BufferedImage[4];
    private final BufferedImage[] leftAnimation = new BufferedImage[4];
    private final BufferedImage[] rightAnimation = new BufferedImage[4];
    // tile
    public int tileRow = 50; // rows of grid
    public int tileCol = 50; // columns of grid
    public int baseLayerGrid[][] = new int[tileRow][tileCol];
    public int objectLayerGrid[][] = new int[tileRow][tileCol];
    tilesManager tiles = new tilesManager(this);
    // map
    public final int tileSize = 32;
    private final int gamePanelSizeX = 800;
    private final int gamePanelSizeY = 600;
    @SuppressWarnings("unused")
    private final int scaleMultiplier = 2;
    public BufferedImage grassTile;
    public BufferedImage tree1;
    // player
    int playerSizeW;
    int playerSizeH;
    Collisions playerCollision;
    Collisions tree1Collision;
    @SuppressWarnings("unused")
    Collisions rockCollision;
    @SuppressWarnings("unused")
    Collisions structureCollision;

    public void entitiesCollision() {
        playerCollision = new Collisions(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
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
    };

    // character movement
    public void moveUp() {
        // map border collision
        if (playerCollision.intersects(tree1Collision)) {
            System.out.println("Collision Detected");
            playerY = playerPY;
            playerCollision.setBounds(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
        } else {
            playerY -= speed;
            playerPY = playerY + 10;
            playerCollision.setBounds(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
        }
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            currentImage = upAnimation[whatFrame];
            frameDelay = 0;
        }

        repaint();
    }

    public void moveDown() {
        if (playerCollision.intersects(tree1Collision)) {
            System.out.println("Collision Detected");
            playerY = playerPY;
            playerCollision.setBounds(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
        } else {
            playerY += speed;
            playerPY = playerY - 10;
            playerCollision.setBounds(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
        }

        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            currentImage = downAnimation[whatFrame];
            frameDelay = 0;
        }

        repaint();
    }

    public void moveLeft() {
        if (playerCollision.intersects(tree1Collision)) {
            System.out.println("Collision Detected");
            playerX = playerPX;
            playerCollision.setBounds(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
        } else {
            playerX -= speed;
            playerPX = playerX + 10;
            playerCollision.setBounds(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
        }
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            currentImage = leftAnimation[whatFrame];
            frameDelay = 0;
        }
        repaint();
    }

    public void moveRight() {
        if (playerCollision.intersects(tree1Collision)) {
            System.out.println("Collision Detected");
            playerX = playerPX;
            playerCollision.setBounds(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
        } else {
            playerX += speed;
            playerPX = playerX - 10;
            playerCollision.setBounds(playerX + 15, playerY, playerSizeW / 2, playerSizeH);
        }
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            currentImage = rightAnimation[whatFrame];
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
        for (int i = 0; i < 4; i++) {
            try {
                currentImage = ImageIO.read(new File("res\\Entities\\Player\\felis_Down1.png"));
                upAnimation[i] = ImageIO.read(new File("res\\Entities\\Player\\felis_Up" + (i + 1) + ".png"));
                downAnimation[i] = ImageIO.read(new File("res\\Entities\\Player\\felis_Down" + (i + 1) + ".png"));
                leftAnimation[i] = ImageIO.read(new File("res\\Entities\\Player\\felis_Left" + (i + 1) + ".png"));
                rightAnimation[i] = ImageIO.read(new File("res\\Entities\\Player\\felis_Right" + (i + 1) + ".png"));
                playerSizeW = downAnimation[i].getWidth();
                playerSizeH = downAnimation[i].getHeight();
                // center character to gamepanel on start
                // playerX = (gamePanelSizeX - playerSizeW) / 2;
                // playerY = (gamePanelSizeY - playerSizeH) / 2;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("playerSizeW: " + playerSizeW);
        System.out.println("playerSizeH: " + playerSizeH);
        System.out.println("PlayerX: " + playerX);
        System.out.println("PLayerY: " + playerY);
        try {
            // sizeTestMapW = testMap.getWidth()*2;
            // sizeTestMapH = testMap.getHeight()*2;
            // center map to gamepanel on start
            // testMapXPos = (gamePanelSizeX-sizeTestMapW)/2;
            // testMapYPos = (gamePanelSizeY + -sizeTestMapH)/2;
            System.out.println("Map Height: " + tileRow * tileSize);
            System.out.println("Map Width: " + tileCol * tileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // bottom layer
        // if (testMap != null){
        // g.drawImage(testMap, testMapXPos, testMapYPos, sizeTestMapW, sizeTestMapH,
        // null);
        // }
        // fill map with grass tiles
        // for (int i = 0; i < tileRow; i++) {
        // for (int j = 0; j < tileCol; j++) {
        // g.drawImage(grassTile, (i * 32)-playerX, (j * 32)-playerY, tileSize,
        // tileSize, null);
        // if (i == 4 && j == 5) {
        // tree1Collision.setBounds((i*tileSize)-playerX, (j)-playerY, (tileSize/2)+12,
        // tileSize/2);
        // g.setColor(Color.RED);
        // g.drawRect(tree1Collision.x, tree1Collision.y,
        // tree1Collision.width,tree1Collision.height);
        // g.drawImage(tree1, (i * 32)-playerX, j-playerY, tileSize, tileSize * 2,
        // null);
        // }
        // }
        // }
        tiles.draw(g2);
        // character
        if (currentImage != null) {
            g.setColor(Color.RED);
            g.drawRect(playerCollision.x, playerCollision.y, playerCollision.width, playerCollision.height);
            g.drawImage(currentImage, playerX, playerY, playerSizeW, playerSizeH, null);
        }
        for (int x = 0; x < (tileRow * tileSize); x += tileSize) {
            g.setColor(Color.red); // Vertical lines
            g.drawLine(x - playerX, 0 - playerY, x - playerX, (tileCol * tileSize) - playerY);
        }
        for (int y = 0; y <= (tileCol * tileSize); y += tileSize) {
            g.drawLine(0 - playerX, y - playerY, (tileCol * tileSize) - playerX, y - playerY); // Horizontal lines
            g.setColor(Color.red);
        }
        g.setColor(Color.green);
        g.drawLine(0, gamePanelSizeY / 2, gamePanelSizeX, gamePanelSizeY / 2);
        g.setColor(Color.green);
        g.drawLine(gamePanelSizeX / 2, gamePanelSizeY, gamePanelSizeX / 2, 0);
        // top layer
    }
}