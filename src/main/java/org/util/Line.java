package org.util;
/**
 * Custom class for representing a line segment defined by two points (x1, y1) and (x2, y2).
 * Primarily used for drawing or connecting elements in the GUI.
 */
public class Line {

    private int x1; // X-coordinate of the starting point
    private int y1; // Y-coordinate of the starting point
    private int x2; // X-coordinate of the ending point
    private int y2; // Y-coordinate of the ending point

    /**
     * Constructs a Line with the given starting and ending coordinates.
     *
     * @param x1 The x-coordinate of the starting point.
     * @param y1 The y-coordinate of the starting point.
     * @param x2 The x-coordinate of the ending point.
     * @param y2 The y-coordinate of the ending point.
     */
    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Gets the x-coordinate of the starting point of the line.
     *
     * @return The x-coordinate of the starting point.
     */
    public int getX1() {
        return x1;
    }

    /**
     * Gets the y-coordinate of the starting point of the line.
     *
     * @return The y-coordinate of the starting point.
     */
    public int getY1() {
        return y1;
    }

    /**
     * Gets the x-coordinate of the ending point of the line.
     *
     * @return The x-coordinate of the ending point.
     */
    public int getX2() {
        return x2;
    }

    /**
     * Gets the y-coordinate of the ending point of the line.
     *
     * @return The y-coordinate of the ending point.
     */
    public int getY2() {
        return y2;
    }
}

