package org.view;

import org.util.MapModel;

import java.util.List;
import java.util.function.Consumer;


public interface ComponentGenerator {
    void dispose();

    enum HorizontalAlignment { LEFT, CENTER, RIGHT }
    enum VerticalAlignment { TOP, MIDDLE, BOTTOM }
    record Color(int r, int g, int b) {
        public static Color lightBrown = new Color(255, 222, 173);
        public static Color darkBrown  = new Color(100, 30, 0);
        public static Color lightGreen = new Color(0, 150, 0);

    }
    enum FieldType{EMPTY, MOVABLE_TO,
        DARK, CHOSEN_DARK, MOVABLE_DARK, REMOVABLE_DARK,
        LIGHT,  CHOSEN_LIGHT, MOVABLE_LIGHT, REMOVABLE_LIGHT
    }

    void modifyButtonText(int ID, String text);
    void modifyLabelText(int ID, String text);

    void updateFieldGraphic(List<Integer> fieldList, FieldType type);
    void updateFieldGraphic(int field, FieldType type);


    void generateBase(int x, int y);


    void paintBackground(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2  , Color color1, Color color2, int layer);
    void paintBoard(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2, Color color1, Color color2, MapModel mapModel, int layer, Consumer<Integer> callback);

    void addUITestGif();

    int addLabel(String text, int x, int y, int width, int height, int size, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int layer);
    int addButton(String text, int x, int y, int width, int height, Runnable callback, int layer);
    <T> int addComboBox(T[] items, T selectedItem, int x, int y, int width, int height, Consumer<String> callback, int layer);


    void modifyStack(int ID, int unit1, Color color1, int unit2, Color color2);

    /**
     * Draws a stack of game pieces at the specified location with given properties.
     * Each piece in the stack is represented as a rectangle, and the pieces are drawn with a specified gap between them.
     * If it is the current player's stack, it is drawn with a border to indicate ownership.
     * @param x               The x-coordinate of the stack's position.
     * @param y               The y-coordinate of the stack's position.
     * @param width           The width of the stack.
     * @param height          The height of the stack.
     * @param gap             The gap between individual pieces in the stack.
     * @param unit            The number of pieces in the stack.
     * @param color           The color of the pieces in the stack.
     * @param layer           The layer on which to draw the stack (higher layers are drawn on top).
     * @param isCorrectPlayer Indicates if the stack belongs to the current player (true) or the opponent (false).
     * @return The ID of the drawn stack component.
     *
     */
    int drawStack(int x, int y, int width, int height, int gap, int unit, Color color, int layer, boolean isCorrectPlayer);

    void show();
    void hide();

    ComponentGenerator copy();
}
