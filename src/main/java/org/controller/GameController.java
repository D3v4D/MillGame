package org.controller;

import com.google.gson.Gson;
import org.model.BoardModel;
import org.model.ConcurrentStack;
import org.model.MapModel;
import org.model.TopList;
import org.view.InitGameScreen;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * GameController manages the game logic, including piece placement,
 * movement, and tracking player turns.
 */
public class GameController {
    private boolean player1 = true; //1 - light; 2 - dark ; true - 1; false -2
    public BoardModel boardModel;
    private InitGameScreen gameScreen;
    private ConcurrentStack<Integer> pressed;
    private int player1Pieces;
    private int player2Pieces;
    private boolean invalid = true;
    private boolean notEnclosed = true;

    /**
     * Constructs a GameController and starts the game phases.
     *
     * @param mapModel the model representing the game map
     * @param p        the stack used to track user inputs
     * @param gS       the screen responsible for rendering the game UI
     * @throws InterruptedException if interrupted while waiting for user input
     */
    public GameController(MapModel mapModel, ConcurrentStack<Integer> p, InitGameScreen gS) throws InterruptedException {
        /*System.out.println("GAMECONTROLLER");*/
        boardModel = new BoardModel(mapModel.fields, mapModel.groups);
        pressed = p;
        gameScreen = gS;
        player1Pieces = player2Pieces = mapModel.pieces;

        gameScreen.phaseUp(); //startphase
        startPhase(mapModel.pieces);

        gameScreen.phaseUp(); //gamephase
        gamePhase();

        gameScreen.winScreen(player1);
        endGame();
    }

    /**
     * Switches the active player.
     */
    private void switchPlayer() {
        player1 = !player1;
        gameScreen.changePlayer();
    }


    /**
     * Handles the start phase where players alternately place their pieces on the board.
     *
     * @param piecesPerPlayer the number of pieces each player has
     * @throws InterruptedException if interrupted while waiting for user input
     */
    public void startPhase(int piecesPerPlayer) throws InterruptedException {
        /*
            ket jatekos felvaltva valaszt mezoket
            pPP*2-szer fut le
         */
        for (int i = 0; i < piecesPerPlayer * 2; i++) {
            BoardModel.Color color;
            boolean valid = false;
            while (!valid) {
                Integer p = pressed.pop();
                if (boardModel.getFields(BoardModel.Color.BLANK).contains(p)) {
                    color = player1 ? BoardModel.Color.LIGHT : BoardModel.Color.DARK;
                    putPiece(p, color);
                    valid = true;
                    gameScreen.putPiecesPlayer(player1);
                    if (boardModel.checkForMill(p)) {
                        mill();
                    }
                    switchPlayer();
                }
            }
        }
    }

    /**
     * Handles the main game phase where players move pieces and attempt to win.
     *
     * @throws InterruptedException if interrupted while waiting for user input
     */

    public void gamePhase() throws InterruptedException {
        boolean running = true;
        notEnclosed = true;
        while (running && notEnclosed) {
            if (player1Pieces < 3 || player2Pieces < 3) {
                running = false;
                /*System.out.print("GAME OVER, ");
                System.out.println(player1 ? "DARK WON" : "LIGHT WON");*/
            } else if (step() && notEnclosed) {
                mill();
            }
            switchPlayer();
        }
    }

    /**
     * Ends the game, updates the leaderboard, and waits for user acknowledgment.
     *
     * @throws InterruptedException if interrupted while waiting for user input
     */
    public void endGame() throws InterruptedException {
        String name = player1 ? "DARK" : "LIGHT";
        try {
            FileReader fr = new FileReader("toplist.json");
            TopList tl = new Gson().fromJson(fr, TopList.class);
            fr.close();

            tl.addWin(name);

            FileWriter fw = new FileWriter("toplist.json");
            new Gson().toJson(tl, fw);
            fw.close();
        } catch (Exception e) {
        }
        pressed.pop();
    }

    /**
     * Handles the logic when a player forms a mill and removes an opponent's piece.
     *
     * @throws InterruptedException if interrupted while waiting for user input
     */
    public void mill() throws InterruptedException {
        boolean valid = false;
        ArrayList<Integer> possibleChoices;
        Integer p;
        possibleChoices = boardModel.getFields(player1 ? BoardModel.Color.DARK : BoardModel.Color.LIGHT, false);
        gameScreen.putPieces(possibleChoices, player1 ? BoardModel.Color.PICK_DARK : BoardModel.Color.PICK_LIGHT);
        if (possibleChoices.isEmpty()) return;
        if (player1) player2Pieces--;
        else player1Pieces--;
        if (player2Pieces < 3 || player1Pieces < 3) {
            player2Pieces--;
            player1Pieces--;
            return;
        }
        while (!valid) {
            p = pressed.pop();
            if (possibleChoices.contains(p)) {
                gameScreen.putPieces(possibleChoices, player1 ? BoardModel.Color.DARK : BoardModel.Color.LIGHT);
                gameScreen.pickPiecesPlayer(player1);
                putPiece(p, BoardModel.Color.BLANK);
                valid = true;
            }
        }
    }

