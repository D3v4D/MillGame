package org.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represents a top list for storing and managing players and their scores.
 * The list maintains a ranking of players sorted by their scores.
 * NOTE: This is used to serialize and deserialize the state of the toplist to or from a json file.
 */
public class TopList {

    /**
     * A list of players participating in the game, along with their scores.
     */
    private ArrayList<Player> list;

    /**
     * Constructor to initialize an empty top list.
     */
    public TopList() {
        list = new ArrayList<>();
    }

    /**
     * Retrieves the list of players, sorted by their scores in descending order.
     *
     * @return A sorted list of players.
     */
    public ArrayList<Player> getList() {
        Collections.sort(list, new PlayerComp());
        return list;
    }


    /**
     * Increments the score of a player with the specified name.
     *
     * @param name The name of the player whose score is to be incremented.
     */
    public void addWin(String name) {
        list.forEach(x -> {
            if (x.name.equals(name)) {
                x.win();
            }
        });
    }

    /**
     * Adds a new player to the list.
     *
     * @param name  The name of the new player.
     * @param score The initial score of the new player.
     * @throws Exception If a player with the same name already exists.
     */
    public void addPlayer(String name, int score) throws Exception {
        for (Player player : list) {
            if (player.name.equals(name)) {
                throw new Exception("USERNAME ALREADY EXISTS");
            }
        }
        list.add(new Player(name, score));
    }

    /**
     * A comparator to sort players by their scores in descending order.
     */
    private class PlayerComp implements Comparator<Player> {
        @Override
        public int compare(Player a, Player b) {
            return b.getScore() - a.getScore();
        }
    }

    /**
     * Represents an individual player with a name and score.
     */
    public static class Player {

        /**
         * The name of the player.
         */
        private String name;

        /**
         * The score of the player.
         */
        private int score;

        /**
         * Retrieves the name of the player.
         *
         * @return The player's name.
         */
        public String getName() {
            return name;
        }

        /**
         * Retrieves the score of the player.
         *
         * @return The player's score.
         */
        public int getScore() {
            return score;
        }

        /**
         * Increments the player's score by one.
         */
        public void win() {
            score++;
        }

        /**
         * Constructs a new player with the specified name and score.
         *
         * @param n The name of the player.
         * @param s The initial score of the player.
         */
        public Player(String n, int s) {
            name = n;
            score = s;
        }
    }
}
