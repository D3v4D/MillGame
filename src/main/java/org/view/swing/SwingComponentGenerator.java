package org.view.swing;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.util.IntTuple;
import org.util.Line;
import org.util.MapModel;
import org.view.ComponentGenerator;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.awt.Font.getFont;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * SwingComponentGenerator is a concrete implementation of the ComponentGenerator interface using Java Swing.
 * It provides methods to create and manage various UI components such as buttons, labels, and game fields.
 */
public class SwingComponentGenerator implements ComponentGenerator {

    private JFrame frame;
    private ExecutorService backendPool;

    private JLayeredPane layeredPane;

    private ArrayList<JButton> buttonList;
    private ArrayList<JLabel> labelList;

    private ArrayList<JButton> fields;
    private ArrayList<FieldType> types;

    private java.awt.Color lightBrown = new java.awt.Color(Color.lightBrown.r(), Color.lightBrown.g(), Color.lightBrown.b());
    private java.awt.Color darkBrown = new java.awt.Color(Color.darkBrown.r(), Color.darkBrown.g(), Color.darkBrown.b());
    private java.awt.Color lightGreen = new java.awt.Color(Color.lightGreen.r(), Color.lightGreen.g(), Color.lightGreen.b());

    /** Constructor for SwingComponentGenerator.
     *
     * @param backendPool An ExecutorService for handling backend tasks.
     */
    public SwingComponentGenerator(ExecutorService backendPool) {
        this.backendPool = backendPool;
    }

    /**
     * Modifies the text of a button identified by its ID.
     *
     * @param ID   The ID of the button to modify.
     * @param text The new text to set on the button.
     */
    @Override
    public void modifyButtonText(int ID, String text) {
        buttonList.get(ID).setText(text);
    }

    /**
     * Modifies the text of a label identified by its ID.
     *
     * @param ID   The ID of the label to modify.
     * @param text The new text to set on the label.
     */
    @Override
    public void modifyLabelText(int ID, String text) {
        labelList.get(ID).setText(text);
    }

    /**
     * Updates the graphical representation of multiple game fields based on their types.
     *
     * @param fieldList A list of field IDs to update.
     * @param type      The new type to set for the specified fields.
     */
    @Override
    public void updateFieldGraphic(@NotNull java.util.List<Integer> fieldList, FieldType type) {
        fieldList.forEach((i) -> {
            types.set(i, type);
            fields.get(i).repaint();
        });
    }

    /**
     * Updates the graphical representation of a single game field based on its type.
     *
     * @param field The ID of the field to update.
     * @param type  The new type to set for the specified field.
     */
    @Override
    public void updateFieldGraphic(int field, FieldType type) {
        types.set(field, type);
        fields.get(field).repaint();
    }

    /** Draws a button representing a game field based on its type.
     *
     * @param g    The Graphics object used for drawing.
     * @param type The type of the field, determining its appearance.
     */
    private void drawButton(Graphics g, @NotNull FieldType type) {
        Graphics2D g2 = (Graphics2D) g;
        switch (type) {
            case LIGHT -> {
                drawPiece(g2, lightBrown, 40);
            }
            case DARK -> {
                drawPiece(g2, darkBrown, 40);
            }
            case MOVABLE_DARK -> {
                drawHighlightedPiece(g2, java.awt.Color.green, darkBrown, 50);
            }
            case MOVABLE_LIGHT -> {
                drawHighlightedPiece(g2, java.awt.Color.green, lightBrown, 50);
            }
            case MOVABLE_TO -> {
                drawHighlightedPiece(g2, java.awt.Color.green, java.awt.Color.DARK_GRAY, 30);
            }
            case CHOSEN_DARK -> {
                drawHighlightedPiece(g2, lightGreen, darkBrown, 50);
            }
            case CHOSEN_LIGHT -> {
                drawHighlightedPiece(g2,lightGreen, lightBrown, 50);
            }
            case REMOVABLE_LIGHT -> {
                drawCrossedPiece(g2, lightBrown);
            }
            case REMOVABLE_DARK -> {
                drawCrossedPiece(g2, darkBrown);
            }
            case EMPTY -> {
                drawPiece(g2, java.awt.Color.DARK_GRAY, 20);
            }
        }
    }

