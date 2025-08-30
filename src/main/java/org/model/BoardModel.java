package org.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code BoardModel} class represents the game board and the pieces in the game.
 * It stores information about the board layout, the pieces' positions, and the groups of pieces that form mills.
 */
public class BoardModel {
    //The board is represented as a graph, where each field is a node and edges connect neighboring fields.
    private final HashMap<Integer, ArrayList<Integer>> boardMap;

    //Array index represents the field number
    //pieces[0] is not used
    private final Piece[] pieces;

    //This is a 2D array where each row represents a group of three pieces that can form a mill.
    //groups[0] is not used
    //groups[i][0]: whether the whole group is in a mill
    //groups[i][1]-[3]: the 3 fields that make up the group
    //1-based indexing for easier understanding
    private final int[][] groups;

    private static final int FALSE = -1;
    private static final int TRUE = 1;

    /**
     * Constructs a new {@code BoardModel} instance.
     *
     * @param f A map representing the neighboring fields for each field on the board.
     * @param g A list of groups representing the groups of three pieces that can form a mill.
     */
    public BoardModel(Map<Integer, ArrayList<Integer>> f, List<int[]> g) {
        pieces = new Piece[f.size() + 1];
        for (int i = 1; i < f.size() + 1; i++) {
            pieces[i] = new Piece();
        }
        groups = new int[g.size() + 1][4];
        for (int i = 0; i < g.size(); i++) {
            int[] group = g.get(i);
            groups[i + 1][0] = FALSE;
            groups[i + 1][1] = group[0];
            groups[i + 1][2] = group[1];
            groups[i + 1][3] = group[2];
        }
        boardMap = (HashMap<Integer, ArrayList<Integer>>) f;
    }

    /**
     * Gets the neighboring fields for a given field.
     *
     * @param i The field index.
     * @return A list of neighboring fields.
     */
    public List<Integer> getNeighbouring(Integer i) {
        return boardMap.get(i);
    }

    /**
     * Gets the neighboring fields for a list of fields.
     *
     * @param list A list of field indices.
     * @return A list of neighboring fields for all the given fields.
     */
    public List<Integer> getNeighbouring(List<Integer> list) {
        ArrayList<Integer> ret = new ArrayList<>();
        list.forEach(i -> ret.addAll(getNeighbouring(i)));
        return ret;
    }

    /**
     * Moves a piece from one field to another.
     *
     * @param from The index of the field where the piece is moving from.
     * @param to   The index of the field where the piece is moving to.
     * @return {@code true} if the move results in a mill, {@code false} otherwise.
     * @throws RuntimeException If the move is not valid (note: this is not possible if the game is played without any modifications)
     */
    public boolean movePiece(Integer from, Integer to) throws RuntimeException {
        if (pieces[to].color != Color.EMPTY || pieces[from].color == Color.EMPTY) {
            throw new RuntimeException("INVALID MOVE");
        }
        pieces[to].color = pieces[from].color;
        pieces[from].color = Color.EMPTY;
        checkForUnmill(from);
        return checkForMill(to);
    }


    /**
     * Checks if moving a piece breaks any mills on the board.
     *
     * @param location The location of the piece that was moved.
     */
    public void checkForUnmill(int location) {
        //Since every time we move, the moved piece's position will be BLANK, meaning every mill in which it is involved will disappear
        //but its neighbors can still be part of a mill, so we need to pay close attention to those.
        for (int i = 1; i < groups.length; i++) {
            //check for "unmilling"
            if (groupContains(groups[i], location)) {
                setMillForGroup(i, false);
                //We need to check for "falsely unmilled mills" tho
                if (location == groups[i][1]) {
                    checkFalseUnmill(i, 2, 3);
                } else if (location == groups[i][3]) {
                    checkFalseUnmill(i, 1, 2);
                } else {
                    checkFalseUnmill(i, 1, 3);
                }
            }
        }
    }

    private void setMillForGroup(int i, boolean mill) {
        pieces[groups[i][1]].inMill = pieces[groups[i][2]].inMill = pieces[groups[i][3]].inMill = mill;
        groups[i][0] = mill? TRUE: FALSE;
    }



