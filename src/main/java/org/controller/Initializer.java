package org.controller;

import org.jetbrains.annotations.NotNull;
import org.model.*;
import org.util.MapModel;
import org.view.*;

import com.google.gson.Gson;
import org.view.ComponentGenerator;
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
    MainMenuScreen mainMenuScreen;
    GameController gameController;
    public MapModel mapModel;
    Gson gson = new Gson();

    public String currentMap;
    public String[] maps;

    public String currentSave;
    public String[] saves;

    public ConcurrentStack<Integer> pressed = new ConcurrentStack<>();

    /**
     * Constructs the `InitAll` class, setting up initial configurations
     * and managing the game's main threads (Swing elements, and the business logic are running on different threads).
     */
    public Initializer(ComponentGenerator componentGenerator) {
        maps = setFromFile("maps");
        currentMap = maps[0];
        saves = setFromFile("saves");
        currentSave = saves[saves.length - 1];

        mainMenuScreen = new MainMenuScreen(this, componentGenerator);
    }


    public void log(String s) {
        System.out.println(s);
    }

    /**
     * Starts a new game by loading the selected map and initializing the game controller
     * with two user game clients.
     */
    public void startGame() {
        mapModel = loadMap();
        gameController = new GameController(mapModel,
                new UserGameClient(new SwingComponentGenerator(Executors.newFixedThreadPool(2))),
                new UserGameClient(new SwingComponentGenerator(Executors.newFixedThreadPool(2))),
                this
        );
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
            log(e.toString() + " Map file not found.");
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