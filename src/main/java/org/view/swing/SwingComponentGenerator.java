package org.view.swing;

import org.view.base.ComponentGenerator;

import javax.swing.*;
import java.awt.*;
import org.model.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class SwingComponentGenerator implements ComponentGenerator {

    private JFrame frame;

    private JLayeredPane layeredPane;

    private ArrayList<JButton> componentList;

    @Override
    public void modifyButtonText(int ID, String text){
        componentList.get(ID).setText(text);
    }

    @Override
    public void generateBase(int x, int y) {
        componentList = new ArrayList<JButton>();
        frame = new JFrame();



        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setPreferredSize(new Dimension(x, y));
        frame.setSize(x, y);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        layeredPane = new JLayeredPane();
        frame.add(layeredPane);
        layeredPane.setLayout(null);
        layeredPane.setOpaque(false);
        layeredPane.setBounds(0, 0, x, y);
        layeredPane.setPreferredSize(new Dimension(x, y));


    }

    @Override
    public int addButton(String text, int x, int y, int width, int height, Runnable callback) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> callback.run());
        button.setBorder(BorderFactory.createLineBorder(new java.awt.Color(100, 100, 100), 3));
        componentList.add(button);
        layeredPane.add(button, Integer.valueOf(10));
        layeredPane.revalidate();
        layeredPane.repaint();
        return componentList.size()-1;
    }

    @Override
    public void paintBackground(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2  , Color color1, Color color2){
        JPanel backgroundPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(fillX1, fillY1, new java.awt.Color(color1.r(), color1.g(), color1.b()), fillX2, fillY2, new java.awt.Color(color2.r(), color2.g(), color2.b())));
                g2d.fillRect(rectX1, rectY1, rectX2, rectY2);
            }
        };
        backgroundPanel.setBounds(rectX1, rectY1, rectX2, rectY2);
        layeredPane.add(backgroundPanel, Integer.valueOf(0));
    }


    @Override
    public <T> int addComboBox(T[] items, T selectedItem, int x, int y, int width, int height, Consumer<String> callback) {
        JComboBox<T> comboBox = new JComboBox<T>(items);
        comboBox.setSelectedItem(selectedItem);
        comboBox.setBounds(x, y, width, height);
        comboBox.addActionListener(e -> callback.accept(comboBox.getSelectedItem().toString()));
        layeredPane.add(comboBox, Integer.valueOf(5));
        return 0;
    }
    @Override
    public int addTable(){
        return 0;
    }

    @Override
    public void show() {
        frame.setVisible(true);
    }

    @Override
    public void hide() {
        frame.setVisible(false);
    }

    @Override
    public ComponentGenerator copy() {
        return new SwingComponentGenerator();
    }
}
