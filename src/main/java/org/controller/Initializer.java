package org.controller;

import org.model.*;
import org.util.MapModel;
import org.view.*;

import com.google.gson.Gson;
import org.view.base.ComponentGenerator;
import org.view.swing.SwingComponentGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.ExecutorService;

/**
 * The `InitAll` class initializes and manages the main components of the game,
 * including game screens, saved games, and map selection.
 */
public class Initializer {
    MainMenuScreen mainMenuScreen;
    public InitGameScreen initGameScreen;
    public MapModel mapModel;
    Gson gson = new Gson();

    public String currentMap;
    public String[] maps;

    public String currentSave;
    public String[] saves;

    public ConcurrentStack<Integer> pressed = new ConcurrentStack<>();
    public boolean stillNotInstantiated = true;

    /**
     * Constructs the `InitAll` class, setting up initial configurations
     * and managing the game's main threads (Swing elements, and the business logic are running on different threads).
     */
    public Initializer(ComponentGenerator componentGenerator, ExecutorService backendPool) {
        maps = setFromFile("maps");
        currentMap = maps[0];
        saves = setFromFile("saves");
        currentSave = saves[saves.length-1];

        GameClient player1;
        GameClient player2;

        mainMenuScreen = new MainMenuScreen(this, new SwingComponentGenerator(), backendPool);

        /*Thread player1Thread = new Thread(()-> {
            player1 = new UserGameClient(new GameScreen(new SwingComponentGenerator(), mapModel));
        });

        Thread player2Thread = new Thread(() -> {
            player2 = new UserGameClient(new GameScreen(new SwingComponentGenerator(), mapModel));
        });

        GameController gameController = new GameController(mapModel, player1, player2);
*/


        /*
//        new Thread(() -> {
//            while (true) {
//                try {
//                    initialized();
//                    stillNotInstantiated = true;
//                    new GameController(mapModel, pressed, initGameScreen);   //!!!!!!!!!
//                } catch (Exception e) {
//                    if (pressed.getLatest() == -69) {
//                        mainMenuScreen.show();
////                        System.out.println("GAME CLOSED");
//                    } else if (pressed.getLatest() == -420) {
////                        System.out.println("-420 recieved, starting a new game");
//                        mainMenuScreen.startGame();
//                    } else {
////                        System.out.println("VMI NEM JAU");
//                    }
//                }
//            }
//        }).start();
*/
    }


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

    private String[] setFromFile(String folder){
        File f = new File(folder);
        File [] subFiles = f.listFiles();
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
            saves = setFromFile("saves");
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