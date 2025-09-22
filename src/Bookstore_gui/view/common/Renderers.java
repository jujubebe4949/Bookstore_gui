package Bookstore_gui.view.common;

import Bookstore_gui.util.Money;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public final class Renderers {
    private Renderers(){}

    /** KOR: 가격 우측정렬+통화 / ENG: right-aligned currency */
    public static class PriceRenderer extends DefaultTableCellRenderer {
        @Override protected void setValue(Object v) {
            setHorizontalAlignment(SwingConstants.RIGHT);
            try { setText(Money.fmt(Double.parseDouble(String.valueOf(v)))); }
            catch (Exception e){ setText(String.valueOf(v)); }
        }
    }

    /** KOR: 재고 배지 / ENG: stock badge */
    public static class StockRenderer extends DefaultTableCellRenderer {
        @Override protected void setValue(Object v) {
            setHorizontalAlignment(SwingConstants.CENTER);
            int n; try { n = Integer.parseInt(String.valueOf(v)); } catch (Exception e){ n = 0; }
            if (n <= 0) { setForeground(new Color(185, 20, 20)); setFont(getFont().deriveFont(Font.BOLD)); setText("OUT"); }
            else if (n <= 2) { setForeground(new Color(210, 120, 0)); setFont(getFont().deriveFont(Font.BOLD)); setText("Low ("+n+")"); }
            else { setForeground(Color.DARK_GRAY); setFont(getFont().deriveFont(Font.PLAIN)); setText(String.valueOf(n)); }
        }
    }

    /** KOR: 썸네일(Icon) / ENG: thumbnail icon */
    public static class ThumbRenderer extends DefaultTableCellRenderer {
        @Override public void setValue(Object v) {
            setHorizontalAlignment(SwingConstants.CENTER);
            setText(null);
            setIcon(v instanceof Icon ? (Icon)v : null);
        }
    }
    
}
