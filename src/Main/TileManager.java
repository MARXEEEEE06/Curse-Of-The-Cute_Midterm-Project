import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class TileManager {
    GamePanel gp;
    public static final int SCALE = 2;

    // TMX / tileset data
    BufferedImage tilesetImage;
    int tilesetTileWidth;
    int tilesetTileHeight;
    int tilesetColumns;
    int tilesetFirstGid = 1;

    // layers: each layer is int[height][width] with gids
    List<int[][]> layers = new ArrayList<>();
    List<String> layerNames = new ArrayList<>();

    // collision rectangles in map pixel coordinates
    List<Rectangle> mapCollisions = new ArrayList<>();

    // spawn data
    public int spawnX = -1;
    public int spawnY = -1;
    public boolean hasSpawn = false;
    Map<String, Point> namedSpawns = new HashMap<>();
    List<SpawnPoint> spawnPoints = new ArrayList<>();

    // fallback image
    BufferedImage fallbackGrass;

    public static class SpawnPoint {
        public final String name;
        public final String type;
        public final String clazz;
        public final int x;
        public final int y;
        public final Map<String,String> props;

        public SpawnPoint(String name, String type, String clazz, int x, int y, Map<String,String> props) {
            this.name = name;
            this.type = type;
            this.clazz = clazz;
            this.x = x;
            this.y = y;
            this.props = props;
        }
    }

    public TileManager(GamePanel gp) {
        this.gp = gp;
        boolean loaded = false;
        String[] candidates = new String[] {
            "res\\maps\\forest.tmx",
            "res\\maps\\map.tmx",
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
        if (!loaded) loadTileSprite();
    }

    public void loadTileSprite() {
        try {
            fallbackGrass = ImageIO.read(new File("res\\maps\\tiles\\grass1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadMap(String tmxPath) {
        try {
            File tmxFile = new File(tmxPath);
            if (!tmxFile.exists()) return false;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(tmxFile);
            doc.getDocumentElement().normalize();

            // read tileset
            NodeList tilesetNodes = doc.getElementsByTagName("tileset");
            if (tilesetNodes.getLength() == 0) return false;
            Element tilesetEl = (Element) tilesetNodes.item(0);

            Element tileset = tilesetEl;
            File tilesetBaseFile = tmxFile;
            if (tilesetEl.hasAttribute("source") && !tilesetEl.getAttribute("source").isEmpty()) {
                String tsxSrc = tilesetEl.getAttribute("source");
                File tsxFile = new File(tmxFile.getParentFile(), tsxSrc);
                if (!tsxFile.exists()) return false;
                Document tsxDoc = dBuilder.parse(tsxFile);
                tsxDoc.getDocumentElement().normalize();
                NodeList ts = tsxDoc.getElementsByTagName("tileset");
                if (ts.getLength() == 0) return false;
                tileset = (Element) ts.item(0);
                tilesetBaseFile = tsxFile;
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

            // load tileset image
            NodeList imageNodes = tileset.getElementsByTagName("image");
            if (imageNodes.getLength() == 0) return false;
            Element imageEl = (Element) imageNodes.item(0);
            String imgSrc = imageEl.getAttribute("source");
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
                    String[] tokens = dataText.split("[\\r\\n]+|,");
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
                    String compressed = dataEl.getAttribute("compression");
                    if (compressed != null && !compressed.isEmpty()) return false;
                    String cleaned = dataText.replaceAll("\\s+", "");
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
                    return false;
                }

                layers.add(layer);
                layerNames.add(lname != null ? lname : "");
            }

            // read objectgroup for collisions and spawns
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

                // collect all objects as spawns
                NodeList objectsAll = ogEl.getElementsByTagName("object");
                for (int o = 0; o < objectsAll.getLength(); o++) {
                    Element obj = (Element) objectsAll.item(o);
                    String objName = obj.getAttribute("name");
                    String objType = obj.getAttribute("type");
                    String objClass = obj.getAttribute("class");
                    if (objName == null) objName = "";
                    if (objType == null) objType = "";
                    if (objClass == null) objClass = "";
                    try {
                        float ox = Float.parseFloat(obj.getAttribute("x"));
                        float oy = Float.parseFloat(obj.getAttribute("y"));
                        int pixX = Math.round(ox);
                        int pixY = Math.round(oy);

                        // read properties
                        Map<String,String> props = new HashMap<>();
                        NodeList propsNodes = obj.getElementsByTagName("properties");
                        if (propsNodes.getLength() > 0) {
                            Element propsEl = (Element) propsNodes.item(0);
                            NodeList propList = propsEl.getElementsByTagName("property");
                            for (int pi = 0; pi < propList.getLength(); pi++) {
                                Element prop = (Element) propList.item(pi);
                                String pn = prop.getAttribute("name");
                                String pv = prop.getAttribute("value");
                                if (pn != null && !pn.isEmpty()) props.put(pn, pv);
                            }
                        }

                        SpawnPoint sp = new SpawnPoint(objName, objType, objClass, pixX, pixY, props);
                        spawnPoints.add(sp);

                        if (!objName.trim().isEmpty()) namedSpawns.put(objName, new Point(pixX, pixY));
                        if (!objClass.trim().isEmpty()) namedSpawns.put(objClass, new Point(pixX, pixY));

                        if ((objName.toLowerCase().contains("spawn") || objType.toLowerCase().contains("spawn") || objClass.toLowerCase().contains("spawn")) && !hasSpawn) {
                            spawnX = pixX;
                            spawnY = pixY;
                            hasSpawn = true;
                        }
                    } catch (NumberFormatException ex) {
                        // ignore
                    }
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("TileManager.loadMap failed for: " + tmxPath + " -> " + e.getMessage());
            return false;
        }
    }

    public void draw(Graphics g) {
        gp.entitiesCollision();

        if (tilesetImage != null && !layers.isEmpty()) {
            for (int li = 0; li < layers.size(); li++) {
                int[][] layer = layers.get(li);
                int height = layer.length;
                int width = layer[0].length;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int gid = layer[y][x];
                        if (gid == 0) continue;
                        int localId = gid - tilesetFirstGid;
                        if (localId < 0) continue;
                        int sx = (localId % tilesetColumns) * tilesetTileWidth;
                        int sy = (localId / tilesetColumns) * tilesetTileHeight;
                        int dx = (x * gp.tileSize * SCALE) - gp.mapX;
                        int dy = (y * gp.tileSize * SCALE) - gp.mapY;
                        int scaledSize = gp.tileSize * SCALE;
                        g.drawImage(tilesetImage, dx, dy, dx + scaledSize, dy + scaledSize,
                            sx, sy, sx + tilesetTileWidth, sy + tilesetTileHeight, null);
                    }
                }
            }
        } else {
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

    public java.awt.Point getSpawnByName(String name) {
        if (name == null) return null;
        Point p = namedSpawns.get(name);
        if (p != null) return p;
        for (Map.Entry<String, Point> e : namedSpawns.entrySet()) {
            if (e.getKey().equalsIgnoreCase(name)) return e.getValue();
        }
        String lower = name.toLowerCase();
        for (Map.Entry<String, Point> e : namedSpawns.entrySet()) {
            String key = e.getKey().toLowerCase();
            if (key.contains(lower) || lower.contains(key)) return e.getValue();
        }
        return null;
    }

    public List<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }
}