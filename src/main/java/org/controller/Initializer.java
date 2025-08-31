package org.controller;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.util.MapModel;
import org.view.MainMenuScreen;
import org.view.swing.SwingComponentGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * The `InitAll` class initializes and manages the main components of the game,
 * including game screens, saved games, and map selection.
 */
public class Initializer {
    public MapModel mapModel;
    public String currentMap;
    public String[] maps;
    public String currentSave;
    public String[] saves;
    MainMenuScreen mainMenuScreen;
    GameController gameController;
    Gson gson = new Gson();

    /**
     * Constructs an `Initializer` object that sets up the available maps and saves,
     * initializes the main menu screen, and selects the default map and save.
     */
    public Initializer() {
        maps = setFromFile("maps");
        saves = setFromFile("saves");
        if (maps.length == 0 || saves.length == 0) {
            throw new IllegalStateException("No maps or saves found in the respective folders.");
        }
        currentMap = maps[0];
        currentSave = saves[saves.length - 1];

        mainMenuScreen = new MainMenuScreen(this, new SwingComponentGenerator(Executors.newFixedThreadPool(1)));
    }

    /**
     * Sets the list of available maps or saves from the specified folder.
     *
     * @param folder the folder to scan for files
     * @return an array of file names (without extensions) found in the folder
     */
    @NotNull
    public static String[] setFromFile(String folder) {
        File f = new File(folder);
        File[] subFiles = f.listFiles();
        String[] ret = new String[subFiles.length];
        for (int i = 0; i < subFiles.length; i++) {
            ret[i] = (subFiles[i].getName().split("\\.")[0]);
        }
        return ret;
    }

    /**
     * Starts a new game by loading the selected map and initializing the game controller
     * with two user game clients.
     */
    public void startGame() {
        mapModel = loadMap();
        gameController = new GameController(mapModel,
                new UserGameClient(new SwingComponentGenerator(Executors.newFixedThreadPool(1))),
                new UserGameClient(new SwingComponentGenerator(Executors.newFixedThreadPool(1))),
                this
        );
    }

    public void log(String s) {
        System.out.println(s);
    }

    /**
     * Returns to the main menu screen.
     */
    public void backToMenu() {
        mainMenuScreen.show();
    }

    /**
     * Gets the current save file name.
     *
     * @return the name of the currently selected save file
     */
    public String getCurrentSave() {
        return currentSave;
    }

    /**
     * Gets the currently selected map.
     *
     * @return the name of the currently selected map
     */
    public String getCurrentMap() {
        return currentMap;
    }

    /**
     * Loads the map model from a JSON file based on the currently selected map.
     *
     * @return the loaded MapModel object
     * @throws IOException if there is an error reading the file
     */
    public MapModel loadMap() {
        Gson gson1 = new Gson();
        try (FileReader fr = new FileReader("maps/".concat(currentMap).concat(".json"))) {
            return gson1.fromJson(fr, MapModel.class);
        } catch (Exception e) {
            log(e + " Map file not found.");
        }
        return null;
    }

    /**
     * Saves the currently running game (by recording the previously pressed buttons)
     */
    public void saveGame(String filename) {
//        try {
//            GameState gs = new GameState(mapModel, pressed);
//            FileWriter fw = new FileWriter(filename);
//            gson.toJson(gs, fw);
//            fw.close();
//            saves = setFromFile("saves");
//        } catch (Exception e) {
//        }
    }

    /**
     * Loads the currently chosen save (by "replaying" the game based upon the list of buttons that have been pressed)
     */
    public void loadGame() {
//        try {
//            FileReader fr = new FileReader("saves/".concat(currentSave).concat(".json"));
//            GameState gs = gson.fromJson(fr, GameState.class);
//            mapModel = gs.mapModel;
//            pressed = gs.pressed;
//            pressed.reload();
//            fr.close();
//        } catch (Exception e) {
//        }
    }
}