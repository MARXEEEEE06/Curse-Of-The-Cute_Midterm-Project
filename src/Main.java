import javax.swing.JFrame;

public class Main{
    public static void main(String[] args){
        JFrame frame = new JFrame("Curse of the Cute");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}