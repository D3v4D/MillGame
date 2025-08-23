package org.model;

import org.view.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the model of a game map, including locations, lines, groups, and associated data.
 * This class serves as a data structure to hold map-related elements such as positions, connections,
 * and game state attributes.
 * NOTE: This is used to deserialize a type of map from a json file.
 */
public class MapModel {

    /**
     * A list of positions on the map, represented as tuples of coordinates.
     * Each position corresponds to a potential piece location.
     */
    public ArrayList<InitGameScreen.IntTuple> locationList = new ArrayList<>();

    /**
     * A list of lines connecting the positions on the map.
     * Each line represents a path between two positions.
     * Note: this is only mean for aesthetical purposes
     */
    public ArrayList<InitGameScreen.Line> lineList = new ArrayList<>();

    /**
     * A mapping of positions to their connected neighbors.
     * The key represents a position, and the value is a list of connected positions.
     */
    public HashMap<Integer, ArrayList<Integer>> fields = new HashMap<>();

    /**
     * A list of groups of positions that are logically associated for mill formation.
     * Each group is represented as an array of integers.
     */
    public ArrayList<int[]> groups = new ArrayList<>();

    /**
     * The number of pieces available in the game (per player).
     */
    public int pieces;
}
