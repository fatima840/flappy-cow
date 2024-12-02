import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Cow Game");
        CowGame gamePanel = new CowGame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(gamePanel.getPreferredSize());
        frame.add(gamePanel);
        frame.pack();
        frame.setVisible(true);
    }
}
