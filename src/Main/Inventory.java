import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Inventory {
    private static final int MAX_ITEMS = 20;
    private ArrayList<InventoryItem> items;
    private boolean isOpen = false;
    private GamePanel gp;
    private BufferedImage inventoryImage;
    
    // UI constants
    private final int PADDING = 20;
    private final int SLOT_SIZE = 50;
    private final int SLOT_SPACING = 60;
    private final int COLUMNS = 4;

    public Inventory(GamePanel gp) {
        this.gp = gp;
        this.items = new ArrayList<>();
        loadInventoryImage();
    }

    /**
     * Load the inventory image from the res folder
     */
    private void loadInventoryImage() {
        try {
            File imageFile = new File("res/Entities/Inventory/inventory.png");
            if (imageFile.exists()) {
                inventoryImage = ImageIO.read(imageFile);
                System.out.println("Inventory image loaded successfully!");
            } else {
                System.out.println("Inventory image not found at: " + imageFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Error loading inventory image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add an item to the inventory
     */
    public boolean addItem(String itemName) {
        if (items.size() < MAX_ITEMS) {
            items.add(new InventoryItem(itemName, items.size()));
            return true;
        }
        return false;
    }

    /**
     * Remove an item from the inventory by index
     */
    public boolean removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Toggle inventory open/closed
     */
    public void toggleInventory() {
        isOpen = !isOpen;
    }

    /**
     * Check if inventory is open
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Set inventory open state
     */
    public void setOpen(boolean open) {
        isOpen = open;
    }

    /**
     * Draw the inventory UI
     */
    public void draw(Graphics g) {
        if (!isOpen) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Darken background
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.gamePanelSizeX, gp.gamePanelSizeY);

        // Draw custom inventory image if loaded
        if (inventoryImage != null) {
            int imageWidth = inventoryImage.getWidth();
            int imageHeight = inventoryImage.getHeight();
            int x = (gp.gamePanelSizeX - imageWidth) / 2;
            int y = (gp.gamePanelSizeY - imageHeight) / 2;
            g2.drawImage(inventoryImage, x, y, null);
        } else {
            // Fallback UI if image not found
            drawFallbackUI(g2);
        }
    }

    /**
     * Fallback UI in case image is not found
     */
    private void drawFallbackUI(Graphics2D g2) {
        int INVENTORY_WIDTH = 400;
        int INVENTORY_HEIGHT = 500;
        
        // Draw inventory panel
        int x = (gp.gamePanelSizeX - INVENTORY_WIDTH) / 2;
        int y = (gp.gamePanelSizeY - INVENTORY_HEIGHT) / 2;

        // Background
        g2.setColor(new Color(40, 40, 40));
        g2.fillRect(x, y, INVENTORY_WIDTH, INVENTORY_HEIGHT);

        // Border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x, y, INVENTORY_WIDTH, INVENTORY_HEIGHT);

        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        String title = "INVENTORY";
        int titleX = x + (INVENTORY_WIDTH - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, y + 35);

        // Draw divider line
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(x + PADDING, y + 50, x + INVENTORY_WIDTH - PADDING, y + 50);

        // Draw inventory slots
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        int slotStartX = x + PADDING + 10;
        int slotStartY = y + 70;

        for (int i = 0; i < MAX_ITEMS; i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            int slotX = slotStartX + (col * SLOT_SPACING);
            int slotY = slotStartY + (row * SLOT_SPACING);

            // Draw slot background
            g2.setColor(new Color(60, 60, 60));
            g2.fillRect(slotX, slotY, SLOT_SIZE, SLOT_SIZE);

            // Draw slot border
            if (i < items.size()) {
                g2.setColor(new Color(100, 200, 100)); // Green border for filled slots
            } else {
                g2.setColor(new Color(80, 80, 80)); // Gray border for empty slots
            }
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(slotX, slotY, SLOT_SIZE, SLOT_SIZE);

            // Draw item if slot is filled
            if (i < items.size()) {
                InventoryItem item = items.get(i);
                g2.setColor(Color.WHITE);
                String itemText = item.getName().substring(0, Math.min(3, item.getName().length()));
                fm = g2.getFontMetrics();
                int textX = slotX + (SLOT_SIZE - fm.stringWidth(itemText)) / 2;
                int textY = slotY + ((SLOT_SIZE - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(itemText, textX, textY);
            }
        }

        // Draw item count
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String itemCount = "Items: " + items.size() + "/" + MAX_ITEMS;
        fm = g2.getFontMetrics();
        int countX = x + (INVENTORY_WIDTH - fm.stringWidth(itemCount)) / 2;
        g2.drawString(itemCount, countX, y + INVENTORY_HEIGHT - 15);

        // Draw close hint
        g2.setFont(new Font("Arial", Font.ITALIC, 12));
        String closeHint = "Press E to close";
        fm = g2.getFontMetrics();
        int hintX = x + (INVENTORY_WIDTH - fm.stringWidth(closeHint)) / 2;
        g2.drawString(closeHint, hintX, y + INVENTORY_HEIGHT - 40);
    }

    /**
     * Get item at specific index
     */
    public InventoryItem getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    /**
     * Get all items
     */
    public ArrayList<InventoryItem> getItems() {
        return items;
    }

    /**
     * Get item count
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Inner class for inventory items
     */
    public static class InventoryItem {
        private String name;
        private int index;

        public InventoryItem(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }
    }
}
