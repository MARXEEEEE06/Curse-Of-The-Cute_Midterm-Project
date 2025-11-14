import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/**
 * Pokémon-style combat screen with skill animations.
 * - Auto-starts and displays skill animations when combat begins
 * - Loads frames from res/Entities/Combat recursively
 * - Groups images by actor and skill number
 * - Turn-based combat with 3 skills and Run button
 */
public class Combat {
    private final GamePanel gp;
    private boolean active = false;

    private int playerHP, playerMaxHP;
    private int enemyHP, enemyMaxHP;

    // actor -> skillId -> frames
    private final Map<String, Map<Integer, List<BufferedImage>>> frames = new HashMap<>();

    // Current animation
    private List<BufferedImage> currentFrames = null;
    private int currentFrameIndex = 0;
    private BufferedImage enemyImage = null;

    public Combat(GamePanel gp) {
        this.gp = gp;
        // Default HP values
        this.playerMaxHP = 100;
        this.enemyMaxHP = 80;
        this.playerHP = playerMaxHP;
        this.enemyHP = enemyMaxHP;
        
        // Load combat frames and enemy image
        loadFrames(new File("res/Entities/Combat"));
        loadEnemyImage(new File("res/Entities/Combat/enemy"));
    }

    private void loadFrames(File root) {
        if (root == null || !root.exists()) return;
        File[] files = root.listFiles();
        if (files == null) return;
        
        for (File f : files) {
            if (f.isDirectory()) {
                // Check if this folder is a skill folder (e.g., skill1, skill2, etc.)
                String folderName = f.getName().toLowerCase();
                int skillNumFromFolder = -1;
                Matcher m = Pattern.compile("skill\\D*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(folderName);
                if (m.find()) {
                    try { 
                        skillNumFromFolder = Integer.parseInt(m.group(1)); 
                    } catch (NumberFormatException ex) { 
                        skillNumFromFolder = -1; 
                    }
                }
                
                loadFramesFromFolder(f, skillNumFromFolder);
                continue;
            }
            
            String name = f.getName();
            String lower = name.toLowerCase();
            if (!(lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg"))) continue;
            
            try {
                BufferedImage img = ImageIO.read(f);
                if (img == null) continue;

                // Determine skill number by searching for "skill <num>" in filename
                int skillNum = 1;
                Matcher m2 = Pattern.compile("skill\\D*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(name);
                if (m2.find()) {
                    try { 
                        skillNum = Integer.parseInt(m2.group(1)); 
                    } catch (NumberFormatException ex) { 
                        skillNum = 1; 
                    }
                } else {
                    // Fallback to trailing digit before extension
                    Matcher t = Pattern.compile("(\\d+)\\.[a-zA-Z]{1,4}$").matcher(name);
                    if (t.find()) {
                        try { 
                            skillNum = Integer.parseInt(t.group(1)); 
                        } catch (NumberFormatException ex) { 
                            skillNum = 1; 
                        }
                    }
                }

                // Actor key: take filename before the SKILL token
                String actorKeyRaw = name;
                int splitPos = name.toLowerCase().indexOf("skill");
                if (splitPos > 0) actorKeyRaw = name.substring(0, splitPos).trim();
                actorKeyRaw = actorKeyRaw.replaceAll("\\.[a-zA-Z]{1,4}$", "").trim();
                String actorKey = actorKeyRaw.replaceAll("[^A-Za-z0-9 ]", "").trim();
                if (actorKey.isEmpty()) actorKey = "OTHER";

                frames.putIfAbsent(actorKey, new HashMap<>());
                Map<Integer, List<BufferedImage>> mmap = frames.get(actorKey);
                mmap.putIfAbsent(skillNum, new ArrayList<>());
                mmap.get(skillNum).add(img);
                
                System.out.println("CombatLoader: " + name + " -> Actor='" + sanitize(actorKey) + 
                    "' Skill=" + skillNum + " (w=" + img.getWidth() + ",h=" + img.getHeight() + ")");
            } catch (Exception ex) {
                System.out.println("CombatLoader: failed to load " + f.getAbsolutePath() + " -> " + ex.getMessage());
            }
        }
        
        // Print summary
        int total = 0;
        for (Map.Entry<String, Map<Integer, List<BufferedImage>>> a : frames.entrySet()) {
            for (Map.Entry<Integer, List<BufferedImage>> e : a.getValue().entrySet()) {
                total += e.getValue().size();
            }
            System.out.println("CombatLoader: actor='" + sanitize(a.getKey()) + "' skills=" + a.getValue().keySet());
        }
        System.out.println("CombatLoader: total frames=" + total);
    }

    private void loadFramesFromFolder(File folder, int skillNumFromFolder) {
        File[] files = folder.listFiles();
        if (files == null) return;
        
        for (File f : files) {
            if (f.isDirectory()) {
                loadFramesFromFolder(f, skillNumFromFolder);
                continue;
            }
            
            String name = f.getName();
            String lower = name.toLowerCase();
            if (!(lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg"))) continue;
            
            try {
                BufferedImage img = ImageIO.read(f);
                if (img == null) continue;

                // Use skill number from parent folder if available
                int skillNum = skillNumFromFolder > 0 ? skillNumFromFolder : 1;
                if (skillNumFromFolder <= 0) {
                    Matcher t = Pattern.compile("(\\d+)\\.[a-zA-Z]{1,4}$").matcher(name);
                    if (t.find()) {
                        try { 
                            skillNum = Integer.parseInt(t.group(1)); 
                        } catch (NumberFormatException ex) { 
                            skillNum = 1; 
                        }
                    }
                }

                // Actor key: take filename before any numeric suffixes
                String actorKeyRaw = name.replaceAll("\\s*\\d+\\.[a-zA-Z]{1,4}$", "").trim();
                String actorKey = actorKeyRaw.replaceAll("[^A-Za-z0-9 ]", "").trim();
                if (actorKey.isEmpty()) actorKey = "OTHER";

                frames.putIfAbsent(actorKey, new HashMap<>());
                Map<Integer, List<BufferedImage>> mmap = frames.get(actorKey);
                mmap.putIfAbsent(skillNum, new ArrayList<>());
                mmap.get(skillNum).add(img);
                
                System.out.println("CombatLoader: " + name + " -> Actor='" + sanitize(actorKey) + 
                    "' Skill=" + skillNum + " (w=" + img.getWidth() + ",h=" + img.getHeight() + ")");
            } catch (Exception ex) {
                System.out.println("CombatLoader: failed to load " + f.getAbsolutePath() + " -> " + ex.getMessage());
            }
        }
    }

    private void loadEnemyImage(File folder) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            System.out.println("CombatLoader: enemy folder not found at " + 
                (folder == null ? "null" : folder.getAbsolutePath()));
            return;
        }
        
        File[] files = folder.listFiles((d, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });
        
        if (files == null || files.length == 0) {
            System.out.println("CombatLoader: no enemy images found in " + folder.getAbsolutePath());
            return;
        }
        
        try {
            BufferedImage img = ImageIO.read(files[0]);
            if (img != null) {
                enemyImage = img;
                System.out.println("CombatLoader: loaded enemy image '" + files[0].getName() + 
                    "' (" + img.getWidth() + "x" + img.getHeight() + ")");
            }
        } catch (Exception ex) {
            System.out.println("CombatLoader: failed to load enemy image -> " + ex.getMessage());
        }
    }

    private String sanitize(String s) {
        if (s == null) return "";
        String low = s.toLowerCase();
        if (low.contains("nigg")) return "[redacted]";
        return s;
    }

    public boolean isActive() { 
        return active; 
    }

    public void startCombat() {
        if (active) return;
        active = true;
        playerHP = playerMaxHP;
        enemyHP = enemyMaxHP;
        autoPlayPreferredSkill();
        gp.repaint();
    }

    private void autoPlayPreferredSkill() {
        // Prefer an actor that contains 'BS'
        String chosenActor = null;
        for (String a : frames.keySet()) {
            if (a.toUpperCase().contains("BS")) { 
                chosenActor = a; 
                break; 
            }
        }
        if (chosenActor == null && !frames.isEmpty()) {
            chosenActor = frames.keySet().iterator().next();
        }

        if (chosenActor == null) return;
        Map<Integer, List<BufferedImage>> skills = frames.get(chosenActor);
        if (skills == null || skills.isEmpty()) return;

        // Prefer skill 2, then skill 1, else smallest available skill id
        List<BufferedImage> f = skills.get(2);
        int playedSkill = 2;
        if (f == null) {
            f = skills.get(1);
            playedSkill = 1;
        }
        if (f == null) {
            Integer min = skills.keySet().stream().min(Integer::compareTo).orElse(null);
            if (min != null) {
                f = skills.get(min);
                playedSkill = min;
            }
        }
        if (f == null || f.isEmpty()) return;

        currentFrames = f;
        currentFrameIndex = 0;
        System.out.println("Combat: auto-playing skill " + playedSkill + 
            " for actor='" + sanitize(chosenActor) + "' frames=" + f.size());
    }

    public void endCombat() {
        active = false;
        currentFrames = null;
        gp.repaint();
    }

    public boolean onMouseClicked(int mx, int my) {
        if (!active) return false;
        
        int menuY = gp.gamePanelSizeY - 130;
        int btnW = 140;
        int btnH = 40;
        int spacing = 20;
        int startX = (gp.gamePanelSizeX - (btnW * 2 + spacing)) / 2;
        int startY = menuY + 20;

        // Skill 1 (top-left)
        if (isClickInRect(mx, my, startX, startY, btnW, btnH)) {
            playerUseSkill(1);
            return true;
        }
        // Skill 2 (top-right)
        if (isClickInRect(mx, my, startX + btnW + spacing, startY, btnW, btnH)) {
            playerUseSkill(2);
            return true;
        }
        // Skill 3 (bottom-left)
        if (isClickInRect(mx, my, startX, startY + btnH + spacing, btnW, btnH)) {
            playerUseSkill(3);
            return true;
        }
        // Run (bottom-right)
        if (isClickInRect(mx, my, startX + btnW + spacing, startY + btnH + spacing, btnW, btnH)) {
            endCombat();
            return true;
        }
        
        return false;
    }
    
    private boolean isClickInRect(int x, int y, int rx, int ry, int rw, int rh) {
        return x >= rx && x <= rx + rw && y >= ry && y <= ry + rh;
    }

    private void playerUseSkill(int id) {
        if (!active) return;
        
        // Update animation
        onSkillPressed(id);
        
        // Calculate damage
        int dmg = switch(id) {
            case 1 -> 18;
            case 2 -> 12;
            case 3 -> 8;
            default -> 10;
        };
        
        enemyHP -= dmg;
        if (enemyHP <= 0) { 
            enemyHP = 0; 
            gp.repaint(); 
            endCombat(); 
            return; 
        }
        gp.repaint();
        
        // Enemy retaliates
        enemyAttack();
    }

    private void enemyAttack() {
        int dmg = 10 + (int)(Math.random() * 8);
        playerHP -= dmg;
        if (playerHP <= 0) { 
            playerHP = 0; 
            gp.repaint(); 
            endCombat(); 
            return; 
        }
        gp.repaint();
    }

    public void onSkillPressed(int id) {
        if (!active) return;
        // Find any actor that has this skill
        for (Map<Integer, List<BufferedImage>> skills : frames.values()) {
            List<BufferedImage> f = skills.get(id);
            if (f != null && !f.isEmpty()) {
                currentFrames = f;
                currentFrameIndex = 0;
                gp.repaint();
                return;
            }
        }
    }

    public void draw(Graphics g) {
        if (!active) return;
        Graphics2D g2 = (Graphics2D) g;
        
        // Battle background
        g2.setColor(new Color(20, 20, 40, 220));
        g2.fillRect(0, 0, gp.gamePanelSizeX, gp.gamePanelSizeY);

        // Draw enemy
        if (enemyImage != null) {
            int ex = (gp.gamePanelSizeX - enemyImage.getWidth()) / 2;
            int ey = 60;
            g2.drawImage(enemyImage, ex, ey, null);
        } else {
            // Placeholder enemy
            g2.setColor(Color.DARK_GRAY);
            int ex = gp.gamePanelSizeX - 240;
            int ey = 40;
            g2.fillRect(ex, ey, 160, 140);
            g2.setColor(Color.WHITE);
            g2.drawString("Enemy", ex + 10, ey + 20);
        }

        // Enemy HP bar
        drawHPBar(g2, gp.gamePanelSizeX - 260, 200, 220, 16, enemyHP, enemyMaxHP, Color.RED);

        // Draw player skill animation
        if (currentFrames != null && !currentFrames.isEmpty()) {
            BufferedImage img = currentFrames.get(Math.max(0, Math.min(currentFrames.size() - 1, currentFrameIndex)));
            int px = 40;
            int py = gp.gamePanelSizeY - 240;
            g2.drawImage(img, px, py, null);
        } else {
            // Placeholder player
            int px = 40;
            int py = gp.gamePanelSizeY - 240;
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(px, py, 120, 120);
            g2.setColor(Color.WHITE);
            g2.drawString("Your Felis", px + 8, py + 18);
        }

        // Player HP bar
        int px = 40;
        int py = gp.gamePanelSizeY - 240;
        drawHPBar(g2, px, py + 130, 220, 16, playerHP, playerMaxHP, Color.GREEN);

        // Battle menu
        int menuY = gp.gamePanelSizeY - 130;
        g2.setColor(new Color(30, 30, 60, 240));
        g2.fillRect(0, menuY, gp.gamePanelSizeX, 130);
        g2.setColor(new Color(200, 180, 100));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(0, menuY, gp.gamePanelSizeX, 130);

        int btnW = 140, btnH = 40, spacing = 20;
        int startX = (gp.gamePanelSizeX - (btnW * 2 + spacing)) / 2;
        int startY = menuY + 20;

        drawMenuButton(g2, startX, startY, btnW, btnH, "Skill 1");
        drawMenuButton(g2, startX + btnW + spacing, startY, btnW, btnH, "Skill 2");
        drawMenuButton(g2, startX, startY + btnH + spacing, btnW, btnH, "Skill 3");
        drawMenuButton(g2, startX + btnW + spacing, startY + btnH + spacing, btnW, btnH, "Run");

        // Prompt
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("What will you do?", 20, menuY + 115);
    }

    private void drawMenuButton(Graphics2D g2, int x, int y, int w, int h, String label) {
        g2.setColor(new Color(70, 60, 100));
        g2.fillRect(x, y, w, h);
        g2.setColor(new Color(200, 180, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, w, h);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        int textW = g2.getFontMetrics().stringWidth(label);
        int textH = g2.getFontMetrics().getAscent();
        g2.drawString(label, x + (w - textW) / 2, y + (h + textH) / 2 - 2);
    }

    private void drawHPBar(Graphics2D g2, int x, int y, int w, int h, int val, int max, Color fill) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, w, h);
        double pct = Math.max(0, Math.min(1.0, (double)val / Math.max(1, max)));
        int fw = (int)(w * pct);
        g2.setColor(fill);
        g2.fillRect(x, y, fw, h);
        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, w, h);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        String t = val + "/" + max;
        g2.drawString(t, x + w + 6, y + h - 2);
    }
}