
package Bookstore_gui;

import Bookstore_gui.view.StartFrame;

public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new StartFrame().setVisible(true));
    }
}