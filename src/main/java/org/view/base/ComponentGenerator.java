package org.view.base;


import org.util.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public interface ComponentGenerator {

    enum HorizontalAlignment {
        LEFT, CENTER, RIGHT
    }

    enum VerticalAlignment {
        TOP, MIDDLE, BOTTOM
    }

    record Color(int r, int g, int b) {

    }

    void modifyButtonText(int ID, String text);


     void generateBase(int x, int y);

     void paintBackground(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2  , Color color1, Color color2, int layer);

    void paintBoard(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2, Color color1, Color color2, List<Line> lineList, int layer);

    int addLabel(String text, int x, int y, int width, int height, int size, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int layer);
    public int addButton(String text, int x, int y, int width, int height, Runnable callback, int layer);
    public <T> int addComboBox(T[] items, T selectedItem, int x, int y, int width, int height, Consumer<String> callback, int layer);

    public void show();
    public void hide();

    public ComponentGenerator copy();
}
