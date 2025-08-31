package org.controller;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.BoardModel;
import org.util.MapModel;
import org.util.PlayerColor;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private static class TestGameClient extends GameClient {
        boolean sendUpCalled = false;
        int sendUpField = -1;
        boolean endGameCalled = false;

        @Override
        public void sendUp(int field) {
            sendUpCalled = true;
            sendUpField = field;
        }

        @Override
        void sendDownNone() {}

        @Override
        void sendDownPlace(java.util.List<Integer> possibleFields) {}

        @Override
        void sendDownRemove(java.util.List<Integer> opponentFields) {}

        @Override
        void sendDownMove(java.util.List<Integer> movableFields) {}

        @Override
        void sendDownMoveTo(Integer fieldToMove, java.util.List<Integer> possibleFields) {}

        @Override
        void endGame() {
            endGameCalled = true;
        }
    }

    private static class TestInitializer extends Initializer {
        boolean initCalled = false;
        @Override
        public void backToMenu() {
            initCalled = true;
        }
    }

    private MapModel mapModel;
    private TestGameClient player1;
    private TestGameClient player2;
    private TestInitializer initializer;
    private GameController controller;

    @BeforeEach
    void setUp() {
        mapModel = new MapModel();
        mapModel.fields = new HashMap<>() {{
            put(1, new ArrayList<>());
            put(2, new ArrayList<>());
        }};
        mapModel.pieces = 9;
        player1 = new TestGameClient();
        player2 = new TestGameClient();
        initializer = new TestInitializer();
        controller = new GameController(mapModel, player1, player2, initializer);
    }

    @Test
    void testExitGameCallsReturnToMenu() {
        controller.exitGame(PlayerColor.LIGHT);
        assertTrue(initializer.initCalled);
    }


    @Test
    void testEndGameCallsEndGameOnClients() {
        controller.exitGame(player1.playerColor);
        assertTrue(player1.endGameCalled);
        assertTrue(player2.endGameCalled);
    }

    // Add more tests as needed for your actual GameController logic
}