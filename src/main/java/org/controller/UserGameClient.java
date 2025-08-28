package org.controller;

import org.util.PlayerColor;
import org.view.GameScreen;

public class UserGameClient implements GameClient {
    GameScreen gameScreen; //view, from which players can interact with the game

    GameController gameController; // the main logic behind the game, to which the client will send informations

    PlayerColor playerColor;

    public UserGameClient(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    @Override
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public int sendAction(int buttonID){

        return 0;
    }

}
