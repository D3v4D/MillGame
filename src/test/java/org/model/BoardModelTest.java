package org.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.util.MapModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.UIManager.put;
import static org.junit.jupiter.api.Assertions.*;

class BoardModelTest {

    static MapModel mapModel = new MapModel();

    @NotNull
    static ArrayList<Integer> listOf(@NotNull Integer... integers){
        ArrayList<Integer> list = new ArrayList<>();
        for (Integer i: integers) list.add(i);
        return list;
    }

    @BeforeAll
    static void setMapModel(){
        mapModel.pieces = 9;
        mapModel.fields = new HashMap<>(){{
            put(1, listOf(2,4));
            put(2, listOf(1,3,5));
            put(3, listOf(2,6));
            put(4, listOf(1,5,7));
            put(5, listOf(2,4,6,8));
            put(6, listOf(3,5,9));
            put(7, listOf(4,8));
            put(8, listOf(5,7,9));
            put(9, listOf(6,8));
        }};

        mapModel.groups = new ArrayList<>(){{
            add(new int[]{1,2,3});
            add(new int[]{4,5,6});
            add(new int[]{7,8,9});
            add(new int[]{1,4,7});
            add(new int[]{2,5,8});
            add(new int[]{3,6,9});
        }};

    }

    BoardModel boardModel;
    @BeforeEach
    void setUp() {
        boardModel = new BoardModel(mapModel);
        boardModel.putPiece(1, BoardModel.Color.LIGHT);
        boardModel.putPiece(2, BoardModel.Color.LIGHT);
        boardModel.putPiece(3, BoardModel.Color.LIGHT);
        boardModel.putPiece(9, BoardModel.Color.DARK);

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
    void isFieldEmpty() {
        assertTrue(boardModel.isFieldEmpty(4));
        assertFalse(boardModel.isFieldEmpty(1));
        assertFalse(boardModel.isFieldEmpty(9));
    }

    @Test
    void isFieldOfColor() {
        assertTrue(boardModel.isFieldOfColor(1, BoardModel.Color.LIGHT));
        assertFalse(boardModel.isFieldOfColor(1, BoardModel.Color.DARK));
        assertFalse(boardModel.isFieldOfColor(4, BoardModel.Color.LIGHT));
        assertFalse(boardModel.isFieldOfColor(4, BoardModel.Color.DARK));
        assertTrue(boardModel.isFieldOfColor(9, BoardModel.Color.DARK));
    }

    @Test
    void isFieldInMill() {
        assertTrue(boardModel.isFieldInMill(1));
        assertTrue(boardModel.isFieldInMill(2));
        assertTrue(boardModel.isFieldInMill(3));
        assertFalse(boardModel.isFieldInMill(4));
        assertFalse(boardModel.isFieldInMill(5));
        assertFalse(boardModel.isFieldInMill(6));
        assertFalse(boardModel.isFieldInMill(7));
        assertFalse(boardModel.isFieldInMill(8));
        assertFalse(boardModel.isFieldInMill(9));
    }

    @Test
    void areNeighbors() {
        assertTrue(boardModel.areNeighbors(1,2));
        assertTrue(boardModel.areNeighbors(1,4));
        assertFalse(boardModel.areNeighbors(1,3));
        assertFalse(boardModel.areNeighbors(1,5));
        assertFalse(boardModel.areNeighbors(1,6));
        assertFalse(boardModel.areNeighbors(1,7));
        assertFalse(boardModel.areNeighbors(1,8));
        assertFalse(boardModel.areNeighbors(1,9));

        assertTrue(boardModel.areNeighbors(5,2));
        assertTrue(boardModel.areNeighbors(5,4));
        assertTrue(boardModel.areNeighbors(5,6));
        assertTrue(boardModel.areNeighbors(5,8));
        assertFalse(boardModel.areNeighbors(5,1));
        assertFalse(boardModel.areNeighbors(5,3));
        assertFalse(boardModel.areNeighbors(5,7));
        assertFalse(boardModel.areNeighbors(5,9));
    }

    @Test
    void movePiece() {
        assertFalse(boardModel.movePiece(3,6));
        assertTrue(boardModel.movePiece(6,3));
        assertThrows(RuntimeException.class, () -> boardModel.movePiece(2,6));
        assertThrows(RuntimeException.class, () -> boardModel.movePiece(4,5));
        assertThrows(RuntimeException.class, () -> boardModel.movePiece(1,2));
    }

    @Test
    void putPiece() {
        assertThrows(RuntimeException.class, () -> boardModel.putPiece(1, BoardModel.Color.DARK));
        assertThrows(RuntimeException.class, () -> boardModel.putPiece(2, BoardModel.Color.LIGHT));
        assertThrows(RuntimeException.class, () -> boardModel.putPiece(9, BoardModel.Color.DARK));
        assertFalse(boardModel.putPiece(7, BoardModel.Color.DARK));
        assertTrue(boardModel.isFieldOfColor(7, BoardModel.Color.DARK));
        assertTrue(boardModel.putPiece(8, BoardModel.Color.DARK));

    }

    @Test
    void getFields() {
        assertEquals(boardModel.getFields(BoardModel.Color.LIGHT), listOf(1, 2, 3));
        assertEquals(boardModel.getFields(BoardModel.Color.DARK), listOf(9));
        assertEquals(boardModel.getFields(BoardModel.Color.LIGHT, true), listOf(1, 2, 3));
        assertEquals(boardModel.getFields(BoardModel.Color.LIGHT, false), new ArrayList<>());
        assertEquals(boardModel.getFields(BoardModel.Color.DARK, false), listOf(9));

    }

    @Test
    void getMovableFields() {
        assertEquals(boardModel.getMovableFields(BoardModel.Color.LIGHT), new ArrayList<>(){{
            add(1);
            add(2);
            add(3);
        }});
        assertEquals(boardModel.getMovableFields(BoardModel.Color.DARK), new ArrayList<>(){{
            add(9);
        }});
        assertThrows(RuntimeException.class, () -> boardModel.getMovableFields(BoardModel.Color.EMPTY));
    }

    @Test
    void filterFieldsByColor() {
        assertEquals(boardModel.filterFieldsByColor(new ArrayList<>(){{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
        }}, BoardModel.Color.LIGHT), new ArrayList<>(){{
            add(1);
            add(2);
            add(3);
        }});
        assertEquals(boardModel.filterFieldsByColor(new ArrayList<>(){{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
        }}, BoardModel.Color.DARK), new ArrayList<>(){});
        assertEquals(boardModel.filterFieldsByColor(new ArrayList<>(){{
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
        }}, BoardModel.Color.EMPTY), new ArrayList<>(){{
            add(4);
            add(5);
        }});
    }

    @Test
    void getPossibleMoves() {
        assertEquals(boardModel.getPossibleMoves(1, false), listOf(4));
        assertEquals(boardModel.getPossibleMoves(2, false), listOf(5));
        assertEquals(boardModel.getPossibleMoves(3, false), listOf(6));
        assertEquals(boardModel.getPossibleMoves(4, false), listOf(5, 7));
        assertEquals(boardModel.getPossibleMoves(5, false), listOf(4,6,8));
        assertEquals(boardModel.getPossibleMoves(6, false), listOf(5));
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

    }
}