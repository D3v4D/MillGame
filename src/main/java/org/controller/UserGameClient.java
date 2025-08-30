package org.controller;

import org.util.MapModel;
import org.util.PlayerColor;
import org.view.GameScreen;
import org.view.base.ComponentGenerator;

import java.util.ArrayList;
import java.util.List;

public class UserGameClient implements GameClient {
    GameScreen gameScreen; //view, from which players can interact with the game
    GameController gameController; // the main logic behind the game, to which the client will send informations
    PlayerColor playerColor;

    boolean focus = false;

    public UserGameClient(ComponentGenerator componentGenerator, MapModel mapModel, PlayerColor playerColor) {
        this.playerColor = playerColor;
        this.gameScreen = new GameScreen(componentGenerator, mapModel, playerColor, this);
    }


    /**
     * Sends the selected field up to the GameController
     * @param field     the selected field
     * @return
     */
    @Override
    public void sendUp(int field) {
        if(focus){
            gameController.receiveField(field);
            focus = false;
        }

    }

    @Override
    public void sendDownNone() {
        focus = false;

    }

    @Override
    public void sendDownRemove(List<Integer> opponentFields) {

    }

    @Override
    public void sendDownMove(List<Integer> movableFields) {

    }

    @Override
    public void sendDownMoveTo(Integer fieldToMove, List<Integer> possibleFields) {

    }

    @Override
    public void sendDownMill(List<Integer> playerFields) {

    }


    @Override
    public void exitGame(){
        gameController.exitGame(playerColor);
    }


    @Override
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
