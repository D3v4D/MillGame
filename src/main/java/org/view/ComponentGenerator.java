package org.view;

import org.util.MapModel;

import java.util.List;
import java.util.function.Consumer;

/**
 * Interface for generating and managing UI components in a game.
 * This interface provides methods to create, modify, and display various UI elements such as buttons, labels, and game fields.
 * It also includes methods for painting backgrounds and boards, as well as handling user interactions.
 */
public interface ComponentGenerator {
    void dispose();

    /**
     * Enum representing horizontal alignment options for UI components.
     */
    enum HorizontalAlignment { LEFT, CENTER, RIGHT }
    /**
     * Enum representing vertical alignment options for UI components.
     */
    enum VerticalAlignment { TOP, MIDDLE, BOTTOM }

    /**
     * Represents a color using RGB values.
     * This record provides predefined colors for common use cases.
     */
    record Color(int r, int g, int b) {
        public static Color lightBrown = new Color(255, 222, 173);
        public static Color darkBrown  = new Color(100, 30, 0);
        public static Color lightGreen = new Color(0, 150, 0);

    }
    enum FieldType{EMPTY, MOVABLE_TO,
        DARK, CHOSEN_DARK, MOVABLE_DARK, REMOVABLE_DARK,
        LIGHT,  CHOSEN_LIGHT, MOVABLE_LIGHT, REMOVABLE_LIGHT
    }

    /** Modifies the text of a button with the specified ID.
     *
     * @param ID   the ID of the button to modify
     * @param text the new text for the button
     */
    void modifyButtonText(int ID, String text);
    /** Modifies the text of a label with the specified ID.
     *
     * @param ID   the ID of the label to modify
     * @param text the new text for the label
     */
    void modifyLabelText(int ID, String text);

    /** Updates the graphical representation of fields on the game board.
     * This method can update multiple fields at once or a single field based on the provided parameters.
     *
     * @param fieldList A list of field indices to update.
     * @param type      The type to set for the specified fields.
     */
    void updateFieldGraphic(List<Integer> fieldList, FieldType type);
    /** Updates the graphical representation of a single field on the game board.
     *
     * @param field The index of the field to update.
     * @param type  The type to set for the specified field.
     */
    void updateFieldGraphic(int field, FieldType type);

    /**
     * Generates the base layout for the UI components with the specified width and height.
     *
     * @param x The width of the base layout.
     * @param y The height of the base layout.
     */
    void generateBase(int x, int y);

    /**
     * Paints the background of a specified rectangle area with a gradient fill.
     *
     * @param rectX1  The x-coordinate of the top-left corner of the rectangle.
     * @param rectY1  The y-coordinate of the top-left corner of the rectangle.
     * @param rectX2  The x-coordinate of the bottom-right corner of the rectangle.
     * @param rectY2  The y-coordinate of the bottom-right corner of the rectangle.
     * @param fillX1  The x-coordinate where the gradient fill starts.
     * @param fillY1  The y-coordinate where the gradient fill starts.
     * @param fillX2  The x-coordinate where the gradient fill ends.
     * @param fillY2  The y-coordinate where the gradient fill ends.
     * @param color1  The starting color of the gradient.
     * @param color2  The ending color of the gradient.
     * @param layer   The layer on which to paint the background (higher layers are drawn on top).
     */
    void paintBackground(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2  , Color color1, Color color2, int layer);
    /** Paints the game board with specified properties.
     *
     * @param rectX1    The x-coordinate of the top-left corner of the rectangle area to paint.
     * @param rectY1    The y-coordinate of the top-left corner of the rectangle area to paint.
     * @param rectX2    The x-coordinate of the bottom-right corner of the rectangle area to paint.
     * @param rectY2    The y-coordinate of the bottom-right corner of the rectangle area to paint.
     * @param fillX1    The x-coordinate where the gradient fill starts.
     * @param fillY1    The y-coordinate where the gradient fill starts.
     * @param fillX2    The x-coordinate where the gradient fill ends.
     * @param fillY2    The y-coordinate where the gradient fill ends.
     * @param color1    The starting color of the gradient.
     * @param color2    The ending color of the gradient.
     * @param mapModel  The model representing the game map to be painted.
     * @param layer     The layer on which to paint the board (higher layers are drawn on top).
     * @param callback  A callback function that receives an integer ID after painting is complete.
     */
    void paintBoard(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2, Color color1, Color color2, MapModel mapModel, int layer, Consumer<Integer> callback);

    /** For UI testing purposes only. Adds a test GIF to the UI.
     * This method is intended for development and testing of the UI components.
     */
    void addUITestGif();

    /** Adds a label to the UI with specified properties.
     *
     * @param text                 The text to display on the label.
     * @param x                    The x-coordinate of the label's position.
     * @param y                    The y-coordinate of the label's position.
     * @param width                The width of the label.
     * @param height               The height of the label.
     * @param size                 The font size of the label text.
     * @param horizontalAlignment  The horizontal alignment of the text within the label (LEFT, CENTER, RIGHT).
     * @param verticalAlignment    The vertical alignment of the text within the label (TOP, MIDDLE, BOTTOM).
     * @param layer                The layer on which to add the label (higher layers are drawn on top).
     * @return The ID of the added label component.
     */
    int addLabel(String text, int x, int y, int width, int height, int size, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int layer);
    /** Adds a button to the UI with specified properties.
     *
     * @param text     The text to display on the button.
     * @param x        The x-coordinate of the button's position.
     * @param y        The y-coordinate of the button's position.
     * @param width    The width of the button.
     * @param height   The height of the button.
     * @param callback A callback function to be executed when the button is clicked.
     * @param layer    The layer on which to add the button (higher layers are drawn on top).
     * @return The ID of the added button component.
     */
    int addButton(String text, int x, int y, int width, int height, Runnable callback, int layer);
    /** Adds a combo box (drop-down list) to the UI with specified properties.
     *
     * @param items        An array of items to display in the combo box.
     * @param selectedItem The item that should be selected by default.
     * @param x            The x-coordinate of the combo box's position.
     * @param y            The y-coordinate of the combo box's position.
     * @param width        The width of the combo box.
     * @param height       The height of the combo box.
     * @param callback     A callback function that receives the selected item as a string when the selection changes.
     * @param layer        The layer on which to add the combo box (higher layers are drawn on top).
     * @param <T>          The type of items in the combo box (e.g., String, Integer).
     * @return The ID of the added combo box component.
     */
    <T> int addComboBox(T[] items, T selectedItem, int x, int y, int width, int height, Consumer<String> callback, int layer);

    /**
     * Modifies the properties of an existing stack of game pieces.
     * This method allows updating the number of pieces and their colors in the stack.
     *
     * @param ID     The ID of the stack component to modify.
     * @param unit1  The new number of pieces for the first color.
     * @param color1 The color of the first set of pieces.
     * @param unit2  The new number of pieces for the second color.
     * @param color2 The color of the second set of pieces.
     */
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
    /** Shows the UI components managed by this generator.
     * This method makes the components visible on the screen.
     */
    void show();
    /** Hides the UI components managed by this generator.
     * This method makes the components invisible on the screen.
     */
    void hide();
    /** Creates and returns a copy of this ComponentGenerator instance.
     * This method is useful for creating separate instances with the same configuration.
     *
     * @return A new ComponentGenerator instance that is a copy of this one.
     */
    ComponentGenerator copy();
}
