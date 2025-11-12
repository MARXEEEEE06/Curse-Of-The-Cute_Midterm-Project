import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class TileManager {
    GamePanel gp;
    // TMX / tileset data
    BufferedImage tilesetImage;
    int tilesetTileWidth;
    int tilesetTileHeight;
    int tilesetColumns;
    int tilesetFirstGid = 1;
    
    // scale factor for rendering (2 = 2x size)
    public static final int SCALE = 2;

    // layers: each layer is int[height][width] with gids
    List<int[][]> layers = new ArrayList<>();
    List<String> layerNames = new ArrayList<>();

    // collision rectangles in map pixel coordinates (not adjusted by gp.mapX/Y)
    List<Rectangle> mapCollisions = new ArrayList<>();

    // optional spawn point from map (map pixel coordinates)
    public int spawnX = -1;
    public int spawnY = -1;
    public boolean hasSpawn = false;

    // fallback image used if TMX fails to load
    BufferedImage fallbackGrass;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        // try several common TMX paths first, then scan res/maps for any .tmx file
        boolean loaded = false;
        String[] candidates = new String[] {
            "res\\maps\\tiles\\map.tmx",
            "res\\maps\\map.tmx",
            "res\\maps\\forest.tmx",
            "res\\maps\\tileset.tmx"
        };
        for (String p : candidates) {
            if (loadMap(p)) { loaded = true; break; }
        }
        if (!loaded) {
            File mapsDir = new File("res\\maps");
            if (mapsDir.exists() && mapsDir.isDirectory()) {
                File[] files = mapsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".tmx"));
                if (files != null) {
                    for (File f : files) {
                        if (loadMap(f.getPath())) { loaded = true; break; }
                    }
                }
            }
        }
        if (!loaded) {
            loadTileSprite();
        }
    }

    public void loadTileSprite() {
        try {
            fallbackGrass = ImageIO.read(new File("res\\maps\\tiles\\grass1.png"));
        } catch (IOException e) {
            // nothing critical; keep null
            e.printStackTrace();
        }
    }

    /**
     * Load a TMX map. Supports simple embedded tileset and CSV-encoded layer data.
     * Returns true if load succeeds.
     */
    public boolean loadMap(String tmxPath) {
        try {
            File tmxFile = new File(tmxPath);
            if (!tmxFile.exists()) return false;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(tmxFile);
            doc.getDocumentElement().normalize();

            // read tileset (first one). Handle external TSX referenced by "source"
            NodeList tilesetNodes = doc.getElementsByTagName("tileset");
            if (tilesetNodes.getLength() == 0) return false;
            Element tilesetEl = (Element) tilesetNodes.item(0);

            Element tileset = tilesetEl; // may be replaced if external
            File tilesetBaseFile = tmxFile; // used to resolve relative image path
            if (tilesetEl.hasAttribute("source") && !tilesetEl.getAttribute("source").isEmpty()) {
                // external TSX
                String tsxSrc = tilesetEl.getAttribute("source");
                File tsxFile = new File(tmxFile.getParentFile(), tsxSrc);
                if (!tsxFile.exists()) return false;
                Document tsxDoc = dBuilder.parse(tsxFile);
                tsxDoc.getDocumentElement().normalize();
                NodeList ts = tsxDoc.getElementsByTagName("tileset");
                if (ts.getLength() == 0) return false;
                tileset = (Element) ts.item(0);
                tilesetBaseFile = tsxFile;
                // firstgid is defined in the original tileset element in TMX
                String firstGidStr = tilesetEl.getAttribute("firstgid");
                if (firstGidStr != null && !firstGidStr.isEmpty()) tilesetFirstGid = Integer.parseInt(firstGidStr);
            } else {
                String firstGidStr = tilesetEl.getAttribute("firstgid");
                if (firstGidStr != null && !firstGidStr.isEmpty()) tilesetFirstGid = Integer.parseInt(firstGidStr);
            }

            String tileWidthStr = tileset.getAttribute("tilewidth");
            String tileHeightStr = tileset.getAttribute("tileheight");
            tilesetTileWidth = Integer.parseInt(tileWidthStr);
            tilesetTileHeight = Integer.parseInt(tileHeightStr);

            // image
            NodeList imageNodes = tileset.getElementsByTagName("image");
            if (imageNodes.getLength() == 0) return false;
            Element imageEl = (Element) imageNodes.item(0);
            String imgSrc = imageEl.getAttribute("source");
            // resolve relative path against the tileset file location
            File imgFile = new File(tilesetBaseFile.getParentFile(), imgSrc);
            tilesetImage = ImageIO.read(imgFile);
            tilesetColumns = tilesetImage.getWidth() / tilesetTileWidth;

            // read layers
            NodeList layerNodes = doc.getElementsByTagName("layer");
            for (int ln = 0; ln < layerNodes.getLength(); ln++) {
                Element layerEl = (Element) layerNodes.item(ln);
                int width = Integer.parseInt(layerEl.getAttribute("width"));
                int height = Integer.parseInt(layerEl.getAttribute("height"));
                String lname = layerEl.getAttribute("name");

                NodeList dataNodes = layerEl.getElementsByTagName("data");
                if (dataNodes.getLength() == 0) continue;
                Element dataEl = (Element) dataNodes.item(0);
                String encoding = dataEl.getAttribute("encoding");
                String dataText = dataEl.getTextContent().trim();
                int[][] layer = new int[height][width];

                if (encoding == null || encoding.isEmpty() || "csv".equalsIgnoreCase(encoding)) {
                    // CSV data
                    String[] tokens = dataText.split("[\r\n]+|,");
                    int idx = 0;
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            while (idx < tokens.length && tokens[idx].trim().isEmpty()) idx++;
                            if (idx >= tokens.length) break;
                            layer[y][x] = Integer.parseInt(tokens[idx].trim());
                            idx++;
                        }
                    }
                } else if ("base64".equalsIgnoreCase(encoding)) {
                    // base64 (possibly uncompressed) -- Tiled writes little-endian 32-bit gids
                    String compressed = dataEl.getAttribute("compression");
                    if (compressed != null && !compressed.isEmpty()) {
                        // not supported
                        return false;
                    }
                    String cleaned = dataText.replaceAll("\s+", "");
                    byte[] decoded = java.util.Base64.getDecoder().decode(cleaned);
                    java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(decoded).order(java.nio.ByteOrder.LITTLE_ENDIAN);
                    int total = width * height;
                    for (int i = 0; i < total; i++) {
                        int gid = bb.getInt();
                        int y = i / width;
                        int x = i % width;
                        layer[y][x] = gid;
                    }
                } else {
                    // unsupported encoding
                    return false;
                }

                layers.add(layer);
                layerNames.add(lname != null ? lname : "");
            }

            // read objectgroup for collisions (look for name containing 'collision')
            NodeList objGroups = doc.getElementsByTagName("objectgroup");
            for (int og = 0; og < objGroups.getLength(); og++) {
                Element ogEl = (Element) objGroups.item(og);
                String oname = ogEl.getAttribute("name");
                if (oname == null) oname = "";
                if (oname.toLowerCase().contains("collision")) {
                    NodeList objects = ogEl.getElementsByTagName("object");
                    for (int o = 0; o < objects.getLength(); o++) {
                        Element obj = (Element) objects.item(o);
                        float ox = Float.parseFloat(obj.getAttribute("x"));
                        float oy = Float.parseFloat(obj.getAttribute("y"));
                        float ow = obj.hasAttribute("width") ? Float.parseFloat(obj.getAttribute("width")) : 0f;
                        float oh = obj.hasAttribute("height") ? Float.parseFloat(obj.getAttribute("height")) : 0f;
                        Rectangle r = new Rectangle(Math.round(ox), Math.round(oy), Math.round(ow), Math.round(oh));
                        mapCollisions.add(r);
                    }
                }
                // also check for spawn objects in any objectgroup (common patterns: name or type contains "spawn")
                NodeList objectsAll = ogEl.getElementsByTagName("object");
                for (int o = 0; o < objectsAll.getLength(); o++) {
                    Element obj = (Element) objectsAll.item(o);
                    String objName = obj.getAttribute("name");
                    String objType = obj.getAttribute("type");
                    if (objName == null) objName = "";
                    if (objType == null) objType = "";
                    if (objName.toLowerCase().contains("spawn") || objType.toLowerCase().contains("spawn")) {
                        try {
                            float ox = Float.parseFloat(obj.getAttribute("x"));
                            float oy = Float.parseFloat(obj.getAttribute("y"));
                            spawnX = Math.round(ox);
                            spawnY = Math.round(oy);
                            hasSpawn = true;
                        } catch (Exception ex) {
                            // ignore malformed spawn object
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            // parsing/loading failed: return false so caller can fallback
            e.printStackTrace();
            return false;
        }
    }

    public void draw(Graphics g) {
        gp.entitiesCollision();

        // If TMX map loaded, render layers from tileset
        if (tilesetImage != null && !layers.isEmpty()) {
            for (int li = 0; li < layers.size(); li++) {
                int[][] layer = layers.get(li);
                int height = layer.length;
                int width = layer[0].length;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int gid = layer[y][x];
                        if (gid == 0) continue; // empty
                        int localId = gid - tilesetFirstGid;
                        if (localId < 0) continue;
                        int sx = (localId % tilesetColumns) * tilesetTileWidth;
                        int sy = (localId / tilesetColumns) * tilesetTileHeight;
                        // scale tile position and size by SCALE factor
                        int dx = (x * gp.tileSize * SCALE) - gp.mapX;
                        int dy = (y * gp.tileSize * SCALE) - gp.mapY;
                        int scaledSize = gp.tileSize * SCALE;
                        // draw scaled to gp.tileSize * SCALE
                        g.drawImage(tilesetImage, dx, dy, dx + scaledSize, dy + scaledSize,
                            sx, sy, sx + tilesetTileWidth, sy + tilesetTileHeight, null);
                    }
                }
            }

            // apply collision rectangles from map (map pixel coords -> screen coords by subtracting mapX/Y)
            // also scale collision rectangles by SCALE factor
            for (int i = 0; i < mapCollisions.size(); i++) {
                Rectangle r = mapCollisions.get(i);
                int rx = (r.x * SCALE) - gp.mapX;
                int ry = (r.y * SCALE) - gp.mapY;
                int rw = r.width * SCALE;
                int rh = r.height * SCALE;
                // map first two collisions to gp fields if available
                if (i == 0 && gp.borderCollision != null) gp.borderCollision.setBounds(rx, ry, rw, rh);
                if (i == 1 && gp.tree1Collision != null) gp.tree1Collision.setBounds(rx, ry, rw, rh);
                // draw debug rectangle
                g.drawRect(rx, ry, rw, rh);
            }
        } else {
            // fallback: simple fill with fallbackGrass (or nothing) and recreate previous collision rects
            for (int row = 0; row < gp.tileRow; row++) {
                for (int col = 0; col < gp.tileCol; col++) {
                    if (fallbackGrass != null) {
                        g.drawImage(fallbackGrass, (col * gp.tileSize) - gp.mapX, (row * gp.tileSize) - gp.mapY,
                            gp.tileSize, gp.tileSize, null);
                    }
                }
            }
        }
    }

    public List<Rectangle> getMapCollisions() {
        return mapCollisions;
    }
}