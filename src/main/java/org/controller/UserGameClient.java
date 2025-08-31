package org.controller;

import org.jetbrains.annotations.NotNull;
import org.model.BoardModel;
import org.util.MapModel;
import org.util.PlayerColor;
import org.view.GameScreen;
import org.view.ComponentGenerator;

import java.util.Iterator;
import java.util.List;

public class UserGameClient extends GameClient {
    private GameScreen gameScreen; //view, from which players can interact with the game
    private final ComponentGenerator componentGenerator;

    /**
     * Constructs a `UserGameClient` with the specified `ComponentGenerator`.
     *
     * @param componentGenerator the component generator used for creating UI components
     */
    public UserGameClient(ComponentGenerator componentGenerator) {
        this.componentGenerator = componentGenerator;
    }

    @Override
    public void placePiece() {
        super.placePiece();
        gameScreen.updateStacks(playerColor, myPiecesLeftToPlace, myPiecesRemoved, opponentPiecesLeftToPlace, opponentPiecesRemoved);
    }

    @Override
    public void removePieceFromOpponent() {
        super.removePieceFromOpponent();
        gameScreen.updateStacks(playerColor, myPiecesLeftToPlace, myPiecesRemoved, opponentPiecesLeftToPlace, opponentPiecesRemoved);
    }

    @Override
    public void opponentPlaced(){
        super.opponentPlaced();
        gameScreen.updateStacks(playerColor, myPiecesLeftToPlace, myPiecesRemoved, opponentPiecesLeftToPlace, opponentPiecesRemoved);
    }

    @Override
    public void removePieceFromMe(){
        super.removePieceFromMe();
        gameScreen.updateStacks(playerColor, myPiecesLeftToPlace, myPiecesRemoved, opponentPiecesLeftToPlace, opponentPiecesRemoved);
    }

    /**
     * Initializes the game client with the provided map model, player color, and game controller.
     * This is not in the constructor, because these parameters are not known at instantiation time.
     * @param mapModel       the map model representing the game board
     * @param playerColor    the color of the player
     * @param gameController the game controller managing the game logic
     */
    @Override
    public void initGameClient(MapModel mapModel, PlayerColor playerColor, GameController gameController) {
        super.initGameClient(mapModel, playerColor, gameController);
        this.gameScreen = new GameScreen(componentGenerator, mapModel, playerColor, this);
    }

    /**
     * Sends the selected field up to the GameController
     * @param field     the selected field
     * @return
     */
    @Override
    public void sendUp(int field) {
        System.out.println("Field " + field + " clicked by " + playerColor);
        if(focus) {
            focus = false;
            gameController.receiveInput(field);
        }
    }

    @Override
    public void sendDownNone() {
        gameScreen.changeLabelText(0, "Not your turn");
        focus = false;
        for (int i = 1; i < fieldColors.length; i++) {
            gameScreen.updateFieldType(i, convertState(fieldColors[i], State.NONE));
        }
    }


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

    @Override
    public void sendDownColor(List<Integer> fields, BoardModel.Color color) {
        super.sendDownColor(fields, color);
        gameScreen.updateFieldType(fields, convertState(color, State.NONE));
    }

    @Override
    public void sendDownColor(Integer field, BoardModel.Color color) {
        super.sendDownColor(field, color);
        gameScreen.updateFieldType(field, convertState(color, State.NONE));
    }

    @Override
    public void sendDownPlace(List<Integer> possibleFields) {
        focus = true;
        gameScreen.updateFieldType(possibleFields, ComponentGenerator.FieldType.MOVABLE_TO);
        gameScreen.changeLabelText(0, "Place a piece");
    }

    @Override
    public void sendDownRemove(@NotNull List<Integer> opponentFields) {
        focus = true;
        for (Integer field : opponentFields) {
            gameScreen.updateFieldType(field, convertState(fieldColors[field], State.REMOVABLE));
        }
        gameScreen.changeLabelText(0, "Remove an opponent's piece");
    }

    @Override
    public void sendDownMove(@NotNull List<Integer> movableFields) {
        focus = true;
        for (Integer field : movableFields) {
            gameScreen.updateFieldType(field, convertState(fieldColors[field], State.MOVABLE));
        }
        gameScreen.changeLabelText(0, "Move a piece");
    }

    @Override
    public void sendDownMoveTo(Integer fieldToMove, List<Integer> possibleFields) {
        focus = true;
        gameScreen.updateFieldType(fieldToMove, convertState(fieldColors[fieldToMove], State.CHOSEN));
        gameScreen.updateFieldType(possibleFields, ComponentGenerator.FieldType.MOVABLE_TO);
        gameScreen.changeLabelText(0, "Select a destination");
    }


    @Override
    public void exitGame(){
        gameController.exitGame(playerColor);
    }

    @Override
    public void endGame(){
        gameScreen.endGame();
    }

}
