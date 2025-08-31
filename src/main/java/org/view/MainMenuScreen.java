package org.view;


import org.controller.*;


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
     * @param componentGenerator The component generator used to create and manage GUI components.
     */
    public MainMenuScreen(Initializer init, ComponentGenerator componentGenerator) {
        int x = 800, y = 450;

        this.initializer = init;

        this.componentGenerator = componentGenerator;

        componentGenerator.generateBase(x, y);

        componentGenerator.paintBackground(0, 0, x, y, 0, 0, 0, y, new ComponentGenerator.Color(230, 230, 230), new ComponentGenerator.Color(50, 50, 50), 0);

        int startButtonId = componentGenerator.addButton("Start Game On ".concat(initializer.getCurrentMap()), 200, 50, 400, 50,
        () -> {
            componentGenerator.hide();
            initializer.loadMap();
            startGame();
        }, 10);  //we save these two buttons' id, so we can modify their text in the future

        int loadButtonId = componentGenerator.addButton("Load Game (".concat(initializer.getCurrentSave().concat(")")), 200, 125, 400, 50,
        () -> {
            componentGenerator.hide();
            initializer.loadGame();
        }, 10);

        componentGenerator.addButton("Exit game", 200, 275, 400, 50, () -> {
            System.exit(0);
        }, 10);

        componentGenerator.addButton("Choose Map", 600, 50, 100, 50, () -> {
            chooseMap(startButtonId);
        }, 10);

        componentGenerator.addButton("Choose Save", 600, 125, 100, 50, () -> {
            chooseSave(loadButtonId);
        }, 10);

        componentGenerator.show();
    }

    /**
     * Hides the main menu GUI and any open dialogs.
     */
    public void hide() {
        componentGenerator.hide();
        if (mapChooser != null) mapChooser.hide();
        if (saveChooser != null) saveChooser.hide();
    }

    /**
     * Displays the main menu GUI.
     */
    public void show() {
        componentGenerator.show();
    }

    /**
     * Updates the text of the "Start Game" button with the currently selected map.
        * @param startButtonId The ID of the "Start Game" button to update.
     */
    public void startButtonText(int startButtonId) {
        componentGenerator.modifyButtonText(startButtonId, "Start Game On ".concat(initializer.getCurrentMap())); //TODO: FIX THIS STRING
    }

    /**
     * Updates the text of the "Load Save" button with the currently selected save.
     * @param loadButtonId The ID of the "Load Save" button to update.
     */
    public void loadButtonText(int loadButtonId) {
        componentGenerator.modifyButtonText(loadButtonId, "Load Game (".concat(initializer.getCurrentSave().concat(")"))); //TODO: FIX THIS STRING
    }

    /**
     * Starts a new game.
     * This method is called when the player clicks the "Start Game" button.
     */
    public void startGame() {
        initializer.startGame();
    }

    /** Opens the map selection dialog for the player.
     * @param startButtonId The ID of the "Start Game" button to update its text after map selection.
     */
    public void chooseMap(int startButtonId) {
        if (mapChooser == null) {
            mapChooser = componentGenerator.copy();
            mapChooser.generateBase(300, 150);

            mapChooser.paintBackground(0, 0, 300, 150, 0, 0, 300, 150, new ComponentGenerator.Color(200, 200, 255), new ComponentGenerator.Color(100, 100, 200), 0);

            mapChooser.addComboBox(initializer.maps, initializer.currentMap, 50, 25, 200, 25, (String map) -> {
                initializer.currentMap = map;
                startButtonText(startButtonId);
            }, 10);

            mapChooser.addButton("OK", 100, 75, 100, 25, () -> mapChooser.hide(), 10);
        }

        mapChooser.show();
    }

    /**
     * Opens the save selection dialog for the player.
     * @param loadButtonId The ID of the "Load Save" button to update its text after save selection.
     */

    public void chooseSave(int loadButtonId) {
        if (saveChooser == null) {
            saveChooser = componentGenerator.copy();
            saveChooser.generateBase(300, 150);

            saveChooser.paintBackground(0, 0, 300, 150, 0, 0, 300, 150, new ComponentGenerator.Color(255, 200, 200), new ComponentGenerator.Color(200, 100, 100), 0);


            saveChooser.addComboBox(initializer.saves, initializer.currentSave, 50, 25, 200, 25, (String save) -> {
                initializer.currentSave = save;
                loadButtonText(loadButtonId);
            }, 10);

            saveChooser.addButton("OK", 100, 75, 100, 25, () -> saveChooser.hide(), 10);
        }

        saveChooser.show();
    }
}