    /** Draws a highlighted game piece with a specified highlight color and size.
     *
     * @param g2                The Graphics2D object used for drawing.
     * @param highlightColor    The color used for the highlight effect.
     * @param color             The main color of the piece.
     * @param sizewithHighLight The size of the piece including the highlight.
     */
    private void drawHighlightedPiece(@NotNull Graphics2D g2, java.awt.Color highlightColor, java.awt.Color color, int sizewithHighLight) {
        drawPiece(g2, highlightColor, sizewithHighLight);
        drawPiece(g2, color, sizewithHighLight-10);
    }

    /** Draws a game piece with a red cross over it, indicating it is not usable.
     *
     * @param g2    The Graphics2D object used for drawing.
     * @param color The main color of the piece.
     */
    private void drawCrossedPiece(@NotNull Graphics2D g2, java.awt.Color color) {
        drawPiece(g2, color, 40);
        g2.setColor(java.awt.Color.red);
        g2.setStroke(new BasicStroke(5));
        g2.drawLine(5, 5, 45, 45);
        g2.drawLine(5, 45, 45, 5);
    }

    /** Draws a circular game piece of a specified color and size.
     *
     * @param g2       The Graphics2D object used for drawing.
     * @param color    The color of the piece.
     * @param size     The diameter of the piece.
     * @param fullSize The full size of the button area (used for centering).
     */
    private void drawPiece(@NotNull Graphics2D g2, java.awt.Color color, int size, int fullSize) {
        g2.setColor(color);
        g2.fillOval((fullSize - size) / 2, (fullSize - size) / 2, size, size);
    }

    /** Overloaded method to draw a game piece with a default full size of 50.
     *
     * @param g2    The Graphics2D object used for drawing.
     * @param color The color of the piece.
     * @param size  The diameter of the piece.
     */
    private void drawPiece(@NotNull Graphics2D g2, java.awt.Color color, int size) {
        drawPiece(g2, color, size, 50);
    }

    private ArrayList<ArrayList<JPanel>> stacks = new ArrayList<ArrayList<JPanel>>();

    /** Modifies the appearance of a stack of game pieces.
     * Updates the colors of the pieces in the stack based on the specified counts and colors.
     *
     * @param ID     The ID of the stack to modify.
     * @param unit1  The number of pieces to color with color1 from the top.
     * @param color1 The color for the first set of pieces.
     * @param unit2  The number of pieces to color with color2 from the bottom.
     * @param color2 The color for the second set of pieces.
     */
    @Override
    public void modifyStack(int ID, int unit1, @NotNull Color color1, int unit2, @NotNull Color color2) {
        ArrayList<JPanel> stack = stacks.get(ID);
        int totalUnits = stack.size();
        for (int i = 0; i < totalUnits; i++) {
            if (i < unit1) {
                stack.get(i).setVisible(true);
                stack.get(i).setBackground(new java.awt.Color(color1.r(), color1.g(), color1.b()));
            } else if (i >= totalUnits - unit2) {
                stack.get(i).setVisible(true);
                stack.get(i).setBackground(new java.awt.Color(color2.r(), color2.g(), color2.b()));
            } else {
                stack.get(i).setVisible(false);
            }
        }


    }

