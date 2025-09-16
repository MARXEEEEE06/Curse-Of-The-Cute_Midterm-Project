import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel {
    // movement
    Timer gameTimer;
    private int speed = 5;
    private int characterXPos = 300, characterYPos = 200;
    private BufferedImage[] upAnimation = new BufferedImage[4];
    private BufferedImage[] downAnimation = new BufferedImage[4];
    private BufferedImage[] leftAnimation = new BufferedImage[4];
    private BufferedImage[] rightAnimation = new BufferedImage[4];
    private BufferedImage currentImage;
    private int whatFrame = 0;
    private int frameDelay = 0; // counts frames
    private int frameSpeed = 3; // higher = slower animation
    // map
    private int gamePanelSizeX = 800;
    private int gamePanelSizeY = 600;
    private int testMapXPos;
    private int testMapYPos;
    int sizeTestMapW;
    int sizeTestMapH;
    private BufferedImage testMap;
    // character
    int sizeCharacterW;
    int sizeCharacterH;

    // character movement
    public void moveUp() {
        //map border collision
        if(testMapYPos > 0){
            System.out.println("Border reached");
            if(characterYPos > -20){
                characterYPos -= speed;
            }
        }else{
            testMapYPos += speed;
            characterYPos -= speed;
        }
        System.out.println("mapX:" + testMapXPos);
        System.out.println("mapY:" + testMapYPos);
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            currentImage = upAnimation[whatFrame];
            frameDelay = 0;
        }

        repaint();
    }

    public void moveDown() {
        if(testMapYPos < -415){
            System.out.println("Border reached");
            if(characterYPos < -435){
                characterYPos += speed;
            }
        }else{
            testMapYPos -= speed;
            characterYPos += speed;
        }
        System.out.println("mapX:" + testMapXPos);
        System.out.println("mapY:" + testMapYPos);
        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            currentImage = downAnimation[whatFrame];
            frameDelay = 0;
        }

        repaint();
    }

    public void moveLeft() {
        if (testMapXPos > 0) {
            System.out.println("Border reached");
            if(characterXPos > -20){
                characterXPos -= speed;
            }
        } else {
            testMapXPos += speed;
            characterXPos -= speed;
        }

        System.out.println("mapX:" + testMapXPos);
        System.out.println("mapY:" + testMapYPos);
        System.out.println("charX:" + characterXPos);
        System.out.println("charY:" + characterYPos);

        frameDelay++;
        if (frameDelay >= frameSpeed) {
            whatFrame = (whatFrame + 1) % 4;
            currentImage = leftAnimation[whatFrame];
            frameDelay = 0;
        }
        repaint();
    }

    public void moveRight() {
        if (testMapXPos < -224) {
            if (characterXPos < 700)
                characterXPos += speed;
            System.out.println("Border reached");
            
        } else {
            characterXPos += speed;
            testMapXPos -= speed;
        }

        System.out.println("mapX:" + testMapXPos);
        System.out.println("mapY:" + testMapYPos);
        System.out.println("charX:" + characterXPos);
        System.out.println("charY:" + characterYPos);
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
    public GamePanel() {
        loadSprites();
        KeyHandler keyH = new KeyHandler(this);
        this.setFocusable(true);
        this.addKeyListener(keyH);
        this.setPreferredSize(new Dimension(gamePanelSizeX, gamePanelSizeY));
        this.setBackground(Color.gray);
        keyH.startGameLoop();
    };

    private void loadSprites() {
        for (int i = 0; i < 4; i++) {
            try {
                currentImage = ImageIO.read(new File("sprite\\FELIS\\felis_Down1.png"));
                upAnimation[i] = ImageIO.read(new File("sprite\\FELIS\\felis_Up" + (i + 1) + ".png"));
                downAnimation[i] = ImageIO.read(new File("sprite\\FELIS\\felis_Down" + (i + 1) + ".png"));
                leftAnimation[i] = ImageIO.read(new File("sprite\\FELIS\\felis_Left" + (i + 1) + ".png"));
                rightAnimation[i] = ImageIO.read(new File("sprite\\FELIS\\felis_Right" + (i + 1) + ".png"));
                sizeCharacterW = downAnimation[i].getWidth();
                sizeCharacterH = downAnimation[i].getHeight();
                //center character to gamepanel on start
                characterXPos = (gamePanelSizeX - sizeCharacterW) / 2;
                characterYPos = (gamePanelSizeY - sizeCharacterH) / 2;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("sizeCharacterW: " + sizeCharacterW);
        System.out.println("sizeCharacterH: " + sizeCharacterH);
        try {
            testMap = ImageIO.read(new File("sprite\\grassChunk.png"));

            sizeTestMapW = testMap.getWidth()*16;
            sizeTestMapH = testMap.getHeight()*16;
            //center map to gamepanel on start
            testMapXPos = (gamePanelSizeX-sizeTestMapW)/2;
            testMapYPos = (gamePanelSizeY + -sizeTestMapH)/2;
            System.out.println("Map Height: " + sizeTestMapH);
            System.out.println("Map Width: " + sizeTestMapW);
            System.out.println("Map X Position: " + testMapXPos);
            System.out.println("Map Y Position: " + testMapYPos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //bottom layer
        if (testMap != null){
            g.drawImage(testMap, testMapXPos, testMapYPos, sizeTestMapW, sizeTestMapH, null);
        }
        if (currentImage != null){
            g.drawImage(currentImage, characterXPos, characterYPos, (sizeCharacterW * 2), (sizeCharacterH * 2), null);
        }
        g.setColor(Color.RED);
        g.fillRect(0, 300, 800, 1);
        g.setColor(Color.RED);
        g.fillRect(400, 0, 1, 600);
        //top layer
    }
}