package org.controller;

import org.jetbrains.annotations.NotNull;
import org.model.BoardModel;
import org.util.MapModel;
import org.util.PlayerColor;
import org.view.ComponentGenerator;

import java.util.Iterator;
import java.util.List;

/**
 * Abstract class representing a game client that interacts with the game controller.
 * It manages the state of the player's pieces, handles communication with the game controller,
 * and updates the visual representation of the game board.
 */
public abstract class GameClient {
    GameController gameController; // the main logic behind the game, to which the client will send informations
    PlayerColor playerColor;
    BoardModel.Color[] fieldColors;
    int myPiecesLeftToPlace = 0;
    int myPiecesRemoved = 0;
    int opponentPiecesLeftToPlace = 0;
    int opponentPiecesRemoved = 0;
    /**
     * Indicates whether the game client currently has focus for user input.
     * This flag can be used to determine if the client is active and should respond to user actions.
     */
    boolean focus = false;

    /**
     * Decreases the count of pieces left to place for the player by one.
     * This method should be called whenever the player successfully places a piece on the board.
     */
    public void placePiece() {
        myPiecesLeftToPlace--;
    }

    /**
     * Decreases the count of pieces left to place for the opponent by one.
     * This method should be called whenever the opponent successfully places a piece on the board.
     */
    public void opponentPlaced() {
        opponentPiecesLeftToPlace--;
    }

    /**
     * Increases the count of pieces removed from the opponent by one.
     * This method should be called whenever the player successfully removes an opponent's piece from the board.
     */
    public void removePieceFromOpponent() {
        opponentPiecesRemoved++;
    }

    /**
     * Increases the count of pieces removed from the player by one.
     * This method should be called whenever the opponent successfully removes a piece from the player's board.
     */
    public void removePieceFromMe() {
        myPiecesRemoved++;
    }

    /**
     * Converts a combination of board color and status to a corresponding field type for the component generator.
     *
     * @param color  The color of the board field (EMPTY, LIGHT, DARK).
     * @param status The status of the field (NONE, REMOVABLE, MOVABLE, CHOSEN).
     * @return The corresponding FieldType for the component generator.
     * @throws IllegalArgumentException if an invalid combination of color and status is provided.
     */
    ComponentGenerator.FieldType convertState(@NotNull BoardModel.Color color, State state) {
        return switch (color) {
            case EMPTY -> switch (state) {
                case NONE -> ComponentGenerator.FieldType.EMPTY;
                case MOVABLE -> ComponentGenerator.FieldType.MOVABLE_TO;
                case REMOVABLE, CHOSEN ->
                        throw new IllegalArgumentException("An empty field cannot be removeable or chosen.");
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

    /**
     * Initializes the game client with the provided map model, player color, and game controller.
     * Sets up the initial state of the game client, including field colors and pieces left to place.
     *
     * @param mapModel       The model of the game map containing field information.
     * @param playerColor    The color assigned to the player (e.g., LIGHT or DARK).
     * @param gameController The controller managing the game's logic and interactions.
     */
    void initGameClient(MapModel mapModel, PlayerColor playerColor, GameController gameController) {
        fieldColors = new BoardModel.Color[mapModel.fields.size() + 1];
        for (int i = 1; i < fieldColors.length; i++) {
            fieldColors[i] = BoardModel.Color.EMPTY;
        }
        myPiecesLeftToPlace = mapModel.pieces;
        opponentPiecesLeftToPlace = mapModel.pieces;
        this.gameController = gameController;
        this.playerColor = playerColor;
    }

    /**
     * Exits the game by notifying the game controller to terminate the game session for the player.
     * This method should be called when the player chooses to leave the game.
     */
    public void exitGame() {
        gameController.exitGame(playerColor);
    }

    /**
     * Sends the selected field index to the game controller.
     * This method is called when the player selects a field on the game board.
     *
     * @param field The index of the selected field.
     */
    abstract public void sendUp(int field);

    /** Informs the client that no action is currently required.
     * This method can be used to reset the client's state or indicate that it is not the player's turn.
     */
    abstract void sendDownNone();


    /**
     * Updates the colors of multiple fields on the game board.
     *
     * @param fields A list of field indices to be updated.
     * @param colors A list of colors corresponding to each field index.
     *               The size of this list should match the size of the fields list.
     */
    public void sendDownColors(@NotNull List<Integer> fields, @NotNull List<BoardModel.Color> colors) {
        Iterator<BoardModel.Color> colorIterator = colors.iterator();
        for (Integer field : fields) {
            fieldColors[field] = colorIterator.next();
        }
    }

    /**
     * Updates the color of a single field on the game board.
     *
     * @param field The index of the field to be updated.
     * @param color The new color to be assigned to the specified field.
     */
    public void sendDownColor(@NotNull List<Integer> fields, BoardModel.Color color) {
        for (Integer field : fields) {
            fieldColors[field] = color;
        }
    }

    /**
     * Updates the color of a single field on the game board.
     *
     * @param field The index of the field to be updated.
     * @param color The new color to be assigned to the specified field.
     */
    public void sendDownColor(@NotNull Integer field, BoardModel.Color color) {
        fieldColors[field] = color;
    }

    /**
     * Informs the client that it is their turn to place a piece on the board.
     * Provides a list of possible fields where the piece can be placed.
     *
     * @param possibleFields A list of field indices where the player can place their piece.
     */
    abstract void sendDownPlace(List<Integer> possibleFields);

    /**
     * Grants focus to the game client, allowing it to respond to user input.
     * This method should be called when it is the player's turn to make a move.
     */
    void giveFocus() {
        focus = true;
    }

    /**
     * Sends a request to the client to remove an opponent's piece from the board.
     * Provides a list of fields where the opponent's pieces are located and can be removed.
     * @param opponentFields A list of field indices containing the opponent's pieces that can be removed.
     */
    abstract void sendDownRemove(List<Integer> opponentFields);

    /**
     * Sends a request to the client to select a piece to move on the board.
     * Provides a list of fields where the player's pieces can be moved from.
     * @param movableFields A list of field indices containing the player's pieces that can be moved.
     */
    abstract void sendDownMove(List<Integer> movableFields);

    /**
     * Sends a request to the client to move a piece to a new field on the board.
     * Provides the field from which the piece is being moved and a list of possible destination fields.
     * @param fieldToMove The index of the field from which the piece is being moved.
     * @param possibleFields A list of field indices where the piece can be moved to.
     */
    abstract void sendDownMoveTo(Integer fieldToMove, List<Integer> possibleFields);

    /**
     * Notifies the client that the game has ended.
     * This method should be called when the game reaches a conclusion, such as when a player wins or a draw occurs.
     */
    abstract void endGame();

    /**
     * Enum representing the status of a board field.
     * NONE: The field has no special status.
     * REMOVABLE: The field contains a piece that can be removed.
     * MOVABLE: The field is a valid destination for moving a piece.
     * CHOSEN: The field is currently selected or highlighted.
     */
    enum State {NONE, REMOVABLE, MOVABLE, CHOSEN}
}
