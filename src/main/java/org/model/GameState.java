package org.model;

import org.util.MapModel;

/**
 * Represents the current state of the game.
 * Contains the map model and a stack of pressed inputs for managing game actions.
 * NOTE: This is used to serialize and deserialize the state of the game to or from a json file.
 */
public class GameState {

    /**
     * The model representing the current state of the map.
     */
    public MapModel mapModel;

    /**
     * A concurrent stack for storing pressed inputs during the game.
     */
    public ConcurrentStack<Integer> pressed;

    /**
     * Constructs a new GameState with the given map model and stack of pressed inputs.
     *
     * @param mM The map model representing the game's current map state.
     * @param p  The concurrent stack for handling pressed inputs.
     */
    public GameState(MapModel mM, ConcurrentStack<Integer> p) {
        mapModel = mM;
        pressed = p;
    }
}
