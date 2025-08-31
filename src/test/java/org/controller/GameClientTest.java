package org.controller;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.BoardModel;
import org.util.MapModel;
import org.util.PlayerColor;
import org.view.ComponentGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameClientTest {

    static ArrayList<Integer> listOf(@NotNull Integer... integers){
        ArrayList<Integer> list = new ArrayList<>();
        for (Integer i: integers) list.add(i);
        return list;
    }

    private static class TestGameController extends GameController {
        boolean exitCalled = false;
        PlayerColor exitColor = null;

        /**
         * Constructs a GameController with the specified map model, players, and initializer.
         *
         * @param mapModel    the map model representing the game board
         * @param player1     the first game client (player)
         * @param player2     the second game client (player)
         * @param initializer the initializer to return to the menu after the game ends
         */
        public TestGameController(@NotNull MapModel mapModel, GameClient player1, GameClient player2, Initializer initializer) {
            super(mapModel, player1, player2, initializer);
        }

        @Override
        public void exitGame(PlayerColor color) {
            exitCalled = true;
            exitColor = color;
        }
    }

    private static class TestGameClient extends GameClient {
        boolean sendUpCalled = false;
        int sendUpField = -1;
        boolean sendDownNoneCalled = false;
        boolean sendDownPlaceCalled = false;
        List<Integer> sendDownPlaceFields = null;
        boolean sendDownRemoveCalled = false;
        List<Integer> sendDownRemoveFields = null;
        boolean sendDownMoveCalled = false;
        List<Integer> sendDownMoveFields = null;
        boolean sendDownMoveToCalled = false;
        Integer sendDownMoveToField = null;
        List<Integer> sendDownMoveToFields = null;
        boolean endGameCalled = false;

        @Override
        public void sendUp(int field) {
            sendUpCalled = true;
            sendUpField = field;
        }

        @Override
        void sendDownNone() {
            sendDownNoneCalled = true;
        }

        @Override
        void sendDownPlace(List<Integer> possibleFields) {
            sendDownPlaceCalled = true;
            sendDownPlaceFields = possibleFields;
        }

        @Override
        void sendDownRemove(List<Integer> opponentFields) {
            sendDownRemoveCalled = true;
            sendDownRemoveFields = opponentFields;
        }

        @Override
        void sendDownMove(List<Integer> movableFields) {
            sendDownMoveCalled = true;
            sendDownMoveFields = movableFields;
        }

        @Override
        void sendDownMoveTo(Integer fieldToMove, List<Integer> possibleFields) {
            sendDownMoveToCalled = true;
            sendDownMoveToField = fieldToMove;
            sendDownMoveToFields = possibleFields;
        }

        @Override
        void endGame() {
            endGameCalled = true;
        }
    }

    private static class TestInitializer extends Initializer {
        boolean initCalled = false;
    }

    private TestGameClient client;
    private TestGameClient client2;
    private TestGameController controller;
    private MapModel mapModel;

    @BeforeEach
    void setUp() {
        client = new TestGameClient();
        client2 = new TestGameClient();
        TestInitializer initializer = new TestInitializer();
        mapModel = new MapModel();
        mapModel.fields = new HashMap<>(){{
            put(1, listOf(2, 4));
            put(2, listOf(1, 3, 5));
            put(3, listOf(2, 6));
            put(4, listOf(1, 5, 7));
            put(5, listOf(2, 4, 6, 8));
            put(6, listOf(3, 5, 9));
            put(7, listOf(4, 8));
            put(8, listOf(5, 7, 9));
            put(9, listOf(6, 8));
        }};
        mapModel.pieces = 9;
        controller = new TestGameController(mapModel, client, client2, initializer );

        client.initGameClient(mapModel, PlayerColor.LIGHT, controller);
        client2.initGameClient(mapModel, PlayerColor.DARK, controller);
    }

    @Test
    void placePiece() {
        int before = client.myPiecesLeftToPlace;
        client.placePiece();
        assertEquals(before - 1, client.myPiecesLeftToPlace);
    }

    @Test
    void opponentPlaced() {
        int before = client.opponentPiecesLeftToPlace;
        client.opponentPlaced();
        assertEquals(before - 1, client.opponentPiecesLeftToPlace);
    }

    @Test
    void removePieceFromOpponent() {
        int before = client.opponentPiecesRemoved;
        client.removePieceFromOpponent();
        assertEquals(before + 1, client.opponentPiecesRemoved);
    }

    @Test
    void removePieceFromMe() {
        int before = client.myPiecesRemoved;
        client.removePieceFromMe();
        assertEquals(before + 1, client.myPiecesRemoved);
    }

    @Test
    void convertState() {
        assertEquals(ComponentGenerator.FieldType.EMPTY, client.convertState(BoardModel.Color.EMPTY, GameClient.State.NONE));
        assertEquals(ComponentGenerator.FieldType.MOVABLE_TO, client.convertState(BoardModel.Color.EMPTY, GameClient.State.MOVABLE));
        assertThrows(IllegalArgumentException.class, () -> client.convertState(BoardModel.Color.EMPTY, GameClient.State.REMOVABLE));
        assertEquals(ComponentGenerator.FieldType.LIGHT, client.convertState(BoardModel.Color.LIGHT, GameClient.State.NONE));
        assertEquals(ComponentGenerator.FieldType.REMOVABLE_LIGHT, client.convertState(BoardModel.Color.LIGHT, GameClient.State.REMOVABLE));
        assertEquals(ComponentGenerator.FieldType.MOVABLE_LIGHT, client.convertState(BoardModel.Color.LIGHT, GameClient.State.MOVABLE));
        assertEquals(ComponentGenerator.FieldType.CHOSEN_LIGHT, client.convertState(BoardModel.Color.LIGHT, GameClient.State.CHOSEN));
        assertEquals(ComponentGenerator.FieldType.DARK, client.convertState(BoardModel.Color.DARK, GameClient.State.NONE));
        assertEquals(ComponentGenerator.FieldType.REMOVABLE_DARK, client.convertState(BoardModel.Color.DARK, GameClient.State.REMOVABLE));
        assertEquals(ComponentGenerator.FieldType.MOVABLE_DARK, client.convertState(BoardModel.Color.DARK, GameClient.State.MOVABLE));
        assertEquals(ComponentGenerator.FieldType.CHOSEN_DARK, client.convertState(BoardModel.Color.DARK, GameClient.State.CHOSEN));
    }

    @Test
    void initGameClient() {
        assertEquals(10, client.fieldColors.length); // fields.size() + 1
        for (int i = 1; i < client.fieldColors.length; i++) {
            assertEquals(BoardModel.Color.EMPTY, client.fieldColors[i]);
        }
        assertEquals(9, client.myPiecesLeftToPlace);
        assertEquals(9, client.opponentPiecesLeftToPlace);
        assertEquals(PlayerColor.LIGHT, client.playerColor);
        assertEquals(controller, client.gameController);
    }

    @Test
    void exitGame() {
        client.exitGame();
        assertTrue(controller.exitCalled);
        assertEquals(PlayerColor.LIGHT, controller.exitColor);
    }

    @Test
    void sendUp() {
        client.sendUp(3);
        assertTrue(client.sendUpCalled);
        assertEquals(3, client.sendUpField);
    }

    @Test
    void sendDownNone() {
        client.sendDownNone();
        assertTrue(client.sendDownNoneCalled);
    }

    @Test
    void sendDownColors() {
        List<Integer> fields = Arrays.asList(1, 2, 3);
        List<BoardModel.Color> colors = Arrays.asList(BoardModel.Color.LIGHT, BoardModel.Color.DARK, BoardModel.Color.EMPTY);
        client.sendDownColors(fields, colors);
        assertEquals(BoardModel.Color.LIGHT, client.fieldColors[1]);
        assertEquals(BoardModel.Color.DARK, client.fieldColors[2]);
        assertEquals(BoardModel.Color.EMPTY, client.fieldColors[3]);
    }

    @Test
    void sendDownColor() {
        List<Integer> fields = Arrays.asList(1, 2, 3);
        client.sendDownColor(fields, BoardModel.Color.DARK);
        assertEquals(BoardModel.Color.DARK, client.fieldColors[1]);
        assertEquals(BoardModel.Color.DARK, client.fieldColors[2]);
        assertEquals(BoardModel.Color.DARK, client.fieldColors[3]);
    }

    @Test
    void testSendDownColor() {
        client.sendDownColor(2, BoardModel.Color.LIGHT);
        assertEquals(BoardModel.Color.LIGHT, client.fieldColors[2]);
    }

    @Test
    void sendDownPlace() {
        List<Integer> possibleFields = Arrays.asList(1, 2, 3);
        client.sendDownPlace(possibleFields);
        assertTrue(client.sendDownPlaceCalled);
        assertEquals(possibleFields, client.sendDownPlaceFields);
    }

    @Test
    void giveFocus() {
        client.giveFocus();
        assertTrue(client.focus);
    }

    @Test
    void sendDownRemove() {
        List<Integer> opponentFields = Arrays.asList(4, 5);
        client.sendDownRemove(opponentFields);
        assertTrue(client.sendDownRemoveCalled);
        assertEquals(opponentFields, client.sendDownRemoveFields);
    }

    @Test
    void sendDownMove() {
        List<Integer> movableFields = Arrays.asList(2, 3);
        client.sendDownMove(movableFields);
        assertTrue(client.sendDownMoveCalled);
        assertEquals(movableFields, client.sendDownMoveFields);
    }

    @Test
    void sendDownMoveTo() {
        List<Integer> possibleFields = Arrays.asList(1, 2);
        client.sendDownMoveTo(3, possibleFields);
        assertTrue(client.sendDownMoveToCalled);
        assertEquals(3, client.sendDownMoveToField);
        assertEquals(possibleFields, client.sendDownMoveToFields);
    }

    @Test
    void endGame() {
        client.endGame();
        assertTrue(client.endGameCalled);
    }
}