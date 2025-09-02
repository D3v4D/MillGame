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
        numberOfPiecesPlaced = 0;
        fields = new BoardModel.Color[0];
        piecesPerPlayer = 0;
        millPhase = false;
        focusOnLight = true;

        lightPlayerPieces = 0;
        darkPlayerPieces = 0;

        mapModel = new MapModel();
    }



    public SaveState(int numberOfPiecesPlaced, BoardModel.Color[] fields, int piecesPerPlayer, boolean millPhase, boolean focusOnLight, int lightPlayerPieces, int darkPlayerPieces, MapModel mapModel) {
        this.numberOfPiecesPlaced = numberOfPiecesPlaced;
        this.fields = fields;
        this.piecesPerPlayer = piecesPerPlayer;
        this.millPhase = millPhase;
        this.focusOnLight = focusOnLight;
        this.lightPlayerPieces = lightPlayerPieces;
        this.darkPlayerPieces = darkPlayerPieces;
        this.mapModel = mapModel;
    }

    /** The number of pieces on the board.
     */
    public int numberOfPiecesPlaced;
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

    /** The number of pieces still in game for the light player. (unplaced pieces included)
     */
    public int lightPlayerPieces;

    /** The number of pieces still in game for the dark player. (unplaced pieces included)
     */
    public int darkPlayerPieces;

    /** Model representing the game map.
     */
    public MapModel mapModel;
}
