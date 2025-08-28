package org.view.base;

import org.model.Color;

import java.util.function.Consumer;


public interface ComponentGenerator {
    void modifyButtonText(int ID, String text);

    public void generateBase(int x, int y);

    public void paintBackground(int rectX1, int rectY1, int rectX2, int rectY2, int fillX1, int fillY1, int fillX2, int fillY2  , Color color1, Color color2);


        public int addButton(String text, int x, int y, int width, int height, Runnable callback);
    public <T> int addComboBox(T[] items, T selectedItem, int x, int y, int width, int height, Consumer<String> callback);
    public int addTable();

    public void show();
    public void hide();

    public ComponentGenerator copy();
}
