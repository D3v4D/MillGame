package org.view;


import org.controller.GameClient;
import org.jetbrains.annotations.NotNull;
import org.util.MapModel;
import org.util.PlayerColor;

public class GameScreen {
    private final ComponentGenerator componentGenerator;
    private final GameClient gameClient;
    private int lightStackID;
    private int darkStackID;
    private final PlayerColor myColor;

    /**
     * Constructs a GameScreen with the specified parameters.
     *
     * @param componentGenerator the component generator used to create UI components
     * @param mapModel           the model representing the game map
     * @param color              the player's color
     * @param gameClient         the game client for handling game logic
     */
    public GameScreen(@NotNull ComponentGenerator componentGenerator, MapModel mapModel, PlayerColor color, GameClient gameClient) {
        myColor = color;
        this.gameClient = gameClient;
        this.componentGenerator = componentGenerator;
        componentGenerator.generateBase(1050, 780);

        //Label
        int labelID = componentGenerator.addLabel("Hello World!", 750, 500, 300, 50, 20, ComponentGenerator.HorizontalAlignment.CENTER, ComponentGenerator.VerticalAlignment.MIDDLE, 10);

        //Board
        componentGenerator.paintBoard(0, 0, 750, 750, 0, 0, 750, 750, new ComponentGenerator.Color(205, 133, 63), new ComponentGenerator.Color(196, 164, 132), mapModel, 0, (i) -> gameClient.sendUp(i));
        //SideMenu
        componentGenerator.paintBackground(750, 0, 300, 750, 0, 0, 300, 750, new ComponentGenerator.Color(150, 150, 150), new ComponentGenerator.Color(90, 90, 90), 0);

        //Stack of the light player
        lightStackID = drawStack(
                815, mapModel.pieces,
                ComponentGenerator.Color.lightBrown,
                PlayerColor.LIGHT
        );

        //Stack of the dark player
        darkStackID = drawStack(
                915, mapModel.pieces,
                ComponentGenerator.Color.darkBrown,
                PlayerColor.DARK
        );

        //Button
        componentGenerator.addButton("Exit", 800, 675, 200, 50, () -> {
            gameClient.exitGame();
        }, 20);

        /*componentGenerator.addButton("Save Game", 800, 600, 200, 50, () -> {
            //save game
        }, 20);*/

        componentGenerator.addUITestGif();

        componentGenerator.show();
    }

    /** Draws the stack of pieces for a player.
     *
     * @param x           The x-coordinate where the stack should be drawn.
     * @param unit        The number of pieces in the stack.
     * @param color       The color of the pieces in the stack.
     * @param playerColor The color representing the player (LIGHT or DARK).
     * @return The ID of the drawn stack component.
     */
    int drawStack(int x, int unit, ComponentGenerator.Color color, PlayerColor playerColor) {
        return componentGenerator.drawStack(x, 30, 70, 20, 10, unit, color, 100, playerColor == myColor);
    }

    public void changeLabelText(int labelID, String text) {
        componentGenerator.modifyLabelText(labelID, text);
    }

    public void updateFieldType(@NotNull java.util.List<Integer> fields, ComponentGenerator.FieldType fieldType) {
        componentGenerator.updateFieldGraphic(fields, fieldType);
    }

    public void updateFieldType(int field, ComponentGenerator.FieldType fieldType) {
        componentGenerator.updateFieldGraphic(field, fieldType);
    }

    public void hide() {
        componentGenerator.hide();
    }

    public void updateStacks(PlayerColor color, int myUnitsLeftToPlace, int myUnitsRemoved, int opponentUnitsLeftToPlace, int opponentUnitsRemoved) {
        if(color == PlayerColor.LIGHT){
            componentGenerator.modifyStack(lightStackID, opponentUnitsRemoved, ComponentGenerator.Color.darkBrown, myUnitsLeftToPlace, ComponentGenerator.Color.lightBrown);
            componentGenerator.modifyStack(darkStackID, myUnitsRemoved, ComponentGenerator.Color.lightBrown, opponentUnitsLeftToPlace, ComponentGenerator.Color.darkBrown);
        }
        else{
            componentGenerator.modifyStack(darkStackID, opponentUnitsRemoved, ComponentGenerator.Color.lightBrown, myUnitsLeftToPlace, ComponentGenerator.Color.darkBrown);
            componentGenerator.modifyStack(lightStackID, myUnitsRemoved, ComponentGenerator.Color.darkBrown, opponentUnitsLeftToPlace, ComponentGenerator.Color.lightBrown);
        }
    }

    public void show() {
        componentGenerator.show();
    }

    public void dispose() {
        componentGenerator.dispose();
    }

    public void endGame(){
        dispose();
    }
}
