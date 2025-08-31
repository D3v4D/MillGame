package org.controller;

import org.jetbrains.annotations.NotNull;
import org.model.BoardModel;
import org.util.MapModel;
import org.util.PlayerColor;

import java.util.List;
import java.util.Random;

/**
 * GameController manages the game logic, including piece placement,
 * movement, and tracking player turns.
 */
public class GameController {
    private final GameClient lightPlayer;
    private int lightPlayerPieces;
    private final GameClient darkPlayer;
    private int darkPlayerPieces;

    private final int defaultPieces;
    private int placedPieces = 0;

    private final Initializer initializer;
    private final BoardModel boardModel;

    //true if it's light player's turn, false if dark player's turn
    private boolean focusOnLight = true;

    //true if the game is in the mill phase, false otherwise
    private boolean millPhase = false;

    //true if the game is in the placing phase, false if in the main game phase
    private boolean placingPhase = true;

    //the currently selected piece for moving, null if no piece is selected
    Integer selectedPiece = null;

    /**
     * Constructs a GameController with the specified map model, players, and initializer.
     *
     * @param mapModel   the map model representing the game board
     * @param player1    the first game client (player)
     * @param player2    the second game client (player)
     * @param initializer the initializer to return to the menu after the game ends
     */
    public GameController(@NotNull MapModel mapModel, GameClient player1, GameClient player2, Initializer initializer) {
        this.initializer = initializer;
        boardModel = new BoardModel(mapModel);
        lightPlayerPieces = darkPlayerPieces = defaultPieces =  mapModel.pieces;

        //Randomly assign player colors
        if (new Random().nextBoolean()) {
            player1.initGameClient(mapModel, PlayerColor.LIGHT, this);
            lightPlayer = player1;
            player2.initGameClient(mapModel, PlayerColor.DARK, this);
            darkPlayer = player2;
        } else {
            player1.initGameClient(mapModel, PlayerColor.DARK, this);
            darkPlayer = player1;
            player2.initGameClient(mapModel, PlayerColor.LIGHT, this);
            lightPlayer = player2;
        }

        //Start the game with the light player placing first
        lightPlayer.sendDownPlace(boardModel.getFields(BoardModel.Color.EMPTY));
    }

    private void askOtherPlayer() {
        if (focusOnLight) {
            lightPlayer.sendDownNone();
            if (placingPhase) {
                darkPlayer.sendDownPlace(boardModel.getFields(BoardModel.Color.EMPTY));
            }
            else {
                List<Integer> movableFields = boardModel.getMovableFields(BoardModel.Color.DARK);
                if (movableFields.isEmpty()) {
                    endGame(true);
                    return;
                }
                darkPlayer.sendDownMove(movableFields);
            }
            focusOnLight = false;
        } else {
            darkPlayer.sendDownNone();
            if (placingPhase) {
                lightPlayer.sendDownPlace(boardModel.getFields(BoardModel.Color.EMPTY));
            }
            else {
                List<Integer> movableFields = boardModel.getMovableFields(BoardModel.Color.LIGHT);
                if (movableFields.isEmpty()) {
                    endGame(true);
                    return;
                }
                lightPlayer.sendDownMove(movableFields);
            }
            focusOnLight = true;
        }
    }

    private BoardModel.Color currentPlayerColor() {
        return focusOnLight ? BoardModel.Color.LIGHT : BoardModel.Color.DARK;
    }

    private BoardModel.Color opponentPlayerColor() {
        return focusOnLight ? BoardModel.Color.DARK : BoardModel.Color.LIGHT;
    }

    private void decreaseOpponentPieces() {
        if (focusOnLight) {
            lightPlayer.removePieceFromOpponent();
            darkPlayer.removePieceFromMe();
            darkPlayerPieces--;
            if (darkPlayerPieces < 3) {
                endGame(false);
            }
        } else {
            darkPlayer.removePieceFromOpponent();
            lightPlayer.removePieceFromMe();
            lightPlayerPieces--;
            if (lightPlayerPieces < 3) {
                endGame(false);
            }
        }
    }

    private GameClient currentPlayer() {
        return focusOnLight ? lightPlayer : darkPlayer;
    }

    private GameClient opponentPlayer(){
        return focusOnLight ? darkPlayer : lightPlayer;
    }

    public void receiveInput(int field) {
        initializer.log("Received field: " + field);

        if (millPhase) {
            millPhaseMove(field);
        } else {
            if (placingPhase) {
                placingPhaseMove(field);
            }else {
                movingPhaseMove(field);
            }
        }
    }

    private void invalidMove() {
        currentPlayer().giveFocus();
    }

