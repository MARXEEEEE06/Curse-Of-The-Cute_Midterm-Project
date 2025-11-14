import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    // map
    public final int tileSize = 32;
    public int gamePanelSizeX = 800;
    public int gamePanelSizeY = 600;
    public int mapX = 13*32;
    public int mapY = 13*32;
    @SuppressWarnings("unused")
    private final int scaleMultiplier = 2;
    public BufferedImage grassTile;
    public BufferedImage tree1;
    PlayerStatus ps = new PlayerStatus(this);
    public int speed = 5;
    public int whatFrame = 0;
    // Controls how many movement ticks occur before the sprite frame advances.
    // Increase this value to make the walking animation slower.
    public final int frameSpeed = 10; // higher = slower animation
    public int frameDelay = 0; // counts frames for delay
    public int playerX = 0;
    public int playerY = 0;
    public int playerXCollision = playerX;
    public int playerYCollision = (playerY-tileSize);
    // tile
    public int tileRow = 50; // rows of grid
    public int tileCol = 50; // columns of grid
    public int baseLayerGrid[][] = new int[tileRow][tileCol];
    public int objectLayerGrid[][] = new int[tileRow][tileCol];
    TileManager tiles = new TileManager(this);
    Entities entities = new Entities(this);
    EnemyNPC enemyNPC = new EnemyNPC(this);
    Inventory inventory = new Inventory(this);
    Menu menu = new Menu(this);
    Combat combat = new Combat(this);
    // collisions
    String playerDirection = "down";
    Collisions playerCollision = new Collisions(0, 0, tileSize/2, tileSize/2);

    public void entitiesCollision() {
        playerCollision = new Collisions(playerX, playerY, tileSize/2, tileSize/2);
        if (null != playerDirection) switch (playerDirection) {
            case "up" -> playerCollision = new Collisions(playerX+23, playerY-(((tileSize/2)-20)), tileSize/2, tileSize/2);
            case "down" -> playerCollision = new Collisions(playerX+23, playerY+tileSize+15, tileSize/2, tileSize/2);
            case "left" -> playerCollision = new Collisions((playerX+15), playerY+23, tileSize/2, tileSize/2);
            case "right" -> playerCollision = new Collisions(playerX+tileSize+3, playerY+23, tileSize/2, tileSize/2);
            default -> {
            }
        }
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
        // Determine camera/player spawn and NPC spawns from TMX named objects
        if (tiles != null) {
            // 1) Player spawn: prefer explicit 'player' or 'playerSpawn' named objects
            java.awt.Point playerP = tiles.getSpawnByName("player");
            if (playerP == null) playerP = tiles.getSpawnByName("playerSpawn");
            // fallback to generic spawn if no explicit player spawn exists
            if (playerP == null && tiles.hasSpawn) playerP = new java.awt.Point(tiles.spawnX, tiles.spawnY);
            if (playerP != null) {
                mapX = playerP.x * TileManager.SCALE - (gamePanelSizeX / 2);
                mapY = playerP.y * TileManager.SCALE - (gamePanelSizeY / 2);
            }

            // 2) Auran NPC spawn: place Auran only if an explicit Auran spawn exists
            if (entities != null) {
                java.awt.Point auranP = tiles.getSpawnByName("auran");
                if (auranP == null) auranP = tiles.getSpawnByName("auranSpawn");
                if (auranP != null) {
                    entities.setMapPosition(auranP.x, auranP.y);
                    entities.setIsEnemyNPC(false); // Auran is friendly
                }
            }
            
            // 3) Enemy NPC spawn: place enemy at enemySpawn if exists
            if (enemyNPC != null) {
                java.awt.Point enemyP = tiles.getSpawnByName("enemy");
                if (enemyP == null) enemyP = tiles.getSpawnByName("enemySpawn");
                if (enemyP != null) {
                    enemyNPC.setMapPosition(enemyP.x, enemyP.y);
                }
            }
        }
        // forward mouse clicks to menu/combat/entities for interaction
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Menu takes priority
                if (menu != null && menu.isShowing()) {
                    menu.onMouseClicked(e.getX(), e.getY());
                    return;
                }
                // Check if clicking on enemy NPC should trigger combat (click-to-start, Pokémon style)
                if (entities != null && entities.getIsEnemyNPC()) {
                    Rectangle npcRect = entities.getScreenRect();
                    if (npcRect.contains(e.getX(), e.getY())) {
                        // Check if player is within proximity
                        Rectangle playerRect = new Rectangle(playerX, playerY, ps.playerSizeW, ps.playerSizeH);
                        double dx = (npcRect.getCenterX() - playerRect.getCenterX());
                        double dy = (npcRect.getCenterY() - playerRect.getCenterY());
                        double dist = Math.hypot(dx, dy);
                        if (dist <= 160) {
                            // Start combat when player clicks nearby enemy NPC
                            if (combat != null && !combat.isActive()) {
                                combat.startCombat();
                                repaint();
                            }
                            return;
                        }
                    }
                }
                // Check if clicking on enemyNPC (separate enemy type) should trigger combat
                if (enemyNPC != null) {
                    Rectangle enemyRect = enemyNPC.getScreenRect();
                    if (enemyRect.contains(e.getX(), e.getY())) {
                        Rectangle playerRect = new Rectangle(playerX, playerY, ps.playerSizeW, ps.playerSizeH);
                        double dx = (enemyRect.getCenterX() - playerRect.getCenterX());
                        double dy = (enemyRect.getCenterY() - playerRect.getCenterY());
                        double dist = Math.hypot(dx, dy);
                        if (dist <= 160) {
                            if (combat != null && !combat.isActive()) {
                                combat.startCombat();
                                repaint();
                            }
                            return;
                        }
                    }
                }
                if (entities != null) entities.onMouseClicked(e.getX(), e.getY());
            }
        });
        // update cursor when mouse moves over NPC
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                entities.updateCursorOnHover(e.getX(), e.getY());
            }
        });
        // ensure camera doesn't start outside bounds
        clampCamera();
    };

    // character movement
    public void moveUp() {
        playerDirection = "up";
        // Check for enemy NPC proximity/combat trigger before dialogue
        checkEnemyProximity();
        // block movement / show dialogue if NPC interaction requires it
        if (entities != null && entities.checkNPCInteraction(playerX, playerY, mapX, mapY, ps)) return;
        
        // Check collision with the new position (move camera up = scroll map up)
        int newMapY = mapY - speed;
        boolean colliding = checkCollisionAtMapPosition(newMapY, mapX);
        
        if (!colliding) {
            mapY = newMapY;
        }
        
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            ps.currentImage = ps.upAnimation[whatFrame];
            frameDelay = 0;
        }
        clampCamera();
        repaint();
    }

    public void moveDown() {
        playerDirection = "down";
        // Check for enemy NPC proximity/combat trigger
        checkEnemyProximity();
        if (entities != null && entities.checkNPCInteraction(playerX, playerY, mapX, mapY, ps)) return;
        
        int newMapY = mapY + speed;
        boolean colliding = checkCollisionAtMapPosition(newMapY, mapX);
        
        if (!colliding) {
            mapY = newMapY;
        }

        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            ps.currentImage = ps.downAnimation[whatFrame];
            frameDelay = 0;
        }
        clampCamera();
        repaint();
    }

    public void moveLeft() {
        playerDirection = "left";
        // Check for enemy NPC proximity/combat trigger
        checkEnemyProximity();
        if (entities != null && entities.checkNPCInteraction(playerX, playerY, mapX, mapY, ps)) return;
        
        int newMapX = mapX - speed;
        boolean colliding = checkCollisionAtMapPosition(mapY, newMapX);
        
        if (!colliding) {
            mapX = newMapX;
        }
        
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            ps.currentImage = ps.leftAnimation[whatFrame];
            frameDelay = 0;
        }
        clampCamera();
        repaint();
    }

    public void moveRight() {
        playerDirection = "right";
        // Check for enemy NPC proximity/combat trigger
        checkEnemyProximity();
        if (entities != null && entities.checkNPCInteraction(playerX, playerY, mapX, mapY, ps)) return;
        
        int newMapX = mapX + speed;
        boolean colliding = checkCollisionAtMapPosition(mapY, newMapX);
        
        if (!colliding) {
            mapX = newMapX;
        }
        
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            ps.currentImage = ps.rightAnimation[whatFrame];
            frameDelay = 0;
        }
        clampCamera();
        repaint();
    }

    /**
     * Check if the player collides with any map obstacles at the given camera position.
     * Uses the player's full bounding box (playerX, playerY, playerSizeW, playerSizeH) for pixel-perfect collision.
     */
    private boolean checkCollisionAtMapPosition(int checkMapY, int checkMapX) {
        // Player's bounding box in screen coordinates
        Rectangle playerScreenRect = new Rectangle(playerX, playerY, ps.playerSizeW, ps.playerSizeH);
        
        for (Rectangle mapCollision : tiles.getMapCollisions()) {
            // Convert map collision to screen coordinates using the test camera position
            Rectangle screenCollision = new Rectangle(
                (mapCollision.x * TileManager.SCALE) - checkMapX,
                (mapCollision.y * TileManager.SCALE) - checkMapY,
                mapCollision.width * TileManager.SCALE,
                mapCollision.height * TileManager.SCALE
            );
            if (playerScreenRect.intersects(screenCollision)) {
                return true; // collision detected
            }
        }
        // Also check collision against the NPC(s)
        if (entities != null) {
            Rectangle npcMapRect = entities.getMapRect();
            if (npcMapRect != null) {
                Rectangle npcScreenRect = new Rectangle(
                    (npcMapRect.x * TileManager.SCALE) - checkMapX,
                    (npcMapRect.y * TileManager.SCALE) - checkMapY,
                    npcMapRect.width * TileManager.SCALE,
                    npcMapRect.height * TileManager.SCALE
                );
                if (playerScreenRect.intersects(npcScreenRect)) return true;
            }
        }
        return false; // no collision
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Check if player is near an enemy NPC and auto-trigger combat
     */
    private void checkEnemyProximity() {
        if (entities == null || !entities.getIsEnemyNPC()) {
            // Check the new enemy NPC instead
            if (enemyNPC != null) {
                Rectangle enemyRect = enemyNPC.getScreenRect();
                Rectangle playerRect = new Rectangle(playerX, playerY, ps.playerSizeW, ps.playerSizeH);
                if (enemyRect.intersects(playerRect)) {
                    // Auto-start combat if not already active
                    if (combat != null && !combat.isActive()) {
                        combat.startCombat();
                        repaint(); // ensure combat UI shows immediately
                    }
                }
            }
            return;
        }
        Rectangle npcRect = entities.getScreenRect();
        Rectangle playerRect = new Rectangle(playerX, playerY, ps.playerSizeW, ps.playerSizeH);
        if (npcRect.intersects(playerRect)) {
            // Auto-start combat if not already active
            if (combat != null && !combat.isActive()) {
                combat.startCombat();
                repaint(); // ensure combat UI shows immediately
            }
        }
    }

    // Clamp camera to map boundaries
    private void clampCamera() {
        int mapPixelWidth = tileCol * tileSize * TileManager.SCALE;
        int mapPixelHeight = tileRow * tileSize * TileManager.SCALE;

        if (mapX < 0) mapX = 0;
        else if (mapX + gamePanelSizeX > mapPixelWidth) mapX = Math.max(0, mapPixelWidth - gamePanelSizeX);

        if (mapY < 0) mapY = 0;
        else if (mapY + gamePanelSizeY > mapPixelHeight) mapY = Math.max(0, mapPixelHeight - gamePanelSizeY);
    }

    // sprite
    private void loadSprites() {
        System.out.println("playerSizeW: " + ps.playerSizeW);
        System.out.println("playerSizeH: " + ps.playerSizeH);
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
        // If menu is showing, draw menu and skip game rendering
        if (menu != null && menu.isShowing()) {
            menu.draw(g);
            return;
        }
        // If combat active, draw combat UI and skip normal gameplay rendering
        if (combat != null && combat.isActive()) {
            combat.draw(g);
            return;
        }
        tiles.draw(g);
        // update and draw entities (NPCs)
        if (entities != null) {
            entities.update();
            entities.draw(g);
        }
        // draw enemy NPC
        if (enemyNPC != null) {
            Graphics2D g2 = (Graphics2D) g;
            enemyNPC.draw(g2);
        }
        ps.draw(g);
        // Draw inventory UIe
        if (inventory != null) {
            inventory.draw(g);
        }
        //gridLines
        //for (int x = 0; x < (tileRow * tileSize); x += tileSize) {
        //    g.setColor(Color.red); // Vertical lines
        //    g.drawLine(x - mapX, 0 - mapY, x - mapX, (tileCol * tileSize) - mapY);
        //}
        //for (int y = 0; y <= (tileCol * tileSize); y += tileSize) {
        //    g.setColor(Color.red);// Horizontal lines
        //    g.drawLine(0 - mapX, y - mapY, (tileCol * tileSize) - mapX, y - mapY); 
        //}
        //g.setColor(Color.green);
        //g.drawLine(0, gamePanelSizeY / 2, gamePanelSizeX, gamePanelSizeY / 2);
        //g.setColor(Color.green);
        //g.drawLine(gamePanelSizeX / 2, gamePanelSizeY, gamePanelSizeX / 2, 0);
        
        ////grid coordinates
        //g.setColor(Color.white);
        //for (int row = 0; row < tileCol; row++) {
        //    for (int col = 0; col < tileRow; col++) {
        //        int drawX = (col * tileSize) - mapX;
        //        int drawY = (row * tileSize) - mapY + 1;
        //        g.setColor(Color.white);
        //        g.drawString(col+","+row, drawX, drawY);
        //    }
        //}
    }
}
