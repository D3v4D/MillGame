package org.controller;

import org.jetbrains.annotations.NotNull;
import org.model.BoardModel;
import org.util.MapModel;
import org.util.PlayerColor;
import org.view.ComponentGenerator;

import java.util.Iterator;
import java.util.List;

public abstract class GameClient {
    GameController gameController; // the main logic behind the game, to which the client will send informations
    PlayerColor playerColor;
    BoardModel.Color[] fieldColors;
    int myPiecesLeftToPlace = 0;
    int myPiecesRemoved = 0;
    int opponentPiecesLeftToPlace = 0;
    int opponentPiecesRemoved = 0;

    public void placePiece(){
        myPiecesLeftToPlace--;
    }

    public void opponentPlaced(){
        opponentPiecesLeftToPlace--;
    }

    public void removePieceFromOpponent(){
        opponentPiecesRemoved++;
    }

    public void removePieceFromMe(){
        myPiecesRemoved++;
    }

    // possible states of a field
    enum State { NONE, REMOVABLE, MOVABLE, CHOSEN }

    /** Converts a combination of board color and status to a corresponding field type for the component generator.
     *
     * @param color  The color of the board field (EMPTY, LIGHT, DARK).
     * @param status The status of the field (NONE, REMOVABLE, MOVABLE, CHOSEN).
     * @return The corresponding FieldType for the component generator.
     * @throws IllegalArgumentException if an invalid combination of color and status is provided.
     */
    ComponentGenerator.FieldType convertState(@NotNull BoardModel.Color color, State state){
        return switch (color) {
            case EMPTY -> switch (state) {
                case NONE -> ComponentGenerator.FieldType.EMPTY;
                case MOVABLE -> ComponentGenerator.FieldType.MOVABLE_TO;
                case REMOVABLE, CHOSEN -> throw new IllegalArgumentException("An empty field cannot be removeable or chosen.");
            };

            case LIGHT -> switch (state) {
                case NONE -> ComponentGenerator.FieldType.LIGHT;
                case REMOVABLE -> ComponentGenerator.FieldType.REMOVABLE_LIGHT;
                case MOVABLE -> ComponentGenerator.FieldType.MOVABLE_LIGHT;
                case CHOSEN -> ComponentGenerator.FieldType.CHOSEN_LIGHT;
            };
            case DARK -> switch (state) {
                case NONE -> ComponentGenerator.FieldType.DARK;
                case REMOVABLE -> ComponentGenerator.FieldType.REMOVABLE_DARK;
                case MOVABLE -> ComponentGenerator.FieldType.MOVABLE_DARK;
                case CHOSEN -> ComponentGenerator.FieldType.CHOSEN_DARK;
            };
        };
    }

    boolean focus = false;

    void initGameClient(MapModel mapModel, PlayerColor playerColor, GameController gameController){
        fieldColors = new BoardModel.Color[mapModel.fields.size() + 1];
        for(int i = 1; i < fieldColors.length; i++){
            fieldColors[i] = BoardModel.Color.EMPTY;
        }
        myPiecesLeftToPlace = mapModel.pieces;
        opponentPiecesLeftToPlace = mapModel.pieces;
        this.gameController = gameController;
        this.playerColor = playerColor;
    }
    //upwards communication from the Client to the GameController
        //the player wants to exit the game
        public void exitGame(){
            gameController.exitGame(playerColor);
        }

        //the player sends the field number, where he wants to put his piece
        abstract public void sendUp(int field);


    //downwards communication from the GameController to the Client
        //the game controller sends a status and a list of possible fields (if applicable) to the player

    abstract void sendDownNone();

    public void sendDownColors(@NotNull List<Integer> fields, @NotNull List<BoardModel.Color> colors){
        Iterator<BoardModel.Color> colorIterator = colors.iterator();
        for(Integer field: fields){
            fieldColors[field] = colorIterator.next();
        }
    }

    public void sendDownColor(@NotNull List<Integer> fields, BoardModel.Color color){
        for(Integer field: fields){
            fieldColors[field] = color;
        }
    }

    public void sendDownColor(@NotNull Integer field, BoardModel.Color color){
        fieldColors[field] = color;
    }

    abstract void sendDownPlace(List<Integer> possibleFields);

    void giveFocus() {
        focus = true;
    }

    abstract void sendDownRemove(List<Integer> opponentFields);
        abstract void sendDownMove(List<Integer> movableFields);
        abstract void sendDownMoveTo(Integer fieldToMove, List<Integer> possibleFields);


    abstract void endGame();
}
