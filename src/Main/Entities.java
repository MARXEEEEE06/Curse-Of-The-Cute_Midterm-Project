import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Entities {
    GamePanel gp;

    // NPC variables (map pixel coordinates)
    private BufferedImage npcImage;
    private int npcMapX = 300; // map pixel X
    private int npcMapY = 200; // map pixel Y
    private final int npcWidth = 32;
    private final int npcHeight = 32;
    private boolean isEnemyNPC = false; // set to true for enemy encounters
    // animation frames for NPC Auran
    private final BufferedImage[] idleAnim = new BufferedImage[4];
    private int animIndex = 0;
    private int frameDelay = 2;

    // Dialogue state
    private boolean showDialogue = false;
    private boolean dialogueDismissed = false;
    private final String dialogText = "Hello there, traveler!";

    public Entities(GamePanel gp) {
        this.gp = gp;
        loadNPCSprite();
    }

    private void loadNPCSprite() {
        try {
            // load Auran idle frames if available
            File base = new File("res\\Entities\\FriendlyNPC\\Auran");
            File f1 = new File(base, "auran_idle1.png");
            File f2 = new File(base, "auran_idle2.png");
            File f3 = new File(base, "auran_idle3.png");
            File f4 = new File(base, "auran_idle4.png");
            if (f1.exists() && f2.exists() && f3.exists() && f4.exists()) {
                idleAnim[0] = ImageIO.read(f1);
                idleAnim[1] = ImageIO.read(f2);
                idleAnim[2] = ImageIO.read(f3);
                idleAnim[3] = ImageIO.read(f4);
                npcImage = idleAnim[0];
            } else {
                // fallback to single image locations
                File fAlt = new File("res\\Entities\\npc.png");
                if (fAlt.exists()) npcImage = ImageIO.read(fAlt);
            }
        } catch (Exception e) {
            // ignore; draw placeholder
        }
    }

    /**
     * Check player-NPC proximity using screen coordinates from GamePanel.
     * playerScreenX/Y are the player's position on the game panel (screen coords).
     * mapX/mapY are the camera offsets.
     * Returns true if interaction should block movement.
     */
    public boolean checkNPCInteraction(int playerScreenX, int playerScreenY, int mapX, int mapY, PlayerStatus ps) {
        // Convert player screen position to world coordinates
        int playerWorldX = mapX + playerScreenX;
        int playerWorldY = mapY + playerScreenY;
        Rectangle playerRect = new Rectangle(playerWorldX, playerWorldY, ps.playerSizeW, ps.playerSizeH);

        // NPC is in world/map coordinates
        Rectangle npcRect = new Rectangle(npcMapX, npcMapY, npcWidth, npcHeight);

        // Check if player is within proximity range (160 pixels) or directly intersecting
        double dx = (npcRect.getCenterX() - playerRect.getCenterX());
        double dy = (npcRect.getCenterY() - playerRect.getCenterY());
        double dist = Math.hypot(dx, dy);

        if (dist <= 160) {
            if (!dialogueDismissed) {
                showDialogue = true;
                return true; // block movement
            } else {
                showDialogue = false;
                return false;
            }
        } else {
            // player moved away; reset dismissed state
            dialogueDismissed = false;
            showDialogue = false;
            return false;
        }
    }

    // Called each frame to allow NPC logic to update (e.g. reset dismissed when player moves away)
    public void update() {
        // animation timing using GamePanel's frameSpeed pattern, but slower (2x multiplier)
        frameDelay++;
        if (frameDelay >= gp.frameSpeed * 2) {
            animIndex = (animIndex + 1) % idleAnim.length;
            // only update npcImage if frame available
            if (idleAnim[animIndex] != null) npcImage = idleAnim[animIndex];
            frameDelay = 0;
        }

        Rectangle npcScreen = getScreenRect();
        Rectangle playerRect = new Rectangle(gp.playerX, gp.playerY, gp.ps.playerSizeW, gp.ps.playerSizeH);
        if (!npcScreen.intersects(playerRect)) {
            dialogueDismissed = false;
        }
    }

    // Draw the NPC (on the map) and the dialogue at the bottom in visual novel style
    public void draw(Graphics g) {
        int drawX = (npcMapX * TileManager.SCALE) - gp.mapX;
        int drawY = (npcMapY * TileManager.SCALE) - gp.mapY;
        int drawWidth = npcWidth * TileManager.SCALE;
        int drawHeight = npcHeight * TileManager.SCALE;

        if (npcImage != null) {
            g.drawImage(npcImage, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(drawX, drawY, drawWidth, drawHeight);
        }

        // Visual novel style dialogue at the bottom
        if (showDialogue) {
            drawDialogueBox(g);
        }
    }

    // Draw dialogue box at bottom with NPC sprite on left side
    private void drawDialogueBox(Graphics g) {
        int panelHeight = 140;
        int panelX = 10;
        int panelY = gp.gamePanelSizeY - panelHeight - 10;
        int panelWidth = gp.gamePanelSizeX - 20;

        // Draw semi-transparent background panel
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 12, 12);

        // Draw border
        g.setColor(new Color(200, 150, 100));
        if (g instanceof java.awt.Graphics2D) {
            ((java.awt.Graphics2D) g).setStroke(new java.awt.BasicStroke(2));
            ((java.awt.Graphics2D) g).drawRoundRect(panelX, panelY, panelWidth, panelHeight, 12, 12);
        }

        // Draw NPC sprite on left side (scaled up)
        int spriteSize = 100;
        int spriteX = panelX + 15;
        int spriteY = panelY + (panelHeight - spriteSize) / 2;
        if (npcImage != null) {
            g.drawImage(npcImage, spriteX, spriteY, spriteSize, spriteSize, null);
        }

        // Draw dialogue text on the right side
        int textX = spriteX + spriteSize + 20;
        int textY = panelY + 20;
        int textAreaWidth = panelWidth - spriteSize - 50;
        
        // Safety check to prevent crashes with very small text area
        if (textAreaWidth < 50) {
            textAreaWidth = 50;
        }

        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 25));
        FontMetrics fm = g.getFontMetrics();

        // Word wrap the dialogue text
        String[] words = dialogText.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = textY;
        int lineHeight = fm.getHeight();
        int maxLines = 3; // limit to 3 lines max
        int lineCount = 0;

        for (String word : words) {
            if (lineCount >= maxLines) break; // don't exceed max lines
            
            if (fm.stringWidth(line.toString() + word) > textAreaWidth) {
                if (line.length() > 0) {
                    g.drawString(line.toString(), textX, currentY);
                    currentY += lineHeight;
                    lineCount++;
                }
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }
        if (line.length() > 0 && lineCount < maxLines) {
            g.drawString(line.toString(), textX, currentY);
        }

        // Draw click prompt at bottom
        g.setColor(new Color(200, 200, 200));
        g.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 10));
        g.drawString("[Click to continue]", panelX + panelWidth - 155, panelY + panelHeight - 10);
    }

    // Return the NPC's screen rectangle (for clicking / collision checks)
    public Rectangle getScreenRect() {
        int drawX = (npcMapX * TileManager.SCALE) - gp.mapX;
        int drawY = (npcMapY * TileManager.SCALE) - gp.mapY;
        return new Rectangle(drawX, drawY, npcWidth * TileManager.SCALE, npcHeight * TileManager.SCALE);
    }

    // Return the NPC's collision rectangle in map/world coordinates (pixels)
    public Rectangle getMapRect() {
        return new Rectangle(npcMapX, npcMapY, npcWidth, npcHeight);
    }

    // allow external code to place NPC at map/world coordinates (pixels)
    public void setMapPosition(int mapX, int mapY) {
        this.npcMapX = mapX;
        this.npcMapY = mapY;
    }

    // allow external code to set this NPC as enemy (triggers combat on interaction)
    public void setIsEnemyNPC(boolean isEnemy) {
        this.isEnemyNPC = isEnemy;
    }

    public boolean getIsEnemyNPC() {
        return isEnemyNPC;
    }

    // Called by GamePanel when the mouse is clicked on the panel
    public void onMouseClicked(int mouseX, int mouseY) {
        Rectangle npcRect = getScreenRect();
        if (showDialogue) {
            // Dismiss dialogue when clicking anywhere on the dialogue box area
            int panelHeight = 140;
            int panelX = 10;
            int panelY = gp.gamePanelSizeY - panelHeight - 10;
            int panelWidth = gp.gamePanelSizeX - 20;
            Rectangle dialogBox = new Rectangle(panelX, panelY, panelWidth, panelHeight);
            if (dialogBox.contains(mouseX, mouseY)) {
                showDialogue = false;
                dialogueDismissed = true;
                gp.repaint();
            }
        } else {
            if (npcRect.contains(mouseX, mouseY)) {
                Rectangle playerRect = new Rectangle(gp.playerX, gp.playerY, gp.ps.playerSizeW, gp.ps.playerSizeH);
                double dx = (npcRect.getCenterX() - playerRect.getCenterX());
                double dy = (npcRect.getCenterY() - playerRect.getCenterY());
                double dist = Math.hypot(dx, dy);
                if (dist <= 160 && !dialogueDismissed) {
                    showDialogue = true;
                    gp.repaint();
                }
            }
        }
    }

    /**
     * Update cursor to pointer when hovering over NPC, otherwise default cursor.
     * Call this from GamePanel's mouse motion listener.
     */
    public void updateCursorOnHover(int mouseX, int mouseY) {
        Rectangle npcRect = getScreenRect();
        if (npcRect.contains(mouseX, mouseY)) {
            // Check if player is within interaction range
            Rectangle playerRect = new Rectangle(gp.playerX, gp.playerY, gp.ps.playerSizeW, gp.ps.playerSizeH);
            double dx = (npcRect.getCenterX() - playerRect.getCenterX());
            double dy = (npcRect.getCenterY() - playerRect.getCenterY());
            double dist = Math.hypot(dx, dy);
            if (dist <= 160) {
                gp.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                gp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        } else {
            gp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}