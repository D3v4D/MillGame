package org.view;


import org.controller.GameClient;
import org.jetbrains.annotations.NotNull;
import org.util.MapModel;
import org.util.PlayerColor;
import org.view.base.ComponentGenerator;

public class GameScreen {
    private final ComponentGenerator componentGenerator;
    private GameClient gameClient;
    int lightStackID;
    int darkStackID;
    private final PlayerColor myColor;

    public GameScreen(@NotNull ComponentGenerator componentGenerator, MapModel mapModel, PlayerColor color, GameClient gameClient) {
        myColor = color;
        this.gameClient = gameClient;
        this.componentGenerator = componentGenerator;
        componentGenerator.generateBase(1050, 780);

        //Label
        int labelID = componentGenerator.addLabel("Hello World!", 750, 500, 300, 50, 32, ComponentGenerator.HorizontalAlignment.CENTER, ComponentGenerator.VerticalAlignment.MIDDLE, 10);

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

        componentGenerator.show();
    }

    int drawStack(int x, int unit, ComponentGenerator.Color color, PlayerColor playerColor) {
        return componentGenerator.drawStack(x, 30, 70, 20, 10, unit, color, 100, playerColor == myColor);
    }

    public void changeLabelText(int labelID, String text) {
        componentGenerator.modifyLabelText(labelID, text);
    }

    public void changeFieldGraphics(@NotNull java.util.List<Integer> fields, ComponentGenerator.FieldType fieldType) {
        componentGenerator.updateFieldGraphic(fields, fieldType);
    }

    public void hide() {
        componentGenerator.hide();
    }



    public void show() {
        componentGenerator.show();
    }

    public void dispose() {
        componentGenerator.dispose();
    }
}
