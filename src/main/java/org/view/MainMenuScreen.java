package org.view;


import org.controller.*;
import org.model.Color;
import org.view.base.ComponentGenerator;

/**
 * Represents the initial graphical user interface (GUI) for the game.
 * It provides options to start a game, load a save, view the toplist, choose a map, or exit the game.
 */
public class MainMenuScreen {

    /**
     * Reference to the main controller for initializing various game components.
     */
    private Initializer initializer;

    ComponentGenerator componentGenerator;

    ComponentGenerator mapChooser = null;
    ComponentGenerator saveChooser = null;

    /**
     * Constructor to initialize the GUI and set up components.
     *
     * @param init The main controller that manages the game initialization logic.
     */
    public MainMenuScreen(Initializer init, ComponentGenerator componentGenerator) {
        int x = 800, y =450;

        this.initializer = init;

        this.componentGenerator = componentGenerator;

        componentGenerator.generateBase(x, y);

        componentGenerator.paintBackground(0, 0, x, y, 0, 0, 0, y, new Color(230, 230, 230), new Color(50, 50, 50));

        int startButtonId = componentGenerator.addButton("Start Game On ".concat(initializer.getCurrentMap()), 200, 50, 400, 50, () -> {
            componentGenerator.hide();
            initializer.loadMap();
            startGame();
        });  //we save these two buttons' id, so we can modify their text in the future

        int loadButtonId = componentGenerator.addButton("Load Game (".concat(initializer.getCurrentSave().concat(")")), 200, 125, 400, 50, () -> {
            componentGenerator.hide();
            initializer.loadGame();
            startGame();
        });

        componentGenerator.addButton("Exit game", 200, 275, 400, 50, () -> {
            System.exit(0);
        });

        componentGenerator.addButton("Choose Map", 600, 50, 100, 50, () -> {
            chooseMap(startButtonId);
        });

        componentGenerator.addButton("Choose Save", 600, 125, 100, 50, () -> {
            chooseSave(loadButtonId);
        });

        componentGenerator.show();
    }


    public void hide() {
        componentGenerator.hide();
        if (mapChooser != null) mapChooser.hide();
        if (saveChooser != null) saveChooser.hide();
    }

    public void show() {
        componentGenerator.show();
    }

    /**
     * Updates the text of the "Start Game" button with the currently selected map.
     */
    public void startButtonText(int startButtonId) {
        componentGenerator.modifyButtonText(startButtonId, "Start Game On ".concat(initializer.getCurrentMap())); //TODO: FIX THIS STRING
    }

    /**
     * Updates the text of the "Load Save" button with the currently selected save.
     */
    public void loadButtonText(int loadButtonId) {
        componentGenerator.modifyButtonText(loadButtonId, "Load Game (".concat(initializer.getCurrentSave().concat(")"))); //TODO: FIX THIS STRING
    }

    /**
     * Starts a new game.
     */
    public void startGame() {
        try {
            hide();
            initializer.gameScreen = new GameScreen(initializer.pressed, initializer.mapModel, initializer);
            initializer.stillNotInstantiated = false;
            initializer.initialized();

        } catch (InterruptedException a) {
//            System.out.println("GATYA");
        }
    }

    /**
     * Opens the map selection dialog for the player.
     */
    public void chooseMap(int startButtonId) {
        if (mapChooser == null) {
            mapChooser = componentGenerator.copy();
            mapChooser.generateBase(300, 150);

            mapChooser.addComboBox(initializer.maps, initializer.currentMap, 50, 25, 200, 25, (String map) -> {
                initializer.currentMap = map;
                startButtonText(startButtonId);
            });

            mapChooser.addButton("OK", 100, 75, 100, 25, () -> mapChooser.hide());
        }

        mapChooser.show();
    }

    /**
     * Opens the save selection dialog for the player.
     */

    public void chooseSave(int loadButtonId) {
        if (saveChooser == null) {
            saveChooser = componentGenerator.copy();
            saveChooser.generateBase(300, 150);

            saveChooser.addComboBox(initializer.saves, initializer.currentSave, 50, 25, 200, 25, (String save) -> {
                initializer.currentSave = save;
                loadButtonText(loadButtonId);
            });

            saveChooser.addButton("OK", 100, 75, 100, 25, () -> saveChooser.hide());
        }

        saveChooser.show();
    }
}