    @Override
    public int drawStack(int x, int y, int width, int height, int gap, int unit, Color color, int layer, boolean isCorrectPlayer) {
        int id = stacks.size();
        JPanel borderPanel;
        if (isCorrectPlayer)
            borderPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(new java.awt.Color(color.r(), color.g(), color.b()));
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect(0, 0, width + 2 * gap, (height + gap) * unit + gap);
                }
            };
        else
            borderPanel = new JPanel();

        borderPanel.setOpaque(false);
        borderPanel.setBounds(x - gap, y - gap, width + 2 * gap, (height + gap) * unit + gap);
        borderPanel.setLayout(null);

        layeredPane.add(borderPanel, Integer.valueOf(layer));
        stacks.add(new ArrayList<JPanel>());


        for (int i = 0; i < unit; i++) {
            JPanel panel = new JPanel();
            panel.setBackground(new java.awt.Color(color.r(), color.g(), color.b()));
            panel.setBounds(gap, gap + i * (height + gap), width, height);
            panel.setVisible(true);
            borderPanel.add(panel);
            stacks.get(id).add(panel);
        }


        return id;
    }

    @Override
    public void paintBoard(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2, Color color1, Color color2, @NotNull MapModel mapModel, int layer, Consumer<Integer> callback) {
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(fillX1, fillY1, new java.awt.Color(color1.r(), color1.g(), color1.b()), fillX2, fillY2, new java.awt.Color(color2.r(), color2.g(), color2.b())));
                g2d.fillRect(fillX1, fillY1, fillX2, fillY2);


                g2d.setColor(java.awt.Color.darkGray);
                g2d.setStroke(new BasicStroke(5));


                for (Line line : mapModel.lineList) {
                    g2d.drawLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
                }

            }
        };
        boardPanel.setBounds(rectX1, rectY1, rectX2, rectY2);
        layeredPane.add(boardPanel, Integer.valueOf(layer));

        JButton unnamed = new JButton();
        fields = new ArrayList<JButton>();
        types = new ArrayList<FieldType>();
        fields.add(unnamed);
        types.add(null);
        int n = 1;
        for (IntTuple intTuple : mapModel.locationList) {
            int tempN = n++; //variable in lambda expressions must be final, so we store it in a different variable

            JButton temp = getJButton(callback, intTuple, tempN);

            fields.add(temp);
            types.add(FieldType.EMPTY);
            layeredPane.add(temp, Integer.valueOf(layer + 1));
            temp.setVisible(true);
        }
    }

    @NotNull
    private JButton getJButton(Consumer<Integer> callback, @NotNull IntTuple intTuple, int tempN) {
        JButton temp = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawButton(g, types.get(tempN));
            }
        };
        temp.setOpaque(false);
        temp.setBounds(intTuple.getX(), intTuple.getY(), 50, 50);
        temp.setBorderPainted(false);
        temp.setContentAreaFilled(false);
        temp.setFocusPainted(false);
        temp.setVisible(true);

        temp.addActionListener(actionEvent -> callback.accept(tempN));
        return temp;
    }

    @Override
    public void addUITestGif(){
        JLabel gifLabel = new JLabel(new ImageIcon("2c14d0f1ef5cb1ed8413cb8c9e6a1c3e.gif"));
        gifLabel.setBounds(750, 300, 300, 200);
        layeredPane.add(gifLabel, Integer.valueOf(500));
    }

    @Override
    public int addLabel(String text, int x, int y, int width, int height, int size, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int layer) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setHorizontalAlignment(convert(horizontalAlignment));
        label.setVerticalAlignment(convert(verticalAlignment));

        Font baseFont = frame.getFont() != null ? frame.getFont() : new Font("SansSerif", Font.PLAIN, size);
        label.setFont(baseFont.deriveFont((float) size));
        layeredPane.add(label, Integer.valueOf(layer));

        labelList.add(label);
        return labelList.size() - 1;
    }

    @Contract(pure = true)
    private int convert(@NotNull HorizontalAlignment horizontalAlignment) {
        switch (horizontalAlignment) {
            case LEFT:
                return SwingConstants.LEFT;
            case CENTER:
                return SwingConstants.CENTER;
            case RIGHT:
                return SwingConstants.RIGHT;
        }
        throw new IllegalArgumentException("Invalid horizontal alignment");
    }

    @Contract(pure = true)
    private int convert(@NotNull VerticalAlignment verticalAlignment) {
        switch (verticalAlignment) {
            case TOP:
                return SwingConstants.TOP;
            case MIDDLE:
                return SwingConstants.CENTER;
            case BOTTOM:
                return SwingConstants.BOTTOM;
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
        button.addActionListener(e ->
            backendPool.execute(callback)
        );
        button.setBorder(BorderFactory.createLineBorder(new java.awt.Color(100, 100, 100), 3));
        buttonList.add(button);
        layeredPane.add(button, Integer.valueOf(layer));
        layeredPane.revalidate();
        layeredPane.repaint();
        return buttonList.size() - 1;
    }

    @Override
    public void paintBackground(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2, Color color1, Color color2, int layer) {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(fillX1, fillY1, new java.awt.Color(color1.r(), color1.g(), color1.b()), fillX2, fillY2, new java.awt.Color(color2.r(), color2.g(), color2.b())));
                g2d.fillRect(fillX1, fillY1, fillX2, fillY2);
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
        return new SwingComponentGenerator(backendPool);
    }

    @Override
    public void dispose() {
        frame.dispose();
    }
}
