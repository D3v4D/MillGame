package org.view.base;

public interface ComponentGenerator {
    public void generateBase(int x, int y);
    public void addButton(String text, int x, int y, int width, int height, int gap, Runnable callback);
    public void render();
}
