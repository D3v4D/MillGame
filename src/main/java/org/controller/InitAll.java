package org.controller;

import org.model.*;
import org.view.*;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

/**
 * The `InitAll` class initializes and manages the main components of the game,
 * including game screens, saved games, and map selection.
 */
public class InitAll {
    MainMenuScreen ig;
    public InitGameScreen gameScreen;
    public MapModel mapModel;
    Gson gson = new Gson();

    public String currentMap;
    public String[] maps;

    public String currentSave;
    public String[] saves;

    public ConcurrentStack<Integer> pressed = new ConcurrentStack<>();
    public boolean stillNotInstantiated = true;

    /**
     * Synchronizes initialization of game resources.
     * does not initialize gameController, until gameScreen is initialized
     */
    public synchronized void initialized() {
        try {
            while (stillNotInstantiated) wait();
            notifyAll();
        } catch (Exception e) {
            System.out.println("INSTATIATION EXCEPTION");
        }
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
     * Retrieves all available map files and sets the first map as the default.
     */
    private void setAllMaps() {
        File f = new File("maps");
        File[] subFiles = f.listFiles();
        maps = new String[subFiles.length];
        for (int i = 0; i < subFiles.length; i++) {
            maps[i] = (subFiles[i].getName().split("\\.")[0]);
        }
        currentMap = maps[0];
    }

    /**
     * Retrieves all available save files and orders them by date.
     */
    private void setAllsaves() {
        File f = new File("saves");
        File[] subFiles = f.listFiles();
        saves = new String[subFiles.length];
        for (int i = 0; i < subFiles.length; i++) {
            saves[i] = (subFiles[i].getName().split("\\.")[0]);
        }
        Arrays.sort(saves);
        for (int i = 0; i < saves.length / 2 ; i++) {
            String temp = saves[i];
            saves[i] = saves[saves.length - 1 - i];
            saves[saves.length - 1 - i] = temp;
        }
        currentSave = saves[0];
    }

    /**
     * Constructs the `InitAll` class, setting up initial configurations
     * and managing the game's main threads (Swing elements, and the business logic are running on different threads).
     */
    public InitAll() {

        setAllMaps();
        setAllsaves();

        new Thread(() -> {
            while (true) {
                try {
                    initialized();
                    stillNotInstantiated = true;
                    new GameController(mapModel, pressed, gameScreen);   //!!!!!!!!!
                } catch (Exception e) {
                    if (pressed.getLatest() == -69) {
                        ig.setVisible(true);
//                        System.out.println("GAME CLOSED");
                    } else if (pressed.getLatest() == -420) {
//                        System.out.println("-420 recieved, starting a new game");
                        ig.startGame();
                    } else {
//                        System.out.println("VMI NEM JAU");
                    }
                }
            }
        }).start();

        new Thread(() -> ig = new MainMenuScreen(this)).start();

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
     * Loads the currently chosen map
     */
    public void loadMap() {
        try (FileReader fr = new FileReader("maps/".concat(currentMap).concat(".json"))) {
            mapModel = gson.fromJson(fr, MapModel.class);
//            System.out.println("<3");
            pressed.clear();
        } catch (Exception e) {
//            System.out.println("</3");
        }
    }

    /**
     * Saves the currently running game (by recording the previously pressed buttons)
     */
    public void saveGame(String filename) {
        try {
            GameState gs = new GameState(mapModel, pressed);
            FileWriter fw = new FileWriter(filename);
            gson.toJson(gs, fw);
            fw.close();
            setAllsaves();
        } catch (Exception e) {
        }
    }

    /**
     * Loads the currently chosen save (by "replaying" the game based upon the list of buttons that have been pressed)
     */
    public void loadGame() {
        try {
            FileReader fr = new FileReader("saves/".concat(currentSave).concat(".json"));
            GameState gs = gson.fromJson(fr, GameState.class);
            mapModel = gs.mapModel;
            pressed = gs.pressed;
            pressed.reload();
            fr.close();
        } catch (Exception e) {
        }
    }
}