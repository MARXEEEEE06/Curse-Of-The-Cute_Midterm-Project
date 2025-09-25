import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

public final class tilesManager {
    Tiles[] tile;
    GamePanel panel;

    public tilesManager(GamePanel panel) {
        this.panel = panel;
        tile = new Tiles[10];
        loadTileSprite();
        loadMap();
    }

    public void loadTileSprite() {

        try {
            tile[0] = new Tiles();
            tile[0].image = ImageIO.read(new File("res\\Maps\\grass1.png"));

            tile[1] = new Tiles();
            tile[1].image = ImageIO.read(new File("res\\Maps\\tree1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap() {
        try {
            InputStream base = getClass().getResourceAsStream("baseLayerGrid.txt");
            InputStream objects = getClass().getResourceAsStream("objectsLayerGrid.txt");
            BufferedReader baseG = new BufferedReader(new InputStreamReader(base));
            BufferedReader objG = new BufferedReader(new InputStreamReader(objects));
            String bLine = baseG.readLine();
            String objLine = objG.readLine();
            for (int i = 0; i < panel.tileRow-1; i++) {
                for (int j = 0; j < panel.tileCol-1; j++) {
                    String baseColNumbers[] = bLine.split(" ");
                    String objColNumbers[] = objLine.split(" ");
                    int toStringBaseNums = Integer.parseInt(baseColNumbers[j]);
                    int toStringObjNums = Integer.parseInt(objColNumbers[j]);
                    panel.baseLayerGrid[i][j] = toStringBaseNums;
                    panel.objectLayerGrid[i][j] = toStringObjNums;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g2) {
        for (int i = 0; i < panel.tileRow; i++) {
            for (int j = 0; j < panel.tileCol; j++) {
                int baseTileNum = panel.baseLayerGrid[i][j];
                int objTileNum = panel.objectLayerGrid[i][j];
                // <-----Base Layer----->
                g2.drawImage(tile[baseTileNum].image, (i * 32) - panel.playerX, (j * 32) - panel.playerY,
                        tile[baseTileNum].image.getWidth(), tile[baseTileNum].image.getHeight(), null);
                // <-----Objects Layer----->
                g2.drawImage(tile[objTileNum].image, (i * 32) - panel.playerX, (j * 32) - panel.playerY,
                        tile[objTileNum].image.getWidth(), tile[objTileNum].image.getHeight(), null);
            }
        }
    }
}