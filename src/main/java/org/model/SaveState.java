package org.model;

import org.util.MapModel;

/**
 * Represents the save state of a game, including the board configuration,
 * player pieces, and game phase information.
 */
public class SaveState {

    /** Constructs a SaveState object with default values.
     */
    public SaveState() {
        numberOfFields = 0;
        fields = new BoardModel.Color[0];
        piecesPerPlayer = 0;
        millPhase = false;
        focusOnLight = true;
        lightPiecesLeftToPlace = 0;
        darkPiecesLeftToPlace = 0;
        mapModel = new MapModel();
    }

    /** The number of fields on the board.
     */
    public int numberOfFields;
    /** The colors of the fields on the board.
     */
    public BoardModel.Color[] fields;
    /** The number of pieces each player has.
     */
    public int piecesPerPlayer;
    /** Whether the game is in the mill phase.
     */
    public boolean millPhase;

    /** The color of the current player. True for light, false for dark.
     */
    public boolean focusOnLight;

    /** The number of pieces removed from the light player.
     */
    public int lightPiecesLeftToPlace;

    /** The number of pieces removed from the dark player.
     */
    public int darkPiecesLeftToPlace;

    /** Model representing the game map.
     */
    public MapModel mapModel;
}
