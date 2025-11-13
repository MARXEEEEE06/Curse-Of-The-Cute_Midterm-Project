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

    // Draw the NPC and the dialogue if active
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

        if (showDialogue) {
            FontMetrics fm = g.getFontMetrics();
            int paddingX = 12;
            int paddingY = 8;
            int textW = fm.stringWidth(dialogText);
            int textH = fm.getHeight();
            int boxW = textW + paddingX * 2;
            int boxH = textH + paddingY * 2;
            int boxX = drawX - 30;
            int boxY = drawY - boxH - 10;

            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect(boxX, boxY, boxW, boxH, 8, 8);
            g.setColor(Color.white);
            g.drawRoundRect(boxX, boxY, boxW, boxH, 8, 8);
            g.drawString(dialogText, boxX + paddingX, boxY + paddingY + fm.getAscent());
        }
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

    // Called by GamePanel when the mouse is clicked on the panel
    public void onMouseClicked(int mouseX, int mouseY) {
        Rectangle npcRect = getScreenRect();
        if (showDialogue) {
            FontMetrics fm = gp.getGraphics().getFontMetrics();
            int paddingX = 12;
            int paddingY = 8;
            int textW = fm.stringWidth(dialogText);
            int textH = fm.getHeight();
            int boxW = textW + paddingX * 2;
            int boxH = textH + paddingY * 2;
            int boxX = npcRect.x - 30;
            int boxY = npcRect.y - boxH - 10;
            Rectangle box = new Rectangle(boxX, boxY, boxW, boxH);
            if (box.contains(mouseX, mouseY)) {
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