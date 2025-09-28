// File: src/Bookstore_gui/app/App.java
package Bookstore_gui.app;

import javax.swing.*;
import Bookstore_gui.view.StartFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // KOR: 시작화면 먼저 / ENG: start from start screen
            new StartFrame().setVisible(true);
        });
    }
}