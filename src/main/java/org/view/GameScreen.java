package org.view;


import org.controller.GameClient;
import org.util.MapModel;
import org.view.base.ComponentGenerator;

public class GameScreen {
    private ComponentGenerator componentGenerator;

    private GameClient gameClient;

    public void setGameClient(GameClient gameClient){
        this.gameClient = gameClient;
    }

    public GameScreen(ComponentGenerator componentGenerator, MapModel mapModel){
        this.componentGenerator = componentGenerator;



        componentGenerator.generateBase(1050, 780);

        int labelID=
                componentGenerator.addLabel("kutyafasz", 750, 500, 300, 50, 32, ComponentGenerator.HorizontalAlignment.CENTER, ComponentGenerator.VerticalAlignment.MIDDLE, 10);

        componentGenerator.paintBoard(0, 0,750, 750, 0, 0, 750, 750, new ComponentGenerator.Color(205, 133, 63), new ComponentGenerator.Color(196, 164, 132), mapModel.lineList, 0);

        componentGenerator.paintBackground(750, 0, 300, 750, 750, 0, 300, 750, new ComponentGenerator.Color(150, 150, 150), new ComponentGenerator.Color(90, 90, 90), 0);




        componentGenerator.show();
    }

}