    private void millPhaseMove(int field) {
        initializer.log("Mill phase move on field: " + field);
        if (boardModel.isFieldOfColor(field, opponentPlayerColor()) && !boardModel.isFieldInMill(field)) { // Check if the field has an opponent's piece -> valid move
            initializer.log("Removing opponent's piece from field: " + field);
            putPiece(field, BoardModel.Color.EMPTY);
            millPhase = false;
            decreaseOpponentPieces();
            askOtherPlayer();
        } else {
            initializer.log("Invalid move during mill phase on field: " + field);
            invalidMove();
        }
    }

    private void placingPhaseMove(int field) {
        if (boardModel.isFieldEmpty(field)) { // Check if the field is empty -> valid move
            //this call puts the piece and checks for mills
            if(++placedPieces == defaultPieces * 2) {
                placingPhase = false;
            }
            currentPlayer().placePiece();
            opponentPlayer().opponentPlaced();
            if (putPiece(field, currentPlayerColor())) {
                initializer.log("Mill formed by placing on field: " + field);
                mill();
            } else {
                askOtherPlayer();
            }
        } else {
            initializer.log("Invalid move during placing phase on field: " + field);
            invalidMove();
        }
    }

    private int getCurrentPlayerPieces() {
        return focusOnLight ? lightPlayerPieces : darkPlayerPieces;
    }

    private void movingPhaseMove(int field) {
        if (selectedPiece == null) { // No piece selected yet
            if (boardModel.isFieldOfColor(field, currentPlayerColor())) { // Check if the field has the current player's piece -> valid selection
                selectedPiece = field;
                List<Integer> possibleMoves = boardModel.getPossibleMoves(field, getCurrentPlayerPieces() == 3);
                if (possibleMoves.isEmpty()) {
                    initializer.log("No possible moves for selected piece on field: " + field);
                    selectedPiece = null;
                    invalidMove();
                } else {
                    currentPlayer().sendDownMoveTo(field, possibleMoves);
                }
            } else {
                initializer.log("Invalid piece selection on field: " + field);
                invalidMove();
            }
        } else { // A piece is already selected
            if (field == selectedPiece) { // Deselect the piece
                selectedPiece = null;
                currentPlayer().sendDownMove(boardModel.getMovableFields(currentPlayerColor()));
            } else if (boardModel.isFieldEmpty(field) && boardModel.areNeighbors(selectedPiece, field) || (focusOnLight ? lightPlayerPieces : darkPlayerPieces) == 3 && boardModel.isFieldEmpty(field)) { // Check if the field is empty and a valid move -> valid move
                //this call moves the piece and checks for mills
                if (movePiece(selectedPiece, field, currentPlayerColor())) {
                    initializer.log("Mill formed by moving to field: " + field);
                    mill();
                } else askOtherPlayer();
                selectedPiece = null;
            } else {
                initializer.log("Invalid move to field: " + field);
                invalidMove();
            }
        }
    }

    private void mill() {
        initializer.log("Mill formed!");
        List<Integer> removableFields = boardModel.getFields(opponentPlayerColor(), false);
        if (!removableFields.isEmpty()) {
            initializer.log(removableFields.size() + " fields found");
            millPhase = true;
            currentPlayer().sendDownNone();
            currentPlayer().sendDownRemove(removableFields);
        } else {
            initializer.log("No field found");
            millPhase = false;
            askOtherPlayer();
        }
    }

    private void endGame(boolean enclosed) {
        initializer.log(currentPlayer().toString()+ " won by "+
                (enclosed ? "enclosing all opponent pieces." : "getting the number of opponent pieces under 3."));
        lightPlayer.endGame();
        darkPlayer.endGame();
        initializer.backToMenu();
    }

    /**
     * Places a piece on the board and updates both players' views.
     *
     * @param field the position on the board
     * @param color the color of the piece
     * @return true if placing the piece forms a mill, false otherwise
     */
    private boolean putPiece(int field, BoardModel.Color color) {
        darkPlayer.sendDownColor(field, color);
        lightPlayer.sendDownColor(field, color);
        return boardModel.putPiece(field, color);
    }

    private boolean movePiece(int from, int to, BoardModel.Color color){
        darkPlayer.sendDownColor(from, BoardModel.Color.EMPTY);
        lightPlayer.sendDownColor(from, BoardModel.Color.EMPTY);
        darkPlayer.sendDownColor(to, color);
        lightPlayer.sendDownColor(to, color);
        return boardModel.movePiece(from, to);
    }


    public void exitGame(PlayerColor playerColor) {
        initializer.log("Player " + playerColor + " exited the game.");
        lightPlayer.endGame();
        darkPlayer.endGame();
        initializer.backToMenu();
    }
}
