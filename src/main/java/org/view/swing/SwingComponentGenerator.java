package org.view.swing;

import org.util.Line;
import org.view.base.ComponentGenerator;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.awt.Font.getFont;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class SwingComponentGenerator implements ComponentGenerator {

    private JFrame frame;

    private JLayeredPane layeredPane;

    private ArrayList<JButton> buttonList;
    private ArrayList<JLabel> labelList;

    @Override
    public void modifyButtonText(int ID, String text){
        buttonList.get(ID).setText(text);
    }

    @Override
    public void paintBoard(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2, Color color1, Color color2, List<Line> lineList, int layer){
        JPanel backgroundPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(fillX1, fillY1, new java.awt.Color(color1.r(), color1.g(), color1.b()), fillX2, fillY2, new java.awt.Color(color2.r(), color2.g(), color2.b())));
                g2d.fillRect(rectX1, rectY1, rectX2, rectY2);


                g2d.setColor(java.awt.Color.darkGray);
                g2d.setStroke(new BasicStroke(5));


                for (Line line : lineList) {
                    g2d.drawLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
                }

            }
        };
        backgroundPanel.setBounds(rectX1, rectY1, rectX2, rectY2);
        layeredPane.add(backgroundPanel, Integer.valueOf(layer));
    }

    @Override
    public int addLabel(String text, int x, int y, int width, int height, int size, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int layer){
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setHorizontalAlignment(convert(horizontalAlignment));
        label.setVerticalAlignment(convert(verticalAlignment));
//        label.setFont(frame.getFont().deriveFont((float)size));
        layeredPane.add(label, Integer.valueOf(layer));
        return labelList.size();
    }

    private int convert(HorizontalAlignment horizontalAlignment){
        switch (horizontalAlignment){
            case LEFT: return SwingConstants.LEFT;
            case CENTER: return SwingConstants.CENTER;
            case RIGHT: return SwingConstants.RIGHT;
        }
        throw new IllegalArgumentException("Invalid horizontal alignment");
    }
    private int convert(VerticalAlignment  verticalAlignment){
        switch (verticalAlignment){
            case TOP: return SwingConstants.TOP;
            case MIDDLE: return SwingConstants.CENTER;
            case BOTTOM: return SwingConstants.BOTTOM;
        }
        throw new IllegalArgumentException("Invalid vertical alignment");
    }

    @Override
    public void generateBase(int x, int y) {
        buttonList = new ArrayList<JButton>();
        labelList = new ArrayList<JLabel>();
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
    public int addButton(String text, int x, int y, int width, int height, Runnable callback, int layer) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> callback.run());
        button.setBorder(BorderFactory.createLineBorder(new java.awt.Color(100, 100, 100), 3));
        buttonList.add(button);
        layeredPane.add(button, Integer.valueOf(layer));
        layeredPane.revalidate();
        layeredPane.repaint();
        return buttonList.size()-1;
    }

    @Override
    public void paintBackground(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2  , Color color1, Color color2, int layer){
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
        layeredPane.add(backgroundPanel, Integer.valueOf(layer));
    }


    @Override
    public <T> int addComboBox(T[] items, T selectedItem, int x, int y, int width, int height, Consumer<String> callback, int layer) {
        JComboBox<T> comboBox = new JComboBox<T>(items);
        comboBox.setSelectedItem(selectedItem);
        comboBox.setBounds(x, y, width, height);
        comboBox.addActionListener(e -> callback.accept(comboBox.getSelectedItem().toString()));
        layeredPane.add(comboBox, Integer.valueOf(layer));
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
