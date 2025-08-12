package TestProject;

import java.awt.*;
import javax.swing.*;

public class StarDrawer {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Star");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new StarPanel());
        frame.setVisible(true);
    }
}

class StarPanel extends JPanel {
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int[] x = {200, 220, 270, 230, 250, 200, 150, 170, 130, 180};
        int[] y = {120, 180, 180, 210, 270, 240, 270, 210, 180, 180};
        g.setColor(Color.RED);
        g.fillPolygon(x, y, x.length);
    }
}