    /**
     * Executes a player's turn by moving a piece or taking a valid action.
     *
     * @return true if the player forms a mill during the turn
     * @throws InterruptedException if interrupted while waiting for user input
     */
    public boolean step() throws InterruptedException {
        BoardModel.Color basic;
        BoardModel.Color movable;
        BoardModel.Color chosen;
        if (player1) {
            basic = BoardModel.Color.LIGHT;
            chosen = BoardModel.Color.CHOSEN_LIGHT;
            movable = BoardModel.Color.MOVABLE_LIGHT;
        } else {
            basic = BoardModel.Color.DARK;
            chosen = BoardModel.Color.CHOSEN_DARK;
            movable = BoardModel.Color.MOVABLE_DARK;
        }
        //we choose a piece
        // (we pick the blank fields,
        //  get their neighbours
        //  filter the given color) -> this gives us the pieces that can move in a given scenario
        boolean lastStage = player1 && player1Pieces == 3 || !player1 && player2Pieces == 3;
        ArrayList<Integer> list = boardModel.getFields(BoardModel.Color.BLANK);
        ArrayList<Integer> onBorder = boardModel.getNeighbouring(list);
        boolean ret = false;
        Integer p;
        invalid = true;
        ArrayList<Integer> filteredList = lastStage ? boardModel.getFields(basic) : boardModel.filterFieldsByColor(onBorder, basic);

        if (filteredList.isEmpty()) {
            //THE OTHER PLAYER WON

            gameScreen.winScreen(player1);
            endGame();
        }

        filteredList.forEach(i -> gameScreen.putPiece(i, movable));

        while (invalid) {
            p = pressed.pop();
            if (filteredList.contains(p)) {
                //we choose a valid move
                // (check the neighbouring fields of the list of moveable pieces,
                //  filter the blank fields)
                ArrayList<Integer> neighbouringBLANK;
                if (lastStage) {
                    neighbouringBLANK = boardModel.getFields(BoardModel.Color.BLANK);
                } else {
                    ArrayList<Integer> neighbouringAll = boardModel.getNeighbouring(p);
                    neighbouringBLANK = boardModel.filterFieldsByColor(neighbouringAll, BoardModel.Color.BLANK);
                }
                filteredList.forEach(i -> gameScreen.putPiece(i, movable));
                gameScreen.putPiece(p, chosen);
                gameScreen.putPieces(list, BoardModel.Color.BLANK);
                gameScreen.putPieces(neighbouringBLANK, BoardModel.Color.CHOOSABLE);
                ret = move(p, neighbouringBLANK, filteredList, list, movable, basic, chosen);
            }
        }
        return ret;
    }

    /**
     * Handles moving a piece to a new position on the board.
     *
     * @param p                 the current position of the piece
     * @param neighbouringBLANK the valid positions to which the piece can move
     * @param filteredList      the list of movable pieces
     * @param list              the list of blank fields
     * @param movable           the color representing movable pieces
     * @param basic             the color representing the player's pieces
     * @param chosen            the color representing a selected piece
     * @return true if the piece movement forms a mill
     * @throws InterruptedException if interrupted while waiting for user input
     */
    private boolean move(Integer p, ArrayList<Integer> neighbouringBLANK, ArrayList<Integer> filteredList, ArrayList<Integer> list, BoardModel.Color movable, BoardModel.Color basic, BoardModel.Color chosen) throws InterruptedException {
        boolean ret = false;
        boolean toInvalid = true;
        Integer p2;
        while (toInvalid) {
            p2 = pressed.pop();
            if (p.equals(p2)) { //if they chose the same place, we revert back to the previous phase
                gameScreen.putPieces(filteredList, movable);
                gameScreen.putPieces(list, BoardModel.Color.BLANK);
                toInvalid = false;
            } else if (neighbouringBLANK.contains(p2)) { //if it is blank
                ret = boardModel.movePiece(p, p2);
                gameScreen.putPieces(neighbouringBLANK, BoardModel.Color.BLANK);
                gameScreen.putPieces(filteredList, basic);
                gameScreen.putPiece(p, BoardModel.Color.BLANK);
                gameScreen.putPiece(p2, basic);
                invalid = false;
                toInvalid = false;
            } else if (filteredList.contains(p2)) { //if its a movable piece, make it the focused piece
                gameScreen.putPiece(p, movable);
                gameScreen.putPiece(p2, chosen);
                toInvalid = false;
                pressed.push(p2);
            }
        }
        return ret;
    }

    /**
     * Places a piece on the board and updates the UI.
     *
     * @param where the position on the board
     * @param what  the color of the piece
     */
    private void putPiece(int where, BoardModel.Color what) {
        boardModel.putPiece(where, what);
        gameScreen.putPiece(where, what);
    }
}
