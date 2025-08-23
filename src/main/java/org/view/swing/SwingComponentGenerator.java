package org.view.swing;

import org.view.base.ComponentGenerator;

import javax.swing.*;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class SwingComponentGenerator implements ComponentGenerator {

    JFrame frame;
    @Override
    public void generateBase(int x, int y) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setPreferredSize(new Dimension(800, 450));
        frame.setSize(800, 600);
        frame.setResizable(true);


    }

    @Override
    public void addButton(String text, int x, int y, int width, int height, int gap, Runnable callback) {

    }



    @Override
    public void render() {
        frame.setVisible(true);
    }
}
