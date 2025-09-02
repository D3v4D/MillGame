package org.controller;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.model.SaveState;
import org.util.MapModel;
import org.view.MainMenuScreen;
import org.view.swing.SwingComponentGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.Executors;

/**
 * The `InitAll` class initializes and manages the main components of the game,
 * including game screens, saved games, and map selection.
 */
public class Initializer {
    private MapModel mapModel;
    private String currentMap;
    private String[] maps;
    private String currentSave;
    private String[] saves;
    MainMenuScreen mainMenuScreen;
    GameController gameController;
    Gson gson = new Gson();

    /**
     * Constructs an `Initializer` object that sets up the available maps and saves,
     * initializes the main menu screen, and selects the default map and save.
     */
    public Initializer() {
        setMaps(setFromFile("maps"));
        setSaves(setFromFile("saves"));
        if (getMaps().length == 0 || getSaves().length == 0) {
            throw new IllegalStateException("No maps or saves found in the respective folders.");
        }
        setCurrentMap(getMaps()[0]);
        setCurrentSave(getSaves()[getSaves().length - 1]);

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
        setMapModel(loadMap());
        gameController = new GameController(getMapModel(),
                new UserGameClient(new SwingComponentGenerator(Executors.newFixedThreadPool(1))),
                new UserGameClient(new SwingComponentGenerator(Executors.newFixedThreadPool(1))),
                this
        );
    }

    /**
     * Logs a message to the console.
     *
     * @param s the message to log
     */
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
     * */
    public MapModel loadMap() {
        Gson gson1 = new Gson();
        try (FileReader fr = new FileReader("maps/".concat(getCurrentMap()).concat(".json"))) {
            return gson1.fromJson(fr, MapModel.class);
        } catch (Exception e) {
            log(e + " Map file not found.");
        }
        return null;
    }

    /**
     * Saves the current game state to a JSON file based on the current time
     * and updates the list of available saves.
     * @param saveState the current state of the game to be saved
     */
    public void saveGame(SaveState saveState) {
        try {
            File dir = new File("saves");
            if (!dir.exists()) dir.mkdirs();
            String filename = "saves/".concat(String.valueOf(System.currentTimeMillis())).concat(".json");
            FileWriter fw = new FileWriter(filename);
            gson.toJson(saveState, fw);
            fw.close();
            addNewSave(filename);
            log("Game saved to " + filename);
        } catch (Exception e) {
            log(e + " Could not save the game.");
        }
    }

    private void addNewSave(String filename) {
        String[] newSaves = new String[getSaves().length + 1];
        System.arraycopy(getSaves(), 0, newSaves, 0, getSaves().length);
        newSaves[getSaves().length] = filename.split("/")[1].split("\\.")[0];
        setSaves(newSaves);
        setCurrentSave(newSaves[newSaves.length - 1]);
    }

    /**
     * Loads a saved game state from a JSON file based on the currently selected save.
     */
    public void loadGame() {
        try (FileReader fr = new FileReader("saves/".concat(getCurrentSave()).concat(".json"))) {
            SaveState saveState = gson.fromJson(fr, SaveState.class);
            setMapModel(loadMap());
            gameController = new GameController(saveState,
                    new UserGameClient(new SwingComponentGenerator(Executors.newFixedThreadPool(1))),
                    new UserGameClient(new SwingComponentGenerator(Executors.newFixedThreadPool(1))),
                    this
            );
        } catch (Exception e) {
            log(e + " Save file not found.");
        }
    }

    public MapModel getMapModel() {
        return mapModel;
    }

    public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public String[] getMaps() {
        return maps;
    }

    public void setMaps(String[] maps) {
        this.maps = maps;
    }

    public void setCurrentSave(String currentSave) {
        this.currentSave = currentSave;
    }

    public String[] getSaves() {
        return saves;
    }

    public void setSaves(String[] saves) {
        this.saves = saves;
    }
}