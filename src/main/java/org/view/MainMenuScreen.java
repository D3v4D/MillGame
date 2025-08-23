package org.view;


import com.google.gson.Gson;
import org.controller.*;
import org.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;

/**
 * Represents the initial graphical user interface (GUI) for the game.
 * It provides options to start a game, load a save, view the toplist, choose a map, or exit the game.
 */
public class MainMenuScreen extends JFrame {
    /**
     * Background panel for the main menu with a gradient effect.
     */
    private JPanel background;

    /**
     * Reference to the main controller for initializing various game components.
     */
    private InitAll initAll;

    JFrame topList = new JFrame();
    JFrame mapChooser = new JFrame();
    JFrame saveChooser = new JFrame();

    /**
     * Constructor to initialize the GUI and set up components.
     *
     * @param iA The main controller that manages the game initialization logic.
     */
    public MainMenuScreen(InitAll iA) {
        initAll = iA;

        background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, Color.gray, 0, 450, Color.darkGray));
                g2d.fillRect(0, 0, 800, 450);
            }
        };
        background.setLayout(null);
        background.setBounds(0, 0, 800, 450);
        add(background);
        addButtons();



        showButtons();
        setResizable(false);
        this.pack();
        setLocationRelativeTo(null);
    }

    // Buttons for interacting with the GUI.
    private JButton startGame, loadGame, viewToplist, exitGame, chooseMap, chooseSave;

    /**
     * Updates the text of the "Start Game" button with the currently selected map.
     */
    public void startButtonText() {
        startGame.setText("Start Game On ".concat(initAll.getCurrentMap()));
    }

    /**
     * Updates the text of the "Load Save" button with the currently selected save.
     */
    public void loadButtonText() {
        loadGame.setText("Load Game (".concat(initAll.getCurrentSave().concat(")")));
    }

    /**
     * Adds buttons to the background panel (but making them invisible) and initializes their properties.
     */
    private void addButtons() {
        startGame = new JButton("Start Game On ".concat(initAll.getCurrentMap()));
        startGame.addActionListener(e -> {
            this.setVisible(false);
            initAll.loadMap();
            startGame();
        });
        startGame.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

        chooseMap = new JButton("Choose Map");
        chooseMap.addActionListener(e -> {
            chooseMap();
        });
        chooseMap.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

        loadGame = new JButton("Load Save");
        loadButtonText();
        loadGame.addActionListener(e -> {
            setVisible(false);
            initAll.loadGame();
            startGame();
        });
        loadGame.setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));

        chooseSave = new JButton("Choose Save");
        chooseSave.addActionListener(e -> {
            chooseSave();
        });
        chooseSave.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));


        viewToplist = new JButton("View Toplist");
        viewToplist.addActionListener(e -> viewToplist());
        viewToplist.setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));

        exitGame = new JButton("Exit Game");
        exitGame.addActionListener(e -> System.exit(0));
        exitGame.setBorder(BorderFactory.createLineBorder(Color.darkGray, 3));


        background.add(startGame);
        background.add(viewToplist);
        background.add(loadGame);
        background.add(exitGame);
        background.add(chooseMap);
        background.add(chooseSave);

        startGame.setBounds(200, 50, 400, 50);
        loadGame.setBounds(200, 125, 400, 50);
        viewToplist.setBounds(200, 200, 400, 50);
        exitGame.setBounds(200, 350, 400, 50);
        chooseMap.setBounds(600, 50, 100, 50);
        chooseSave.setBounds(600, 125, 100, 50);


        startGame.setVisible(false);
        loadGame.setVisible(false);
        viewToplist.setVisible(false);
        exitGame.setVisible(false);
        chooseMap.setVisible(false);
        chooseSave.setVisible(false);
    }

    /**
     * Displays all buttons by setting them visible.
     */
    private void showButtons() {
        exitGame.setVisible(true);
        startGame.setVisible(true);
        loadGame.setVisible(true);
        viewToplist.setVisible(true);
        chooseMap.setVisible(true);
        chooseSave.setVisible(true);
    }


    /**
     * Starts a new game.
     */
    public void startGame() {
        try {
            topList.dispose();
            saveChooser.dispose();
            mapChooser.dispose();
            initAll.gameScreen = new InitGameScreen(initAll.pressed, initAll.mapModel, initAll);
            topList = new JFrame();
            mapChooser = new JFrame();
            saveChooser = new JFrame();
//            System.out.println("GAMESCREEN");
            initAll.stillNotInstantiated = false;
            initAll.initialized();

        } catch (InterruptedException a) {
//            System.out.println("GATYA");
        }
    }
    /**
     * Displays the leaderboard with top players and their scores.
     */
    public void viewToplist() {
        TopList tl = new TopList();
        topList.dispose();
        try {
            FileReader fr = new FileReader("toplist.json");
            tl = (new Gson()).fromJson(fr, TopList.class);
        } catch (Exception e) {
            System.out.println("GATYA");
        }
        topList = new JFrame();
        int x = 300, y = 150;
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(x, y, new Color(46, 139, 87), 0, 0, new Color(143, 188, 143)));
                g2d.fillRect(0, 0, x, y);
            }
        };
        background.setBounds(0, 0, x, y);
        background.setLayout(null);

        DefaultTableModel defaultTableModel = new DefaultTableModel(new String[]{"Name", "Score"}, 0);

        for (TopList.Player player : tl.getList()) {
            defaultTableModel.addRow(new Object[]{player.getName(), player.getScore()});
        }

        JTable jTable = new JTable(defaultTableModel);

        JScrollPane jScrollPane = new JScrollPane(jTable);

        jScrollPane.setBounds(25, 25, x - 50, y - 50 - 30);

        background.add(jScrollPane);

        topList.setLayout(null);
        topList.add(background);
        topList.setLocationRelativeTo(null);
        topList.pack();
        topList.setVisible(true);
        topList.setSize(x, y);
    }
    /**
     * Opens the map selection dialog for the player.
     */
    public void chooseMap() {
        mapChooser = new JFrame();
        int x = 300, y = 150;
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, Color.cyan, x, y, new Color(0, 128, 128)));
                g2d.fillRect(0, 0, x, y);
            }
        };
        background.setBounds(0, 0, x, y);
        background.setLayout(null);

        JComboBox<String> comboBox = new JComboBox<>(initAll.maps);
        comboBox.setSelectedItem(initAll.currentMap);
        comboBox.setBounds(50, 25, 200, 25);
        comboBox.addActionListener(e -> {
            initAll.currentMap = (String) comboBox.getSelectedItem();
            startButtonText();
        });

        background.add(comboBox);

        JButton okButton = new JButton("OK");
        okButton.setBounds(100, 75, 100, 25);
        okButton.addActionListener(e -> mapChooser.dispose());
        background.add(okButton);

        mapChooser.setLayout(null);
        mapChooser.add(background);
        mapChooser.setLocationRelativeTo(null);
        mapChooser.pack();
        mapChooser.setVisible(true);
        mapChooser.setSize(x, y);
    }

    /**
     * Opens the save selection dialog for the player.
     */
    public void chooseSave() {
        saveChooser = new JFrame();
        int x = 300, y = 150;
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(255, 128, 128), x, y, new Color(255, 64, 64)));
                g2d.fillRect(0, 0, x, y);
            }
        };
        background.setBounds(0, 0, x, y);
        background.setLayout(null);

        JComboBox<String> comboBox = new JComboBox<>(initAll.saves);
        comboBox.setSelectedItem(initAll.currentSave);
        comboBox.setBounds(50, 25, 200, 25);
        comboBox.addActionListener(e -> {
            initAll.currentSave = (String) comboBox.getSelectedItem();
            loadButtonText();
        });

        background.add(comboBox);

        JButton okButton = new JButton("OK");
        okButton.setBounds(100, 75, 100, 25);
        okButton.addActionListener(e -> saveChooser.dispose());
        background.add(okButton);

        saveChooser.setLayout(null);
        saveChooser.add(background);
        saveChooser.setLocationRelativeTo(null);
        saveChooser.pack();
        saveChooser.setVisible(true);
        saveChooser.setSize(x, y);
    }
}