    /**
     * Helper method to check and restore mills that may have been falsely "unmilled."
     *
     * @param i The index of the group.
     * @param a One of the positions in the group.
     * @param b Another position in the group.
     */
    private void checkFalseUnmill(int i, int a, int b) {
        for (int j = 1; j < groups.length; j++) {
            if (groupContains(groups[j], groups[i][a]) && groups[j][0] == TRUE) {
                pieces[groups[i][a]].inMill = true;
            }
            if (groupContains(groups[j], groups[i][b]) && groups[j][0] == TRUE) {
                pieces[groups[i][b]].inMill = true;
            }
        }
    }

    /**
     * Helper method to check if a group contains a specific value.
     * @param arr The group to check.
     * @param val The value to look for.
     * @return {@code true} if the group contains the value, {@code false} otherwise.
     */
    private boolean groupContains(int[] arr, int val) {
        return arr[1] == val || arr[2] == val || arr[3] == val;
    }

    private boolean groupSameColor(int[] arr) {
        return pieces[arr[1]].color == pieces[arr[2]].color && pieces[arr[2]].color == pieces[arr[3]].color;
    }

    /**
     * Checks if moving a piece creates a mill.
     *
     * @param location The index of the field where the piece is being moved to.
     * @return {@code true} if the move creates a mill, {@code false} otherwise.
     */
    public boolean checkForMill(int location) {
        boolean ret = false;
        for (int i = 1; i < groups.length; i++) {
            if (groupContains(groups[i], location) && (groupSameColor(groups[i]))) {
                setMillForGroup(i, true);
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Places a piece on the specified field.
     *
     * @param where The index of the field where the piece is to be placed.
     * @param color  The color of the piece to be placed.
     * @throws RuntimeException If the field is already occupied.
     */
    public void putPiece(int where, Color color) throws RuntimeException {
        if (pieces[where].color != Color.EMPTY) {
            throw new RuntimeException("INVALID PUT");
        }
        pieces[where].color = color;
    }

    /**
     * Gets a list of fields that contain pieces of the specified color and status (whether they are part of a mill or not).
     *
     * @param byColor The color of the pieces to check.
     * @param byMill  {@code true} to filter for pieces that are in a mill, {@code false} for those not in a mill.
     * @return A list of field indices with the specified color and mill status.
     */
    public List<Integer> getFields(Color byColor, boolean byMill) {
        ArrayList<Integer> ret = new ArrayList<>();
        if (byMill) {
            for (int i = 1; i < pieces.length; i++) {
                if (pieces[i].color == byColor && pieces[i].inMill) {
                    ret.add(i);
                }
            }
        } else {
            for (int i = 1; i < pieces.length; i++) {
                if (pieces[i].color == byColor && !pieces[i].inMill) {
                    ret.add(i);
                }
            }
        }
        return ret;
    }

    /**
     * Gets a list of fields that contain pieces of the specified color.
     *
     * @param byColor The color of the pieces to check.
     * @return A list of field indices with the specified color.
     */
    public List<Integer> getFields(Color byColor) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = 1; i < pieces.length; i++) {
            if (pieces[i].color == byColor) {
                ret.add(i);
            }
        }
        return ret;
    }

    /**
     * Filters a list of fields based on the color of the pieces at those fields.
     *
     * @param fieldsToSort The list of fields to be filtered.
     * @param byColor      The color to filter by.
     * @return A list of field indices that contain pieces of the specified color.
     */
    public List<Integer> filterFieldsByColor(@NotNull List<Integer> fieldsToSort, Color byColor) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (Integer i : fieldsToSort) {
            if (pieces[i].color == byColor) {
                ret.add(i);
            }
        }
        return ret;
    }

    /**
     * Enum representing the color states of a game piece.
     */

    public enum Color {LIGHT, DARK, EMPTY}

    /**
     * The {@code Piece} class represents a single piece on the board.
     * Each piece has a color and a status indicating whether it is part of a mill.
     */
    private static class Piece {

        private Color color; // The color of the piece
        private boolean inMill = false; // Whether the piece is part of a mill

        /**
         * Constructs a new {@code Piece} with the default color of {@code BLANK}.
         */
        public Piece() {
            color = Color.EMPTY;
        }
    }
}