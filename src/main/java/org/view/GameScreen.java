package org.view;

import java.awt.Color;
import org.controller.*;
import org.model.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


import javax.swing.*;

/**
 * Initializes the game screen with a graphical user interface for the board and menu.
 */
public class GameScreen extends JFrame {
    private JButton[] fields;
    private JPanel mainBoard;
    private JPanel sideMenu;
    private BoardModel.Color[] colors;
    private JLabel label = new JLabel();
    private JButton saveButton;
    private JButton exitButton;
    private JLayeredPane jLayeredPane = new JLayeredPane();
    private JPanel winBox = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(new GradientPaint(0, 0, new java.awt.Color(0, 106, 133), 250, 550, new java.awt.Color(0, 0, 250)));
            g2d.fillRect(0, 0, 550, 450);
        }
    };
    private ConcurrentStack<Integer> pressed;

    private int phase = 0;
    private int piecesPlayer;
    private int piecesPlayer1;
    private int piecesPlayer2;
    private int piecesOffBoardPlayer1 = 0;
    private int piecesOffBoardPlayer2 = 0;

    /**
     * Initializes the game screen with the given game data.
     *
     * @param p        The stack of pressed buttons.
     * @param mapModel The model representing the game board.
     * @param initializer  The object responsible for saving and managing the game.
     * @throws InterruptedException If the thread is interrupted during initialization.
     */

    public GameScreen(ConcurrentStack<Integer> p, MapModel mapModel, Initializer initializer) throws InterruptedException {
        ArrayList<IntTuple> locations = mapModel.locationList;
        piecesPlayer = piecesPlayer1 = piecesPlayer2 = mapModel.pieces;
        pressed = p;
        this.setPreferredSize(new Dimension(1050, 780));
        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        label.setBounds(0, 500, 300, 50);
        label.setFont(getFont().deriveFont(32f));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        mainBoard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(750, 750, new java.awt.Color(205, 133, 63), 0, 0, new java.awt.Color(196, 164, 132)));
                g2d.fillRect(0, 0, 750, 750);

                g2d.setColor(java.awt.Color.darkGray);
                g2d.setStroke(new BasicStroke(5));

                for (Line line : mapModel.lineList) {
                    g2d.drawLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
                }
            }
        };
        mainBoard.setBounds(0, 0, 750, 750);
        mainBoard.setLayout(null);

        saveButton = new JButton("Save");
        saveButton.setBounds(75, 600, 150, 50);
        saveButton.addActionListener(e -> {
            initializer.saveGame("saves/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".json");
        });

        exitButton = new JButton("Exit To Menu");
        exitButton.setBounds(75, 675, 150, 50);
        exitButton.addActionListener(e -> {
            try {
                pressed.push(-69);
                this.dispose();
            } catch (Exception e2) {

            }
        });

        sideMenu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMenu((Graphics2D) g);
            }
        };

        sideMenu.setBackground(Color.GRAY);
        sideMenu.setBounds(750, 0, 300, 750);
        sideMenu.add(label);
        sideMenu.setLayout(null);
        sideMenu.add(saveButton);
        sideMenu.add(exitButton);
        sideMenu.setVisible(true);

        jLayeredPane.add(sideMenu, JLayeredPane.DEFAULT_LAYER);
        setLayout(null);
        fields = new JButton[locations.size() + 1];
        colors = new BoardModel.Color[locations.size() + 1];

        //mezők

        for (int i = 0; i < locations.size(); i++) {
            colors[i + 1] = BoardModel.Color.BLANK;
            int index = i;
            fields[i + 1] = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawButton((Graphics2D) g, colors[index + 1]);
                }
            };
            fields[index + 1].setOpaque(false);
            fields[index + 1].setContentAreaFilled(false);
            fields[index + 1].setBorderPainted(false);
            fields[index + 1].setFocusPainted(false);
            fields[index + 1].setVisible(true);
            fields[index + 1].setBounds(locations.get(index).getX(), locations.get(index).getY(), 50, 50);
            fields[index + 1].addActionListener(e -> {
                try {
                    pressed.push(index + 1);
                } catch (Exception e1) {
//                    System.out.println("GATYA");
                }
//                label.setText(String.valueOf(index + 1));
            });
            jLayeredPane.add(fields[i + 1], JLayeredPane.DEFAULT_LAYER);
        }
        jLayeredPane.add(mainBoard, JLayeredPane.DEFAULT_LAYER);
        this.setTitle(":)");
        revalidate();
        mainBoard.revalidate();
        mainBoard.repaint();
        this.setVisible(true);
        this.pack();
        jLayeredPane.revalidate();
        jLayeredPane.repaint();
        jLayeredPane.setBounds(0, 0, 1050, 750);
        add(jLayeredPane);

    }

    /**
     * Displays a win screen indicating the winner of the game.
     *
     * @param player1 True if player 1 (light) wins, false if player 2 (dark) wins.
     */
    public void winScreen(boolean player1) {
        try {
            winBox.setBounds(250, 150, 550, 350);
            winBox.setLayout(null);
            JLabel winText = new JLabel(player1 ? "LIGHT WON" : "DARK WON");
            winText.setHorizontalAlignment(SwingConstants.CENTER);
            winText.setVerticalAlignment(SwingConstants.CENTER);
            winBox.add(winText);
            winText.setFont(getFont().deriveFont(32f));
            winText.setBounds(0, 25, 550, 50);
            jLayeredPane.add(winBox, JLayeredPane.PALETTE_LAYER);
            JButton rematchButton = new JButton("Rematch");
            rematchButton.addActionListener(e -> rematch());
            rematchButton.setBounds(175, 150, 200, 50);
            winBox.add(rematchButton);
            JButton exitToMenu = new JButton("Exit To Menu");
            exitToMenu.addActionListener(e -> {
                try {
                    this.dispose();
                    pressed.push(-69);
                } catch (Exception asdf) {

                }
            });
            exitToMenu.setBounds(175, 250, 200, 50);
            winBox.add(exitToMenu);
            winBox.repaint();
            winBox.revalidate();
            jLayeredPane.revalidate();
        } catch (Exception e) {

        }

    }

    /**
     * Starts a rematch by throwing exception on the other thread.
     */
    private void rematch() {
        try {
            pressed.push(-420);
            this.dispose();
        } catch (Exception e) {

        }
    }

    /**
     * Draws the side menu based on the current phase of the game.
     *
     * @param g2d The Graphics2D object used to draw the menu.
     */
    private void drawMenu(Graphics2D g2d) {
        g2d.setPaint(new GradientPaint(300, 750, new Color(95, 95, 95), 0, 0, new Color(150, 150, 150)));
        g2d.fillRect(0, 0, 300, 750);
        switch (phase) {
            case 1 -> { //start phase
                g2d.setColor(new Color(255, 222, 173));
                for (int i = 0; i < piecesPlayer1; i++) {
                    g2d.fillRect(75, piecesPlayer * 30 - i * 30, 50, 20);
                }
                g2d.setColor(new Color(100, 0, 0));

                for (int i = 0; i < piecesPlayer2; i++) {
                    g2d.fillRect(175, piecesPlayer * 30 - i * 30, 50, 20);
                }
            }
            case 2 -> {

            }
            default -> {
                label.setText("vmi nem jau");
            }
        }

        g2d.setColor(new Color(255, 222, 173));
        for (int i = 0; i < piecesOffBoardPlayer1; i++) {
            g2d.fillRect(175, (i + 1) * 30, 50, 20);
        }

        g2d.setColor(new Color(100, 0, 0));

        for (int i = 0; i < piecesOffBoardPlayer2; i++) {
            g2d.fillRect(75, (i + 1) * 30, 50, 20);
        }
        label.setText(player1 ? "LIGHT COMES" : "DARK COMES");
    }

    private boolean player1 = true;

    public void changePlayer() {
        player1 = !player1;
    }

    /**
     * Draws the individual button representing a board field.
     *
     * @param g2    The Graphics2D object used to draw the button.
     * @param color The color to be used for the button background.
     */
    private void drawButton(Graphics2D g2, BoardModel.Color color) {
        switch (color) {
            case LIGHT -> {
                g2.setColor(new Color(255, 222, 173));
                g2.fillOval(0, 0, 50, 50);
            }
            case DARK -> {
                g2.setColor(new Color(100, 0, 0));
                g2.fillOval(0, 0, 50, 50);
            }
            case MOVABLE_DARK -> {
                g2.setColor(Color.green);
                g2.fillOval(0, 0, 50, 50);
                g2.setColor(new Color(100, 0, 0));
                g2.fillOval(5, 5, 40, 40);
            }
            case MOVABLE_LIGHT -> {
                g2.setColor(Color.green);
                g2.fillOval(0, 0, 50, 50);
                g2.setColor(new Color(255, 222, 173));
                g2.fillOval(5, 5, 40, 40);
            }
            case CHOOSABLE -> {
                g2.setColor(Color.green);
                g2.fillOval(10, 10, 30, 30);
                g2.setColor(Color.DARK_GRAY);
                g2.fillOval(15, 15, 20, 20);
            }
            case CHOSEN_DARK -> {
                g2.setColor(new Color(0, 150, 0));
                g2.fillOval(0, 0, 50, 50);
                g2.setColor(new Color(100, 0, 0));
                g2.fillOval(5, 5, 40, 40);
            }
            case CHOSEN_LIGHT -> {
                g2.setColor(new Color(0, 150, 0));
                g2.fillOval(0, 0, 50, 50);
                g2.setColor(new Color(255, 222, 173));
                g2.fillOval(5, 5, 40, 40);
            }
            case PICK_LIGHT -> {
                g2.setColor(new Color(255, 222, 173));
                g2.fillOval(5, 5, 40, 40);
                g2.setColor(Color.red);
                g2.setStroke(new BasicStroke(5));
                g2.drawLine(5, 5, 45, 45);
                g2.drawLine(5, 45, 45, 5);
            }
            case PICK_DARK -> {
                g2.setColor(new Color(100, 0, 0));
                g2.fillOval(5, 5, 40, 40);
                g2.setColor(Color.red);
                g2.setStroke(new BasicStroke(5));
                g2.drawLine(5, 5, 45, 45);
                g2.drawLine(5, 45, 45, 5);
            }
            default -> {
                g2.setColor(Color.DARK_GRAY);
                g2.fillOval(15, 15, 20, 20);
            }
        }
    }

    /**
     * Puts a piece on the game screen
     *
     * @param where determines where to put it
     * @param what  determines what color to place
     */
    public void putPiece(int where, BoardModel.Color what) {
        colors[where] = what;
        fields[where].repaint();

        sideMenu.repaint();
    }

    /**
     * Puts several pieces on the game screen
     *
     * @param where a list that determines the places to put the pieces
     * @param what  what color to place
     */
    public void putPieces(List<Integer> where, BoardModel.Color what) {
        where.forEach(x -> putPiece(x, what));
    }


    /**
     * Takes away a piece from the current player
     *
     * @param player1 determines the current player
     */
    public void putPiecesPlayer(boolean player1) {
        if (player1) piecesPlayer1--;
        else piecesPlayer2--;
    }

    /**
     * Puts the taken down pieces on the side menu to show how many are left
     *
     * @param player1 determines the current player
     */
    public void pickPiecesPlayer(boolean player1) {
        if (player1) piecesOffBoardPlayer2++;
        else piecesOffBoardPlayer1++;
    }

    /**
     * phase is used to update the side menu depending on which stage the game is currently in
     */
    public void phaseUp() {
        if (++phase > 2) throw new RuntimeException("PHASE CANNOT BE MORE THAN 2");
    }

    /**
     * Custom class for representing a pair of integer coordinates (x, y).
     * Primarily used for placing buttons or other elements on the GUI.
     */
    public static class IntTuple {

        private int x; // X-coordinate
        private int y; // Y-coordinate

        /**
         * Constructs an IntTuple with the given x and y coordinates.
         *
         * @param x The x-coordinate.
         * @param y The y-coordinate.
         */
        public IntTuple(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Gets the x-coordinate of the tuple.
         *
         * @return The x-coordinate.
         */
        public int getX() {
            return x;
        }

        /**
         * Gets the y-coordinate of the tuple.
         *
         * @return The y-coordinate.
         */
        public int getY() {
            return y;
        }
    }

    /**
     * Custom class for representing a line segment defined by two points (x1, y1) and (x2, y2).
     * Primarily used for drawing or connecting elements in the GUI.
     */
    public static class Line {

        private int x1; // X-coordinate of the starting point
        private int y1; // Y-coordinate of the starting point
        private int x2; // X-coordinate of the ending point
        private int y2; // Y-coordinate of the ending point

        /**
         * Constructs a Line with the given starting and ending coordinates.
         *
         * @param x1 The x-coordinate of the starting point.
         * @param y1 The y-coordinate of the starting point.
         * @param x2 The x-coordinate of the ending point.
         * @param y2 The y-coordinate of the ending point.
         */
        public Line(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        /**
         * Gets the x-coordinate of the starting point of the line.
         *
         * @return The x-coordinate of the starting point.
         */
        public int getX1() {
            return x1;
        }

        /**
         * Gets the y-coordinate of the starting point of the line.
         *
         * @return The y-coordinate of the starting point.
         */
        public int getY1() {
            return y1;
        }

        /**
         * Gets the x-coordinate of the ending point of the line.
         *
         * @return The x-coordinate of the ending point.
         */
        public int getX2() {
            return x2;
        }

        /**
         * Gets the y-coordinate of the ending point of the line.
         *
         * @return The y-coordinate of the ending point.
         */
        public int getY2() {
            return y2;
        }
    }

}
