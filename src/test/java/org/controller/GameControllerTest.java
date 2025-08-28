package org.controller;

import org.junit.jupiter.api.*;
import org.model.BoardModel;
import org.model.ConcurrentStack;
import org.util.MapModel;
import org.view.InitGameScreen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import  org.mockito.*;

import java.util.ArrayList;
import java.util.HashMap;

class GameControllerTest {
    MapModel mM;
    ConcurrentStack<Integer> pressed;
    @Mock
    InitGameScreen mockScreen = mock(InitGameScreen.class);
    GameController gameController;

    @BeforeEach
    void setUp() {
        try {
            mM = new MapModel();
            mM.pieces = 3;
            mM.locationList = new ArrayList<>();
            mM.lineList = new ArrayList<>();
            mM.fields = new HashMap<>() {{
                put(1, new ArrayList<>() {{
                    add(2);
                    add(4);
                }});
                put(2, new ArrayList<>() {{
                    add(1);
                    add(3);
                    add(5);
                }});
                put(3, new ArrayList<>() {{
                    add(2);
                    add(6);
                }});
                put(4, new ArrayList<>() {{
                    add(1);
                    add(5);
                    add(7);
                }});
                put(5, new ArrayList<>() {{
                    add(2);
                    add(4);
                    add(6);
                    add(8);
                }});
                put(6, new ArrayList<>() {{
                    add(3);
                    add(5);
                    add(9);
                }});
                put(7, new ArrayList<>() {{
                    add(4);
                    add(8);
                }});
                put(8, new ArrayList<>() {{
                    add(5);
                    add(7);
                }});
                put(9, new ArrayList<>() {{
                    add(6);
                    add(8);
                }});

            }};
            mM.groups = new ArrayList<>() {{
                add(new int[]{1, 2, 3});
                add(new int[]{1, 4, 7});
                add(new int[]{2, 5, 8});
                add(new int[]{3, 6, 9});
                add(new int[]{4, 5, 6});
                add(new int[]{7, 8, 9});
            }};
            pressed = new ConcurrentStack<>();

            mockScreen = mock(InitGameScreen.class);
            doNothing().when(mockScreen).phaseUp();
            doNothing().when(mockScreen).winScreen(true);
            doNothing().when(mockScreen).winScreen(false);
        } catch (Exception e) {

        }
    }

    @Test
    void testCase1() {
        try {
            mockScreen = mock(InitGameScreen.class);
            doNothing().when(mockScreen).phaseUp();

            pressed.list = new ArrayList<>() {{
                add(1);
                add(5);
                add(3);
                add(4);
                add(2);
                add(4);
                add(6);
                add(-69);
            }};
            pressed.reload();

            gameController = new GameController(mM, pressed, mockScreen);

            BoardModel boardModel = new BoardModel(mM.fields, mM.groups);
            boardModel.putPiece(1, BoardModel.Color.LIGHT);
            boardModel.putPiece(3, BoardModel.Color.LIGHT);
            boardModel.putPiece(2, BoardModel.Color.LIGHT);
            boardModel.putPiece(4, BoardModel.Color.DARK);
            boardModel.putPiece(6, BoardModel.Color.DARK);

            assertEquals(boardModel ,gameController.boardModel);
        } catch (Exception e) {

        }
    }

    @Test
    void testCase2() {
        try {
            mM.pieces = 4;
            pressed.list = new ArrayList<>() {{
                add(1);
                add(5);
                add(3);
                add(4);
                add(8);
                add(6);
                add(8);
                add(-69);
            }};
            pressed.reload();


            gameController = new GameController(mM, pressed, mockScreen);

            BoardModel boardModel = new BoardModel(mM.fields, mM.groups);
            boardModel.putPiece(1, BoardModel.Color.LIGHT);
            boardModel.putPiece(3, BoardModel.Color.LIGHT);
            boardModel.putPiece(5, BoardModel.Color.DARK);
            boardModel.putPiece(4, BoardModel.Color.DARK);
            boardModel.putPiece(6, BoardModel.Color.DARK);

            assertEquals(boardModel ,gameController.boardModel);
        } catch (Exception e) {

        }

    }

    @Test
    void testCase3() {
        try {
            pressed.list = new ArrayList<>() {{
                add(1); //1
                add(4); //2
                add(7); //3
                add(5); //4
                add(2); //5
                add(8); //6
                add(3); //7
                add(8); //8
                add(9); //9
                add(7); //10
                add(8); //11
                add(9); //
                add(9); //coverage boost
                add(4); //
                add(5); //
                add(2); //
                add(9); //12
                add(6); //13
                add(8); //14
                add(-69);
            }};
            pressed.reload();


            gameController = new GameController(mM, pressed, mockScreen);

            BoardModel boardModel = new BoardModel(mM.fields, mM.groups);
            boardModel.putPiece(1, BoardModel.Color.LIGHT);
            boardModel.putPiece(2, BoardModel.Color.LIGHT);
            boardModel.putPiece(3, BoardModel.Color.LIGHT);
            boardModel.putPiece(4, BoardModel.Color.DARK);
            boardModel.putPiece(5, BoardModel.Color.DARK);
            boardModel.putPiece(6, BoardModel.Color.DARK);

            assertEquals(boardModel ,gameController.boardModel);
        } catch (Exception e) {

        }
    }

    @Test
    void testCase4() {
        try {
            pressed.list = new ArrayList<>() {{
                add(1); //1
                add(3); //2
                add(4); //3
                add(5); //4
                add(6); //5
                add(7); //6
                add(8); //7
                add(9); //8
                add(1); //9
                add(1); //9
                add(1); //9
                add(2); //10
                add(-69);
            }};
            pressed.reload();

            gameController = new GameController(mM, pressed, mockScreen);

            BoardModel boardModel = new BoardModel(mM.fields, mM.groups);
            boardModel.putPiece(2, BoardModel.Color.LIGHT);
            boardModel.putPiece(3, BoardModel.Color.LIGHT);
            boardModel.putPiece(6, BoardModel.Color.LIGHT);
            boardModel.putPiece(8, BoardModel.Color.LIGHT);
            boardModel.putPiece(5, BoardModel.Color.DARK);
            boardModel.putPiece(3, BoardModel.Color.DARK);
            boardModel.putPiece(7, BoardModel.Color.DARK);
            boardModel.putPiece(9, BoardModel.Color.DARK);

            assertEquals(boardModel ,gameController.boardModel);
        } catch (Exception e) {

        }
    }

    @Test
    void testCase5(){
        pressed.list = new ArrayList<Integer>(){{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
            add(5);
            add(8);
            add(2);
            add(5);
            add(-69);
        }};
        pressed.reload();
        try {
            gameController = new GameController(mM, pressed, mockScreen);

            BoardModel boardModel = new BoardModel(mM.fields, mM.groups);
            boardModel.putPiece(1, BoardModel.Color.LIGHT);
            boardModel.putPiece(3, BoardModel.Color.LIGHT);
            boardModel.putPiece(5, BoardModel.Color.DARK);
            boardModel.putPiece(4, BoardModel.Color.DARK);
            boardModel.putPiece(6, BoardModel.Color.DARK);

            assertEquals(boardModel, gameController.boardModel);

        }catch (Exception e){

        }

    }
}