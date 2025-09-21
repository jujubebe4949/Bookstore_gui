package Bookstore_gui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

public class BackgroundPanel extends JPanel {
    private BufferedImage img;

    public BackgroundPanel(String classpath) {
        setLayout(new BorderLayout());
        setOpaque(true);
        try {
            URL url = getClass().getResource(classpath);
            if (url != null) img = ImageIO.read(url);
            else System.err.println("[BackgroundPanel] not found: " + classpath);
        } catch (Exception ignored) { img = null; }
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img == null) { g.setColor(new Color(245,245,245)); g.fillRect(0,0,getWidth(),getHeight()); return; }
        double pw = getWidth(), ph = getHeight(), iw = img.getWidth(), ih = img.getHeight();
        double s = Math.max(pw/iw, ph/ih); // why: cover 효과
        int w = (int)(iw*s), h = (int)(ih*s);
        int x = (int)((pw - w)/2), y = (int)((ph - h)/2);
        g.drawImage(img, x, y, w, h, this);
    }
}