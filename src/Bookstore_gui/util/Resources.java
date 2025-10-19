// Bookstore_gui.util.Resources
package Bookstore_gui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

public final class Resources {
    private Resources(){}

    public static ImageIcon icon(String classpath) {
        if (classpath == null) return null;
        URL url = Resources.class.getResource(classpath);
        return (url != null) ? new ImageIcon(url) : null;
    }

    public static ImageIcon iconStrict(String classpath) {
        try {
            URL url = Resources.class.getResource(classpath);
            if (url == null) throw new IllegalArgumentException("not found: " + classpath);
            Image img = ImageIO.read(url);
            if (img == null) throw new IllegalStateException("ImageIO.read returned null for " + classpath);
            return new ImageIcon(img);
        } catch (Exception ex) {
            System.out.println("[RES] strict load error " + classpath + " -> " + ex);
            return null;
        }
    }

    public static ImageIcon scale(ImageIcon src, int w, int h) {
        if (src == null) return null;
        Image scaled = src.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static ImageIcon placeholder(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(230,230,230));
        g.fillRect(0,0,w,h);
        g.setColor(new Color(160,160,160));
        g.drawRect(0,0,w-1,h-1);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String s = "No Image";
        FontMetrics fm = g.getFontMetrics();
        int tx = (w - fm.stringWidth(s))/2;
        int ty = (h - fm.getHeight())/2 + fm.getAscent();
        g.drawString(s, tx, ty);
        g.dispose();
        return new ImageIcon(img);
    }
}