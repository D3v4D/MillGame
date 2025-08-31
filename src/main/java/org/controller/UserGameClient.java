package org.controller;

import org.jetbrains.annotations.NotNull;
import org.model.BoardModel;
import org.util.MapModel;
import org.util.PlayerColor;
import org.view.ComponentGenerator;
import org.view.GameScreen;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a game client for a user player, extending the base GameClient class.
 * This class handles user interactions and updates the game screen accordingly.
 */
public class UserGameClient extends GameClient {
    private final ComponentGenerator componentGenerator;
    private GameScreen gameScreen; //view, from which players can interact with the game

    /**
     * Constructs a `UserGameClient` with the specified `ComponentGenerator`.
     *
     * @param componentGenerator the component generator used for creating UI components
     */
    public UserGameClient(ComponentGenerator componentGenerator) {
        this.componentGenerator = componentGenerator;
    }

    /**
     * Whether the player can currently interact with the game (i.e., it's their turn).
     * This is set to true when it's the player's turn and false otherwise.
     */
    @Override
    public void placePiece() {
        super.placePiece();
        gameScreen.updateStacks(playerColor, myPiecesLeftToPlace, myPiecesRemoved, opponentPiecesLeftToPlace, opponentPiecesRemoved);
    }

    /**
     * Updates the game screen to reflect the current state of pieces left to place and pieces removed.
     * This method is called whenever a piece is removed from the opponent.
     */
    @Override
    public void removePieceFromOpponent() {
        super.removePieceFromOpponent();
        gameScreen.updateStacks(playerColor, myPiecesLeftToPlace, myPiecesRemoved, opponentPiecesLeftToPlace, opponentPiecesRemoved);
    }

    /**
     * Updates the game screen to reflect the current state of pieces left to place and pieces removed.
     * This method is called whenever the opponent places a piece on the board.
     */
    @Override
    public void opponentPlaced() {
        super.opponentPlaced();
        gameScreen.updateStacks(playerColor, myPiecesLeftToPlace, myPiecesRemoved, opponentPiecesLeftToPlace, opponentPiecesRemoved);
    }

    /**
     * Updates the game screen to reflect the current state of pieces left to place and pieces removed.
     * This method is called whenever a piece is removed from the player.
     */
    @Override
    public void removePieceFromMe() {
        super.removePieceFromMe();
        gameScreen.updateStacks(playerColor, myPiecesLeftToPlace, myPiecesRemoved, opponentPiecesLeftToPlace, opponentPiecesRemoved);
    }

    /**
     * Initializes the game client with the provided map model, player color, and game controller.
     * This is not in the constructor, because these parameters are not known at instantiation time.
     *
     * @param mapModel       the map model representing the game board
     * @param playerColor    the color of the player
     * @param gameController the game controller managing the game logic
     */
    @Override
    public void initGameClient(MapModel mapModel, PlayerColor playerColor, GameController gameController) {
        super.initGameClient(mapModel, playerColor, gameController);
        this.gameScreen = new GameScreen(componentGenerator, mapModel, playerColor, this);
    }

    /** Sends a message up to the game controller indicating that the player has clicked on a field.
     * If it is the player's turn (focus is true), the input is forwarded to the game controller.
     *
     * @param field the index of the field that was clicked
     */
    @Override
    public void sendUp(int field) {
        System.out.println("Field " + field + " clicked by " + playerColor);
        if (focus) {
            focus = false;
            gameController.receiveInput(field);
        }
    }

    /**
     * Sends a message down to the client indicating that it is not their turn.
     * Updates the game screen to reflect that the player cannot interact with the game at this time.
     * Resets all fields to their default state based on their colors.
     */
    @Override
    public void sendDownNone() {
        gameScreen.changeLabelText(0, "Not your turn");
        focus = false;
        for (int i = 1; i < fieldColors.length; i++) {
            gameScreen.updateFieldType(i, convertState(fieldColors[i], State.NONE));
        }
    }

