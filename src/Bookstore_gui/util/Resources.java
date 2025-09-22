// File: src/Bookstore_gui/util/Resources.java
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

    public static ImageIcon scale(ImageIcon raw, int w, int h) {
        if (raw == null) return null;
        Image img = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static ImageIcon placeholder(int w, int h) {
        BufferedImage img = new BufferedImage(Math.max(1,w), Math.max(1,h), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(245,245,245)); g.fillRect(0,0,w,h);
        g.setColor(new Color(200,200,200)); g.drawRect(0,0,w-1,h-1);
        g.setColor(new Color(150,150,150)); g.drawString("No Image", 8, 16);
        g.dispose();
        return new ImageIcon(img);
    }

    // --- NEW: 엄격 로더 (ImageIO로 진짜 디코딩) ---
    public static ImageIcon iconStrict(String classpath) {
        try {
            URL url = Resources.class.getResource(classpath);
            System.out.println("[RES] strict load " + classpath + " url=" + url);
            if (url == null) return null;
            BufferedImage bi = ImageIO.read(url); // 디코딩 실패 시 null
            if (bi == null) {
                System.out.println("[RES] ImageIO.read returned null for " + classpath);
                return null;
            }
            return new ImageIcon(bi);
        } catch (Exception ex) {
            System.out.println("[RES] strict load error " + classpath + " -> " + ex);
            return null;
        }
    }
        public static ImageIcon image(String classpath, int w, int h) {
        ImageIcon raw = iconStrict(classpath); // why: 손상/포맷문제 잡으려고 엄격 로더 사용
        if (raw == null) return null;
        return scale(raw, w, h);
    }
}