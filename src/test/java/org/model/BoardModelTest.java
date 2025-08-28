package org.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.util.MapModel;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BoardModelTest {

    @Test
    void gameState() {
        GameState gs = new GameState(new MapModel(), new ConcurrentStack<>());
    }

    BoardModel boardModel;

    @BeforeEach
    void setUp() {
        boardModel = new BoardModel(new HashMap<>() {{
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

        }}, new ArrayList<>() {{
            add(new int[]{1, 2, 3});
            add(new int[]{2, 3, 1});
            add(new int[]{1, 4, 7});
            add(new int[]{2, 5, 8});
            add(new int[]{3, 6, 9});
            add(new int[]{4, 5, 6});
            add(new int[]{7, 8, 9});
        }});
        boardModel.putPiece(1, BoardModel.Color.LIGHT);
        boardModel.putPiece(2, BoardModel.Color.LIGHT);
        boardModel.putPiece(3, BoardModel.Color.LIGHT);
        boardModel.putPiece(6, BoardModel.Color.DARK);

        /*
            X       - dark
            O       - light
            number  - blank

            O---O---O
            |   |   |
            4---5---6
            |   |   |
            7---8---X
         */
    }

    @Test
    void getNeighbouring() {
        assertEquals(boardModel.getNeighbouring(5), new ArrayList<>() {{
            add(2);
            add(4);
            add(6);
            add(8);
        }});
    }

    @Test
    void testGetNeighbouring() {
        assertEquals(boardModel.getNeighbouring(new ArrayList<>() {{
            add(1);
            add(9);
        }}), new ArrayList<>() {{
            add(2);
            add(4);
            add(6);
            add(8);
        }});
    }

    @Test
    void movePiece() {
        try {
            assertEquals(false, boardModel.movePiece(1, 2));
            assertThrows(RuntimeException.class, () -> boardModel.movePiece(1, 6));
        } catch (Exception e) {

        }
    }

    @Test
    void checkForUnmill() {
        boardModel.checkForMill(2);
        boardModel.checkForMill(1);
        boardModel.checkForMill(3);
        boardModel.putPiece(4, BoardModel.Color.LIGHT);
        boardModel.putPiece(7, BoardModel.Color.LIGHT);
        boardModel.putPiece(9, BoardModel.Color.LIGHT);
        boardModel.putPiece(6, BoardModel.Color.LIGHT);
        boardModel.movePiece(2, 5);
        assertTrue(boardModel.checkForMill(1));
        assertTrue(boardModel.checkForMill(3));
    }

    @Test
    void checkForMill() {
        assertTrue(boardModel.checkForMill(2));
    }

    @Test
    void putPiece() {
        boardModel.putPiece(8, BoardModel.Color.DARK);
        boardModel.putPiece(2, BoardModel.Color.BLANK);

    }

    @Test
    void getFields() {
        checkForMill();
        assertEquals(new ArrayList<Integer>() {{
                         add(1);
                         add(2);
                         add(3);
                     }},
                boardModel.getFields(BoardModel.Color.LIGHT, true));

        assertEquals(new ArrayList<Integer>() {{
                         add(6);
                     }},
                boardModel.getFields(BoardModel.Color.DARK, false));
    }

    @Test
    void testGetFields() {
        assertEquals(new ArrayList<Integer>() {{
                         add(1);
                         add(2);
                         add(3);
                     }},
                boardModel.getFields(BoardModel.Color.LIGHT));
    }

    @Test
    void filterFieldsByColor() {
        assertEquals(new ArrayList<Integer>() {{
                         add(1);
                         add(2);
                         add(3);
                     }},
                boardModel.filterFieldsByColor(new ArrayList<Integer>() {{
                    add(1);
                    add(2);
                    add(3);
                    add(4);
                    add(5);
                    add(6);
                    add(7);
                    add(8);
                    add(9);
                }}, BoardModel.Color.LIGHT));
    }
}