    /**
     * Sends a message down to the client to update multiple fields with their respective colors.
     * Updates the game screen to reflect the colors of the specified fields.
     *
     * @param fields a list of field indices to be updated
     * @param colors a list of colors corresponding to the fields
     */
    @Override
    public void sendDownColors(List<Integer> fields, List<BoardModel.Color> colors) {
        super.sendDownColors(fields, colors);
        Iterator<BoardModel.Color> colorIterator = colors.iterator();
        for (Integer field : fields) {
            if (colorIterator.hasNext()) {
                BoardModel.Color color = colorIterator.next();
                gameScreen.updateFieldType(field, convertState(color, State.NONE));
            }
        }
    }

    /**
     * Sends a message down to the client to update multiple fields with a specific color.
     * Updates the game screen to reflect the specified color for the given fields.
     *
     * @param fields a list of field indices to be updated
     * @param color  the color to be applied to the specified fields
     */
    @Override
    public void sendDownColor(List<Integer> fields, BoardModel.Color color) {
        super.sendDownColor(fields, color);
        gameScreen.updateFieldType(fields, convertState(color, State.NONE));
    }

    /**
     * Sends a message down to the client to update a single field with a specific color.
     * Updates the game screen to reflect the specified color for the given field.
     *
     * @param field the index of the field to be updated
     * @param color the color to be applied to the specified field
     */
    @Override
    public void sendDownColor(Integer field, BoardModel.Color color) {
        super.sendDownColor(field, color);
        gameScreen.updateFieldType(field, convertState(color, State.NONE));
    }

    /**
     * Sends a message down to the client indicating that it is their turn to place a piece.
     * Updates the game screen to highlight possible fields where the player can place their piece.
     * Changes the label text to prompt the player to place a piece.
     *
     * @param possibleFields a list of field indices where the player can place their piece
     */
    @Override
    public void sendDownPlace(List<Integer> possibleFields) {
        focus = true;
        gameScreen.updateFieldType(possibleFields, ComponentGenerator.FieldType.MOVABLE_TO);
        gameScreen.changeLabelText(0, "Place a piece");
    }

    /**
     * Sends a message down to the client indicating that it is their turn to remove an opponent's piece.
     * Updates the game screen to highlight opponent fields that can be removed.
     * Changes the label text to prompt the player to remove an opponent's piece.
     *
     * @param opponentFields a list of field indices where the player can remove an opponent's piece
     */
    @Override
    public void sendDownRemove(@NotNull List<Integer> opponentFields) {
        focus = true;
        for (Integer field : opponentFields) {
            gameScreen.updateFieldType(field, convertState(fieldColors[field], State.REMOVABLE));
        }
        gameScreen.changeLabelText(0, "Remove an opponent's piece");
    }

    /**
     * Sends a message down to the client indicating that it is their turn to move a piece.
     * Updates the game screen to highlight pieces that can be moved.
     * Changes the label text to prompt the player to move a piece.
     *
     * @param movableFields a list of field indices where the player can move their pieces from
     */
    @Override
    public void sendDownMove(@NotNull List<Integer> movableFields) {
        focus = true;
        for (Integer field : movableFields) {
            gameScreen.updateFieldType(field, convertState(fieldColors[field], State.MOVABLE));
        }
        gameScreen.changeLabelText(0, "Move a piece");
    }

    /**
     * Sends a message down to the client indicating that it is their turn to move a specific piece to a new location.
     * Updates the game screen to highlight the selected piece and possible destination fields.
     * Changes the label text to prompt the player to select a destination for the move.
     *
     * @param fieldToMove    the index of the field containing the piece to be moved
     * @param possibleFields a list of field indices where the player can move the selected piece to
     */
    @Override
    public void sendDownMoveTo(Integer fieldToMove, List<Integer> possibleFields) {
        focus = true;
        gameScreen.updateFieldType(fieldToMove, convertState(fieldColors[fieldToMove], State.CHOSEN));
        gameScreen.updateFieldType(possibleFields, ComponentGenerator.FieldType.MOVABLE_TO);
        gameScreen.changeLabelText(0, "Select a destination");
    }

    /** Exits the game and notifies the game controller.
     * This method is called when the player chooses to exit the game.
     */
    @Override
    public void exitGame() {
        gameController.exitGame(playerColor);
    }

    /**
     * Ends the game and updates the game screen accordingly.
     * This method is called when the game concludes, either by a player winning or other end conditions.
     */
    @Override
    public void endGame() {
        gameScreen.endGame();
    }
}
