package org.controller;

import org.jetbrains.annotations.NotNull;
import org.model.BoardModel;
import org.model.SaveState;
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
    private final GameClient darkPlayer;
    private final int defaultPieces;
    private final Initializer initializer;
    private final BoardModel boardModel;

    /**
     * Currently selected piece for movement; null if no piece is selected.
     */
    Integer selectedPiece = null;

    // * NOTE: we store the number of pieces each player has on the board
    // *    and the number of pieces placed on the board
    // *    because we need to know when the placing phase ends (piecesPlaced == defaultPieces*2)
    // *    and we need to know when a player has 3 (or less than 3) pieces on the board

    /**
     * Number of pieces the light player has on the board (starting at the maximum number of pieces one can have on the board)
     */
    private int lightPlayerPieces;

    /**
     * Number of pieces the dark player has on the board (starting at the maximum number of pieces one can have on the board)
     */
    private int darkPlayerPieces;

    /** Number of pieces placed on the board (starting at 0) */
    private int placedPieces = 0;


    //true if it's light player's turn, false if dark player's turn
    private boolean focusOnLight = true;
    //true if the game is in the mill phase, false otherwise
    private boolean millPhase = false;
    //true if the game is in the placing phase, false if in the main game phase
    private boolean placingPhase = true;

    /**
     * Constructs a GameController with the specified map model, players, and initializer.
     *
     * @param mapModel    the map model representing the game board
     * @param player1     the first game client (player)
     * @param player2     the second game client (player)
     * @param initializer the initializer to return to the menu after the game ends
     */
    public GameController(@NotNull MapModel mapModel, GameClient player1, GameClient player2, Initializer initializer) {
        this.initializer = initializer;
        boardModel = new BoardModel(mapModel);
        lightPlayerPieces = darkPlayerPieces = defaultPieces = mapModel.pieces;

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

    /**
     * Constructs a GameController with a saved game state, players, and initializer.
     *
     * @param saveState   the saved state of the game
     * @param lightPlayer the game client for the light player
     * @param darkPlayer  the game client for the dark player
     * @param initializer the initializer to return to the menu after the game ends
     */
    public GameController(@NotNull SaveState saveState, @NotNull GameClient lightPlayer, @NotNull GameClient darkPlayer, Initializer initializer) {
        this.initializer = initializer;
        boardModel = new BoardModel(saveState.mapModel);
        defaultPieces = saveState.piecesPerPlayer;
        millPhase = saveState.millPhase;
        focusOnLight = saveState.focusOnLight;
        lightPlayerPieces = saveState.lightPlayerPieces;
        darkPlayerPieces = saveState.darkPlayerPieces;

        placedPieces = saveState.numberOfPiecesPlaced;
        placingPhase = (placedPieces < defaultPieces * 2);

        this.darkPlayer = darkPlayer;
        this.lightPlayer = lightPlayer;

        //Assign player colors
        lightPlayer.initGameClient(saveState .mapModel, PlayerColor.LIGHT, this);
        darkPlayer.initGameClient(saveState.mapModel, PlayerColor.DARK, this);

        int darkPiecesPlaced = placedPieces / 2; // Integer division to get the number of pieces placed by the dark player

        lightPlayer.setPieces(defaultPieces - (placedPieces - darkPiecesPlaced),
                defaultPieces - lightPlayerPieces,
                defaultPieces - darkPiecesPlaced,
                defaultPieces - darkPlayerPieces);

        darkPlayer.setPieces(defaultPieces - darkPiecesPlaced,
                defaultPieces - darkPlayerPieces,
                defaultPieces - (placedPieces - darkPiecesPlaced),
                defaultPieces - lightPlayerPieces);

        //Set the board to the saved state
        for (int i = 1; i < saveState.fields.length; i++)
            putPiece(i, saveState.fields[i]);


        //Start the game with the correct phase and player
        if (millPhase) {
            List<Integer> removableFields = boardModel.getFields(opponentPlayerColor(), false);
            if (!removableFields.isEmpty()) {
                currentPlayer().sendDownRemove(removableFields);
            } else {
                millPhase = false;
                askOtherPlayer();
            }
        } else {
            if (placingPhase) {
                currentPlayer().sendDownPlace(boardModel.getFields(BoardModel.Color.EMPTY));
            } else {
                currentPlayer().sendDownMove(boardModel.getMovableFields(currentPlayerColor()));
            }
        }
    }

    /**
     * Asks the other player to make a move or place a piece based on the current game phase.
     * If the current player is in the placing phase, the other player is prompted to place a piece.
     * If in the moving phase, the other player is prompted to move a piece.
     * If the other player has no valid moves, the game ends with the current player winning by enclosure.
     */
    private void askOtherPlayer() {
        if (focusOnLight) {
            lightPlayer.sendDownNone();
            if (placingPhase) {
                darkPlayer.sendDownPlace(boardModel.getFields(BoardModel.Color.EMPTY));
            } else {
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
            } else {
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

    /**
     * Returns the color of the current player based on whose turn it is.
     *
     * @return the color of the current player
     */
    private BoardModel.Color currentPlayerColor() {
        return focusOnLight ? BoardModel.Color.LIGHT : BoardModel.Color.DARK;
    }

    /**
     * Returns the color of the opponent player based on whose turn it is.
     *
     * @return the color of the opponent player
     */
    private BoardModel.Color opponentPlayerColor() {
        return focusOnLight ? BoardModel.Color.DARK : BoardModel.Color.LIGHT;
    }

    /**
     * Decreases the number of pieces of the opponent player by one.
     * If the opponent player has less than 3 pieces after the decrease, the game ends with the current player winning.
     */
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

    /**
     * Returns the GameClient of the current player based on whose turn it is.
     *
     * @return the GameClient of the current player
     */
    private GameClient currentPlayer() {
        return focusOnLight ? lightPlayer : darkPlayer;
    }

    /**
     * Returns the GameClient of the opponent player based on whose turn it is.
     *
     * @return the GameClient of the opponent player
     */
    private GameClient opponentPlayer() {
        return focusOnLight ? darkPlayer : lightPlayer;
    }

    /**
     * Receives input from the current player and processes it based on the current game phase.
     * Depending on whether the game is in the mill phase, placing phase, or moving phase,
     * the input is handled accordingly to update the game state.
     *
     * @param field the position on the board where the player wants to place or move a piece
     */
    public void receiveInput(int field) {
        initializer.log("Received field: " + field);

        if (millPhase) {
            millPhaseMove(field);
        } else {
            if (placingPhase) {
                placingPhaseMove(field);
            } else {
                movingPhaseMove(field);
            }
        }
    }

    /**
     * Notifies the current player that their last move was invalid and prompts them to try again.
     * The current player retains focus for input.
     */
    private void invalidMove() {
        currentPlayer().giveFocus();
    }

    /**
     * Handles a move during the mill phase, allowing the current player to remove an opponent's piece.
     * Validates the move to ensure the selected field contains an opponent's piece that is not part of a mill.
     * If valid, removes the piece, updates the game state, and switches turns.
     * If invalid, notifies the current player and prompts them to try again.
     *
     * @param field the position on the board where the player wants to remove an opponent's piece
     */
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



    /**
     * Handles a move during the placing phase, allowing the current player to place a piece on the board.
     * Validates the move to ensure the selected field is empty.
     * If valid, places the piece, updates the game state, and checks for mills.
     * If a mill is formed, enters the mill phase; otherwise, switches turns.
     * If invalid, notifies the current player and prompts them to try again.
     *
     * @param field the position on the board where the player wants to place a piece
     */
    private void placingPhaseMove(int field) {
        if (boardModel.isFieldEmpty(field)) { // Check if the field is empty -> valid move
            //this call puts the piece and checks for mills
            if (++placedPieces == defaultPieces * 2) {
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

    /**
     * Returns the number of pieces the current player has on the board.
     *
     * @return the number of pieces the current player has
     */
    private int getCurrentPlayerPieces() {
        return focusOnLight ? lightPlayerPieces : darkPlayerPieces;
    }

    /**
     * Handles a move during the moving phase, allowing the current player to select and move a piece on the board.
     * Validates the selection and movement of pieces, ensuring that moves are legal according to game rules.
     * If a piece is selected, highlights possible moves; if a move is made, updates the game state and checks for mills.
     * If a mill is formed, enters the mill phase; otherwise, switches turns.
     * If invalid, notifies the current player and prompts them to try again.
     *
     * @param field the position on the board where the player wants to select or move a piece
     */
    private void movingPhaseMove(int field) {
        if (selectedPiece == null) { // No piece selected yet
            selectFieldToMove(field);
        } else { // A piece is already selected
            selectFieldToMoveTo(field);
        }
    }

    private void selectFieldToMoveTo(int field) {
        if (field == selectedPiece) { // Deselect the piece
            selectedPiece = null;
            currentPlayer().sendDownNone(); // Clear highlights
            currentPlayer().sendDownMove(boardModel.getMovableFields(currentPlayerColor()));
        } else if (boardModel.isFieldEmpty(field) && boardModel.areNeighbors(selectedPiece, field) || (focusOnLight ? lightPlayerPieces : darkPlayerPieces) == 3 && boardModel.isFieldEmpty(field)) { // Check if the field is empty and a valid move -> valid move
            //this call moves the piece and checks for mills
            if (movePiece(selectedPiece, field, currentPlayerColor())) {
                initializer.log("Mill formed by moving to field: " + field);
                mill();
            } else askOtherPlayer();
            selectedPiece = null;
        } else if (!boardModel.getPossibleMoves(selectedPiece, getCurrentPlayerPieces() == 3).isEmpty() && boardModel.isFieldOfColor(field, currentPlayerColor())) { // Select a different piece
            selectedPiece = field;
            currentPlayer().sendDownNone();
            currentPlayer().sendDownMoveTo(field, boardModel.getPossibleMoves(field, getCurrentPlayerPieces() == 3));
        } else {
            initializer.log("Invalid move to field: " + field);
            invalidMove();
        }
    }

    private void selectFieldToMove(int field) {
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
    }

    /**
     * Initiates the mill phase, allowing the current player to remove an opponent's piece.
     * Checks for removable opponent pieces and prompts the current player to select one.
     * If no removable pieces are found, exits the mill phase and switches turns.
     */
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

    /**
     * Ends the game and logs the reason for the win.
     * Notifies both players that the game has ended and returns to the main menu.
     *
     * @param enclosed true if the win was due to enclosing all opponent pieces, false if due to opponent having less than 3 pieces
     */
    private void endGame(boolean enclosed) {
        initializer.log(currentPlayer().toString() + " won by " + (enclosed ? "enclosing all opponent pieces." : "getting the number of opponent pieces under 3."));
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

    /**
     * Moves a piece on the board and updates both players' views.
     *
     * @param from  the starting position of the piece
     * @param to    the destination position of the piece
     * @param color the color of the piece
     * @return true if moving the piece forms a mill, false otherwise
     */
    private boolean movePiece(int from, int to, BoardModel.Color color) {
        darkPlayer.sendDownColor(from, BoardModel.Color.EMPTY);
        lightPlayer.sendDownColor(from, BoardModel.Color.EMPTY);
        darkPlayer.sendDownColor(to, color);
        lightPlayer.sendDownColor(to, color);
        return boardModel.movePiece(from, to);
    }

    /**
     * Handles the exit of a player from the game.
     * Logs the exit event, ends the game for both players, and returns to the main menu.
     *
     * @param playerColor the color of the player who exited
     */
    public void exitGame(PlayerColor playerColor) {
        initializer.log("Player " + playerColor + " exited the game.");
        lightPlayer.endGame();
        darkPlayer.endGame();
        initializer.backToMenu();
    }

    /**
     * Saves the current game state using the initializer's saving functionality.
     * Constructs a SaveState object with the current board configuration, player pieces,
     * game phase, and other relevant information before passing it to the initializer.
     */
    public void saveGame() {
        initializer.log("Saving game state...");
        SaveState saveState = new SaveState();
        saveState.numberOfPiecesPlaced = placedPieces;
        saveState.mapModel = boardModel.getMapModel();
        saveState.fields = new BoardModel.Color[saveState.mapModel.fields.size()];
        for (int i = 1; i < saveState.fields.length; i++)
            saveState.fields[i] = boardModel.getFieldColor(i);
        saveState.piecesPerPlayer = defaultPieces;
        saveState.millPhase = millPhase;
        saveState.focusOnLight = focusOnLight;
        saveState.lightPlayerPieces = lightPlayerPieces;
        saveState.darkPlayerPieces = darkPlayerPieces;

        initializer.saveGame(saveState);
    }
}
