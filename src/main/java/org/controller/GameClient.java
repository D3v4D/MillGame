package org.controller;

import java.util.List;

public interface GameClient {

    void setGameController(GameController gameController);

    //upwards communication from the Client to the GameController
        //the player wants to exit the game
        void exitGame();
        //the player sends the field number, where he wants to put his piece
        void sendUp(int field);

    //downwards communication from the GameController to the Client
        //the game controller sends a status and a list of possible fields (if applicable) to the player

        void sendDownNone();

        void sendDownRemove(List<Integer> opponentFields);
        void sendDownMove(List<Integer> movableFields);
        void sendDownMoveTo(Integer fieldToMove, List<Integer> possibleFields);
        void sendDownMill(List<Integer> playerFields);


}
